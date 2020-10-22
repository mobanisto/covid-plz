// Copyright (c) 2020 Mobanisto UG (haftungsbeschränkt)
//
// This file is part of covid-plz.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

package de.mobanisto.covidplz.brandenburg.kkm;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import lombok.Getter;

public class BrandenburgKkmParser
{

	final static Logger logger = LoggerFactory
			.getLogger(BrandenburgKkmParser.class);

	private static Set<String> validNames = new HashSet<>();
	static {
		validNames.addAll(Arrays.asList("Barnim", "Brandenburg a. d. H.",
				"Cottbus", "Dahme-Spreewald", "Elbe-Elster", "Frankfurt (Oder)",
				"Havelland", "Märkisch-Oderland", "Oberhavel",
				"Oberspreewald-Lausitz", "Oder-Spree", "Ostprignitz-Ruppin",
				"Potsdam", "Potsdam-Mittelmark", "Prignitz", "Spree-Neiße",
				"Teltow-Fläming", "Uckermark", "Brandenburg gesamt"));
	}

	private static enum Column {
		NAME,
		NEW_CASES_24,
		CASES_TOTAL,
		DEATHS_TOTAL,
		INCIDENCE100_7,
		INCIDENCE100,
	}

	@Getter
	private SingleBrandenburgKkmData data = new SingleBrandenburgKkmData();

	private LocalDate date;

	public void parse(Document doc, LocalDate date) throws ParsingException
	{
		this.date = date;

		Elements tables = doc.select("table");
		if (tables.size() == 0) {
			logger.warn("No table found");
		}

		for (int i = 0; i < tables.size(); i++) {
			Element table = tables.get(i);
			parseTable(table);
			data.setValid(true);
		}
	}

	private void parseTable(Element table) throws ParsingException
	{
		Elements rows = table.select("tr");
		for (int i = 0; i < rows.size(); i++) {
			Element row = rows.get(i);
			Elements cells = row.select("td");
			if (i == 0) {
				parseHeader(cells);
			} else {
				try {
					parseRow(cells);
				} catch (ParseException e) {
					throw new ParsingException("Error while parsing row", e);
				}
			}
		}
	}

	private void parseHeader(Elements cells) throws ParsingException
	{
		for (int k = 0; k < cells.size(); k++) {
			Element cell = cells.get(k);
			String text = cell.text();
			boolean passed = map(text, k);

			if (!passed) {
				throw new ParsingException(
						String.format("Unexpected text in cell '%s'", text));
			}
		}
	}

	private static Map<Column, List<String>> requiredWords = new HashMap<>();
	static {
		requiredWords.put(Column.NAME,
				Arrays.asList("landkreis", "kreisfreie"));
		requiredWords.put(Column.NEW_CASES_24,
				Arrays.asList("neue", "bestätigt", "fälle", "24-h"));
		requiredWords.put(Column.CASES_TOTAL,
				Arrays.asList("zahl", "bestätigt", "fälle"));
		requiredWords.put(Column.DEATHS_TOTAL,
				Arrays.asList("sterbefälle", "kumuliert"));
		requiredWords.put(Column.INCIDENCE100_7,
				Arrays.asList("7-tage", "inzidenz", "100.000"));
		requiredWords.put(Column.INCIDENCE100,
				Arrays.asList("inzidenz", "bestätigt", "100.000"));
	}

	private BiMap<Integer, Column> map = HashBiMap.create();

	private boolean map(String text, int k)
	{
		int found = 0;
		String lower = text.toLowerCase();
		for (Column column : Column.values()) {
			if (matcher(column).apply(lower)) {
				if (++found == 1) {
					map.put(k, column);
				} else {
					logger.warn(String.format("Ambigious column '%s'", text));
					logger.warn(String.format("Found '%s'",
							requiredWords.get(map.get(k))));
					logger.warn(String.format("But also '%s'",
							requiredWords.get(column)));
				}
			}
		}
		return found == 1;
	}

	private Function<String, Boolean> matcher(Column column)
	{
		if (column == Column.CASES_TOTAL) {
			return s -> {
				boolean containsAll = containsAll(s, requiredWords.get(column));
				if (!containsAll) {
					return false;
				}
				return s.contains("kumuliert") || s.contains("kumulativ");
			};
		} else if (column == Column.DEATHS_TOTAL) {
			if (date.isBefore(LocalDate.of(2020, 5, 7))) {
				return s -> containsAll(s,
						Arrays.asList("sterbefälle", "wohnort"));
			} else {
				return s -> containsAll(s, requiredWords.get(column));
			}
		} else {
			return s -> containsAll(s, requiredWords.get(column));
		}
	}

	private boolean containsAll(String text, Iterable<String> contained)
	{
		for (String string : contained) {
			if (!text.contains(string)) {
				return false;
			}
		}
		return true;
	}

	private void parseRow(Elements cells) throws ParseException
	{
		String name = null;
		int newCases24 = 0;
		int casesTotal = 0;
		double incidence100k_7 = 0;
		double incidence100k = 0;
		int deathsTotal = 0;

		for (int k = 0; k < cells.size(); k++) {
			Element cell = cells.get(k);
			String text = cell.text();
			Column column = map.get(k);
			if (column == null) {
				continue;
			}
			switch (column) {
			default:
				break;
			case NAME:
				name = text;
				break;
			case NEW_CASES_24:
				newCases24 = parseInt(text);
				break;
			case CASES_TOTAL:
				casesTotal = parseInt(text);
				break;
			case DEATHS_TOTAL:
				deathsTotal = parseInt(text);
				break;
			case INCIDENCE100:
				incidence100k = parseDouble(text);
				break;
			case INCIDENCE100_7:
				incidence100k_7 = parseDouble(text);
				break;
			}
		}

		KkmData rowData = new KkmData(newCases24, casesTotal, incidence100k,
				incidence100k_7, deathsTotal);

		if (validNames.contains(name)) {
			data.getNameToData().put(name, rowData);
		} else {
			// if this is not one of the valid names, it could be that it was
			// marked with an asterisk for some reason
			for (String valid : validNames) {
				int distance = LevenshteinDistance.getDefaultInstance()
						.apply(name, valid);
				if (distance < 3) {
					if (distance > 0) {
						logger.warn(String.format(
								"Approximate match '%s' -> '%s'", name, valid));
					}
					data.getNameToData().put(valid, rowData);
					break;
				}
			}
		}
	}

	private NumberFormat numberFormat = NumberFormat.getInstance(Locale.GERMAN);

	private double parseDouble(String text) throws ParseException
	{
		return numberFormat.parse(text).doubleValue();
	}

	private int parseInt(String text) throws ParseException
	{
		if (text.startsWith("+")) {
			text = text.substring(1).trim();
		}
		return numberFormat.parse(text).intValue();
	}

}

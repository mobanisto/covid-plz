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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import lombok.Getter;

public class BrandenburgKkmParser
{

	private static Set<String> validNames = new HashSet<>();
	static {
		validNames.addAll(Arrays.asList("Barnim", "Brandenburg a. d. H.",
				"Cottbus", "Dahme-Spreewald", "Elbe-Elster", "Frankfurt (Oder)",
				"Havelland", "Märkisch-Oderland", "Oberhavel",
				"Oberspreewald-Lausitz", "Oder-Spree", "Ostprignitz-Ruppin",
				"Potsdam", "Potsdam-Mittelmark", "Prignitz", "Spree-Neiße",
				"Teltow-Fläming", "Uckermark", "Brandenburg gesamt"));
	}

	@Getter
	private BrandenburgKkmData data = new BrandenburgKkmData();

	public void parse(Document doc) throws ParsingException
	{
		Elements tables = doc.select("table");
		for (int i = 0; i < tables.size(); i++) {
			Element table = tables.get(i);
			parseTable(table);
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
			boolean passed = false;
			if (k == 0) {
				passed = check(text, "kreis");
			} else if (k == 1) {
				passed = check(text, "neue", "bestätigt", "fälle", "24");
			} else if (k == 2) {
				passed = check(text, "zahl", "bestätigt", "fälle", "kumuliert");
			} else if (k == 3) {
				passed = check(text, "7", "tage", "inzidenz");
			} else if (k == 4) {
				passed = check(text, "inzidenz", "kumuliert");
			} else if (k == 5) {
				passed = check(text, "sterbefälle", "kumuliert");
			}
			if (!passed) {
				throw new ParsingException(
						String.format("Unexpected text in cell '%s'", text));
			}
		}
	}

	private boolean check(String text, String... contained)
	{
		String lower = text.toLowerCase();
		for (String string : contained) {
			if (!lower.contains(string)) {
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
			if (k == 0) {
				name = text;
			} else if (k == 1) {
				newCases24 = parseInt(text);
			} else if (k == 2) {
				casesTotal = parseInt(text);
			} else if (k == 3) {
				incidence100k_7 = parseDouble(text);
			} else if (k == 4) {
				incidence100k = parseDouble(text);
			} else if (k == 5) {
				deathsTotal = parseInt(text);
			}
		}

		Data rowData = new Data(newCases24, casesTotal, incidence100k,
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

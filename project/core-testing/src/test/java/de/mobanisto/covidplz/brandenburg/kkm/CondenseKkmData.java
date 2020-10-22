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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.mobanisto.covidplz.DailyDataLoader;
import de.mobanisto.covidplz.model.DailyData;
import de.mobanisto.covidplz.model.RegionData;
import de.mobanisto.covidplz.rki.daily.Fields;
import de.topobyte.melon.commons.io.Resources;
import de.topobyte.system.utils.SystemPaths;

public class CondenseKkmData
{

	final static Logger logger = LoggerFactory.getLogger(CondenseKkmData.class);

	public static void main(String[] args) throws IOException
	{
		CondenseKkmData task = new CondenseKkmData();
		task.execute();
	}

	private BrandenburgKkmData data = new BrandenburgKkmData();
	private DailyData baseData;
	private Map<String, String> genToRs;

	private void execute() throws IOException
	{
		parseBaseData();
		parseKkmData();
		writeData();
	}

	private void parseBaseData()
	{
		try (InputStream input = Resources.stream("data.csv")) {
			baseData = DailyDataLoader.load(input);
		} catch (IOException e) {
			logger.error("Error while loading base data", e);
		}

		genToRs = new HashMap<>();
		Map<String, RegionData> rsToRegionData = baseData.getRsToRegionData();
		for (String rs : rsToRegionData.keySet()) {
			RegionData regionData = rsToRegionData.get(rs);
			String gen = regionData.getData().get(Fields.GEN);
			genToRs.put(gen, rs);
		}

		genToRs.put("Brandenburg a. d. H.",
				genToRs.get("Brandenburg an der Havel"));
	}

	private void parseKkmData() throws IOException
	{
		Path repo = SystemPaths.CWD.getParent().getParent();
		Path dirKkm = repo.resolve("data/brandenburg/kkm");

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");

		LocalDate start = LocalDate.of(2020, 4, 20);
		LocalDate end = LocalDate.now();

		for (LocalDate date = start; !date.isAfter(end); date = date
				.plusDays(1)) {
			String filename = String.format("%s.html", formatter.format(date));
			Path file = dirKkm.resolve(filename);
			if (!Files.exists(file)) {
				continue;
			}
			try {
				parse(file, date);
			} catch (ParsingException e) {
				logger.warn("error while paring file: " + e.getMessage());
				continue;
			}
		}
	}

	private void parse(Path file, LocalDate date)
			throws IOException, ParsingException
	{
		InputStream input = Files.newInputStream(file);
		SingleBrandenburgKkmData dayData = BrandenburgKkm.parse(input, date);

		if (!dayData.isValid()) {
			return;
		}

		Map<LocalDate, Map<String, Data>> dateToNameToData = data
				.getDateToNameToData();
		Map<String, Data> nameToData = dateToNameToData.get(date);
		if (nameToData == null) {
			nameToData = new TreeMap<>();
			dateToNameToData.put(date, nameToData);
		}

		Map<String, Data> currentNameToData = dayData.getNameToData();
		for (String name : currentNameToData.keySet()) {
			if (name.equals("Brandenburg gesamt")) {
				continue;
			}
			String rs = genToRs.get(name);
			nameToData.put(rs, currentNameToData.get(name));
		}
	}

	private void writeData() throws IOException
	{
		Path repo = SystemPaths.CWD.getParent().getParent();
		Path bb = repo.resolve("data/brandenburg");
		Path fileOutput = bb.resolve("kkm.json");

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try (BufferedWriter output = Files.newBufferedWriter(fileOutput)) {
			gson.toJson(data, output);
		}
	}

}

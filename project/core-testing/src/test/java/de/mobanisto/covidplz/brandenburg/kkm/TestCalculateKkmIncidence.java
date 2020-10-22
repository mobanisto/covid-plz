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
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;

import com.google.gson.Gson;

import de.mobanisto.covidplz.DailyDataLoader;
import de.mobanisto.covidplz.GsonUtil;
import de.mobanisto.covidplz.model.DailyData;
import de.mobanisto.covidplz.model.RegionData;
import de.mobanisto.covidplz.rki.daily.Fields;
import de.topobyte.melon.commons.io.Resources;
import de.topobyte.system.utils.SystemPaths;

public class TestCalculateKkmIncidence
{

	public static void main(String[] args) throws IOException
	{
		Gson gson = GsonUtil.gson();

		String jsonKkm = Resources.loadString("kkm.json");
		BrandenburgKkmData data = gson.fromJson(jsonKkm,
				BrandenburgKkmData.class);

		Path repo = SystemPaths.CWD.getParent().getParent();
		Path bb = repo.resolve("data/brandenburg");
		Path fileOutput = bb.resolve("kkm.json");

		Path file = repo.resolve("data/daily/2020_10_08.csv");

		DailyData rki = DailyDataLoader.load(file);

		calculate(data, rki);

		try (BufferedWriter output = Files.newBufferedWriter(fileOutput)) {
			gson.toJson(data, output);
		}
	}

	private static void calculate(BrandenburgKkmData data, DailyData rki)
	{
		Map<LocalDate, Map<String, KkmData>> dateToNameToData = data
				.getDateToNameToData();
		for (LocalDate date : dateToNameToData.keySet()) {
			System.out.println(date);
			if (date.isBefore(LocalDate.of(2020, 10, 15))) {
				continue;
			}
			Map<String, KkmData> daily = dateToNameToData.get(date);
			calculate(daily, rki);
		}
	}

	private static void calculate(Map<String, KkmData> daily, DailyData rki)
	{
		for (String rs : daily.keySet()) {
			KkmData data = daily.get(rs);
			RegionData regionData = rki.getRsToRegionData().get(rs);
			int population = Integer
					.parseInt(regionData.getData().get(Fields.EWZ));

			int total = data.getCasesTotal();
			double incidence = total / (double) population * 100_000;

			if (data.getIncidence100k() == 0) {
				System.out
						.println("zero: " + rs + " " + total + " " + incidence);
				data.setIncidence100k(round(incidence, 2));
			} else {
				System.out.println(String.format("%f vs. %f",
						data.getIncidence100k(), incidence));
			}
		}
	}

	public static double round(double value, int places)
	{
		int factor = (int) Math.pow(10, places);
		value = value * factor;
		return Math.round(value) / (double) factor;
	}

}

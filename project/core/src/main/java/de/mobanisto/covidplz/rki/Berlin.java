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

package de.mobanisto.covidplz.rki;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.mobanisto.covidplz.model.DailyData;
import de.mobanisto.covidplz.model.RegionData;
import de.mobanisto.covidplz.rki.daily.Fields;

public class Berlin
{

	public static final String RS = "11000";

	public static boolean isBerlinBorough(RegionData regionData)
	{
		Map<String, String> data = regionData.getData();
		return data.get(Fields.BL).equals("Berlin");
	}

	public static void deriveBerlinData(DailyData data)
	{
		Set<String> berlinRss = new HashSet<>();

		Map<String, RegionData> rss = data.getRsToRegionData();
		for (String rs : rss.keySet()) {
			if (isBerlinBorough(rss.get(rs))) {
				berlinRss.add(rs);
			}
		}

		Map<String, String> berlinData = new HashMap<>();
		berlinData.put(Fields.BEZ, "Land");
		berlinData.put(Fields.GEN, "Berlin");

		String lastUpdate = null;
		int totalCases = 0;
		int totalDeaths = 0;
		int totalEwz = 0;
		int totalCases7 = 0;

		for (String rs : berlinRss) {
			Map<String, String> borough = data.getRsToRegionData().get(rs)
					.getData();
			if (lastUpdate == null) {
				lastUpdate = borough.get(Fields.LAST_UPDATE);
			}

			int cases = Integer.parseInt(borough.get(Fields.CASES));
			int deaths = Integer.parseInt(borough.get(Fields.DEATHS));
			int ewz = Integer.parseInt(borough.get(Fields.EWZ));

			totalCases += cases;
			totalDeaths += deaths;
			totalEwz += ewz;

			double cases7Per100k = Double
					.parseDouble(borough.get(Fields.CASES7_PER_100K));
			totalCases7 += Math.round(cases7Per100k * ewz / 100_000);
		}

		double casesPerPopulation = totalCases / (double) totalEwz * 100;
		double casesPer100k = totalCases / (double) totalEwz * 100_000;
		double cases7Per100k = totalCases7 / (double) totalEwz * 100_000;

		berlinData.put(Fields.LAST_UPDATE, lastUpdate);
		berlinData.put(Fields.CASES, Integer.toString(totalCases));
		berlinData.put(Fields.DEATHS, Integer.toString(totalDeaths));
		berlinData.put(Fields.EWZ, Integer.toString(totalEwz));
		berlinData.put(Fields.CASES_PER_POPULATION,
				Double.toString(casesPerPopulation));
		berlinData.put(Fields.CASES_PER_100K, Double.toString(casesPer100k));
		berlinData.put(Fields.CASES7_PER_100K, Double.toString(cases7Per100k));

		data.getRsToRegionData().put(RS, new RegionData(RS, berlinData));
	}

}

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

package de.mobanisto.covidplz.berlin;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import de.mobanisto.covidplz.DailyDataLoader;
import de.mobanisto.covidplz.model.DailyData;
import de.mobanisto.covidplz.model.RegionData;
import de.mobanisto.covidplz.rki.Berlin;
import de.mobanisto.covidplz.rki.Fields;
import de.topobyte.system.utils.SystemPaths;

public class TestValidateRKIBerlinBoroughData
{

	private int totalCases = 0;

	@Test
	public void test() throws IOException
	{
		Path repo = SystemPaths.CWD.getParent().getParent();
		Path file = repo.resolve("data/daily/2020_10_11.csv");

		DailyData data = DailyDataLoader.load(file);

		Map<String, RegionData> rss = data.getRsToRegionData();
		for (String rs : rss.keySet()) {
			if (Berlin.isBerlinBorough(rss.get(rs))) {
				RegionData borough = rss.get(rs);
				check(borough);
			}
		}

		System.out.println("total cases: " + totalCases);
	}

	private void check(RegionData data)
	{
		Map<String, String> map = data.getData();
		int ewz = Integer.parseInt(map.get(Fields.EWZ));
		int cases = Integer.parseInt(map.get(Fields.CASES));
		double casesPerPopulation = Double
				.parseDouble(map.get(Fields.CASES_PER_POPULATION));
		double casesPer100k = Double
				.parseDouble(map.get(Fields.CASES_PER_100K));
		double cases7Per100k = Double
				.parseDouble(map.get(Fields.CASES7_PER_100K));

		System.out.println("Name:                  " + map.get(Fields.GEN));
		System.out.println(
				"last update:           " + map.get(Fields.LAST_UPDATE));
		System.out.println("EWZ:                   " + ewz);
		System.out.println("cases:                 " + cases);
		System.out.println("cases per population:  " + casesPerPopulation);
		System.out.println("cases per 100k:        " + casesPer100k);
		System.out.println("cases7 per 100k:       " + cases7Per100k);

		double calcCasesPerPopulation = cases / (double) ewz * 100;
		Assert.assertEquals(casesPerPopulation, calcCasesPerPopulation, 0.001);

		double calcCasesPer100k = cases / (double) ewz * 100_000;
		Assert.assertEquals(casesPer100k, calcCasesPer100k, 0.001);

		double cases7 = cases7Per100k * ewz / 100_000;
		System.out.println("cases7:                " + cases7);

		double calcCases7Per100k = cases7 / ewz * 100_000;
		Assert.assertEquals(cases7Per100k, calcCases7Per100k, 0.001);

		totalCases += cases;
	}

}

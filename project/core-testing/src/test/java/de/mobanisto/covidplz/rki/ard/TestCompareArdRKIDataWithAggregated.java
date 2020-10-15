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

package de.mobanisto.covidplz.rki.ard;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import de.mobanisto.covidplz.ArdDataLoader;
import de.mobanisto.covidplz.DailyDataLoader;
import de.mobanisto.covidplz.model.ArdData;
import de.mobanisto.covidplz.model.ArdEntry;
import de.mobanisto.covidplz.model.DailyData;
import de.mobanisto.covidplz.model.RegionData;
import de.mobanisto.covidplz.rki.daily.Fields;
import de.topobyte.system.utils.SystemPaths;

@RunWith(Parameterized.class)
public class TestCompareArdRKIDataWithAggregated
{

	@Parameters
	public static Collection<Object[]> data()
	{
		return Arrays.asList(new Object[][] { //
				{ "data_2020-10-06-02-31.json.bz2", "2020_10_06.csv" }, //
				{ "data_2020-10-07-02-30.json.bz2", "2020_10_07.csv" }, //
				{ "data_2020-10-08-02-31.json.bz2", "2020_10_08.csv" }, //
				{ "data_2020-10-11-02-31.json.bz2", "2020_10_11.csv" }, //
				{ "data_2020-10-15-02-31.json.bz2", "2020_10_15.csv" } //
		});
	}

	private String filenameArd;
	private String filenameDaily;

	public TestCompareArdRKIDataWithAggregated(String filenameArd,
			String filenameDaily)
	{
		this.filenameArd = filenameArd;
		this.filenameDaily = filenameDaily;
	}

	@Test
	public void test() throws IOException
	{
		Path repo = SystemPaths.CWD.getParent().getParent();
		Path fileArd = repo.resolve("data/ard").resolve(filenameArd);
		Path fileDaily = repo.resolve("data/daily").resolve(filenameDaily);

		ArdData data = ArdDataLoader.load(fileArd);
		List<ArdEntry> entries = data.getEntries();

		DailyData dailyData = DailyDataLoader.load(fileDaily);

		Multiset<String> faelle = HashMultiset.create();
		Multiset<String> todesFaelle = HashMultiset.create();

		for (ArdEntry entry : entries) {
			int anzahlFall = entry.getAnzahlFall();
			if (anzahlFall > 0) {
				faelle.add(entry.getIdLandkreis(), anzahlFall);
			}
			int anzahlTodesFall = entry.getAnzahlTodesfall();
			if (anzahlTodesFall > 0) {
				todesFaelle.add(entry.getIdLandkreis(), anzahlTodesFall);
			}
		}

		for (String id : faelle.elementSet()) {
			RegionData regionData = dailyData.getRsToRegionData().get(id);
			int casesDaily = Integer
					.parseInt(regionData.getData().get(Fields.CASES));
			int casesRaw = faelle.count(id);
			Assert.assertEquals(casesDaily, casesRaw);

			int deathsDaily = Integer
					.parseInt(regionData.getData().get(Fields.DEATHS));
			int deathsRaw = todesFaelle.count(id);
			Assert.assertEquals(deathsDaily, deathsRaw);
		}
	}

}

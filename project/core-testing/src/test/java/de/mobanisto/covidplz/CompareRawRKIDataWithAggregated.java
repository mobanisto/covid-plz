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

package de.mobanisto.covidplz;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import de.mobanisto.covidplz.model.DailyData;
import de.mobanisto.covidplz.model.RawData;
import de.mobanisto.covidplz.model.RawEntry;
import de.mobanisto.covidplz.model.RegionData;
import de.mobanisto.covidplz.rki.daily.Fields;
import de.topobyte.system.utils.SystemPaths;

public class CompareRawRKIDataWithAggregated
{

	@Test
	public void test() throws IOException
	{
		Path repo = SystemPaths.CWD.getParent().getParent();
		Path fileRaw = repo.resolve("data/raw/2020_10_15.csv.gz");
		Path fileDaily = repo.resolve("data/daily/2020_10_15.csv");

		InputStream input = Files.newInputStream(fileRaw);
		GZIPInputStream gzipInput = new GZIPInputStream(input);

		RawData data = RawDataLoader.load(gzipInput);
		List<RawEntry> entries = data.getEntries();

		DailyData dailyData = DailyDataLoader.load(fileDaily);

		Multiset<String> landkreisFaelle = HashMultiset.create();

		for (RawEntry entry : entries) {
			int anzahlFall = entry.getAnzahlFall();
			landkreisFaelle.add(entry.getIdLandkreis(), anzahlFall);
		}

		for (String id : landkreisFaelle.elementSet()) {
			RegionData regionData = dailyData.getRsToRegionData().get(id);
			int casesDaily = Integer
					.parseInt(regionData.getData().get(Fields.CASES));
			int casesRaw = landkreisFaelle.count(id);
			Assert.assertEquals(casesDaily, casesRaw);
		}
	}

}

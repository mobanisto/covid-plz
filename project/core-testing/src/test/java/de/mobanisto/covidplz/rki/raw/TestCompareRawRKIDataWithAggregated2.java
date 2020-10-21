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

package de.mobanisto.covidplz.rki.raw;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.junit.Test;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import de.mobanisto.covidplz.DailyDataLoader;
import de.mobanisto.covidplz.RawDataLoader;
import de.mobanisto.covidplz.model.DailyData;
import de.mobanisto.covidplz.model.RawData;
import de.mobanisto.covidplz.model.RawEntry;
import de.mobanisto.covidplz.model.RegionData;
import de.mobanisto.covidplz.rki.daily.Fields;
import de.topobyte.system.utils.SystemPaths;

public class TestCompareRawRKIDataWithAggregated2
{

	@Test
	public void test() throws IOException
	{
		Path repo = SystemPaths.CWD.getParent().getParent();
		Path fileRaw = repo.resolve("data/raw/2020_10_15.csv.gz");
		Path fileDaily = repo.resolve("data/daily/2020_10_07.csv");

		LocalDate dailyDate = LocalDate.of(2020, 10, 6);

		InputStream input = Files.newInputStream(fileRaw);
		GZIPInputStream gzipInput = new GZIPInputStream(input);

		RawData data = RawDataLoader.load(gzipInput);
		List<RawEntry> entries = data.getEntries();

		DailyData dailyData = DailyDataLoader.load(fileDaily);

		Multiset<String> faelle = HashMultiset.create();
		Multiset<String> todesFaelle = HashMultiset.create();

		for (RawEntry entry : entries) {
			// if (entry.getRefdatum().isAfter(dailyDate)) {
			// continue;
			// }
			// if (entry.getRefdatum().isEqual(dailyDate)) {
			// continue;
			// }
			if (entry.getMeldedatum().isAfter(dailyDate)) {
				continue;
			}
			// if (entry.getMeldedatum().isEqual(dailyDate)) {
			// continue;
			// }

			if (entry.getNeuerFall() < 0 || entry.getNeuerFall() > 0) {
				System.out.println("neuer fall: " + entry.getNeuerFall());
				System.out.println(entry.getMeldedatum());
				System.out.println(entry.getRefdatum());
			}

			int anzahlFall = entry.getAnzahlFall();
			if (anzahlFall > 0) {
				faelle.add(entry.getIdLandkreis(), anzahlFall);
			} else {
				System.out.println(dailyData.getRsToRegionData()
						.get(entry.getIdLandkreis()).getData().get(Fields.GEN));
				System.out.println(anzahlFall);
				// faelle.remove(entry.getIdLandkreis(), 1);
			}
			int anzahlTodesFall = entry.getAnzahlTodesfall();
			if (anzahlTodesFall > 0) {
				todesFaelle.add(entry.getIdLandkreis(), anzahlTodesFall);
			}
		}

		int e = 0;
		for (String id : faelle.elementSet()) {
			RegionData regionData = dailyData.getRsToRegionData().get(id);
			String name = regionData.getData().get(Fields.GEN);

			int casesDaily = Integer
					.parseInt(regionData.getData().get(Fields.CASES));
			int casesRaw = faelle.count(id);
			if (casesDaily != casesRaw) {
				System.out.println(String.format("%d Fälle %s: %d vs %d", ++e,
						name, casesDaily, casesRaw));
			}
			// Assert.assertEquals("Fälle " + name, casesDaily, casesRaw);

			// int deathsDaily = Integer
			// .parseInt(regionData.getData().get(Fields.DEATHS));
			// int deathsRaw = todesFaelle.count(id);
			// if (deathsDaily != deathsRaw) {
			// System.out.println(String.format("Todesfälle %s: %d vs %d",
			// name, deathsDaily, deathsRaw));
			// }
			// Assert.assertEquals("Todesfälle " + name, deathsDaily,
			// deathsRaw);
		}
	}

}

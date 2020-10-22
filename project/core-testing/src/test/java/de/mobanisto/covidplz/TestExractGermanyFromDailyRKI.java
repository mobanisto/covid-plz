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

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.google.gson.Gson;

import de.mobanisto.covidplz.model.DailyData;
import de.mobanisto.covidplz.model.RegionData;
import de.mobanisto.covidplz.model.germany.Bundesland;
import de.mobanisto.covidplz.model.germany.Germany;
import de.mobanisto.covidplz.model.germany.Kreis;
import de.mobanisto.covidplz.model.germany.Stadtteil;
import de.mobanisto.covidplz.rki.daily.Fields;
import de.topobyte.system.utils.SystemPaths;

public class TestExractGermanyFromDailyRKI
{

	@Test
	public void test() throws IOException
	{
		Path repo = SystemPaths.CWD.getParent().getParent();
		Path file = repo.resolve("data/daily/2020_10_08.csv");

		DailyData data = DailyDataLoader.load(file);

		Germany germany = build(data);
		Gson gson = GsonUtil.gson();
		Gson gsonGermany = GsonUtil.germany(null);

		Path dir = repo.resolve("project/core/src/main/resources/germany");
		Files.createDirectories(dir);

		Path fileLaender = dir.resolve("laender.json");
		Path fileKreise = dir.resolve("kreise.json");
		Path fileStadtteile = dir.resolve("stadtteile.json");

		try (BufferedWriter output = Files.newBufferedWriter(fileLaender)) {
			gson.toJson(germany.getLaender(), output);
		}
		try (BufferedWriter output = Files.newBufferedWriter(fileKreise)) {
			gsonGermany.toJson(germany.getKreise(), output);
		}
		try (BufferedWriter output = Files.newBufferedWriter(fileStadtteile)) {
			gsonGermany.toJson(germany.getStadtteile(), output);
		}
	}

	private Germany build(DailyData data)
	{
		Germany germany = new Germany();

		Map<String, Bundesland> idToBundesland = new HashMap<>();
		Set<String> names = new HashSet<>();

		Map<String, RegionData> map = data.getRsToRegionData();
		List<String> rss = new ArrayList<>(map.keySet());
		Collections.sort(rss);

		for (String rs : rss) {
			RegionData regionData = map.get(rs);
			Map<String, String> rd = regionData.getData();
			String idBundesland = rd.get(Fields.BL_ID);

			Bundesland bundesland = idToBundesland.get(idBundesland);

			if (bundesland == null) {
				String nameBundesland = rd.get(Fields.BL);
				bundesland = new Bundesland(idBundesland, nameBundesland);
				germany.getLaender().add(bundesland);
				idToBundesland.put(idBundesland, bundesland);
			}

			String name = rd.get(Fields.GEN);
			String bez = rd.get(Fields.BEZ);
			if (names.contains(name)) {
				continue;
			}
			names.add(name);

			if (name.equals("Region Hannover")) {
				germany.getKreise().add(new Kreis(rs, "Hannover",
						Kreis.Type.REGION, bundesland));
			} else if (name.equals("Städteregion Aachen")) {
				germany.getKreise().add(
						new Kreis(rs, "Aachen", Kreis.Type.REGION, bundesland));

			} else if (bez.equals("Landkreis")) {
				germany.getKreise().add(
						new Kreis(rs, name, Kreis.Type.LANDKREIS, bundesland));
			} else if (bez.equals("Kreisfreie Stadt")) {
				germany.getKreise().add(
						new Kreis(rs, name, Kreis.Type.STADTKREIS, bundesland));
			} else if (bez.equals("Bezirk")) {
				name = name.substring(7);
				germany.getStadtteile().add(new Stadtteil(rs, name));
			}
		}

		return germany;
	}

}

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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.junit.Test;

import com.google.gson.Gson;

import de.mobanisto.covidplz.model.RawData;
import de.mobanisto.covidplz.model.RawEntry;
import de.mobanisto.covidplz.model.germany.Bundesland;
import de.mobanisto.covidplz.model.germany.Germany;
import de.mobanisto.covidplz.model.germany.Kreis;
import de.mobanisto.covidplz.model.germany.Stadtteil;
import de.topobyte.system.utils.SystemPaths;

public class TestExractGermanyFromRawRKI
{

	@Test
	public void test() throws IOException
	{
		Path repo = SystemPaths.CWD.getParent().getParent();
		Path file = repo.resolve("data/raw/2020_10_15.csv.gz");

		InputStream input = Files.newInputStream(file);
		GZIPInputStream gzipInput = new GZIPInputStream(input);

		RawData data = RawDataLoader.load(gzipInput);
		List<RawEntry> entries = data.getEntries();

		Germany germany = build(entries);
		Gson gson = GsonUtil.gson();
		Gson gsonGermany = GsonUtil.germany();

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

	private Germany build(List<RawEntry> entries)
	{
		Germany germany = new Germany();

		Map<String, Bundesland> idToBundesland = new HashMap<>();
		Set<String> names = new HashSet<>();

		for (RawEntry entry : entries) {
			String id = Integer.toString(entry.getId());
			String idBundesland = entry.getIdBundesland();

			Bundesland bundesland = idToBundesland.get(idBundesland);

			if (bundesland == null) {
				String nameBundesland = entry.getBundesland();
				bundesland = new Bundesland(idBundesland, nameBundesland);
				germany.getLaender().add(bundesland);
				idToBundesland.put(idBundesland, bundesland);
			}

			String landkreis = entry.getLandkreis();
			if (names.contains(landkreis)) {
				continue;
			}
			names.add(landkreis);

			if (landkreis.startsWith("LK ")) {
				String name = landkreis.substring(3);
				germany.getKreise().add(
						new Kreis(id, name, Kreis.Type.LANDKREIS, bundesland));
			} else if (landkreis.startsWith("SK Berlin ")) {
				String name = landkreis.substring(10);
				germany.getStadtteile().add(new Stadtteil(id, name));
			} else if (landkreis.startsWith("SK ")) {
				String name = landkreis.substring(3);
				germany.getKreise().add(
						new Kreis(id, name, Kreis.Type.STADTKREIS, bundesland));
			} else if (landkreis.equals("StadtRegion Aachen")) {
				String name = "Aachen";
				germany.getKreise().add(
						new Kreis(id, name, Kreis.Type.REGION, bundesland));
			} else if (landkreis.equals("Region Hannover")) {
				String name = "Hannover";
				germany.getKreise().add(
						new Kreis(id, name, Kreis.Type.REGION, bundesland));
			}
		}

		return germany;
	}

}

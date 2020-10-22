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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import de.mobanisto.covidplz.brandenburg.kkm.BrandenburgKkmData;
import de.mobanisto.covidplz.brandenburg.kkm.KkmData;
import de.mobanisto.covidplz.mapping.Mapping;
import de.mobanisto.covidplz.mapping.PartialRsRelation;
import de.mobanisto.covidplz.model.Data;
import de.mobanisto.covidplz.model.germany.Bundesland;
import de.mobanisto.covidplz.model.germany.Germany;
import de.mobanisto.covidplz.model.germany.GermanyRegionLookup;
import de.mobanisto.covidplz.model.germany.Kreis;
import de.mobanisto.covidplz.model.germany.Stadtteil;
import de.topobyte.melon.commons.io.Resources;

public class DataLoader
{

	final static Logger logger = LoggerFactory.getLogger(DataLoader.class);

	public Data loadData() throws IOException
	{
		Data data = new Data();
		Gson gson = GsonUtil.gson();

		String jsonMapping = Resources.loadString("mapping.json");
		Mapping mapping = gson.fromJson(jsonMapping, Mapping.class);

		String jsonKkm = Resources.loadString("kkm.json");
		BrandenburgKkmData brandenburgKkmData = gson.fromJson(jsonKkm,
				BrandenburgKkmData.class);

		data.setMapping(mapping);
		data.setBrandenburgKkmData(brandenburgKkmData);

		// Load Germany administrative region model

		Germany germany = new Germany();

		String jsonLaender = Resources.loadString("germany/laender.json");
		List<Bundesland> laender = gson.fromJson(jsonLaender,
				new TypeToken<ArrayList<Bundesland>>() {
				}.getType());
		Map<String, Bundesland> idToLand = new HashMap<>();
		for (Bundesland land : laender) {
			idToLand.put(land.getId(), land);
		}

		Gson gsonGermany = GsonUtil.germany(idToLand);

		String jsonKreise = Resources.loadString("germany/kreise.json");
		List<Kreis> kreise = gsonGermany.fromJson(jsonKreise,
				new TypeToken<ArrayList<Kreis>>() {
				}.getType());

		String jsonStadtteile = Resources.loadString("germany/stadtteile.json");
		List<Stadtteil> stadtteile = gsonGermany.fromJson(jsonStadtteile,
				new TypeToken<ArrayList<Stadtteil>>() {
				}.getType());

		germany.getLaender().addAll(laender);
		germany.getKreise().addAll(kreise);
		germany.getStadtteile().addAll(stadtteile);

		data.setGermany(germany);
		data.setGermanyRegionLookup(new GermanyRegionLookup(germany));

		// Process mapping

		Map<String, List<PartialRsRelation>> codeToRKI = mapping.getCodeToRKI();

		// Get postal codes from mapping

		Set<String> postalCodes = new HashSet<>();
		postalCodes.addAll(codeToRKI.keySet());

		data.setPostalCodes(postalCodes);

		// Get RKI identifiers from mapping

		Set<String> rkiIdentifiers = new HashSet<>();

		for (String key : codeToRKI.keySet()) {
			List<PartialRsRelation> rkis = codeToRKI.get(key);
			for (PartialRsRelation rki : rkis) {
				rkiIdentifiers.add(rki.getObject());
			}
		}

		data.setRkiIdentifiers(rkiIdentifiers);

		// Check KKM data can be matched

		checkKkmData(brandenburgKkmData, data.getGermanyRegionLookup());

		return data;
	}

	private void checkKkmData(BrandenburgKkmData brandenburgKkmData,
			GermanyRegionLookup lookup)
	{
		Map<LocalDate, Map<String, KkmData>> dateToNameToData = brandenburgKkmData
				.getDateToNameToData();
		Map<String, KkmData> data = dateToNameToData
				.get(LocalDate.of(2020, 10, 13));
		for (String rs : data.keySet()) {
			Kreis kreis = lookup.getIdToKreis().get(rs);
			if (kreis == null) {
				logger.warn("Unable to match KKM id to Kreis: " + rs);
			}
		}
	}

}

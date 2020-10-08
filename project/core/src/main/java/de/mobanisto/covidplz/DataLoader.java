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
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;

import de.mobanisto.covidplz.mapping.Mapping;
import de.mobanisto.covidplz.mapping.PartialRsRelation;
import de.mobanisto.covidplz.model.Data;
import de.topobyte.melon.commons.io.Resources;

public class DataLoader
{

	public Data loadData(Path dir) throws IOException
	{
		Data data = new Data();

		String json = Resources.loadString("mapping.json");
		Gson gson = new Gson();
		Mapping mapping = gson.fromJson(json, Mapping.class);

		data.setMapping(mapping);

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

		return data;
	}

}

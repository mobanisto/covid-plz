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
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.mobanisto.covidplz.mapping.Mapping;
import de.mobanisto.covidplz.mapping.PartialRsRelation;
import de.mobanisto.covidplz.model.Data;

public class ShowDataInfo
{

	public void execute() throws IOException
	{
		Data data = new DataLoader().loadData();

		Set<String> postalCodes = data.getPostalCodes();
		System.out.println(String.format("Number of postal code regions: %d",
				postalCodes.size()));

		Set<String> rss = data.getRkiIdentifiers();
		System.out.println(
				String.format("Number of RKI regions: %d", rss.size()));

		Mapping mapping = data.getMapping();
		Map<String, List<PartialRsRelation>> codeToRKI = mapping.getCodeToRKI();

		int n = codeToRKI.size();

		int unambiguous = 0;
		int ambiguous = 0;
		for (String key : codeToRKI.keySet()) {
			List<PartialRsRelation> rkis = codeToRKI.get(key);
			if (rkis.size() == 1) {
				unambiguous += 1;
			} else {
				ambiguous += 1;
			}
		}

		System.out.println(String.format("Mapped unambiguously: %d/%d (%.1f%%)",
				unambiguous, n, unambiguous / (double) n * 100));
		System.out.println(String.format("Mapped ambiguously: %d/%d (%.1f%%)",
				ambiguous, n, ambiguous / (double) n * 100));
	}

}

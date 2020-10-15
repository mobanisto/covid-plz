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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import de.mobanisto.covidplz.ArdDataLoader;
import de.mobanisto.covidplz.model.ArdData;
import de.mobanisto.covidplz.model.ArdEntry;
import de.topobyte.system.utils.SystemPaths;

public class TestCountArdRKIDataCasesPerBundesland
{

	@Test
	public void test() throws IOException
	{
		Path repo = SystemPaths.CWD.getParent().getParent();
		Path fileArd = repo.resolve("data/ard/data_2020-10-15-02-31.json.bz2");

		ArdData data = ArdDataLoader.load(fileArd);
		List<ArdEntry> entries = data.getEntries();

		Multiset<String> bundeslandFaelle = HashMultiset.create();
		Multiset<String> landkreisFaelle = HashMultiset.create();

		for (ArdEntry entry : entries) {
			String bundesland = entry.getBundesland();
			int anzahlFall = entry.getAnzahlFall();
			if (anzahlFall < 0) {
				continue;
			}
			bundeslandFaelle.add(bundesland, anzahlFall);
			landkreisFaelle.add(entry.getIdLandkreis(), anzahlFall);
		}

		List<String> names = new ArrayList<>(bundeslandFaelle.elementSet());
		Collections.sort(names);

		int total = bundeslandFaelle.size();
		for (String name : names) {
			System.out.println(String.format("%s: %d", name,
					bundeslandFaelle.count(name)));
		}
		System.out.println(String.format("Total: %d", total));
	}

}

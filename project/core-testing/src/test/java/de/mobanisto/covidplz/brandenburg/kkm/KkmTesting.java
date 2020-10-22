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

package de.mobanisto.covidplz.brandenburg.kkm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;

public class KkmTesting
{

	public static void assertSums(Map<String, KkmData> nameToData)
	{
		String nameTotal = "Brandenburg gesamt";

		KkmData total = nameToData.get(nameTotal);

		List<KkmData> entries = new ArrayList<>();
		for (String key : nameToData.keySet()) {
			if (key.equals(nameTotal)) {
				continue;
			}
			entries.add(nameToData.get(key));
		}

		int newCases24 = 0;
		int casesTotal = 0;
		int deathsTotal = 0;

		for (KkmData data : entries) {
			newCases24 += data.getNewCases24();
			casesTotal += data.getCasesTotal();
			deathsTotal += data.getDeathsTotal();
		}

		Assert.assertEquals("new cases", total.getNewCases24(), newCases24);
		Assert.assertEquals("total cases", total.getCasesTotal(), casesTotal);
		Assert.assertEquals("deaths total", total.getDeathsTotal(),
				deathsTotal);
	}

}

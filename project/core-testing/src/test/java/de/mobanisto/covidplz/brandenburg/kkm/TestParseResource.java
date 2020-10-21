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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import de.topobyte.melon.commons.io.Resources;

public class TestParseResource
{

	@Test
	public void test1() throws IOException, ParsingException
	{
		InputStream input = Resources.stream("brandenburg/kkm/2020-08-20.html");
		BrandenburgKkmData data = BrandenburgKkm.parse(input);

		Map<String, Data> nameToData = data.getNameToData();

		Data total = nameToData.get("Brandenburg gesamt");
		Assert.assertEquals(17, total.getNewCases24());
		Assert.assertEquals(3782, total.getCasesTotal());
		Assert.assertEquals(149.2, total.getIncidence100k(), 0.0001);
		Assert.assertEquals(3.2, total.getIncidence100k_7(), 0.0001);
		Assert.assertEquals(173, total.getDeathsTotal());

		KkmTesting.assertSums(nameToData);
	}

	@Test
	public void test2() throws IOException, ParsingException
	{
		InputStream input = Resources.stream("brandenburg/kkm/2020-10-20.html");
		BrandenburgKkmData data = BrandenburgKkm.parse(input);

		Map<String, Data> nameToData = data.getNameToData();

		Data total = nameToData.get("Brandenburg gesamt");
		Assert.assertEquals(74, total.getNewCases24());
		Assert.assertEquals(5761, total.getCasesTotal());
		Assert.assertEquals(228.4, total.getIncidence100k(), 0.0001);
		Assert.assertEquals(31.3, total.getIncidence100k_7(), 0.0001);
		Assert.assertEquals(182, total.getDeathsTotal());

		KkmTesting.assertSums(nameToData);

		Data cottbus = nameToData.get("Cottbus");
		Assert.assertEquals(9, cottbus.getNewCases24());
		Assert.assertEquals(190, cottbus.getCasesTotal());
		Assert.assertEquals(190.6, cottbus.getIncidence100k(), 0.0001);
		Assert.assertEquals(87.3, cottbus.getIncidence100k_7(), 0.0001);
		Assert.assertEquals(2, cottbus.getDeathsTotal());
	}

}

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

package de.mobanisto.covidplz.berlin;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Test;

import de.mobanisto.covidplz.DailyDataLoader;
import de.mobanisto.covidplz.model.DailyData;
import de.mobanisto.covidplz.rki.Berlin;
import de.topobyte.system.utils.SystemPaths;

public class TestAggregateRKIBerlinData
{

	@Test
	public void test() throws IOException
	{
		Path repo = SystemPaths.CWD.getParent().getParent();
		Path file = repo.resolve("data/daily/2020_10_06.csv");

		DailyData data = DailyDataLoader.load(file);
		Berlin.deriveBerlinData(data);

		// TODO: what to assert here?
	}

}

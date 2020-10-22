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
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.system.utils.SystemPaths;

public class ParseAllKkmData
{

	final static Logger logger = LoggerFactory.getLogger(ParseAllKkmData.class);

	public static void main(String[] args) throws IOException
	{
		Path repo = SystemPaths.CWD.getParent().getParent();
		Path dirKkm = repo.resolve("data/brandenburg/kkm");

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");

		LocalDate start = LocalDate.of(2020, 4, 20);
		LocalDate end = LocalDate.now();

		for (LocalDate date = start; !date.isAfter(end); date = date
				.plusDays(1)) {
			String filename = String.format("%s.html", formatter.format(date));
			Path file = dirKkm.resolve(filename);
			if (!Files.exists(file)) {
				continue;
			}
			try {
				parse(file, date);
			} catch (ParsingException e) {
				logger.warn("error while paring file: " + e.getMessage());
				continue;
			}
		}
	}

	private static void parse(Path file, LocalDate date)
			throws IOException, ParsingException
	{
		System.out.println(file);
		InputStream input = Files.newInputStream(file);
		SingleBrandenburgKkmData data = BrandenburgKkm.parse(input, date);

		if (data.isValid()) {
			Map<String, KkmData> nameToData = data.getNameToData();
			try {
				KkmTesting.assertSums(nameToData);
			} catch (AssertionError e) {
				System.out.println(e.getMessage());
			}
		}
	}

}

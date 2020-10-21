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
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class BrandenburgKkm
{

	public static BrandenburgKkmData parse(LocalDate date)
			throws IOException, ParsingException
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-YYYY");
		String dateString = formatter.format(date);
		String spec = String.format(
				"https://kkm.brandenburg.de/kkm/de/presse/pressemitteilungen/detail/~%s-aktuelle-fallzahlen-covid-19-in-brandenburg-%s",
				dateString, dateString);
		URL url = new URL(spec);
		URLConnection connection = url.openConnection();
		try (InputStream input = connection.getInputStream()) {
			return parse(input);
		}
	}

	public static BrandenburgKkmData parse(InputStream input)
			throws IOException, ParsingException
	{
		Document doc = Jsoup.parse(input, "UTF-8", "");
		BrandenburgKkmParser parser = new BrandenburgKkmParser();
		parser.parse(doc);
		return parser.getData();
	}

}

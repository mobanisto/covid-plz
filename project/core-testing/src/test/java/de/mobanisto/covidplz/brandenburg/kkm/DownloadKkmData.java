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
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.system.utils.SystemPaths;

public class DownloadKkmData
{

	final static Logger logger = LoggerFactory.getLogger(DownloadKkmData.class);

	public static void main(String[] args) throws IOException
	{
		Path repo = SystemPaths.CWD.getParent().getParent();
		Path dirKkm = repo.resolve("data/brandenburg/kkm");

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");

		LocalDate start = LocalDate.of(2020, 4, 20);
		LocalDate end = LocalDate.now();

		for (LocalDate date = start; !date.isAfter(end); date = date
				.plusDays(1)) {
			String url = BrandenburgKkm.url(date);
			String filename = String.format("%s.html", formatter.format(date));
			System.out.println(String.format("%s → %s", url, filename));
			Path file = dirKkm.resolve(filename);
			if (Files.exists(file)) {
				continue;
			}

			download(url, file);
		}
	}

	private static void download(String spec, Path file)
			throws ClientProtocolException, IOException
	{
		CloseableHttpClient httpClient = HttpClients.custom().build();
		String userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.157 Safari/537.36";

		URL url = new URL(spec);
		try {
			HttpGet get = new HttpGet(url.toURI());
			get.addHeader("User-Agent", userAgent);
			try (CloseableHttpResponse response = httpClient.execute(get)) {
				HttpEntity entity = response.getEntity();
				try (InputStream input = entity.getContent()) {
					Files.copy(input, file);
				}
			}
		} catch (URISyntaxException e) {
			System.out.println("URI Syntax problem: " + spec);
		}
	}

}

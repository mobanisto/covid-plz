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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.apache.commons.io.input.BOMInputStream;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

import de.mobanisto.covidplz.model.RawData;
import de.mobanisto.covidplz.model.RawEntry;

public class RawDataLoader
{

	public static RawData load(Path file) throws IOException
	{
		try (InputStream input = Files.newInputStream(file)) {
			return load(input);
		}
	}

	public static RawData load(InputStream input) throws IOException
	{
		BOMInputStream bomInput = new BOMInputStream(input);

		RawData data = new RawData();

		try (InputStreamReader reader = new InputStreamReader(bomInput)) {
			ICsvMapReader csvReader = null;
			try {
				csvReader = new CsvMapReader(reader,
						CsvPreference.EXCEL_PREFERENCE);

				final String[] header = csvReader.getHeader(true);

				Map<String, String> map;
				while ((map = csvReader.read(header)) != null) {
					data.getEntries().add(new RawEntry(map));
				}
			} finally {
				if (csvReader != null) {
					csvReader.close();
				}
			}
		}

		return data;
	}

}

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
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mobanisto.covidplz.model.DailyData;
import de.mobanisto.covidplz.model.Data;
import de.mobanisto.covidplz.rki.Berlin;
import de.topobyte.melon.commons.io.Resources;

public class WebsiteData
{

	final static Logger logger = LoggerFactory.getLogger(WebsiteData.class);

	public static boolean loadAll(Path file)
	{
		loadBaseData();

		return loadDailyData(file);
	}

	private static void loadBaseData()
	{
		logger.info("loading base data...");
		try {
			DataLoader dataLoader = new DataLoader();
			Data data = dataLoader.loadData();
			Website.INSTANCE.setData(data);
		} catch (IOException e) {
			logger.error("Error while loading data", e);
		}
	}

	public static boolean loadDailyData(Path file)
	{
		logger.info("loading daily data: " + file);
		try {
			DailyData data = DailyDataLoader.load(file);
			Berlin.deriveBerlinData(data);
			Website.INSTANCE.setDailyData(data);
			return true;
		} catch (IOException e) {
			logger.error("Error while loading daily data", e);
			return false;
		}
	}

	public static boolean loadDailyDataFromResoures()
	{
		logger.info("loading daily data from resources...");
		try (InputStream input = Resources.stream("data.csv")) {
			DailyData data = DailyDataLoader.load(input);
			Website.INSTANCE.setDailyData(data);
			return true;
		} catch (IOException e) {
			logger.error("Error while loading daily data", e);
			return false;
		}
	}

}

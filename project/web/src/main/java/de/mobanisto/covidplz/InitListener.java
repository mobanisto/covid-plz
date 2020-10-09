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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import de.topobyte.cachebusting.CacheBusting;
import de.topobyte.melon.commons.io.Resources;

@WebListener
public class InitListener implements ServletContextListener
{

	final static Logger logger = LoggerFactory.getLogger(InitListener.class);

	@Override
	public void contextInitialized(ServletContextEvent sce)
	{
		logger.info("context initialized");
		long start = System.currentTimeMillis();

		logger.info("setting up website factories");
		Website.INSTANCE.setCacheBuster(filename -> {
			return CacheBusting.resolve(filename);
		});

		logger.info("loading configuration...");
		Properties config = new Properties();
		try (InputStream input = Resources.stream("config.properties")) {
			config.load(input);
		} catch (Throwable e) {
			logger.error("Unable to load configuration", e);
		}

		Path dirData = Paths.get(config.getProperty("data"));
		Config.INSTANCE.setDirData(dirData);

		logger.info("fetching data if not present...");
		Path lastWorking = DataManagement.getLastWorking();
		Path current = DataManagement.getCurrent();

		if (!Files.exists(current)) {
			try {
				DataManagement.downloadData(current);
			} catch (IOException e) {
				logger.warn("Error while downloading data", e);
			}
		}

		logger.info("loading data...");
		boolean successful = WebsiteData.loadAll(current);
		if (successful) {
			try {
				DataManagement.copyData(current, lastWorking);
			} catch (IOException e) {
				logger.warn("Error while backing up latest working data", e);
			}
		} else if (Files.exists(lastWorking)) {
			successful = WebsiteData.loadDailyData(lastWorking);
		}

		if (!successful) {
			WebsiteData.loadDailyDataFromResoures();
		}

		long stop = System.currentTimeMillis();

		logger.info("done");
		logger.info(String.format("Initialized in %.2f seconds",
				(stop - start) / 1000d));
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce)
	{
		logger.info("context destroyed");

		logger.info("shutting down Logback");
		LoggerContext loggerContext = (LoggerContext) LoggerFactory
				.getILoggerFactory();
		loggerContext.stop();
	}

}

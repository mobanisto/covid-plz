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

package de.mobanisto.covidplz.pages.base;

import java.io.IOException;

import org.jsoup.nodes.Element;

import de.mobanisto.covidplz.CacheBuster;
import de.mobanisto.covidplz.Website;
import de.mobanisto.covidplz.widgets.MainFooter;
import de.mobanisto.covidplz.widgets.MainMenu;
import de.topobyte.jsoup.ElementBuilder;
import de.topobyte.jsoup.FaviconUtil;
import de.topobyte.jsoup.bootstrap4.components.Menu;
import de.topobyte.jsoup.components.Head;
import de.topobyte.pagegen.bootstrap.Bootstrap4Generator;
import de.topobyte.webgun.exceptions.PageNotFoundException;
import de.topobyte.webpaths.WebPath;

public class BaseGenerator extends Bootstrap4Generator
{

	public BaseGenerator(WebPath path)
	{
		super(path, false);
	}

	@Override
	public void generate() throws IOException, PageNotFoundException
	{
		super.generate();
		Element html = builder.getHtml();

		html.attr("lang", "en");

		Head head = builder.getHead();

		CacheBuster cacheBuster = Website.INSTANCE.getCacheBuster();

		FaviconUtil.addToHeader(head, "/" + cacheBuster.resolve("favicon.ico"));

		FaviconUtil.addToHeader(head,
				"/" + cacheBuster.resolve("images/favicon-16.png"), 16);
		FaviconUtil.addToHeader(head,
				"/" + cacheBuster.resolve("images/favicon-32.png"), 32);
		FaviconUtil.addToHeader(head,
				"/" + cacheBuster.resolve("images/favicon-48.png"), 48);
		FaviconUtil.addToHeader(head,
				"/" + cacheBuster.resolve("images/favicon-64.png"), 64);
		FaviconUtil.addToHeader(head,
				"/" + cacheBuster.resolve("images/favicon-96.png"), 96);

		head.ac(ElementBuilder
				.styleSheet("/" + cacheBuster.resolve("custom.css")));
	}

	protected void menu()
	{
		Menu menu = MainMenu.create(this);
		getBuilder().getBody().prependChild(menu);
	}

	protected void footer()
	{
		getBuilder().getBody().appendChild(new MainFooter(this));
	}

}

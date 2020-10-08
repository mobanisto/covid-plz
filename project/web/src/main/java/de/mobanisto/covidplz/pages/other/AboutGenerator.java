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

package de.mobanisto.covidplz.pages.other;

import de.mobanisto.covidplz.Website;
import de.mobanisto.covidplz.model.Data;
import de.mobanisto.covidplz.pages.base.SimpleBaseGenerator;
import de.topobyte.cachebusting.CacheBusting;
import de.topobyte.jsoup.HTML;
import de.topobyte.jsoup.bootstrap4.Bootstrap;
import de.topobyte.jsoup.bootstrap4.components.ListGroup;
import de.topobyte.jsoup.components.Div;
import de.topobyte.jsoup.components.Img;
import de.topobyte.jsoup.components.P;
import de.topobyte.webpaths.WebPath;
import de.topobyte.webpaths.WebPaths;

public class AboutGenerator extends SimpleBaseGenerator
{

	public AboutGenerator(WebPath path)
	{
		super(path);
	}

	@Override
	protected void content()
	{
		content.ac(HTML.h1("Über dieses Projekt"));

		Div row = content.ac(Bootstrap.row());
		row.addClass("align-items-center");

		Div colLeft = row.ac(HTML.div("col-12 col-sm-4"));
		Div colRight = row.ac(HTML.div("col-12 col-sm-8"));

		Img image = colLeft.ac(HTML.img("/"
				+ WebPaths.get(CacheBusting.resolve("images/mobanisto.svg"))));
		image.addClass("img-fluid");
		image.attr("style", "width: 100%; padding: 15%");

		P p = colRight.ac(HTML.p());
		p.appendText(
				"Ein Webservice zur schnellen Abfrage von Informationen zum"
						+ " COVID-19 Infektionsgeschehen in Deutschland.");
		p = colRight.ac(HTML.p());
		p.appendText("Ein Projekt der ");
		p.ac(HTML.a("https://www.mobanisto.de",
				"Mobanisto UG (haftungsbeschränkt)"));
		p.appendText(" in Zusammenarbeit mit der ");
		p.ac(HTML.a("https://www.reiseland-brandenburg.de",
				"TMB Tourismus-Marketing Brandenburg GmbH"));
		p.appendText(".");

		Div colData = row.ac(HTML.div("col-12 col-sm-8"));

		Data data = Website.INSTANCE.getData();

		colData.ac(HTML.h1("Daten"));

		ListGroup list = colData.ac(Bootstrap.listGroup());
		list.addTextItem(String.format("Anzahl Postleitzahlen: %d",
				data.getPostalCodes().size()));
		list.addTextItem(String.format("Anzahl RKI-Regionen: %d",
				data.getRkiIdentifiers().size()));
	}

}

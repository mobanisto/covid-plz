// Copyright (c) 2020 Sebastian Kuerten
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

package de.mobanisto.covidplz.content;

import de.topobyte.cachebusting.CacheBusting;
import de.topobyte.jsoup.HTML;
import de.topobyte.jsoup.bootstrap4.Bootstrap;
import de.topobyte.jsoup.bootstrap4.components.ListGroupDiv;
import de.topobyte.jsoup.bootstrap4.components.listgroup.ListGroupA;
import de.topobyte.jsoup.components.Div;
import de.topobyte.jsoup.components.Img;

public class Shop
{

	public static void content(Div row)
	{
		Div col;
		ListGroupDiv ul;

		col = row.ac(HTML.div("col-12 col-md-4"));

		ul = col.ac(Bootstrap.listGroupDiv());
		item(ul, "https://amzn.to/2FKXFrM", "Toilettenpapier",
				"images/emojis/1F9FB.svg");
		item(ul, "https://amzn.to/2HjRD1u", "Hand-Desinfektion",
				"images/emojis/1F9F4.svg");
		item(ul, "https://amzn.to/3dFW7vN", "Seife", "images/emojis/1F9FC.svg");

		col = row.ac(HTML.div("col-12 col-md-4"));

		ul = col.ac(Bootstrap.listGroupDiv());
		item(ul, "https://amzn.to/3dMNrnm", "Einwegmasken",
				"images/emojis/1F637.svg");
		item(ul, "https://amzn.to/31lCwvS", "Stoffmasken",
				"images/emojis/1F637-rainbow.svg");
		item(ul, "https://amzn.to/349bGZw", "Gesichtsschutz",
				"images/emojis/E2D9.svg");

		col = row.ac(HTML.div("col-12 col-md-4"));

		ul = col.ac(Bootstrap.listGroupDiv());
		item(ul, "https://amzn.to/34aW1ZO", "Hinweisschilder und Aufkleber",
				"images/emojis/1FAA7.svg");
		item(ul, "https://amzn.to/3kfZDzh", "Laptopständer fürs Homeoffice",
				"images/emojis/1F431-200D-1F4BB.svg");
	}

	private static void item(ListGroupDiv ul, String link, String name,
			String imageFile)
	{
		ListGroupA item = ul.addA(link, name);
		Img image = HTML.img("/" + CacheBusting.resolve(imageFile));
		image.attr("style", "height:3em");
		item.prependChild(image);
	}

}

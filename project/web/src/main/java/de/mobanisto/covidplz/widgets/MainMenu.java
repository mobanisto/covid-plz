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

package de.mobanisto.covidplz.widgets;

import static de.topobyte.jsoup.HTML.a;

import de.topobyte.cachebusting.CacheBusting;
import de.topobyte.jsoup.HTML;
import de.topobyte.jsoup.bootstrap4.components.Expand;
import de.topobyte.jsoup.bootstrap4.components.Menu;
import de.topobyte.jsoup.bootstrap4.components.MenuBuilder;
import de.topobyte.jsoup.components.A;
import de.topobyte.jsoup.components.Img;
import de.topobyte.jsoup.nodes.Element;
import de.topobyte.pagegen.core.LinkResolver;
import de.topobyte.webpaths.WebPaths;

public class MainMenu
{

	public static Menu create(LinkResolver resolver)
	{
		MenuBuilder builder = new MenuBuilder();
		builder.setExpand(Expand.MD).setFixed(true);
		Menu menu = builder.create();

		menu.addClass("shadow-sm");

		A brand = a("/");

		Img image = HTML.img(
				"/" + WebPaths.get(CacheBusting.resolve("images/icon.svg")));
		image.attr("height", "40px");
		image.attr("style", "padding-right:15px");
		brand.ap(image);

		brand.appendText("Covid-PLZ");

		menu.addBrand(brand);
		menu.addToggleButton();

		Element collapse = menu.addCollapsible();
		Element main = menu.addSection(collapse);
		Element right = menu.addSectionRight(collapse);

		menu.addLink(main, "/abfrage", "Abfrage", false);

		menu.addLink(right, "/about", "Über", false);

		return menu;
	}

}

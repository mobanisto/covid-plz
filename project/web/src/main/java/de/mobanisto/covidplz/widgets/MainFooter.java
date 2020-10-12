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
import static de.topobyte.jsoup.HTML.p;

import de.mobanisto.covidplz.PathHelper;
import de.topobyte.jsoup.HTML;
import de.topobyte.jsoup.bootstrap4.Bootstrap;
import de.topobyte.jsoup.components.A;
import de.topobyte.jsoup.components.UnorderedList;
import de.topobyte.jsoup.feather.Feather;
import de.topobyte.jsoup.nodes.Element;
import de.topobyte.pagegen.core.LinkResolver;

public class MainFooter extends Element
{

	public MainFooter(LinkResolver resolver)
	{
		super("footer");
		attr("class", "footer");

		Element div = ac(Bootstrap.container());

		UnorderedList links = div.ac(HTML.ul());

		String imprintLink = resolver.getLink(PathHelper.imprint());
		A linkAbout = a(imprintLink, "Impressum");
		links.addItem(linkAbout);

		String privacyLink = resolver.getLink(PathHelper.privacy());
		A linkPrivacy = a(privacyLink, "Datenschutz");
		links.addItem(linkPrivacy);

		A linkGithub = a("https://github.com/mobanisto/covid-plz",
				"Quellcode auf Github");
		links.addItem(linkGithub);

		A linkTwitter = a("https://twitter.com/mobanisto", "Twitter");
		links.addItem(linkTwitter);

		Element p = div.ac(p().addClass("text-muted"));

		p.appendText("Made with ");
		p.ac(Feather.heart(16));
		p.appendText(" in Berlin-Brandenburg");
	}

}

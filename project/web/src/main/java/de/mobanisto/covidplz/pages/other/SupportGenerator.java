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

import de.mobanisto.covidplz.content.Shop;
import de.mobanisto.covidplz.pages.base.SimpleBaseGenerator;
import de.topobyte.jsoup.HTML;
import de.topobyte.jsoup.bootstrap4.Bootstrap;
import de.topobyte.jsoup.components.Div;
import de.topobyte.jsoup.components.P;
import de.topobyte.webpaths.WebPath;

public class SupportGenerator extends SimpleBaseGenerator
{

	public SupportGenerator(WebPath path)
	{
		super(path);
	}

	@Override
	protected void content()
	{
		content.ac(HTML.h1("Warum unterstützen?"));

		Div row = content.ac(Bootstrap.row());
		Div col = row.ac(HTML.div("col-12"));

		P p = col.ac(HTML.p());
		p.appendText(
				"Wir werden aktuell für die Weiterentwicklung von diesem Tool nicht finanziert."
						+ " Ursprünglich haben wir den Covid-PLZ-Check zusammen mit der"
						+ " TMB Tourismus-Marketing Brandenburg GmbH und dem OK Lab Fläming"
						+ " für die Beherbergungsbranche in Brandenburg entwickelt."
						+ " Die Resonanz war jedoch so positiv und motivierend, dass wir an dem"
						+ " Tool weiterarbeiten wollen – denn uns haben viele Wünsche und"
						+ " Erweiterungsideen von Nutzern deutschlandweit erreicht.");

		p = col.ac(HTML.p());
		p.appendText("Wir möchten auf nervige Werbung weitestgehend verzichten"
				+ " und das Tool weiterhin simpel, neutral, und schnell verfügbar machen."
				+ " Um diesen Service weiterhin unabhängig am Leben zu erhalten und"
				+ " ihn Weiterzuentwicklen, haben wir diesen kleinen Pandemie-Shop"
				+ " eingerichtet."
				+ " Wir sind dankbar für jede Unterstützung und jedes Feedback.");

		p = col.ac(HTML.p());
		p.ac(HTML.b().appendText("So geht's:"));
		p.appendText(
				" Wenn Sie Ihren nächsten Einkauf bei Amazon einfach von einem unserer Links aus starten,"
						+ " dann verdienen wir eine kleine Provision von Amazon.");

		p = col.ac(HTML.p());
		p.appendText("Viel Spaß beim Shoppen und bleiben Sie gesund.");

		Shop.content(row);
	}

}

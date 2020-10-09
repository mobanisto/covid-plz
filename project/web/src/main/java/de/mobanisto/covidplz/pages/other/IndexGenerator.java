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

import de.mobanisto.covidplz.pages.base.SimpleBaseGenerator;
import de.topobyte.jsoup.HTML;
import de.topobyte.jsoup.bootstrap4.Bootstrap;
import de.topobyte.jsoup.bootstrap4.components.Alert;
import de.topobyte.jsoup.bootstrap4.components.ContextualType;
import de.topobyte.jsoup.components.P;
import de.topobyte.webpaths.WebPath;

public class IndexGenerator extends SimpleBaseGenerator
{

	public IndexGenerator(WebPath path)
	{
		super(path);
	}

	@Override
	protected void content()
	{
		content.ac(HTML.h1("Statusabfrage zu COVID-19 in Deutschland"));

		P p = content.ac(HTML.p());
		p.appendText(
				"Auf dieser Seite können Sie die Daten zum Infektionsgeschen der"
						+ " COVID-19-Pandemie abrufen, die täglich vom Robert-Koch-Institut"
						+ " veröffentlicht werden.");
		p = content.ac(HTML.p());
		p.appendText("Geben Sie einfach auf der ");
		p.ac(HTML.a("/abfrage", "Abfrage-Seite"));
		p.appendText(" eine Postleitzahl ein."
				+ " Nach Bestätigung der Eingabe erscheinen die vorhandenen Daten zu"
				+ " der entsprechenden Stadt bzw. dem entsprechenden Landkreis.");

		Alert alert = content.ac(Bootstrap.alert(ContextualType.WARNING));
		alert.appendText(
				"Alle Angaben ohne Gewähr. Bitte informieren Sie sich zusätzlich"
						+ " immer bei den für Sie zuständigen Behörden.");
	}

}

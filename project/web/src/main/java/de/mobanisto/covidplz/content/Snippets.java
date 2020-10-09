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

package de.mobanisto.covidplz.content;

import de.topobyte.jsoup.HTML;
import de.topobyte.jsoup.bootstrap4.Bootstrap;
import de.topobyte.jsoup.bootstrap4.components.ListGroup;
import de.topobyte.jsoup.bootstrap4.components.listgroup.ListGroupItem;
import de.topobyte.jsoup.nodes.Element;

public class Snippets
{

	public static void references(Element element)
	{
		ListGroup list = element.ac(Bootstrap.listGroup());

		ListGroupItem item = list.addTextItem(
				"Postleitzahlen: © OpenStreetMap contributors, lizensiert unter der ");
		item.ac(HTML.a("https://opendatacommons.org/licenses/odbl/",
				"Open Data Commons Open Database License"));
		item.appendText(" (ODbL) von der ");
		item.ac(HTML.a("https://osmfoundation.org/",
				"OpenStreetMap Foundation"));
		item.appendText(" (OSMF).");

		item = list.addTextItem("Infektionsdaten: © ");
		item.ac(HTML.a(
				"https://www.rki.de/DE/Content/InfAZ/N/Neuartiges_Coronavirus/Fallzahlen.html",
				"Robert-Koch-Institut"));
		item.appendText(" (RKI), lizensiert unter der ");
		item.ac(HTML.a("https://www.govdata.de/dl-de/by-2-0",
				"Open Data Datenlizenz Deutschland – Namensnennung – Version 2.0"));
		item.appendText(", abgerufen über die ");
		item.ac(HTML.a(
				"https://npgeo-corona-npgeo-de.hub.arcgis.com/datasets/917fc37a709542548cc3be077a786c17_0",
				"Nationale Plattform für geographische Daten"));
		item.appendText(" (NPGEO-DE)");
	}

}

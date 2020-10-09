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

import java.util.Locale;
import java.util.function.Function;

import de.mobanisto.covidplz.model.RegionData;
import de.mobanisto.covidplz.rki.Fields;
import de.topobyte.jsoup.HTML;
import de.topobyte.jsoup.components.Table;
import de.topobyte.jsoup.components.TableRow;
import de.topobyte.jsoup.nodes.Element;

public class DataTable
{

	private RegionData data;

	public DataTable(RegionData data)
	{
		this.data = data;
	}

	public void add(Element element)
	{
		Table table = element.ac(HTML.table());
		table.addClass("table");

		row(table, "Datenstand", Fields.LAST_UPDATE);
		rowD(table, "Fälle pro 100.000 Einwohner in den letzten 7 Tagen",
				Fields.CASES7_PER_100K).attr("style", "font-weight: bold");
		row(table, "Fälle insgesamt", Fields.CASES);
		row(table, "Tote insgesamt", Fields.DEATHS);
		rowI(table, "Einwohner", Fields.EWZ);
		rowD(table, "Fälle pro 100.000 Einwohner insgesamt",
				Fields.CASES_PER_100K);
	}

	private TableRow row(Table table, String title, String id)
	{
		return row(table, title, id, s -> s);
	}

	private TableRow rowD(Table table, String title, String id)
	{
		return row(table, title, id, s -> {
			try {
				double d = Double.parseDouble(s);
				return String.format(Locale.GERMAN, "%.2f", d);
			} catch (NumberFormatException e) {
				return s;
			}
		});
	}

	private TableRow rowI(Table table, String title, String id)
	{
		return row(table, title, id, s -> {
			try {
				int i = Integer.parseInt(s);
				return String.format(Locale.GERMAN, "%,d", i);
			} catch (NumberFormatException e) {
				return s;
			}
		});
	}

	private TableRow row(Table table, String title, String id,
			Function<String, String> formatter)
	{
		TableRow row = table.row();
		row.cell(title);
		String value = data.getData().get(id);
		row.cell(formatter.apply(value));
		return row;
	}

}

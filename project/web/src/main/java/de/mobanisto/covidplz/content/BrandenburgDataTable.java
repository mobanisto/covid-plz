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

import java.time.LocalDate;

import de.mobanisto.covidplz.brandenburg.kkm.KkmData;
import de.mobanisto.covidplz.model.RegionData;
import de.mobanisto.covidplz.rki.daily.Fields;
import de.topobyte.jsoup.HTML;
import de.topobyte.jsoup.components.Table;
import de.topobyte.jsoup.components.TableRow;
import de.topobyte.jsoup.nodes.Element;

public class BrandenburgDataTable
{

	private RegionData rkiData;
	private KkmData kkmData;
	private LocalDate date;

	public BrandenburgDataTable(RegionData rkiData, KkmData data,
			LocalDate date)
	{
		this.rkiData = rkiData;
		this.kkmData = data;
		this.date = date;
	}

	public void add(Element element)
	{
		Table table = element.ac(HTML.table());
		Tables.setup(table);

		int population = Integer.parseInt(rkiData.getData().get(Fields.EWZ));

		row(table, "Datenstand", date);
		Element row7 = row(table,
				"Fälle pro 100.000 Einwohner in den letzten 7 Tagen",
				kkmData.getIncidence100k_7()).attr("style",
						"font-weight: bold");
		row(table, "Fälle insgesamt", kkmData.getCasesTotal());
		row(table, "Todesfälle insgesamt", kkmData.getDeathsTotal());
		row(table, "Einwohner", population);
		row(table, "Fälle pro 100.000 Einwohner insgesamt",
				kkmData.getIncidence100k());

		try {
			double cases7 = kkmData.getIncidence100k_7();
			if (cases7 < 35) {
				row7.addClass("table-success");
			} else if (cases7 < 50) {
				row7.addClass("table-warning");
			} else {
				row7.addClass("table-danger");
			}
		} catch (NumberFormatException e) {
			// ignore
		}
	}

	private TableRow row(Table table, String title, LocalDate value)
	{
		String formatted = Formatters.FORMATTER_DATE.apply(value);
		return row(table, title, formatted);
	}

	private TableRow row(Table table, String title, double value)
	{
		String formatted = Formatters.FORMATTER_D.apply(value);
		return row(table, title, formatted);
	}

	private TableRow row(Table table, String title, int value)
	{
		String formatted = Formatters.FORMATTER_I.apply(value);
		return row(table, title, formatted);
	}

	private TableRow row(Table table, String title, String value)
	{
		TableRow row = table.row();
		row.cell(title);
		row.cell(value);
		return row;
	}

}

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

import java.util.List;
import java.util.Map;

import de.mobanisto.covidplz.Website;
import de.mobanisto.covidplz.content.DataTable;
import de.mobanisto.covidplz.content.Snippets;
import de.mobanisto.covidplz.mapping.Mapping;
import de.mobanisto.covidplz.mapping.PartialRsRelation;
import de.mobanisto.covidplz.model.DailyData;
import de.mobanisto.covidplz.model.RegionData;
import de.mobanisto.covidplz.pages.base.SimpleBaseGenerator;
import de.mobanisto.covidplz.rki.Fields;
import de.topobyte.jsoup.HTML;
import de.topobyte.jsoup.bootstrap4.Bootstrap;
import de.topobyte.jsoup.bootstrap4.components.Alert;
import de.topobyte.jsoup.bootstrap4.components.ContextualType;
import de.topobyte.jsoup.components.Button;
import de.topobyte.jsoup.components.Div;
import de.topobyte.jsoup.components.Form;
import de.topobyte.jsoup.components.Input;
import de.topobyte.jsoup.jquery.JQuery;
import de.topobyte.jsoup.nodes.Element;
import de.topobyte.webgun.util.ParameterUtil;
import de.topobyte.webpaths.WebPath;

public class AbfrageGenerator extends SimpleBaseGenerator
{

	private static final String PARAM_PLZ = "plz";

	private String plz;

	public AbfrageGenerator(WebPath path, Map<String, String[]> parameters)
	{
		super(path);

		plz = ParameterUtil.get(parameters, PARAM_PLZ);
	}

	@Override
	protected void content()
	{
		content.ac(HTML.h1("Anfrage stellen"));

		form(content);

		if (plz != null) {
			results(content);
		}
	}

	private void form(Element element)
	{
		Form form = element.ac(HTML.form());
		form.setAction("/abfrage");

		queryAndButton(form, "Bitte hier Postleitzahl eingeben",
				plz == null ? "" : plz, "plz");

		element.ac(JQuery.focusById("plz"));
	}

	public static void queryAndButton(Form form, String placeholder,
			String value, String id)
	{
		Div group = form.ac(HTML.div("form-group"));
		group = group.ac(HTML.div("input-group"));

		Input input = group.ac(HTML.input());
		if (id != null) {
			input.setId(id);
		}
		input.setName(PARAM_PLZ);
		if (value != null) {
			input.setValue(value);
		}
		input.addClass("form-control");
		input.attr("placeholder", placeholder);
		input.attr("autocomplete", "off");

		Div div = group.ac(HTML.div());
		div.addClass("input-group-append");
		Button button = div.ac(HTML.button());
		button.attr("type", "submit");
		button.addClass("btn");
		button.addClass("btn-primary");
		button.appendText("abfragen");
	}

	private void results(Element element)
	{
		Mapping mapping = Website.INSTANCE.getData().getMapping();
		DailyData dailyData = Website.INSTANCE.getDailyData();

		List<PartialRsRelation> rkis = mapping.getCodeToRKI().get(plz);
		if (rkis == null) {
			Alert alert = element.ac(Bootstrap.alert(ContextualType.DANGER));
			alert.appendText("Ungültige Postleitzahl");
			return;
		}

		for (PartialRsRelation rki : rkis) {
			String rs = rki.getObject();
			RegionData regionData = dailyData.getRsToRegionData().get(rs);
			data(element, rs, regionData);
		}

		Alert alert = element.ac(Bootstrap.alert(ContextualType.WARNING));
		alert.appendText(
				"Alle Angaben ohne Gewähr. Bitte informieren Sie sich zusätzlich"
						+ " immer bei den für Sie zuständigen Behörden.");

		element.ac(HTML.h3("Quellen").addClass("mt-2"));
		Snippets.references(element);
	}

	private void data(Element element, String rs, RegionData data)
	{
		String bezeichnungRegion = data.getData().get(Fields.BEZ);
		String nameRegion = data.getData().get(Fields.GEN);

		element.ac(
				HTML.h2(String.format("%s %s", bezeichnungRegion, nameRegion)));

		DataTable dataTable = new DataTable(data);
		dataTable.add(element);
	}

}

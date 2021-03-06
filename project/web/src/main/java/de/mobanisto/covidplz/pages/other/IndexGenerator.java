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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.mobanisto.covidplz.Website;
import de.mobanisto.covidplz.brandenburg.kkm.BrandenburgKkmData;
import de.mobanisto.covidplz.brandenburg.kkm.KkmData;
import de.mobanisto.covidplz.content.BrandenburgDataTable;
import de.mobanisto.covidplz.content.RkiDataTable;
import de.mobanisto.covidplz.content.Shop;
import de.mobanisto.covidplz.content.Snippets;
import de.mobanisto.covidplz.mapping.Mapping;
import de.mobanisto.covidplz.mapping.PartialRsRelation;
import de.mobanisto.covidplz.model.DailyData;
import de.mobanisto.covidplz.model.RegionData;
import de.mobanisto.covidplz.model.germany.Bundesland;
import de.mobanisto.covidplz.model.germany.GermanyRegionLookup;
import de.mobanisto.covidplz.model.germany.Kreis;
import de.mobanisto.covidplz.pages.base.SimpleBaseGenerator;
import de.mobanisto.covidplz.rki.Berlin;
import de.mobanisto.covidplz.rki.daily.Fields;
import de.topobyte.collections.util.ListUtil;
import de.topobyte.jsoup.HTML;
import de.topobyte.jsoup.bootstrap4.Bootstrap;
import de.topobyte.jsoup.bootstrap4.components.Alert;
import de.topobyte.jsoup.bootstrap4.components.ContextualType;
import de.topobyte.jsoup.components.A;
import de.topobyte.jsoup.components.Button;
import de.topobyte.jsoup.components.Div;
import de.topobyte.jsoup.components.Form;
import de.topobyte.jsoup.components.Input;
import de.topobyte.jsoup.components.P;
import de.topobyte.jsoup.feather.Feather;
import de.topobyte.jsoup.jquery.JQuery;
import de.topobyte.jsoup.nodes.Element;
import de.topobyte.webgun.util.ParameterUtil;
import de.topobyte.webpaths.WebPath;

public class IndexGenerator extends SimpleBaseGenerator
{

	private static final String PARAM_PLZ = "plz";

	private String plz;

	public IndexGenerator(WebPath path, Map<String, String[]> parameters)
	{
		super(path);

		plz = ParameterUtil.get(parameters, PARAM_PLZ);
	}

	@Override
	protected void content()
	{
		content.ac(HTML.h1("Statusabfrage zu COVID-19 in Deutschland"));

		P p = content.ac(HTML.p());
		p.appendText(
				"Auf dieser Seite können Sie die Daten zum Infektionsgeschehen der"
						+ " COVID-19-Pandemie abrufen, die täglich vom Robert-Koch-Institut"
						+ " veröffentlicht werden.");
		p = content.ac(HTML.p());
		p.appendText("Geben Sie einfach unten eine Postleitzahl ein."
				+ " Nach Bestätigung der Eingabe erscheinen die vorhandenen Daten zu"
				+ " der entsprechenden Stadt bzw. dem entsprechenden Landkreis.");
		p.appendText(" Postleitzahlen zu einer Adresse bestimmen können Sie ");
		A link = p.ac(HTML.a("https://www.postdirekt.de/plzserver/", "hier"));
		link.ac(Feather.externalLink("1em"));
		p.appendText(".");

		form(content);

		if (plz != null) {
			results(content);
		}

		shop();
	}

	private void form(Element element)
	{
		Form form = element.ac(HTML.form());
		form.setAction("/");

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

		input.attr("type", "tel");
		input.attr("pattern", "[0-9]*");
		input.attr("novalidate");

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
		GermanyRegionLookup germany = Website.INSTANCE.getData()
				.getGermanyRegionLookup();
		Mapping mapping = Website.INSTANCE.getData().getMapping();
		DailyData dailyData = Website.INSTANCE.getDailyData();
		BrandenburgKkmData kkm = Website.INSTANCE.getData()
				.getBrandenburgKkmData();

		List<PartialRsRelation> rkis = mapping.getCodeToRKI().get(plz);
		if (rkis == null) {
			Alert alert = element.ac(Bootstrap.alert(ContextualType.DANGER));
			alert.appendText("Ungültige Postleitzahl");
			return;
		}

		// This is the list of IDs we're actually going to display
		List<String> rss = new ArrayList<>();

		boolean isBerlin = false;
		boolean isBrandenburg = false;

		Bundesland brandenburg = germany.getIdToLand().get("12");

		// For Berlin boroughs, add state Berlin; make sure to add Berlin only
		// once should it occur multiple times (example 14195 which is in
		// Steglitz-Zehlendorf and Charlottenburg-Wilmersdorf)
		for (PartialRsRelation rki : rkis) {
			String rs = rki.getObject();
			RegionData regionData = dailyData.getRsToRegionData().get(rs);
			if (Berlin.isBerlinBorough(regionData)) {
				isBerlin = true;
				if (!rss.contains(Berlin.RS)) {
					rss.add(Berlin.RS);
				}
			}
			rss.add(rs);
		}

		for (String rs : rss) {
			Kreis kreis = germany.getIdToKreis().get(rs);
			if (kreis != null && kreis.getBundesland() == brandenburg) {
				isBrandenburg = true;
			}
		}

		if (rss.size() > 1) {
			Alert alertMultiple = element
					.ac(Bootstrap.alert(ContextualType.WARNING));
			if (isBerlin) {
				alertMultiple.appendText(
						"Für Berlin werden Ergebnisse auf Landes- und Bezirksebene angezeigt.");
				if (rkis.size() > 1) {
					alertMultiple.appendText(
							" Diese Postleitzahl liegt außerdem in mehreren Bezirken."
									+ " Bitte prüfen Sie weitere Angaben um die richtige Region zu"
									+ " ermitteln.");
				}
			} else {
				alertMultiple.appendText(
						"Diese Postleitzahl liegt in mehreren Landkreisen."
								+ " Bitte prüfen Sie weitere Angaben um die richtige Region zu"
								+ " ermitteln.");
			}
		}

		Map<LocalDate, Map<String, KkmData>> dateToNameToData = kkm
				.getDateToNameToData();
		List<LocalDate> kkmDates = new ArrayList<>(dateToNameToData.keySet());
		Collections.sort(kkmDates);

		if (isBrandenburg) {
			// Alert alertBrandenburg = element
			// .ac(Bootstrap.alert(ContextualType.WARNING));
			// alertBrandenburg.appendText(
			// "Für Brandenburg werden mehrere Datenquellen angezeigt.");
		}

		// For each ID, print a section with data table
		for (String rs : rss) {
			RegionData regionData = dailyData.getRsToRegionData().get(rs);

			Kreis kreis = germany.getIdToKreis().get(rs);
			if (kreis != null && kreis.getBundesland() == brandenburg) {
				LocalDate latestDate = ListUtil.last(kkmDates);
				Map<String, KkmData> map = dateToNameToData.get(latestDate);
				KkmData data = map.get(rs);
				data(element, rs, regionData, data, latestDate);
			} else {
				data(element, rs, regionData);
			}
		}

		Alert alertNoWarranty = element
				.ac(Bootstrap.alert(ContextualType.WARNING));
		alertNoWarranty.appendText(
				"Alle Angaben ohne Gewähr. Bitte informieren Sie sich zusätzlich"
						+ " immer bei den für Sie zuständigen Behörden.");

		Alert alertDelay = element.ac(Bootstrap.alert(ContextualType.INFO));
		if (!isBrandenburg) {
			alertDelay.appendText(
					"Für den Check werden die Daten des Robert-Koch-Instituts verwendet."
							+ " Diese können von den Meldungen lokaler und regionaler Stellen"
							+ " zeitlich abweichen.");
		} else {
			alertDelay.appendText(
					"Für den Check werden die Daten des RKI und des LAVG verwendet."
							+ " Diese können von den Meldungen lokaler und regionaler Stellen"
							+ " zeitlich abweichen.");
		}

		element.ac(HTML.h3("Quellen").addClass("mt-2"));
		Snippets.references(element);
	}

	private void data(Element element, String rs, RegionData data)
	{
		String bezeichnungRegion = data.getData().get(Fields.BEZ);
		String nameRegion = data.getData().get(Fields.GEN);

		element.ac(
				HTML.h2(String.format("%s %s", bezeichnungRegion, nameRegion)));

		element.ac(HTML.h6("Robert-Koch-Institut (RKI):"));
		RkiDataTable dataTable = new RkiDataTable(data, true);
		dataTable.add(element);
	}

	private void data(Element element, String rs, RegionData rkiData,
			KkmData kkmData, LocalDate date)
	{
		String bezeichnungRegion = rkiData.getData().get(Fields.BEZ);
		String nameRegion = rkiData.getData().get(Fields.GEN);

		element.ac(
				HTML.h2(String.format("%s %s", bezeichnungRegion, nameRegion)));

		BrandenburgDataTable dataTable1 = new BrandenburgDataTable(rkiData,
				kkmData, date, true);
		element.ac(HTML.h6(
				"Landesamt für Arbeitsschutz, Verbraucherschutz und Gesundheit (LAVG):"));
		dataTable1.add(element);

		// RkiDataTable dataTable2 = new RkiDataTable(rkiData, false);
		// element.ac(HTML.h6("Robert-Koch-Institut (RKI):"));
		// dataTable2.add(element);
	}

	private void shop()
	{
		content.ac(HTML.h2("Pandemie-Shop")).addClass("mt-3");

		P p = content.ac(HTML.p());
		p.appendText("Wir haben hier einen kleinen Shop eingerichtet."
				+ " Als Amazon-Partner verdienen wir an qualifizierten Verkäufen."
				+ " Viel Spaß beim Shoppen und bleiben Sie gesund.");

		Div row = content.ac(Bootstrap.row());
		Shop.content(row);
	}

}

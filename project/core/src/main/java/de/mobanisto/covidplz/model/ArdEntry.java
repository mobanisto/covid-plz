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

package de.mobanisto.covidplz.model;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonObject;

import de.mobanisto.covidplz.rki.ard.Fields;
import lombok.Getter;

@Getter
public class ArdEntry
{

	private int id;
	private LocalDate meldedatum;
	private LocalDate datenstand;
	private LocalDate refdatum;
	private String bundesland;
	private String idBundesland;
	private String landkreis;
	private String idLandkreis;
	private String altersgruppe;
	private String altersgruppe2;
	private String geschlecht;
	private int anzahlFall;
	private int anzahlGenesen;
	private int anzahlTodesfall;
	private int neuerFall;
	private int neuerTodesFall;
	private int neuGenesen;
	private int istErkrankungsbeginn;

	private static final Pattern patternDates = Pattern
			.compile("(\\d{4,4})-(\\d{2,2})-(\\d{2,2})");

	private static LocalDate date(String value)
	{
		Matcher matcher = patternDates.matcher(value);
		if (matcher.matches()) {
			int year = Integer.parseInt(matcher.group(1));
			int month = Integer.parseInt(matcher.group(2));
			int day = Integer.parseInt(matcher.group(3));
			return LocalDate.of(year, month, day);
		}
		throw new IllegalArgumentException();
	}

	public ArdEntry(JsonObject map)
	{
		id = map.get(Fields.OBJECT_ID).getAsInt();

		meldedatum = date(map.get(Fields.MELDEDATUM_ISO).getAsString());
		datenstand = date(map.get(Fields.DATENSTAND_ISO).getAsString());
		refdatum = date(map.get(Fields.REFDATUM_ISO).getAsString());

		bundesland = map.get(Fields.BUNDESLAND).getAsString();
		idBundesland = map.get(Fields.ID_BUNDESLAND).getAsString();
		landkreis = map.get(Fields.LANDKREIS).getAsString();
		idLandkreis = map.get(Fields.ID_LANDKREIS).getAsString();

		altersgruppe = map.get(Fields.ALTERSGRUPPE).getAsString();
		altersgruppe2 = map.get(Fields.ALTERSGRUPPE2).getAsString();
		geschlecht = map.get(Fields.GESCHLECHT).getAsString();

		anzahlFall = map.get(Fields.ANZAHL_FALL).getAsInt();
		anzahlGenesen = map.get(Fields.ANZAHL_GENESEN).getAsInt();
		anzahlTodesfall = map.get(Fields.ANZAHL_TODESFALL).getAsInt();

		neuerFall = map.get(Fields.NEUER_FALL).getAsInt();
		neuerTodesFall = map.get(Fields.NEUER_TODESFALL).getAsInt();
		neuGenesen = map.get(Fields.NEU_GENESEN).getAsInt();

		istErkrankungsbeginn = map.get(Fields.IST_ERKRANKUNGSBEGINN).getAsInt();
	}

}

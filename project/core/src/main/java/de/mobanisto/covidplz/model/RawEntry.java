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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.mobanisto.covidplz.rki.raw.Fields;
import lombok.Getter;

public class RawEntry
{

	@Getter
	private int id;
	@Getter
	private LocalDate meldedatum;
	@Getter
	private String datenstand;
	@Getter
	private LocalDate refdatum;
	@Getter
	private String bundesland;
	@Getter
	private String idBundesland;
	@Getter
	private String landkreis;
	@Getter
	private String idLandkreis;
	@Getter
	private String altersgruppe;
	@Getter
	private String altersgruppe2;
	@Getter
	private String geschlecht;
	@Getter
	private int anzahlFall;
	@Getter
	private int anzahlGenesen;
	@Getter
	private int anzahlTodesfall;
	@Getter
	private int neuerFall;
	@Getter
	private int neuerTodesFall;
	@Getter
	private int neuGenesen;
	@Getter
	private int istErkrankungsbeginn;

	private static final Pattern patternDates = Pattern.compile(
			"(\\d{4,4})/(\\d{2,2})/(\\d{2,2}) (\\d{2,2}):(\\d{2,2}):(\\d{2,2})");

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

	public RawEntry(Map<String, String> map)
	{
		id = Integer.parseInt(map.get(Fields.OBJECT_ID));
		meldedatum = date(map.get(Fields.MELDEDATUM));
		datenstand = map.get(Fields.DATENSTAND);
		refdatum = date(map.get(Fields.REFDATUM));

		bundesland = map.get(Fields.BUNDESLAND);
		idBundesland = map.get(Fields.ID_BUNDESLAND);
		landkreis = map.get(Fields.LANDKREIS);
		idLandkreis = map.get(Fields.ID_LANDKREIS);

		altersgruppe = map.get(Fields.ALTERSGRUPPE);
		altersgruppe2 = map.get(Fields.ALTERSGRUPPE2);
		geschlecht = map.get(Fields.GESCHLECHT);

		anzahlFall = Integer.parseInt(map.get(Fields.ANZAHL_FALL));
		anzahlGenesen = Integer.parseInt(map.get(Fields.ANZAHL_GENESEN));
		anzahlTodesfall = Integer.parseInt(map.get(Fields.ANZAHL_TODESFALL));

		neuerFall = Integer.parseInt(map.get(Fields.NEUER_FALL));
		neuerTodesFall = Integer.parseInt(map.get(Fields.NEUER_TODESFALL));
		neuGenesen = Integer.parseInt(map.get(Fields.NEU_GENESEN));

		istErkrankungsbeginn = Integer
				.parseInt(map.get(Fields.IST_ERKRANKUNGSBEGINN));
	}

}

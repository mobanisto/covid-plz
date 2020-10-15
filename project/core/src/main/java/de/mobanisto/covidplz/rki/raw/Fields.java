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

package de.mobanisto.covidplz.rki.raw;

public class Fields
{

	// ObjectId,IdBundesland,Bundesland,Landkreis,Altersgruppe,
	// Geschlecht,AnzahlFall,AnzahlTodesfall,Meldedatum,IdLandkreis,
	// Datenstand,NeuerFall,NeuerTodesfall,Refdatum,NeuGenesen,
	// AnzahlGenesen,IstErkrankungsbeginn,Altersgruppe2

	// @formatter:off
	public static final String OBJECT_ID = "ObjectId";
	public static final String ID_BUNDESLAND = "IdBundesland";
	public static final String BUNDESLAND = "Bundesland";
	public static final String LANDKREIS = "Landkreis";
	public static final String ALTERSGRUPPE = "Altersgruppe";
	public static final String GESCHLECHT = "Geschlecht";
	public static final String ANZAHL_FALL = "AnzahlFall";
	public static final String ANZAHL_TODESFALL = "AnzahlTodesfall";
	public static final String MELDEDATUM = "Meldedatum";
	public static final String ID_LANDKREIS = "IdLandkreis";
	public static final String DATENSTAND = "Datenstand";
	public static final String NEUER_FALL = "NeuerFall";
	public static final String NEUER_TODESFALL = "NeuerTodesfall";
	public static final String REFDATUM = "Refdatum";
	public static final String NEU_GENESEN = "NeuGenesen";
	public static final String ANZAHL_GENESEN = "AnzahlGenesen";
	public static final String IST_ERKRANKUNGSBEGINN = "IstErkrankungsbeginn";
	public static final String ALTERSGRUPPE2 = "Altersgruppe2";
	// @formatter:on

}

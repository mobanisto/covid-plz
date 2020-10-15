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

package de.mobanisto.covidplz.rki.daily;

public class Fields
{

	// OBJECTID,ADE,GF,BSG,RS,AGS,SDV_RS,GEN,BEZ,IBZ,BEM,NBD,
	// SN_L,SN_R,SN_K,SN_V1,SN_V2,SN_G,FK_S3,NUTS,RS_0,AGS_0,
	// WSK,EWZ,KFL,DEBKG_ID,death_rate,cases,deaths,cases_per_100k,
	// cases_per_population,BL,BL_ID,county,last_update,cases7_per_100k,
	// recovered,SHAPE_Length,SHAPE_Area

	// @formatter:off
	public static final String RS = "RS";
	public static final String GEN = "GEN"; // Name der Region
	public static final String BEZ = "BEZ"; // Landreis, Kreis, Kreisfreie Stadt, Bezirk
	public static final String BL = "BL"; // Bundesland
	public static final String BL_ID = "BL_ID"; // Bundesland-ID
	public static final String EWZ = "EWZ"; // Einwohnerzahl
	public static final String DEATH_RATE = "death_rate";
	public static final String CASES = "cases";
	public static final String DEATHS = "deaths";
	public static final String CASES_PER_100K = "cases_per_100k";
	public static final String CASES_PER_POPULATION = "cases_per_population";
	public static final String LAST_UPDATE = "last_update";
	public static final String CASES7_PER_100K = "cases7_per_100k";
	// @formatter:on

}

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
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Function;

public class Formatters
{

	public static Function<String, String> FORMATTER_DS = s -> {
		try {
			double d = Double.parseDouble(s);
			return String.format(Locale.GERMAN, "%.2f", d);
		} catch (NumberFormatException e) {
			return s;
		}
	};

	public static Function<String, String> FORMATTER_IS = s -> {
		try {
			int i = Integer.parseInt(s);
			return String.format(Locale.GERMAN, "%,d", i);
		} catch (NumberFormatException e) {
			return s;
		}
	};

	public static Function<Double, String> FORMATTER_D = s -> {
		return String.format(Locale.GERMAN, "%.2f", s);
	};

	public static Function<Integer, String> FORMATTER_I = s -> {
		return String.format(Locale.GERMAN, "%,d", s);
	};

	private static final DateTimeFormatter _FORMATTER_DATE = DateTimeFormatter
			.ofPattern("dd.MM.YYYY");

	public static Function<LocalDate, String> FORMATTER_DATE = s -> {
		return _FORMATTER_DATE.format(s);
	};

}

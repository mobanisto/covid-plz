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

package de.mobanisto.covidplz;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.mobanisto.covidplz.mapping.Mapping;
import de.mobanisto.covidplz.mapping.PartialRsRelation;

public class TestSerializeDeserializeMapping
{

	public static void main(String[] args)
	{
		Mapping mapping = new Mapping();
		mapping.getCodeToRKI().put("12345",
				Arrays.asList(new PartialRsRelation("11111", 1)));
		mapping.getCodeToRKI().put("23456",
				Arrays.asList(new PartialRsRelation("22222", 0.8),
						new PartialRsRelation("33333", 0.2)));

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(mapping);
		System.out.println(json);

		Mapping deserialized = gson.fromJson(json, Mapping.class);
		Map<String, List<PartialRsRelation>> codeToRKI = deserialized
				.getCodeToRKI();
		for (String key : codeToRKI.keySet()) {
			System.out.println(key);
			List<PartialRsRelation> rss = codeToRKI.get(key);
			for (PartialRsRelation rs : rss) {
				System.out.println(
						" → " + rs.getObject() + "   " + rs.getCoverage());
			}
		}
	}

}

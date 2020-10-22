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

import java.lang.reflect.Type;
import java.time.LocalDate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import de.mobanisto.covidplz.model.germany.Bundesland;

public class GsonUtil
{

	public static Gson gson()
	{
		return new GsonBuilder().registerTypeAdapter(LocalDate.class,
				new JsonDeserializer<LocalDate>() {

					@Override
					public LocalDate deserialize(JsonElement json, Type type,
							JsonDeserializationContext context)
							throws JsonParseException
					{
						return LocalDate.parse(json.getAsString());
					}

				}).setPrettyPrinting().create();
	}

	public static Gson germany()
	{
		return new GsonBuilder().registerTypeAdapter(Bundesland.class,
				new JsonSerializer<Bundesland>() {

					@Override
					public JsonElement serialize(Bundesland src, Type typeOfSrc,
							JsonSerializationContext context)
					{
						return new JsonPrimitive(src.getId());
					}

				}).setPrettyPrinting().create();
	}

}

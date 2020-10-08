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

package de.mobanisto.covidplz.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.function.Consumer;

import javax.servlet.http.HttpServletResponse;

import org.jsoup.nodes.Document;

import de.mobanisto.covidplz.pages.other.ErrorGenerator;
import de.topobyte.jsoup.nodes.Element;
import de.topobyte.webpaths.WebPath;

public class ServletUtil
{

	public static void respond404(WebPath output, HttpServletResponse response,
			Void data) throws IOException
	{
		respond(404, output, response, content -> {
			ErrorUtil.write404(content);
		}, data);
	}

	public static void respond404(WebPath output, HttpServletResponse response,
			Consumer<Element> contentGenerator, Void data) throws IOException
	{
		respond(404, output, response, contentGenerator, data);
	}

	public static void respond(int code, WebPath output,
			HttpServletResponse response, Consumer<Element> contentGenerator,
			Void data) throws IOException
	{
		response.setStatus(code);

		response.setCharacterEncoding("UTF-8");

		PrintWriter writer = response.getWriter();

		ErrorGenerator generator = new ErrorGenerator(output);
		generator.generate();
		Element content = generator.getContent();

		contentGenerator.accept(content);

		Document document = generator.getBuilder().getDocument();
		writer.write(document.toString());

		writer.close();
	}

}

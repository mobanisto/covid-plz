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

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.mobanisto.covidplz.resolving.MainPathResolver;
import de.mobanisto.covidplz.util.ServletUtil;
import de.topobyte.jsoup.ContentGeneratable;
import de.topobyte.jsoup.JsoupServletUtil;
import de.topobyte.webgun.exceptions.WebStatusException;
import de.topobyte.webgun.resolving.PathResolver;
import de.topobyte.webgun.resolving.Redirecter;
import de.topobyte.webpaths.WebPath;
import de.topobyte.webpaths.WebPaths;

@WebServlet("/*")
public class IndexServlet extends HttpServlet
{

	private static final long serialVersionUID = 1L;

	static List<PathResolver<ContentGeneratable, Void>> resolvers = new ArrayList<>();
	static {
		resolvers.add(new MainPathResolver());
	}

	private interface Responder<T>
	{

		public void respond(int code, WebPath output,
				HttpServletResponse response, T data) throws IOException;

	}

	private <T> void tryGenerate(HttpServletResponse response, WebPath path,
			ContentGeneratable generator, Responder<T> responder, T data)
			throws IOException
	{
		if (generator != null) {
			try {
				JsoupServletUtil.respond(response, generator);
			} catch (WebStatusException e) {
				responder.respond(e.getCode(), path, response, data);
			}
		} else {
			responder.respond(404, path, response, data);
		}

	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		String uri = URLDecoder.decode(request.getRequestURI(), "UTF-8");
		WebPath path = WebPaths.get(uri);

		Map<String, String[]> parameters = request.getParameterMap();

		List<Redirecter> redirecters = new ArrayList<>();

		for (Redirecter redirecter : redirecters) {
			String location = redirecter.redirect(path, parameters);
			if (location != null) {
				response.sendRedirect(location);
				return;
			}
		}

		ContentGeneratable generator = null;

		for (PathResolver<ContentGeneratable, Void> resolver : resolvers) {
			generator = resolver.getGenerator(path, request, null);
			if (generator != null) {
				break;
			}
		}

		tryGenerate(response, path, generator, ServletUtil::respond,
				(Void) null);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		String uri = URLDecoder.decode(request.getRequestURI(), "UTF-8");
		WebPath path = WebPaths.get(uri);

		Map<String, String[]> parameters = request.getParameterMap();

		List<Redirecter> redirecters = new ArrayList<>();

		for (Redirecter redirecter : redirecters) {
			String location = redirecter.redirect(path, parameters);
			if (location != null) {
				response.sendRedirect(location);
				return;
			}
		}

		ContentGeneratable generator = null;

		for (PathResolver<ContentGeneratable, Void> resolver : resolvers) {
			generator = resolver.getGenerator(path, request, null);
			if (generator != null) {
				break;
			}
		}

		tryGenerate(response, path, generator, ServletUtil::respond,
				(Void) null);
	}

}

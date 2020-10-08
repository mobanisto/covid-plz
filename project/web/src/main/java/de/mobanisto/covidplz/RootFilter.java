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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.cachebusting.CacheBusting;
import de.topobyte.servlet.utils.Servlets;

@WebFilter("/*")
public class RootFilter implements Filter
{

	final static Logger logger = LoggerFactory.getLogger(RootFilter.class);

	private static Set<String> staticFiles = new HashSet<>();
	private static List<String> staticPatterns = new ArrayList<>();

	static {
		staticPatterns.add("/client/");
		staticPatterns.add("/images/");
		for (String entry : CacheBusting.getValues()) {
			staticFiles.add("/" + entry);
		}
		staticFiles.add("/googleb4d9f938be253b7a.html");
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		// nothing to do here
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException
	{
		if (request instanceof HttpServletRequest) {
			HttpServletRequest hsr = (HttpServletRequest) request;

			String path = hsr.getRequestURI();

			if (staticFiles.contains(path)) {
				HttpServletResponse r = (HttpServletResponse) response;
				r.setHeader("Cache-Control", "public, max-age=31536000");
				Servlets.forwardToDefault(request, response);
				return;
			}
			for (String staticPrefix : staticPatterns) {
				if (path.startsWith(staticPrefix)) {
					Servlets.forwardToDefault(request, response);
					return;
				}
			}

			// Normalize multiple consecutive forward slashes to single slashes
			if (path.contains("//")) {
				String newUri = path.replaceAll("/+/", "/");
				((HttpServletResponse) response).sendRedirect(newUri);
				return;
			}

			request.setCharacterEncoding("UTF-8");
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy()
	{
	}

}

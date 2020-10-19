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

package de.mobanisto.covidplz.resolving;

import javax.servlet.http.HttpServletRequest;

import de.mobanisto.covidplz.pages.markdown.MarkdownResourceGenerator;
import de.mobanisto.covidplz.pages.other.AboutGenerator;
import de.mobanisto.covidplz.pages.other.IndexGenerator;
import de.mobanisto.covidplz.pages.other.RefreshGenerator;
import de.mobanisto.covidplz.pages.other.SupportGenerator;
import de.topobyte.jsoup.ContentGeneratable;
import de.topobyte.webgun.resolving.pathspec.PathSpec;
import de.topobyte.webgun.resolving.pathspec.PathSpecResolver;
import de.topobyte.webpaths.WebPath;

public class MainPathResolver extends PathSpecResolver<ContentGeneratable, Void>
{

	@Override
	public ContentGeneratable getGenerator(WebPath path,
			HttpServletRequest request, Void data)
	{
		if (path.getNameCount() == 0) {
			return new IndexGenerator(path, request.getParameterMap());
		}
		return super.getGenerator(path, request, data);
	}

	{
		map(new PathSpec("about"), (path, output, request, data) -> {
			return new AboutGenerator(path);
		});
		map(new PathSpec("impressum"), (path, output, request, data) -> {
			return new MarkdownResourceGenerator(path,
					"markdown/de/impressum.md");
		});
		map(new PathSpec("privacy-policy"), (path, output, request, data) -> {
			return new MarkdownResourceGenerator(path,
					"markdown/de/privacy-policy.md");
		});

		map(new PathSpec("refresh"), (path, output, request, data) -> {
			return new RefreshGenerator(path);
		});
		map(new PathSpec("support-us"), (path, output, request, data) -> {
			return new SupportGenerator(path);
		});
	}

}

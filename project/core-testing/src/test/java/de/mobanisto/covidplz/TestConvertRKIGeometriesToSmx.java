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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.locationtech.jts.geom.Geometry;

import de.topobyte.simplemapfile.core.EntityFile;
import de.topobyte.simplemapfile.xml.SmxFileWriter;
import de.topobyte.system.utils.SystemPaths;

public class TestConvertRKIGeometriesToSmx
{

	/**
	 * Convert the RKI geometries from GeoJSON to a directory with simple
	 * mapfile (SMX) files, so that they can be easily viewed or otherwise
	 * examined.
	 */
	public static void main(String[] args) throws IOException,
			TransformerException, ParserConfigurationException
	{
		Path repo = SystemPaths.CWD.getParent().getParent();
		Path fileData = repo.resolve("data/RKI_Corona_Landkreise.geojson");
		Path dirOutput = repo.resolve("smx/rki");

		Files.createDirectories(dirOutput);

		RKIBaseData data = RKILoader.load(fileData);
		Map<String, Geometry> map = data.getRsToGeometry();

		for (String rs : map.keySet()) {
			Geometry geometry = map.get(rs);

			EntityFile entity = new EntityFile();
			entity.setGeometry(geometry);

			Path file = dirOutput.resolve(String.format("%s.smx", rs));
			SmxFileWriter.write(entity, file);
		}
	}

}

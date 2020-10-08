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
import java.nio.file.Path;
import java.util.Map;

import org.locationtech.jts.geom.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.osm4j.core.access.OsmIteratorInput;
import de.topobyte.osm4j.core.dataset.InMemoryListDataSet;
import de.topobyte.osm4j.core.dataset.InMemoryMapDataSet;
import de.topobyte.osm4j.core.dataset.ListDataSetLoader;
import de.topobyte.osm4j.core.dataset.MapDataSetLoader;
import de.topobyte.osm4j.core.model.iface.OsmRelation;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;
import de.topobyte.osm4j.core.resolve.EntityNotFoundException;
import de.topobyte.osm4j.geometry.GeometryBuilder;
import de.topobyte.osm4j.utils.FileFormat;
import de.topobyte.osm4j.utils.OsmFileInput;

public class PostalCodeLoader
{

	final static Logger logger = LoggerFactory
			.getLogger(PostalCodeLoader.class);

	public static PostalCodeData load(Path dir) throws IOException
	{
		PostalCodeData data = new PostalCodeData();

		PostalCodeLoader loader = new PostalCodeLoader();
		loader.loadData(dir, data);

		return data;
	}

	private void loadData(Path dir, PostalCodeData data) throws IOException
	{
		OsmFileInput osmFileNodes = new OsmFileInput(dir.resolve("nodes.pbf"),
				FileFormat.PBF);
		OsmFileInput osmFileWays = new OsmFileInput(dir.resolve("ways.pbf"),
				FileFormat.PBF);
		OsmFileInput osmFileRelations = new OsmFileInput(
				dir.resolve("relations.pbf"), FileFormat.PBF);

		OsmIteratorInput nodeInput = osmFileNodes.createIterator(false, false);
		InMemoryMapDataSet nodes = MapDataSetLoader
				.read(nodeInput.getIterator(), false, false, false);
		nodeInput.close();

		OsmIteratorInput wayInput = osmFileWays.createIterator(false, false);
		InMemoryMapDataSet ways = MapDataSetLoader.read(wayInput.getIterator(),
				false, false, false);
		wayInput.close();

		OsmIteratorInput relationInput = osmFileRelations.createIterator(true,
				false);
		InMemoryListDataSet relations = ListDataSetLoader
				.read(relationInput.getIterator(), false, false, true);
		relationInput.close();

		InMemoryMapDataSet builderData = new InMemoryMapDataSet();
		builderData.setNodes(nodes.getNodes());
		builderData.setWays(ways.getWays());

		GeometryBuilder gb = new GeometryBuilder();

		for (OsmRelation relation : relations.getRelations()) {
			Map<String, String> tags = OsmModelUtil.getTagsAsMap(relation);
			String code = tags.get("postal_code");
			if (code == null) {
				continue;
			}
			if (code.length() != 5) {
				continue;
			}
			try {
				Geometry geometry = gb.build(relation, builderData);
				data.getCodeToGeometry().put(code, geometry);
			} catch (EntityNotFoundException e) {
				logger.warn(String.format(
						"Error while building geometry for relation %d (%s)",
						relation.getId(), tags));
			}
		}
	}

}

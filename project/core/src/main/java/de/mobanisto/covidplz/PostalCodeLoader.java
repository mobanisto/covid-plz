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

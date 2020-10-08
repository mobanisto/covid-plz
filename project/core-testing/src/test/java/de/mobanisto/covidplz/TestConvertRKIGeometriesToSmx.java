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
		Path dirOutput = repo.resolve("smx");

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

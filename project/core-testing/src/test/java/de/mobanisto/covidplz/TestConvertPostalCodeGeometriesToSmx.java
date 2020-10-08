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

public class TestConvertPostalCodeGeometriesToSmx
{

	/**
	 * Convert the postal code geometries from GeoJSON to a directory with
	 * simple mapfile (SMX) files, so that they can be easily viewed or
	 * otherwise examined.
	 */
	public static void main(String[] args) throws IOException,
			TransformerException, ParserConfigurationException
	{
		Path repo = SystemPaths.CWD.getParent().getParent();
		Path dirData = repo.resolve("data/postal_codes");
		Path dirOutput = repo.resolve("smx/postal_codes");

		Files.createDirectories(dirOutput);

		PostalCodeData data = PostalCodeLoader.load(dirData);
		Map<String, Geometry> map = data.getCodeToGeometry();

		for (String rs : map.keySet()) {
			Geometry geometry = map.get(rs);

			EntityFile entity = new EntityFile();
			entity.setGeometry(geometry);

			Path file = dirOutput.resolve(String.format("%s.smx", rs));
			SmxFileWriter.write(entity, file);
		}
	}

}

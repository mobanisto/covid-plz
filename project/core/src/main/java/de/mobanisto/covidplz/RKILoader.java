package de.mobanisto.covidplz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RKILoader
{

	final static Logger logger = LoggerFactory.getLogger(RKILoader.class);

	public static RKIBaseData load(Path file) throws IOException
	{
		RKIBaseData data = new RKIBaseData();

		RKILoader loader = new RKILoader();
		loader.loadRkiBaseData(file, data);

		return data;
	}

	private void loadRkiBaseData(Path file, RKIBaseData data) throws IOException
	{
		try (BufferedReader reader = Files.newBufferedReader(file)) {
			loadRkiBaseData(reader, data);
		}
	}

	private void loadRkiBaseData(Reader reader, RKIBaseData data)
	{
		GeoJsonReader geoJsonReader = new GeoJsonReader();
		JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
		JsonObject crs = object.get("crs").getAsJsonObject();
		JsonArray features = object.get("features").getAsJsonArray();

		logger.debug("CRS: " + crs);
		logger.debug("Number of features: " + features.size());
		for (int i = 0; i < features.size(); i++) {
			JsonObject feature = features.get(i).getAsJsonObject();
			String type = feature.get("type").getAsString();
			if (!type.equals("Feature")) {
				continue;
			}
			JsonObject properties = feature.get("properties").getAsJsonObject();
			JsonObject jGeometry = feature.get("geometry").getAsJsonObject();

			String geometryType = jGeometry.get("type").getAsString();
			if (!geometryType.equals("MultiPolygon")) {
				logger.warn(
						"Encountered feature with geometry other than MultiPolygon: "
								+ geometryType);
				continue;
			}

			// RS is the only identifier present on all entries. It is the AGS
			// (Amtlicher Gemeinde-Schlüssel) for the counties and for ther
			// districts of Berlin it is some other unknown but distinct value
			JsonElement jrs = properties.get("RS");
			if (jrs.isJsonNull()) {
				logger.warn("property 'RS' not defined: " + properties);
				continue;
			}

			String rs = jrs.getAsString();

			try {
				Geometry geometry = geoJsonReader.read(jGeometry.toString());
				data.getRsToGeometry().put(rs, geometry);
			} catch (ParseException e) {
				logger.error("Error while parsing geometry", e);
			}
		}
	}

}

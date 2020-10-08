package de.mobanisto.covidplz;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.junit.Assert;
import org.locationtech.jts.geom.Geometry;

import de.topobyte.system.utils.SystemPaths;

public class TestReadRKIGeometries
{

	public static void main(String[] args) throws IOException
	{
		Path file = SystemPaths.CWD.getParent().getParent()
				.resolve("data/RKI_Corona_Landkreise.geojson");

		RKIBaseData data = RKILoader.load(file);
		Map<String, Geometry> map = data.getRsToGeometry();

		Assert.assertEquals(412, map.size());
	}

}

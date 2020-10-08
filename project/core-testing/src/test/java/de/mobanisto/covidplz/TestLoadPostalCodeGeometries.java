package de.mobanisto.covidplz;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.Assert;

import de.topobyte.system.utils.SystemPaths;

public class TestLoadPostalCodeGeometries
{

	public static void main(String[] args) throws IOException
	{
		Path repo = SystemPaths.CWD.getParent().getParent();
		Path file = repo.resolve("data/postal_codes");

		PostalCodeData data = PostalCodeLoader.load(file);

		Assert.assertEquals(8168, data.getCodeToGeometry().size());
	}

}

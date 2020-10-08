package de.mobanisto.covidplz;

import java.io.IOException;
import java.nio.file.Path;

import de.mobanisto.covidplz.model.PreprocessingData;

public class PreprocessingDataLoader
{

	public PreprocessingData loadData(Path dir) throws IOException
	{
		PreprocessingData data = new PreprocessingData();

		data.setBaseData(
				RKILoader.load(dir.resolve("RKI_Corona_Landkreise.geojson")));

		data.setPostalCodeData(
				PostalCodeLoader.load(dir.resolve("postal_codes")));

		return data;
	}

}

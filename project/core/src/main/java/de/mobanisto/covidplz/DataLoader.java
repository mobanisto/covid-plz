package de.mobanisto.covidplz;

import java.io.IOException;
import java.nio.file.Path;

import de.mobanisto.covidplz.model.Data;

public class DataLoader
{

	public Data loadData(Path dir) throws IOException
	{
		Data data = new Data();

		data.setBaseData(
				RKILoader.load(dir.resolve("RKI_Corona_Landkreise.geojson")));

		return data;
	}

}

package de.mobanisto.covidplz;

import java.util.HashMap;
import java.util.Map;

import org.locationtech.jts.geom.Geometry;

import lombok.Getter;

public class PostalCodeData
{

	@Getter
	private Map<String, Geometry> codeToGeometry = new HashMap<>();

}

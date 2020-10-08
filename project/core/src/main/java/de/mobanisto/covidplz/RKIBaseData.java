package de.mobanisto.covidplz;

import java.util.HashMap;
import java.util.Map;

import org.locationtech.jts.geom.Geometry;

import lombok.Getter;

public class RKIBaseData
{

	@Getter
	private Map<String, Geometry> rsToGeometry = new HashMap<>();

}

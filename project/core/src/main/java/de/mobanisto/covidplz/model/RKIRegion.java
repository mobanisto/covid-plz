package de.mobanisto.covidplz.model;

import org.locationtech.jts.geom.Geometry;

import lombok.Getter;

public class RKIRegion
{

	@Getter
	private String rs;
	@Getter
	private Geometry geometry;

	public RKIRegion(String rs, Geometry geometry)
	{
		this.rs = rs;
		this.geometry = geometry;
	}

}

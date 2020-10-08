package de.mobanisto.covidplz.model;

import org.locationtech.jts.geom.Geometry;

import lombok.Getter;

public class PostalCode
{

	@Getter
	private String code;
	@Getter
	private Geometry geometry;

	public PostalCode(String code, Geometry geometry)
	{
		this.code = code;
		this.geometry = geometry;
	}

}

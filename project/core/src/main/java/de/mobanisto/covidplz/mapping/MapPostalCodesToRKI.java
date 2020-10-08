// Copyright (c) 2020 Mobanisto UG (haftungsbeschränkt)
//
// This file is part of covid-plz.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

package de.mobanisto.covidplz.mapping;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.locationtech.jts.geom.Geometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import de.mobanisto.covidplz.PostalCodeData;
import de.mobanisto.covidplz.PreprocessingDataLoader;
import de.mobanisto.covidplz.RKIBaseData;
import de.mobanisto.covidplz.model.PreprocessingData;
import de.mobanisto.covidplz.model.RKIRegion;
import de.topobyte.jts.indexing.GeometryTesselationMap;

public class MapPostalCodesToRKI
{

	final static Logger logger = LoggerFactory
			.getLogger(MapPostalCodesToRKI.class);

	private static final double MIN_THRESHOLD_MULTIPLE_USE = 0.05;

	public Mapping execute(Path dir) throws IOException
	{
		logger.info("loading data...");
		PreprocessingDataLoader dataLoader = new PreprocessingDataLoader();
		PreprocessingData data = dataLoader.loadData(dir);

		PostalCodeData postalCodeData = data.getPostalCodeData();
		RKIBaseData baseData = data.getBaseData();

		logger.info("building RKI region lookup...");
		GeometryTesselationMap<RKIRegion> map = new GeometryTesselationMap<>(
				true);
		Map<String, Geometry> rsToGeometry = baseData.getRsToGeometry();
		for (String rs : rsToGeometry.keySet()) {
			Geometry geometry = rsToGeometry.get(rs);
			map.add(geometry, new RKIRegion(rs, geometry));
		}

		Mapping mapping = new Mapping();

		logger.info("processing postal codes...");
		Map<String, Geometry> codeToGeometry = postalCodeData
				.getCodeToGeometry();
		for (String code : codeToGeometry.keySet()) {
			Geometry geometry = codeToGeometry.get(code);
			try {
				List<PartialRKIRelation> results = robustCovering(map, code,
						geometry);
				List<PartialRsRelation> converted = convert(results);
				mapping.getCodeToRKI().put(code, converted);
			} catch (NoCoveringRegionException e) {
				logger.warn(String.format(
						"No covering region was found for postal code %s: %s",
						code, e.getMessage()));
			}
		}

		return mapping;
	}

	private List<PartialRsRelation> convert(List<PartialRKIRelation> rkis)
	{
		List<PartialRsRelation> rs = new ArrayList<>();
		for (PartialRKIRelation rki : rkis) {
			rs.add(new PartialRsRelation(rki.getObject().getRs(),
					rki.getCoverage()));
		}
		return rs;
	}

	private static List<PartialRKIRelation> robustCovering(
			GeometryTesselationMap<RKIRegion> map, String code,
			Geometry geometry) throws NoCoveringRegionException
	{
		Set<RKIRegion> covering = map.covering(geometry);
		if (!covering.isEmpty()) {
			List<PartialRKIRelation> results = new ArrayList<>();
			for (RKIRegion region : covering) {
				results.add(new PartialRKIRelation(region, 1));
			}
			return results;
		}

		List<RKIRegion> candidates = new ArrayList<>(
				map.intersecting(geometry));
		List<PartialRKIRelation> withCoverage = new ArrayList<>();

		double area = geometry.getArea();
		for (RKIRegion rki : candidates) {
			Geometry intersection = rki.getGeometry().intersection(geometry);
			double coverage = intersection.getArea() / area;
			withCoverage.add(new PartialRKIRelation(rki, coverage));
		}

		Collections.sort(withCoverage);
		Collections.reverse(withCoverage);

		List<PartialRKIRelation> take = new ArrayList<>();
		for (PartialRKIRelation candidate : withCoverage) {
			if (candidate.getCoverage() > MIN_THRESHOLD_MULTIPLE_USE) {
				take.add(candidate);
			}
		}

		if (take.size() > 1) {
			List<String> values = new ArrayList<>();
			for (PartialRelation<RKIRegion> candidate : take) {
				values.add(ref(candidate));
			}
			logger.warn(String.format("Selected multiple for %s: %s", code,
					Joiner.on(", ").join(values)));
		}

		return take;
	}

	private static String ref(PartialRelation<RKIRegion> candidate)
	{
		return String.format("%s (%.3f)", candidate.getObject().getRs(),
				candidate.getCoverage());
	}

}

package org.openmrs.ui.framework.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.api.LocationService;
import org.openmrs.ui.framework.converter.util.ConversionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

public class StringToLocationConverter implements Converter<String, Location> {
	
	@Autowired
	private LocationService locationService;
	
	@Override
	public Location convert(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		} else if (ConversionUtil.onlyDigits(id)) {
			return locationService.getLocation(Integer.valueOf(id));
		} else {
			return locationService.getLocationByUuid(id);
		}
	}
	
}

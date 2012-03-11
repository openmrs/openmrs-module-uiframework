package org.openmrs.ui.framework.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.springframework.core.convert.converter.Converter;

public class StringToLocationConverter implements Converter<String, Location> {
	
	@Override
	public Location convert(String locationId) {
		if (StringUtils.isBlank(locationId))
			return null;
		return Context.getLocationService().getLocation(Integer.valueOf(locationId));
	}
	
}

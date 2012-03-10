package org.openmrs.ui2.core.converter;

import org.openmrs.OpenmrsObject;
import org.openmrs.ui2.core.SimpleObject;
import org.springframework.core.convert.converter.Converter;

public class OpenmrsObjectToSimpleObjectConverter implements Converter<OpenmrsObject, SimpleObject> {
	
	@Override
	public SimpleObject convert(OpenmrsObject o) {
		SimpleObject ret = new SimpleObject();
		ret.put("id", o.getId());
		ret.put("label", o.toString());
		return ret;
	}
	
}

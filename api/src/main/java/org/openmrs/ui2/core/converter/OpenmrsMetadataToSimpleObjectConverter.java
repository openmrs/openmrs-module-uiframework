package org.openmrs.ui2.core.converter;

import org.openmrs.OpenmrsMetadata;
import org.openmrs.ui2.core.SimpleObject;
import org.springframework.core.convert.converter.Converter;

public class OpenmrsMetadataToSimpleObjectConverter implements Converter<OpenmrsMetadata, SimpleObject> {
	
	@Override
	public SimpleObject convert(OpenmrsMetadata md) {
		return new SimpleObject(md);
	}
	
}

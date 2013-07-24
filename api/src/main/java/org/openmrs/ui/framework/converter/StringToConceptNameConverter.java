package org.openmrs.ui.framework.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.ConceptName;
import org.openmrs.api.context.Context;
import org.springframework.core.convert.converter.Converter;

/**
 *
 */
public class StringToConceptNameConverter implements Converter<String, ConceptName> {

    @Override
    public ConceptName convert(String id) {
        if (StringUtils.isBlank(id))
            return null;
        return Context.getConceptService().getConceptName(Integer.valueOf(id));
    }

}

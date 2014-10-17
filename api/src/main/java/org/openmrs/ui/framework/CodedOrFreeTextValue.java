/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.ui.framework;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.util.OpenmrsUtil;

/**
 * Wrapper for coded or free text value. The coded value could be a Concept or ConceptName
 */
public class CodedOrFreeTextValue {
	
	public static final String CONCEPT_PREFIX = "CONCEPT:";
	
	public static final String CONCEPT_NAME_PREFIX = "CONCEPT_NAME:";
	
	public static final String NON_CODED_PREFIX = "NON_CODED:";
	
	private Concept codedValue;
	
	private ConceptName codedNameValue;
	
	private String nonCodedValue;
	
	public CodedOrFreeTextValue() {
	}
	
	public CodedOrFreeTextValue(Concept codedValue) {
		this.codedValue = codedValue;
	}
	
	public CodedOrFreeTextValue(ConceptName codedNameValue) {
		this.codedNameValue = codedNameValue;
	}
	
	public CodedOrFreeTextValue(String nonCodedValue) {
		this.nonCodedValue = nonCodedValue;
	}
	
	public ConceptName getCodedNameValue() {
		return codedNameValue;
	}
	
	public void setCodedNameValue(ConceptName codedNameValue) {
		this.codedNameValue = codedNameValue;
	}
	
	public Concept getCodedValue() {
		return codedValue;
	}
	
	public void setCodedValue(Concept codedValue) {
		this.codedValue = codedValue;
	}
	
	public String getNonCodedValue() {
		return nonCodedValue;
	}
	
	public void setNonCodedValue(String nonCodedValue) {
		this.nonCodedValue = nonCodedValue;
	}
	
	public Object getValue() {
		if (codedNameValue != null) {
			return codedNameValue;
		} else if (codedValue != null) {
			return codedValue;
		} else {
			return nonCodedValue;
		}
	}
	
	@Override
	public String toString() {
		if (codedNameValue != null) {
			return codedNameValue.getName();
		} else if (codedValue != null) {
			return codedValue.getName().getName();
		} else {
			return nonCodedValue;
		}
	}

    public String getRawValue() {
        if (codedNameValue != null) {
            return CONCEPT_NAME_PREFIX + codedNameValue.getUuid();
        } else if (codedValue != null) {
            return CONCEPT_PREFIX + codedValue.getUuid();
        } else {
            return NON_CODED_PREFIX + nonCodedValue;
        }
    }
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof CodedOrFreeTextValue)) {
			return false;
		}
		CodedOrFreeTextValue other = (CodedOrFreeTextValue) o;
		return OpenmrsUtil.nullSafeEquals(codedValue, other.codedValue)
		        && OpenmrsUtil.nullSafeEquals(codedNameValue, other.codedNameValue)
		        && OpenmrsUtil.nullSafeEquals(nonCodedValue, other.nonCodedValue);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(codedValue).append(codedNameValue).append(nonCodedValue).toHashCode();
	}
}

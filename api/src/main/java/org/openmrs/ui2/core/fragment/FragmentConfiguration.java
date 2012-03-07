package org.openmrs.ui2.core.fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.ui2.core.AttributeExpressionException;
import org.openmrs.ui2.core.AttributeHolder;
import org.openmrs.ui2.core.AttributeHolderUtil;

/**
 * The configuration for a fragment 
 */
public class FragmentConfiguration extends HashMap<String, Object> implements AttributeHolder {
	
	private static final long serialVersionUID = 1L;
	
	public FragmentConfiguration() {
		super();
	}
	
	public FragmentConfiguration(Map<String, Object> configuration) {
		super();
		if (configuration != null)
			super.putAll(configuration);
	}
	
	/**
	 * Adds an attribute to this configuration
	 * 
	 * @param name
	 * @param value
	 * @return this configuration, for chaining of calls
	 */
	public FragmentConfiguration addAttribute(String name, Object value) {
		super.put(name, value);
		return this;
	}
	
	@Override
	public Object getAttribute(String name) {
		return super.get(name);
	}
	
	/**
	 * @see org.openmrs.ui2.core.AttributeHolder#require(java.lang.String[])
	 */
	@Override
	public void require(String... expressions) throws AttributeExpressionException {
		List<String> failed = AttributeHolderUtil.unsatisfiedExpressions(this, expressions);
		if (failed.size() > 0) {
			throw new AttributeExpressionException(expressions, failed);
		}
	}
	
	/**
	 * Adds any of the specified attributes that aren't already defined
	 * @param moreAttributes
	 * @return this object (for chaining)
	 */
	public FragmentConfiguration merge(Map<String, Object> moreAttributes) {
		for (String name : moreAttributes.keySet()) {
			if (getAttribute(name) == null)
				addAttribute(name, moreAttributes.get(name));
		}
		return this;
	}
}

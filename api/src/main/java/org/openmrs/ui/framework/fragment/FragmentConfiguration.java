package org.openmrs.ui.framework.fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.ui.framework.AttributeExpressionException;
import org.openmrs.ui.framework.AttributeHolder;
import org.openmrs.ui.framework.AttributeHolderUtil;

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
	 * Copy all attributes in the supplied <code>Map</code> into this <code>Map</code>,
	 * with existing objects of the same name taking precedence (i.e. not getting
	 * replaced).
	 */
	public FragmentConfiguration mergeAttributes(Map<String, ?> attributes) {
		if (attributes != null) {
			for (String key : attributes.keySet()) {
				if (!containsKey(key)) {
					put(key, attributes.get(key));
				}
			}
		}
		return this;
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
	 * @see org.openmrs.ui.framework.AttributeHolder#require(java.lang.String[])
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

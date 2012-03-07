package org.openmrs.ui2.core;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A model (in the MVC sense, used by a controller to pass content to a view)
 * (This is based on Spring MVC's ModelMap.)
 */
public class Model extends LinkedHashMap<String, Object> implements AttributeHolder {
	
	private static final long serialVersionUID = 1L;
	
	public void addAttribute(String name, Object value) {
		super.put(name, value);
	}
	
	/**
	 * @see org.openmrs.ui2.core.AttributeHolder#getAttribute(java.lang.String)
	 */
	@Override
	public Object getAttribute(String name) {
		return super.get(name);
	}
	
	/**
	 * Copy all attributes in the supplied <code>Map</code> into this <code>Map</code>,
	 * with existing objects of the same name taking precedence (i.e. not getting
	 * replaced).
	 */
	public Model mergeAttributes(Map<String, ?> attributes) {
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
	 * @see org.openmrs.ui2.core.AttributeHolder#require(java.lang.String[])
	 */
	@Override
	public void require(String... expressions) throws AttributeExpressionException {
		List<String> failed = AttributeHolderUtil.unsatisfiedExpressions(this, expressions);
		if (failed.size() > 0) {
			throw new AttributeExpressionException(expressions, failed);
		}
	}
	
}

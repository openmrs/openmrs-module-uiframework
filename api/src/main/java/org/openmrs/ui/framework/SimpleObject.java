package org.openmrs.ui.framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.OpenmrsMetadata;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

/**
 * Used to generate simplified representations of data or metadata, making it easier to serialize to json
 */
public class SimpleObject extends LinkedHashMap<String, Object> {
	
	private static final long serialVersionUID = 1L;
	
	public SimpleObject() {
		super();
	}
	
	/**
	 * Convenience constructor for creating a {@link SimpleObject} representing {@link OpenmrsMetadata},
	 * which will set 'id' and 'label' properties
	 * @param metadata
	 */
	public SimpleObject(OpenmrsMetadata metadata) {
		super();
		put("id", metadata.getId());
		put("label", metadata.getName());
	}
	
	/**
	 * Utility method to create a {@link SimpleObject} given a varargs style list of property names and
	 * values. The array passed in must have even length. Every other element (starting from the 0-index one)
	 * must be a String (representing a property name) and be followed by its value.
	 * @param propertyNamesAndValues
	 * @return
	 */
	public static SimpleObject create(Object... propertyNamesAndValues) {
		SimpleObject ret = new SimpleObject();
		for (int i = 0; i < propertyNamesAndValues.length; i += 2) {
			String prop = (String) propertyNamesAndValues[i];
			ret.put(prop, propertyNamesAndValues[i + 1]);
		}
		return ret;
	}
	
	/**
	 * Builds a simplified version of the object passed in, such that the result can be automatically
	 * converted to json without worrying about hibernate proxies, loops in the object graph, etc.
	 * Takes the specified properties from the given object, formats them using {@link UiUtils} and builds
	 * a {@link SimpleObject} out of them.
	 * @param fromObject the bean to simplify
	 * @param ui
	 * @param propertiesToInclude properties to include in the returned object. dot-separated to refer to subproperties
	 * @return
	 */
	public static SimpleObject fromObject(Object fromObject, UiUtils ui, String... propertiesToInclude) {
		if (fromObject instanceof Collection) {
			throw new IllegalArgumentException("Can only be called on an object, not a collection");
		}
		Map<String, Set<String>> propertiesByLevel = splitIntoLevels(propertiesToInclude);
		return (SimpleObject) fromObjectHelper(fromObject, ui, "", propertiesByLevel);
	}
	
	/**
	 * Like {@link #fromObject(Object, UiUtils, String...)}, but takes in a collection of objects and
	 * returns a List of {@link SimpleObject}s
	 * @param fromCollection
	 * @param ui
	 * @param propertiesToInclude
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<SimpleObject> fromCollection(Collection<?> fromCollection, UiUtils ui, String... propertiesToInclude) {
		Map<String, Set<String>> propertiesByLevel = splitIntoLevels(propertiesToInclude);
		return (List<SimpleObject>) fromObjectHelper(fromCollection, ui, "", propertiesByLevel);
	}
	
	private static Map<String, Set<String>> splitIntoLevels(String[] propertiesToInclude) {
		Map<String, Set<String>> ret = new LinkedHashMap<String, Set<String>>();
		for (String prop : propertiesToInclude) {
			String[] components = prop.split("\\.");
			for (int i = 0; i < components.length; ++i) {
				splitIntoLevelsHelper(ret, Arrays.asList(components), i);
			}
		}
		return ret;
	}
	
	private static void splitIntoLevelsHelper(Map<String, Set<String>> ret, List<String> components, int index) {
		String level = OpenmrsUtil.join(components.subList(0, index), ".");
		Set<String> atLevel = ret.get(level);
		if (atLevel == null) {
			atLevel = new LinkedHashSet<String>();
			ret.put(level, atLevel);
		}
		atLevel.add(components.get(index));
	}
	
	private static Object fromObjectHelper(Object obj, UiUtils ui, String currentLevel,
	        Map<String, Set<String>> propertiesByLevel) {
		if (obj instanceof Collection<?>) {
			// iterate over everything, but this doesn't count as a level
			List ret = new ArrayList();
			for (Object o : (Collection<?>) obj) {
				ret.add(fromObjectHelper(o, ui, currentLevel, propertiesByLevel));
			}
			return ret;
		} else {
			BindingResult accessor = new DataBinder(obj).getBindingResult();
			SimpleObject ret = new SimpleObject();
			for (String property : propertiesByLevel.get(currentLevel)) {
				Object propertyValue = accessor.getFieldValue(property);
				String nextLevel = "".equals(currentLevel) ? property : currentLevel + "." + property;
				if (propertiesByLevel.containsKey(nextLevel)) {
					// deep property: recurse into this
					ret.put(property, fromObjectHelper(propertyValue, ui, nextLevel, propertiesByLevel));
				} else {
					// shallow property: format it
					ret.put(property, ui.format(propertyValue));
				}
			}
			return ret;
		}
	}
	
}

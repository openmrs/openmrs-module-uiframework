package org.openmrs.ui.framework;

/**
 * A common interface for UI classes that hold a String -> Object map of attributes or properties
 */
public interface AttributeHolder {
	
	public Object getAttribute(String name);
	
	/**
	 * Assert that all the given attribute-related expressions are satisfied, otherwise an exception is thrown.
	 * @see AttributeHolderUtil#unsatisfiedExpressions(AttributeHolder, String[])
	 * @param expressions
	 * @throws IllegalArgumentException if any of the given expressions is not satisfied
	 */
	public void require(String... expressions) throws AttributeExpressionException;
	
}

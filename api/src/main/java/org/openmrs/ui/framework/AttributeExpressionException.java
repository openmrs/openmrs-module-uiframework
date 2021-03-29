package org.openmrs.ui.framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Indicates that an attribute check failed (for example because a required attribute was missing)
 * 
 * @see AttributeHolderUtil#unsatisfiedExpressions(AttributeHolder, String[])
 */
public class AttributeExpressionException extends UiFrameworkException {
	
	private static final long serialVersionUID = 1L;
	
	private Collection<String> expressions;
	
	private Collection<String> failedExpressions;
	
	public AttributeExpressionException(Collection<String> expressions, Collection<String> missingArguments) {
		this.expressions = expressions;
		this.failedExpressions = missingArguments;
	}
	
	public AttributeExpressionException(String[] requiredArguments, Collection<String> missingArguments) {
		this.expressions = Arrays.asList(requiredArguments);
		this.failedExpressions = missingArguments;
	}
	
	/**
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		List<String> passed = new ArrayList<String>(expressions);
		passed.removeAll(failedExpressions);
		return "Failed checks: " + failedExpressions + " (the following passed: " + passed + ")";
	}
	
}

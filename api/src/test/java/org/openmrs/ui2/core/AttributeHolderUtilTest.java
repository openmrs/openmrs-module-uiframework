package org.openmrs.ui2.core;

import org.junit.Assert;
import org.junit.Test;

public class AttributeHolderUtilTest {
	
	/**
	 * @see AttributeHolderUtil#unsatisfiedExpressions(AttributeHolder,String[])
	 * @verifies handle a and b
	 */
	@Test
	public void unsatisfiedExpressions_shouldHandleAAndB() throws Exception {
		String[] test = { "href", "label" };
		Model holder = new Model();
		holder.addAttribute("href", "okay");
		Assert.assertTrue(AttributeHolderUtil.unsatisfiedExpressions(holder, test).contains(test[1]));
		holder.addAttribute("label", "dokay");
		Assert.assertEquals(0, AttributeHolderUtil.unsatisfiedExpressions(holder, test).size());
	}
	
	/**
	 * @see AttributeHolderUtil#unsatisfiedExpressions(AttributeHolder,String[])
	 * @verifies handle a and either b or c
	 */
	@Test
	public void unsatisfiedExpressions_shouldHandleAAndEitherBOrC() throws Exception {
		String[] test = { "href", "label | icon" };
		Model holder = new Model();
		holder.addAttribute("href", "okay");
		Assert.assertTrue(AttributeHolderUtil.unsatisfiedExpressions(holder, test).contains(test[1]));
		holder.addAttribute("icon", "dokay");
		Assert.assertEquals(0, AttributeHolderUtil.unsatisfiedExpressions(holder, test).size());
	}
}

package org.openmrs.ui.framework;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.ui.framework.BasicUiUtils;
import org.openmrs.ui.framework.UiUtils;

public class UiUtilsTest {
	
	UiUtils ui;
	
	@Before
	public void before() {
		this.ui = new BasicUiUtils();
	}
	
	/**
	 * @see UiUtils#pageLink(String,Map)
	 * @verifies handle page name
	 */
	@Test
	public void pageLink_shouldHandlePageName() throws Exception {
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("one", "1");
		params.put("two", "2");
		String link = ui.pageLink("myPage", params);
		Assert.assertEquals("null/myPage.page?one=1&two=2&", link);
	}
	
	/**
	 * @see UiUtils#pageLink(String,Map)
	 * @verifies handle page name with question mark and query string
	 */
	@Test
	public void pageLink_shouldHandlePageNameWithQuestionMarkAndQueryString() throws Exception {
		Map<String, Object> params = new LinkedHashMap<String, Object>();
		params.put("one", "1");
		params.put("two", "2");
		String link = ui.pageLink("myPage?three=3&four=4", params);
		Assert.assertEquals("null/myPage.page?one=1&two=2&three=3&four=4", link);
	}
	
	/**
	 * @see UiUtils#pageLink(String,Map)
	 * @verifies handle page name with anchor
	 */
	@Test
	public void pageLink_shouldHandlePageNameWithAnchor() throws Exception {
		String link = ui.pageLink("myPage#mySection", null);
		Assert.assertEquals("null/myPage.page#mySection", link);
	}
	
	/**
	 * @see UiUtils#pageLink(String,Map)
	 * @verifies handle page name with anchor and query string
	 */
	@Test
	public void pageLink_shouldHandlePageNameWithAnchorAndQueryString() throws Exception {
		String link = ui.pageLink("myPage?param=val#mySection", null);
		Assert.assertEquals("null/myPage.page?param=val#mySection", link);
	}
}

package org.openmrs.ui.framework.page;

import org.openmrs.ui.framework.Model;

/**
 * The shared model for a page, used so a page controller can pass data to a page view, and so
 * multiple fragments in the same page can share data. (This class exists for usage in controller
 * methods with flexible parameter types--it has no methods beyond those in a regular model.)
 */
public class PageModel extends Model {
	
	private static final long serialVersionUID = 1L;
	
}

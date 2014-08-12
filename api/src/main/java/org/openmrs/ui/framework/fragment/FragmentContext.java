package org.openmrs.ui.framework.fragment;

import org.openmrs.ui.framework.Decoratable;
import org.openmrs.ui.framework.Formatter;
import org.openmrs.ui.framework.FragmentIncluder;
import org.openmrs.ui.framework.Model;
import org.openmrs.ui.framework.extension.ExtensionAware;
import org.openmrs.ui.framework.extension.ExtensionManager;
import org.openmrs.ui.framework.page.PageAction;
import org.openmrs.ui.framework.page.PageContext;

/**
 * State and methods involved in taking a fragment request and generating fragment output 
 */
public class FragmentContext implements Decoratable, FragmentIncluder, ExtensionAware {
	
	private PageContext pageContext;
	
	private FragmentContext parentFragmentContext;
	
	private FragmentRequest request;
	
	private Object controller;
	
	private FragmentView view;
	
	private FragmentModel model;
	
	private FragmentRequest decorateWith;
	
	public FragmentContext(FragmentRequest request, PageContext pageContext) {
		this.request = request;
		this.pageContext = pageContext;
		model = new FragmentModel();
	}
	
	public FragmentContext(FragmentRequest request, FragmentContext parentFragmentContext) {
		this(request, parentFragmentContext.getPageContext());
		this.parentFragmentContext = parentFragmentContext;
	}
	
	/**
	 * Returns a merged version of this fragment's model and the shared page model
	 * @return
	 */
	public Model getMergedModel() {
		if (pageContext != null)
			model.mergeAttributes(pageContext.getModel());
		return model;
	}
	
	public String includeFragment(FragmentRequest request) throws PageAction {
		FragmentContext context = new FragmentContext(request, this);
		return pageContext.getFragmentFactory().process(context);
	}
	
	/**
	 * Requests that this fragment be decorated with another one. (The output of this fragment
	 * will be passed to the decorator as "content".)
	 * @param fragmentRequest
	 */
	public void setDecorateWith(FragmentRequest fragmentRequest) {
		this.decorateWith = fragmentRequest;
	}
	
	/**
	 * @return the decorateWith
	 */
	public FragmentRequest getDecorateWith() {
		return decorateWith;
	}
	
	/**
	 * @return the request
	 */
	public FragmentRequest getRequest() {
		return request;
	}
	
	/**
	 * @param request the request to set
	 */
	public void setRequest(FragmentRequest request) {
		this.request = request;
	}
	
	/**
	 * @return the model
	 */
	public FragmentModel getModel() {
		return model;
	}
	
	/**
	 * @param model the model to set
	 */
	public void setModel(FragmentModel model) {
		this.model = model;
	}
	
	/**
	 * @return the pageContext
	 */
	public PageContext getPageContext() {
		return pageContext;
	}
	
	/**
	 * @param pageContext the pageContext to set
	 */
	public void setPageContext(PageContext pageContext) {
		this.pageContext = pageContext;
	}
	
	/**
	 * @return the parentFragmentContext
	 */
	public FragmentContext getParentFragmentContext() {
		return parentFragmentContext;
	}
	
	/**
	 * @param parentFragmentContext the parentFragmentContext to set
	 */
	public void setParentFragmentContext(FragmentContext parentFragmentContext) {
		this.parentFragmentContext = parentFragmentContext;
	}
	
	/**
	 * @return the controller
	 */
	public Object getController() {
		return controller;
	}
	
	/**
	 * @param controller the controller to set
	 */
	public void setController(Object controller) {
		this.controller = controller;
	}
	
	/**
	 * @return the view
	 */
	public FragmentView getView() {
		return view;
	}
	
	/**
	 * @param view the view to set
	 */
	public void setView(FragmentView view) {
		this.view = view;
	}
	
	/**
	 * @see ExtensionAware#getExtensionManager()
	 */
	@Override
	public ExtensionManager getExtensionManager() {
		return pageContext.getExtensionManager();
	}
	
	public int getRequestDepth() {
		if (parentFragmentContext != null)
			return parentFragmentContext.getRequestDepth() + 1;
		else
			return 1;
	}

    public Formatter getFormatter() {
        return pageContext.getFormatter();
    }

}

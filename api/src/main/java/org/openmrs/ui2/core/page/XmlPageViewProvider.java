package org.openmrs.ui2.core.page;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.ui2.core.UiFrameworkException;
import org.openmrs.ui2.core.XmlViewUtil;
import org.openmrs.ui2.core.fragment.CompoundFragmentView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlPageViewProvider implements PageViewProvider {
	
	Log log = LogFactory.getLog(getClass());
	
	private File viewDirectory;
	
	@Override
	public PageView getView(String name) {
		File file = new File(viewDirectory, name + ".xml");
		if (!file.exists())
			return null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(file);
			Element el = document.getDocumentElement();
			if (!el.getTagName().equals("page-definition")) {
				throw new UiFrameworkException("page.xml file must start with a page-definition node");
			}
			// TODO page views should be able to specify their own controllers
			String controller = el.getAttribute("controller");
			CompoundFragmentView compound = XmlViewUtil.parseFragmentView(el.getChildNodes());
			
			return new SingleCompoundFragmentPageView(controller, compound);
		}
		catch (UiFrameworkException ex) {
			throw ex;
		}
		catch (Exception ex) {
			throw new UiFrameworkException("Error parsing fragment xml definition", ex);
		}
	}
	
	/**
	 * @return the viewDirectory
	 */
	public File getViewDirectory() {
		return viewDirectory;
	}
	
	/**
	 * @param viewDirectory the viewDirectory to set
	 */
	public void setViewDirectory(File viewDirectory) {
		this.viewDirectory = viewDirectory;
	}
	
}

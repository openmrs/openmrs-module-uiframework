package org.openmrs.ui2.core.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.openmrs.ui2.core.UiFrameworkException;
import org.openmrs.ui2.core.XmlViewUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlFragmentViewProvider implements FragmentViewProvider {
	
	private File viewDirectory;
	
	@Override
	public FragmentView getView(String name) {
		File file = new File(viewDirectory, name + ".xml");
		if (!file.exists())
			return null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(file);
			Element el = document.getDocumentElement();
			if (!el.getTagName().equals("fragment-definition")) {
				throw new UiFrameworkException("fragment .xml file must start with a fragment-definition node");
			}
			// TODO decide whether fragment views should be able to specify their own controllers
			// String controller = el.getAttribute("controller");
			
			FragmentView compound = XmlViewUtil.parseFragmentView(el.getChildNodes());
			return compound;
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

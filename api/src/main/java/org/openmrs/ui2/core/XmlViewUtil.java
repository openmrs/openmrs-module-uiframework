package org.openmrs.ui2.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.ui2.core.fragment.CompoundFragmentView;
import org.openmrs.ui2.core.fragment.FragmentRequest;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlViewUtil {
	
	// TODO make this return just a FragmentView
	public static CompoundFragmentView parseFragmentView(NodeList nodes) {
		List<FragmentRequest> fragments = parseFragmentList(nodes);
		return new CompoundFragmentView(fragments);
	}
	
	private static FragmentRequest parseFragment(Node node) {
		String fragmentName = node.getAttributes().getNamedItem("name").getNodeValue();
		Map<String, Object> config = parseMap(node);
		return new FragmentRequest(fragmentName, config);
	}
	
	private static Map<String, Object> parseMap(Node node) {
		Map<String, Object> ret = new LinkedHashMap<String, Object>();
		NodeList entryNodes = node.getChildNodes();
		for (int j = 0; j < entryNodes.getLength(); ++j) {
			Node entryNode = entryNodes.item(j);
			if (entryNode.getNodeType() == Node.ELEMENT_NODE)
				parseMapEntry(ret, entryNode);
		}
		return ret;
	}
	
	// entries whose key is repeated are turned into a list
	private static void parseMapEntry(Map<String, Object> config, Node node) {
		String key = node.getNodeName();
		NodeList children = node.getChildNodes();
		if (children.getLength() == 1 && children.item(0).getNodeType() == Node.TEXT_NODE) {
			// a single text value like <decorator>widget</decorator>
			String textValue = children.item(0).getNodeValue().trim();
			addConfigValue(config, key, textValue);
		} else if (isFragmentList(children)) {
			List<FragmentRequest> fragments = parseFragmentList(children);
			if (fragments.size() == 0) {
				throw new UiFrameworkException("configuration property " + key + " has no recognized content");
			} else if (fragments.size() == 1) {
				addConfigValue(config, key, (FragmentRequest) fragments.get(0));
			} else {
				addConfigValue(config, key, fragments);
			}
		} else {
			Map<String, Object> map = parseMap(node);
			addConfigValue(config, key, map);
		}
	}
	
	// skips text nodes (presumed to be whitespace)
	private static List<FragmentRequest> parseFragmentList(NodeList children) {
		List<FragmentRequest> ret = new ArrayList<FragmentRequest>();
		for (int i = 0; i < children.getLength(); ++i) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.TEXT_NODE)
				continue;
			ret.add(parseFragment(child));
		}
		return ret;
	}
	
	private static boolean isFragmentList(NodeList nodes) {
		for (int i = 0; i < nodes.getLength(); ++i) {
			Node node = nodes.item(i);
			if (node.getNodeType() == Node.TEXT_NODE)
				continue;
			if (node.getNodeName().equals("fragment"))
				return true;
		}
		return false;
	}
	
	private static void addConfigValue(Map<String, Object> config, String propertyName, Object value) {
		if (config.containsKey(propertyName)) {
			Object current = config.get(propertyName);
			if (current instanceof Collection) {
				((Collection) current).add(value);
			} else {
				List<Object> list = new ArrayList<Object>();
				list.add(current);
				list.add(value);
				config.put(propertyName, list);
			}
		} else {
			config.put(propertyName, value);
		}
	}
	
}

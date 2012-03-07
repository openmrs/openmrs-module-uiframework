package org.openmrs.ui2.core;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.ui2.core.extension.ExtensionManager;
import org.openmrs.ui2.core.fragment.FragmentRequest;
import org.openmrs.ui2.core.fragment.action.ObjectResult;
import org.openmrs.ui2.core.page.PageAction;
import org.openmrs.ui2.core.page.PageContext;
import org.springframework.core.convert.ConversionService;

/**
 * Utility methods that should be available in view technologies for pages and fragments 
 */
public abstract class UiUtils {
	
	protected PageContext pageContext;
	
	protected ResourceIncluder resourceIncluder;
	
	protected FragmentIncluder fragmentIncluder;
	
	protected Formatter formatter;
	
	protected Messager messager;
	
	protected Decoratable decoratable;
	
	protected ExtensionManager extensionManager;
	
	protected ConversionService conversionService;
	
	public void setPageTitle(String title) {
		pageContext.setPageTitle(title);
	}
	
	public String getPageTitle() {
		return pageContext.getPageTitle();
	}
	
	public void includeCss(String file) {
		resourceIncluder.includeCss(file);
	}
	
	public void includeJavascript(String file) {
		resourceIncluder.includeJavascript(file);
	}
	
	public String thisUrl() {
		return pageContext.getUrl(false);
	}
	
	public String thisUrlWithContextPath() {
		return pageContext.getUrl(true);
	}
	
	public String startForm(String fragment, String action) {
		return startForm(fragment, action, null);
	}
	
	public String startForm(String fragment, String action, Map<String, CharSequence> parameters) {
		StringBuilder ret = new StringBuilder();
		String url = actionLink(fragment, action, parameters);
		ret.append("<form method='post' action='" + url + "'>");
		return ret.toString();
	}
	
	public String endForm() {
		return "</form>";
	}
	
	public String message(String code, Object... args) {
		return messager.message(code, args);
	}
	
	public String includeFragment(String fragmentName) throws PageAction {
		return fragmentIncluder.includeFragment(new FragmentRequest(fragmentName, null));
	}
	
	public String includeFragment(String fragmentName, Map<String, Object> config) throws PageAction {
		return fragmentIncluder.includeFragment(new FragmentRequest(fragmentName, config));
	}
	
	public void decorateWith(String fragmentName) {
		decorateWith(fragmentName, null);
	}
	
	public void decorateWith(String fragmentName, Map<String, Object> config) {
		decoratable.setDecorateWith(new FragmentRequest("decorators/" + fragmentName, config));
	}
	
	public String decorate(String decoratorName, String contents) throws PageAction {
		return decorate(decoratorName, null, contents);
	}
	
	public String decorate(String decoratorName, Map<String, Object> decoratorConfig, String contents) throws PageAction {
		if (decoratorConfig == null)
			decoratorConfig = new HashMap<String, Object>();
		decoratorConfig.put("content", contents);
		return includeFragment("decorators/" + decoratorName, decoratorConfig);
	}
	
	public String contextPath() {
		return WebConstants.CONTEXT_PATH;
	}
	
	public String resourceLink(String path) {
		StringBuilder sb = new StringBuilder();
		sb.append(contextPath());
		if (!path.startsWith("/"))
			sb.append("/");
		sb.append(path);
		return sb.toString();
	}
	
	public String pageLink(String pageName) {
		return pageLink(pageName, null);
	}
	
	/**
	 * Supports query parameters and anchors in the pageName, e.g. "myPage?one=1&two=2#mySection"
	 * @param pageName
	 * @param params
	 * @return
	 * @should handle page name
	 * @should handle page name with question mark and query string
	 * @should handle page name with anchor
	 * @should handle page name with anchor and query string
	 */
	public String pageLink(String pageName, Map<String, Object> params) {
		String extraQuery = null;
		String extraAnchor = null;
		if (pageName.indexOf('?') > 0) {
			String[] pageAndQuery = pageName.split("\\?", 2);
			pageName = pageAndQuery[0];
			extraQuery = pageAndQuery[1];
		} else if (pageName.indexOf('#') > 0) {
			String[] pageAndAnchor = pageName.split("#", 2);
			pageName = pageAndAnchor[0];
			extraAnchor = pageAndAnchor[1];
		}
		String ret = resourceLink(pageName + ".page");
		if (params != null || extraQuery != null) {
			ret += "?";
			if (params != null) {
				for (Map.Entry<String, Object> e : params.entrySet()) {
					CharSequence asChars;
					if (e.getValue() == null)
						asChars = "";
					else if (e.getValue() instanceof CharSequence)
						asChars = (CharSequence) e.getValue();
					else
						asChars = convert(e.getValue(), String.class);
					ret += e.getKey() + "=" + urlEncode(asChars) + "&";
				}
			}
			if (extraQuery != null) {
				ret += extraQuery;
			}
		}
		if (extraAnchor != null)
			ret += "#" + extraAnchor;
		return ret;
	}
	
	public String actionLink(String controllerName, String action) {
		return actionLink(controllerName, action, null);
	}
	
	public String actionLink(String controllerName, String action, Map<String, ?> args) {
		StringBuilder sb = new StringBuilder(resourceLink(controllerName + "/" + action + ".action?"));
		String successUrl = null;
		if (args != null) {
			for (Map.Entry<String, ?> e : args.entrySet()) {
				if (e.getValue() != null) {
					if ("successUrl".equals(e.getKey()))
						successUrl = e.getValue().toString();
					else
						sb.append(e.getKey()).append("=").append(urlEncode(e.getValue().toString())).append("&");
				}
			}
		}
		if (successUrl == null)
			successUrl = thisUrl();
		sb.append("successUrl=" + urlEncode(successUrl));
		return sb.toString();
	}
	
	public Object urlEncode(CharSequence string) {
		// TODO fix this
		return java.net.URLEncoder.encode(string.toString());
	}
	
	public String dateToString(Date date) {
		return new SimpleDateFormat(WebConstants.DATE_FORMAT_TIMESTAMP).format(date);
	}
	
	public String format(Object o) {
		return formatter.format(o);
	}
	
	public String formatTimeAgo(Date date) {
		long diff = System.currentTimeMillis() - date.getTime();
		return message("duration.secondsAgo", (diff / 1000));
	}
	
	public String formatAsText(Object o) {
		return formatter.formatAsText(o);
	}
	
	public static String randomId(String prefix) {
		if (prefix == null)
			prefix = "id";
		return prefix + new Random().nextInt(10000);
	}
	
	public String escapeJs(String input) {
		input = input.replaceAll("\n", "\\\\n");
		input = input.replaceAll("'", "\\\\'");
		input = input.replaceAll("\"", "\\\\\"");
		return input;
	}
	
	public String escapeAttribute(String input) {
		input = input.replaceAll("\"", "&quot;");
		return input;
	}
	
	public String escapeHtml(String input) {
		input = input.replaceAll("<", "&lt;");
		input = input.replaceAll(">", "&gt;");
		return input;
	}
	
	public ExtensionManager getExtensionManager() {
		return extensionManager;
	}
	
	public <T> T convert(Object fromValue, Class<T> clazz) {
		return conversionService.convert(fromValue, clazz);
	}
	
	/**
	 * @param complex
	 * @return a simplified representation of 'complex', suitable for serialization to JSON
	 */
	public SimpleObject simplifyObject(Object complex) {
		return convert(complex, SimpleObject.class);
	}
	
	public SimpleObject[] simplifyCollection(Collection<?> complex) {
		return convert(complex, SimpleObject[].class);
	}
	
	public String toJson(Object object) {
		try {
			if (object instanceof ObjectResult) {
				object = ((ObjectResult) object).getWrapped();
			}
			ObjectMapper mapper = new ObjectMapper();
			StringWriter sw = new StringWriter();
			mapper.writeValue(sw, object);
			return sw.toString();
		}
		catch (Exception ex) {
			throw new UiFrameworkException("Error generating JSON", ex);
		}
	}
	
}

package org.openmrs.ui.framework;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.api.context.Context;
import org.openmrs.module.uiframework.UiFrameworkActivator;
import org.openmrs.ui.framework.extension.ExtensionManager;
import org.openmrs.ui.framework.extension.MapResourceExtension;
import org.openmrs.ui.framework.fragment.FragmentRequest;
import org.openmrs.ui.framework.fragment.action.ObjectResult;
import org.openmrs.ui.framework.page.PageAction;
import org.openmrs.ui.framework.page.PageContext;
import org.openmrs.ui.framework.resource.Resource;
import org.openmrs.ui.framework.util.DateExt;
import org.openmrs.util.PrivilegeConstants;
import org.owasp.encoder.Encode;
import org.springframework.core.convert.ConversionService;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static org.openmrs.util.TimeZoneUtil.toTimezone;

/**
 * Utility methods that should be available in view technologies for pages and fragments
 */
public abstract class UiUtils {
	
	protected Locale locale;
	
	protected PageContext pageContext;
	
	protected ResourceIncluder resourceIncluder;
	
	protected FragmentIncluder fragmentIncluder;
	
	protected Formatter formatter;
	
	protected Messager messager;
	
	protected Decoratable decoratable;
	
	protected ExtensionManager extensionManager;
	
	protected ConversionService conversionService;
	
	public void includeCss(String file) {
		includeCss(null, file, null);
	}
	
	public void includeCss(String providerName, String file) {
		includeCss(providerName, file, null);
	}
	
	public void includeCss(String providerName, String file, Integer priority) {
		includeCss(providerName, file, priority, true);
	}
	
	public void includeCss(String providerName, String file, Integer priority, boolean pathIsRelativeToStyles) {
		MapResourceExtension resource = mapResource(providerName, file);
		
		resourceIncluder.includeResource(new Resource(Resource.CATEGORY_CSS, resource.getProviderId(),
		        (pathIsRelativeToStyles ? "styles/" : "") + resource.getResourceId(), priority));
	}
	
	public void includeJavascript(String file) {
		includeJavascript(null, file, null);
	}
	
	public void includeJavascript(String providerName, String file) {
		includeJavascript(providerName, file, null);
	}
	
	public void includeJavascript(String providerName, String file, Integer priority) {
		includeJavascript(providerName, file, priority, true);
	}
	
	public void includeJavascript(String providerName, String file, Integer priority, boolean pathIsRelativeToScripts) {
		MapResourceExtension resource = mapResource(providerName, file);
		
		resourceIncluder.includeResource(new Resource(Resource.CATEGORY_JS, resource.getProviderId(),
		        (pathIsRelativeToScripts ? "scripts/" : "") + resource.getResourceId(), priority));
	}
	
	/**
	 * Generates HTML resource linkages for all resources requested by the page the fragments on this
	 * request
	 * 
	 * @return the html
	 */
	public String resourceLinks() {
		StringBuilder ret = new StringBuilder();
		
		// Include all Javascript resources
		for (Resource resource : pageContext.uniqueSortedResourcesByCategory(Resource.CATEGORY_JS)) {
			ret.append("<script type=\"text/javascript\" src=\"/");
			ret.append(WebConstants.CONTEXT_PATH);
			ret.append("/ms/uiframework/resource/");
			ret.append(resource.getProviderName() + "/" + resource.getResourcePath());
			ret.append("?cache=" + UiFrameworkActivator.getContextLastRefreshedTime());
			ret.append("\"></script>\n");
		}
		
		// Include all CSS resources
		for (Resource resource : pageContext.uniqueSortedResourcesByCategory(Resource.CATEGORY_CSS)) {
			ret.append("<link rel=\"stylesheet\" href=\"/");
			ret.append(WebConstants.CONTEXT_PATH);
			ret.append("/ms/uiframework/resource/");
			ret.append(resource.getProviderName() + "/" + resource.getResourcePath());
			ret.append("?cache=" + UiFrameworkActivator.getContextLastRefreshedTime());
			ret.append("\" type=\"text/css\"/>\n");
		}
		
		return ret.toString();
	}
	
	public String thisUrl() {
		// TODO determine whether we still need both this method and thisUrlWithContextPath()
		return pageContext.getUrl(true);
	}
	
	public String thisUrlWithContextPath() {
		return pageContext.getUrl(true);
	}
	
	public String startForm(String providerName, String fragment, String action) {
		return startForm(providerName, fragment, action, null);
	}
	
	public String startForm(String providerName, String fragment, String action, Map<String, CharSequence> parameters) {
		StringBuilder ret = new StringBuilder();
		String url = actionLink(providerName, fragment, action, parameters);
		ret.append("<form method='post' action='" + url + "'>");
		return ret.toString();
	}
	
	public String endForm() {
		return "</form>";
	}
	
	public String message(String code, Object... args) {
		if (StringUtils.isBlank(code)) {
			return "";
		}
		return messager.message(code, args);
	}
	
	public String includeFragment(String providerName, String fragmentId) throws PageAction {
		MapResourceExtension resource = mapResource(providerName, fragmentId);
		
		return fragmentIncluder.includeFragment(new FragmentRequest(resource.getProviderId(), resource.getResourceId()));
	}
	
	public String includeFragment(String providerName, String fragmentId, Map<String, Object> config) throws PageAction {
		MapResourceExtension resource = mapResource(providerName, fragmentId);
		
		return fragmentIncluder
		        .includeFragment(new FragmentRequest(resource.getProviderId(), resource.getResourceId(), config));
	}
	
	public void decorateWith(String providerName, String fragmentId) {
		decorateWith(providerName, fragmentId, null);
	}
	
	public void decorateWith(String providerName, String fragmentId, Map<String, Object> config) {
		MapResourceExtension resource = mapResource(providerName, fragmentId);
		
		decoratable.setDecorateWith(
		    new FragmentRequest(resource.getProviderId(), "decorator/" + resource.getResourceId(), config));
	}
	
	private MapResourceExtension mapResource(String providerName, String fragmentId) {
		Collection<MapResourceExtension> extensions = extensionManager.getExtensions(MapResourceExtension.class,
		    UiFrameworkConstants.MAP_RESOURCE_EXTENSION_POINT_ID);
		for (MapResourceExtension extension : extensions) {
			if (providerName.equals(extension.getProviderIdToMap()) && fragmentId.equals(extension.getResourceIdToMap())) {
				return extension;
			}
		}
		
		//no mapping
		MapResourceExtension resource = new MapResourceExtension();
		resource.setResourceId(fragmentId);
		resource.setProviderId(providerName);
		return resource;
	}
	
	public String decorate(String providerName, String decoratorName, String contents) throws PageAction {
		return decorate(providerName, decoratorName, null, contents);
	}
	
	public String decorate(String providerName, String decoratorName, Map<String, Object> decoratorConfig, String contents)
	        throws PageAction {
		if (decoratorConfig == null)
			decoratorConfig = new HashMap<String, Object>();
		decoratorConfig.put("content", contents);
		return includeFragment(providerName, "decorator/" + decoratorName, decoratorConfig);
	}
	
	public String contextPath() {
		return WebConstants.CONTEXT_PATH;
	}
	
	public String resourceLink(String path) {
		return resourceLink(null, path);
	}
	
	public String resourceLink(String providerName, String path) {
		if (providerName == null)
			providerName = "*";
		StringBuilder sb = new StringBuilder();
		sb.append("/").append(contextPath()).append("/ms/uiframework/resource/" + providerName);
		if (!path.startsWith("/"))
			sb.append("/");
		sb.append(path);
		return sb.toString();
	}
	
	public String pageLink(String providerName, String pageName) {
		return pageLink(providerName, pageName, null);
	}
	
	/**
	 * Like #pageLink(String, String, Map), but doesn't add the context path at the beginning.
	 * 
	 * @param providerName
	 * @param pageName
	 * @param params
	 * @return
	 * @since 2.5
	 */
	public String pageLinkWithoutContextPath(String providerName, String pageName, Map<String, Object> params) {
		if (providerName == null) {
			throw new UiFrameworkException("pageLink requires you specify a provider");
		}
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
		String ret = "/" + providerName + "/" + pageName + ".page";
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
	
	/**
	 * Supports query parameters and anchors in the pageName, e.g. "myPage?one=1&two=2#mySection"
	 * 
	 * @param providerName
	 * @param pageName
	 * @param params
	 * @return
	 * @should handle page name
	 * @should handle page name with question mark and query string
	 * @should handle page name with anchor
	 * @should handle page name with anchor and query string
	 */
	public String pageLink(String providerName, String pageName, Map<String, Object> params) {
		return "/" + contextPath() + pageLinkWithoutContextPath(providerName, pageName, params);
	}
	
	public String actionLink(String providerName, String controllerName, String action) {
		return actionLink(providerName, controllerName, action, null);
	}
	
	public String actionLink(String providerName, String controllerName, String action, Map<String, ?> args) {
		if (providerName == null) {
			providerName = "*";
		}
		StringBuilder sb = new StringBuilder(
		        "/" + contextPath() + "/" + providerName + "/" + controllerName + "/" + action + ".action?");
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
	
	public String urlBind(String url, Visit visit) {
		url = url.replace("{{visitId}}", visit.getId().toString());
		url = url.replace("{{visit.id}}", visit.getId().toString());
		url = url.replace("{{visit.visitId}}", visit.getId().toString());
		url = url.replace("{{visit.uuid}}", visit.getUuid());
		url = url.replace("{{visitUuid}}", visit.getUuid());
		url = urlBind(url, visit.getPatient());
		return url;
	}
	
	public String urlBind(String url, Patient patient) {
		url = url.replace("{{patientId}}", patient.getId().toString());
		url = url.replace("{{patient.id}}", patient.getId().toString());
		url = url.replace("{{patient.patientId}}", patient.getId().toString());
		url = url.replace("{{patient.uuid}}", patient.getUuid());
		url = url.replace("{{patientUuid}}", patient.getUuid());
		return url;
	}
	
	public String urlBind(String url, Map<String, Object> bindings) {
		for (Map.Entry<String, Object> binding : bindings.entrySet()) {
			String key = binding.getKey().replace(" ", "");
			url = url.replace("{{" + key + "}}", "" + binding.getValue());
		}
		
		return url;
	}
	
	public String dateToString(Date date) {
		return new SimpleDateFormat(WebConstants.DATE_FORMAT_TIMESTAMP).format(date);
	}
	
	/**
	 * Formats the specified date as a string using ISO 8601 format ("2014-04-25T01:32:21.196+06:00");
	 * this is a "Javascript-friendly" format, good for when you want to render a Date in Groovy to be
	 * parsed by Javascript
	 * 
	 * @param date date to format
	 * @return string version of date formatted as ("2014-04-25T01:32:21.196+0600").
	 */
	public String dateToISOString(Date date) {
		return new SimpleDateFormat(WebConstants.DATE_FORMAT_ISO).format(date);
	}
	
	/**
	 * Formats the specified date to a string using the specified format and drops the time component,
	 * the text 'Today' is returned, if the date matches the previous date or the text Yesterday is
	 * returned if the date matches the previous day of the year.
	 * 
	 * @param date the date to format
	 * @should replace the current date with today text
	 * @should replace the previous date with yesterday
	 */
	public String formatDatePretty(Date date) {
		DateExt dateExt = new DateExt(date);
		Date today = new Date();
		
		if (dateExt.isSameDay(today)) {
			return message("uiframework.today");
		} else if (dateExt.isDayBefore(today)) {
			return message("uiframework.yesterday");
		} else {
			if (BooleanUtils.toBoolean(
			    Context.getAdministrationService().getGlobalProperty(UiFrameworkConstants.GP_TIMEZONE_CONVERSIONS))) {
				return formatDateWithClientTimezone(date);
			}
			return format(dateExt.getDateWithoutTime());
		}
	}
	
	/**
	 * Formats a date with this format: dd MMM yyyy hh:mm a
	 * 
	 * @param date the date to format
	 */
	public String formatDatetimePretty(Date date) {
		if (convertTimezones()) {
			return toTimezone(date, getDatetimeFormat());
		}
		return formatDatePretty(date) + " " + DateFormatUtils.format(date, "hh:mm a", locale);
	}
	
	/**
	 * Formats a time, in the client timezone with the format in the GP_FORMATTER_TIME_FORMAT Change the
	 * date to the client timezone, then only use the time and format it with GP_FORMATTER_TIME_FORMAT
	 * 
	 * @param date the date to be converted to client timezone
	 * @return string version of time with GP_FORMATTER_TIME_FORMAT format ("15:05:00").
	 */
	public String formatTimeWithClientTimezone(Date date) {
		return toTimezone(date, getTimeFormat());
	}
	
	/**
	 * Formats a date, in the client timezone with the format in the GP_FORMATTER_DATE_FORMAT
	 * 
	 * @param date the date to be converted to client timezone
	 * @return string version of date with GP_FORMATTER_DATE_FORMAT format, only date without time.
	 */
	public String formatDateWithClientTimezone(Date date) {
		return toTimezone(date, getDateFormat());
	}
	
	public String format(Object o) {
		return formatter.format(o, getLocale());
	}
	
	public String formatTimeAgo(Date date) {
		long diff = System.currentTimeMillis() - date.getTime();
		return message("duration.secondsAgo", (diff / 1000));
	}
	
	/**
	 * @param o
	 * @return
	 * @deprecated use #format(Object o)
	 */
	@Deprecated
	public String formatAsText(Object o) {
		return formatter.format(o, getLocale());
	}
	
	public static String randomId(String prefix) {
		if (prefix == null)
			prefix = "id";
		return prefix + new Random().nextInt(10000);
	}
	
	/**
	 * Used to make sure that a value can safely compile and be used inside a JavaScript string
	 * 
	 * @param input the input text which may have characters that JavaScript requires to escape
	 * @return text that is fit to be used as a JavaScript string
	 */
	public String escapeJs(String input) {
		if (input == null) {
			return null;
		}
		input = input.replaceAll("\n", "\\\\n");
		input = input.replaceAll("'", "\\\\'");
		input = input.replaceAll("\"", "\\\\\"");
		return input;
	}
	
	/**
	 * Used to make sure that a string can safely be used in a URL
	 * 
	 * @param input the input text which may have characters that are URL unsafe
	 * @return text that is fit to be used in an URL
	 */
	public String encodeForSafeURL(String input) {
		return Encode.forUriComponent(input);
	}
	
	/**
	 * @see org.owasp.encoder.Encode#forJavaScriptAttribute(java.lang.String)
	 */
	public String encodeJavaScriptAttribute(String input) {
		return Encode.forJavaScriptAttribute(input);
	}
	
	/**
	 * @see org.owasp.encoder.Encode#forJavaScript(java.lang.String)
	 */
	public String encodeJavaScript(String input) {
		return Encode.forJavaScript(input);
	}
	
	/**
	 * @see org.owasp.encoder.Encode#forJavaScriptBlock(java.lang.String)
	 */
	public String encodeJavaScriptBlock(String input) {
		return Encode.forJavaScriptBlock(input);
	}
	
	/**
	 * @see org.owasp.encoder.Encode#forJavaScriptSource(java.lang.String)
	 */
	public String encodeJavaScriptSource(String input) {
		return Encode.forJavaScriptSource(input);
	}
	
	/**
	 * @see org.owasp.encoder.Encode#forHtmlContent(java.lang.String)
	 */
	public String encodeHtmlContent(String input) {
		return Encode.forHtmlContent(input);
	}
	
	/**
	 * @see org.owasp.encoder.Encode#forHtmlAttribute(java.lang.String)
	 */
	public String encodeHtmlAttribute(String input) {
		return Encode.forHtmlAttribute(input);
	}
	
	/**
	 * @see org.owasp.encoder.Encode#forHtml(java.lang.String)
	 */
	public String encodeHtml(String input) {
		return Encode.forHtml(input);
	}
	
	public String escapeAttribute(String input) {
		if (input == null) {
			return null;
		}
		input = input.replaceAll("\"", "&quot;");
		return input;
	}
	
	public String escapeHtml(String input) {
		if (input == null) {
			return null;
		}
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
	 * Simplifies a complex object into a simple object suitable for serialization to JSON
	 * 
	 * @param complex the complex object
	 * @return the simplified representation
	 */
	public SimpleObject simplifyObject(Object complex) {
		return convert(complex, SimpleObject.class);
	}
	
	/**
	 * Simplifies a collection of complex objects into an array of simple objects suitable for
	 * serialization to JSON
	 * 
	 * @param complex the collection of complex objects
	 * @return the simplified representation
	 */
	public SimpleObject[] simplifyCollection(Collection<?> complex) {
		if (complex.size() == 0) {
			return new SimpleObject[] {};
		}
		
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
	
	/**
	 * Validates target with validator. If any errors are found, this throws an exception which can be
	 * caught by the UI Framework
	 * 
	 * @param target
	 * @param validator
	 * @param bindingPrefix
	 * @throws BindParamsValidationException
	 */
	public void validate(Object target, Validator validator, String bindingPrefix) {
		BindingResult result = new BeanPropertyBindingResult(target, "");
		validator.validate(target, result);
		if (result.hasErrors())
			throw new BindParamsValidationException(bindingPrefix, result);
	}
	
	/**
	 * @return the configured locale, or Context.getLocale() if none is set
	 */
	public Locale getLocale() {
		return Context.getLocale();
	}
	
	/**
	 * Change the current locale.
	 * 
	 * @param locale The locale.
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	/**
	 * @return the value of the Global Property GP_TIMEZONE_CONVERSIONS
	 */
	public boolean convertTimezones() {
		return BooleanUtils.toBoolean(
		    Context.getAdministrationService().getGlobalProperty(UiFrameworkConstants.GP_TIMEZONE_CONVERSIONS));
		
	}
	
	public String getJSDatetimeFormat() {
		return Context.getAdministrationService().getGlobalProperty(UiFrameworkConstants.GP_FORMATTER_JS_DATETIME_FORMAT);
	}
	
	public String getJSDateFormat() {
		return Context.getAdministrationService().getGlobalProperty(UiFrameworkConstants.GP_FORMATTER_JS_DATE_FORMAT);
	}
	
	public String getDatetimeFormat() {
		return Context.getAdministrationService().getGlobalProperty(UiFrameworkConstants.GP_FORMATTER_DATETIME_FORMAT);
	}
	
	public String getDateFormat() {
		return Context.getAdministrationService().getGlobalProperty(UiFrameworkConstants.GP_FORMATTER_DATE_FORMAT);
	}
	
	public String getTimeFormat() {
		return Context.getAdministrationService().getGlobalProperty(UiFrameworkConstants.GP_FORMATTER_TIME_FORMAT);
	}
	
	/**
	 * @return the value of the User Property clientTimezone, that indicates the client timezone
	 */
	public String getClientTimezoneProperty() {
		return Context.getAuthenticatedUser().getUserProperty("clientTimezone");
	}
	
	/**
	 * Change the user property clientTimezone, that have the value of the user timezone.
	 * 
	 * @param clientTimezone The client timezone.
	 */
	public void setClientTimezoneProperty(String clientTimezone) {
		try {
			Context.addProxyPrivilege(PrivilegeConstants.EDIT_USERS);
			Context.getUserService().setUserProperty(Context.getAuthenticatedUser(), "clientTimezone", clientTimezone);
		}
		finally {
			Context.removeProxyPrivilege(PrivilegeConstants.EDIT_USERS);
		}
	}
	
}

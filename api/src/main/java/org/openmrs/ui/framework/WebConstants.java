package org.openmrs.ui.framework;

public class WebConstants {
	
	public static String CONTEXT_PATH = null;
	
	public static final String DATE_FORMAT_TIMESTAMP = "yyyy-MM-dd HH:mm:ss.SSS";
	
	public static final String DATE_FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";
	
	public static final String DATE_FORMAT_DATE = "yyyy-MM-dd";
	
	public static final String DATE_FORMAT_ISO = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ";
	
	// TODO BW: This file can probably be cleaned out, most are specific to the old webapp
	
	public static final String SESSION_SESSION_ATTRIBUTE = "__openmrs_session";
	
	public static final String SESSION_ROLE_ATTRIBUTE = "__openmrs_role";
	
	public static final String SESSION_LOCATION_ATTRIBUTE = "__openmrs_location";
	
	public static final String INIT_REQ_UNIQUE_ID = "__INIT_REQ_UNIQUE_ID__";
	
	public static final String OPENMRS_USER_CONTEXT_HTTPSESSION_ATTR = "__openmrs_user_context";
	
	public static final String OPENMRS_MSG_ATTR = "openmrs_msg";
	
	public static final String OPENMRS_MSG_ARGS = "openmrs_msg_arguments";
	
	public static final String OPENMRS_ERROR_ATTR = "openmrs_error";
	
	public static final String OPENMRS_ERROR_ARGS = "openmrs_error_arguments";
	
	public static final String OPENMRS_CLIENT_IP_HTTPSESSION_ATTR = "__openmrs_client_ip";
	
	public static final String OPENMRS_LOGIN_REDIRECT_HTTPSESSION_ATTR = "__openmrs_login_redirect";
	
	/**
	 * Global property name for the number of times one IP can fail at logging in before being locked
	 * out. A value of 0 for this property means no IP lockout checks.
	 * 
	 * @see org.openmrs.ui2.servlet.LoginServlet
	 */
	public static String GP_ALLOWED_LOGIN_ATTEMPTS_PER_IP = "security.loginAttemptsAllowedPerIP";
	
	//should be replaced with the contextPath by the org.openmrs.ui2.webapp.Listener at start up 
	public static String WEBAPP_NAME = "openmrs2";
	
	public static String DEFAULT_USER_DEFINED_TEMPLATE_TYPE = "groovy";
	
}

package org.openmrs.ui.framework.formatter;

import org.openmrs.ui.framework.Formatter;

/**
 * Implementations of this interface support letting modules and implementations define custom
 * formatters that override those provided by the UI Framework in
 * org.openmrs.ui.framework.FormatterImpl.
 * {@link org.openmrs.ui.framework.formatter.FormatterService} will go through all Spring beans that
 * implement this interface and have the highest-precedence one for any class create a formatter to
 * be used when that class is formatted via any UiUtils instance.
 */
public interface FormatterFactory {
	
	/**
	 * @return the fully-qualified classname that a formatter created by this instance should be used to
	 *         format.
	 */
	String getForClass();
	
	/**
	 * Lower numbers are higher precedence, as in org.springframework.core.Ordered#HIGHEST_PRECEDENCE
	 * and org.springframework.core.Ordered#LOWEST_PRECEDENCE
	 * 
	 * @return
	 */
	Integer getOrder();
	
	/**
	 * @param service
	 * @return
	 * @throws Exception
	 */
	Formatter createFormatter(FormatterService service) throws Exception;
	
}

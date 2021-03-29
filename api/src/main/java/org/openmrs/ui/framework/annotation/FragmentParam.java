package org.openmrs.ui.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ValueConstants;

/**
 * Equivalent of {@link RequestParam}, but used in fragment controller methods, where we are
 * fetching parameters from the fragment config, not from the underlying web request
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface FragmentParam {
	
	/**
	 * The name of the request parameter to bind to.
	 */
	String value() default "";
	
	/**
	 * Whether the parameter is required. Default is <code>true</code>.
	 */
	boolean required() default true;
	
	/**
	 * The default value to use as a fallback. Supplying a default value implicitly sets
	 * {@link #required()} to false.
	 */
	String defaultValue() default ValueConstants.DEFAULT_NONE;
}

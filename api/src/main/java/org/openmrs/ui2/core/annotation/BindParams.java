package org.openmrs.ui2.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the given controller or fragment action method parameter should have parameters bound to
 * it from the underlying web request.
 * If you specify a non-empty value, then only parameters with the prefix "${ value }." will be bound (after
 * stripping the prefix) whereas if value is empty, all values are bound.
 * (Parameters that don't have the same name as a settable property on the annotated parameter will not be
 * bound.)
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface BindParams {
	
	/**
	 * The root name of the request parameters to bind to, or blank to bind all of them.
	 */
	String value() default "";
	
}

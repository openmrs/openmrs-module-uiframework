package org.openmrs.ui.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.validation.Validator;

/**
 * Indicates that the annotated controller or action method parameter should be validated.
 * If a Class is specified as the value, it will be used, otherwise the OpenMRS API's ValidateUtil method
 * will be used. 
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Validate {
	
	Class<? extends Validator> value() default Validator.class;
	
}

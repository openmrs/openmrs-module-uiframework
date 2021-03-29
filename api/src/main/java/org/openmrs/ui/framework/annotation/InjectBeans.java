package org.openmrs.ui.framework.annotation;

/**
 *
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a parameter to a page/fragment controller should have a new instance constructed,
 * and any properties annotated with @Autowired should have spring beans injected.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectBeans {}

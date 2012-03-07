package org.openmrs.ui2.core;

import org.openmrs.ui2.core.extension.ExtensionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.convert.ConversionService;

/**
 * An implementation of UiUtils with very limited functionality.
 * (Typically this should be used to generate links to pages and fragments, and messages.)  
 */
public class BasicUiUtils extends UiUtils implements ApplicationListener<ContextRefreshedEvent> {
	
	@Autowired
	MessageSource messageSource;
	
	@Autowired
	ExtensionManager extensionManagerFromSpring;
	
	/*
	 * We need to make this bean an ApplicationListener and fetch the conversionService after Spring is
	 * loaded, to allow this object to be injected into converter implementations. Otherwise we would
	 * do the simpler: @Autowired ConversionService conversionServiceFromSpring; 
	 */

	public BasicUiUtils() {
	}
	
	public void init() {
		messager = new MessagerImpl(null, messageSource);
		extensionManager = extensionManagerFromSpring;
	}
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		conversionService = (ConversionService) event.getApplicationContext().getBean("conversionService");
	}
	
}

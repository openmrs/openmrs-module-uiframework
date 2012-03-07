package org.openmrs.ui2.core.fragment;

import org.openmrs.api.context.Context;
import org.openmrs.ui2.core.FormatterImpl;
import org.openmrs.ui2.core.MessagerImpl;
import org.openmrs.ui2.core.UiUtils;
import org.openmrs.ui2.core.extension.ExtensionManager;
import org.springframework.context.MessageSource;
import org.springframework.core.convert.ConversionService;

public class FragmentActionUiUtils extends UiUtils {
	
	public FragmentActionUiUtils(MessageSource messageSource, ExtensionManager extensionManager,
	    ConversionService conversionService) {
		this.formatter = new FormatterImpl();
		this.messager = new MessagerImpl(Context.getLocale(), messageSource);
		this.extensionManager = extensionManager;
		this.conversionService = conversionService;
		// no fragmentIncluder 
		// no decoratable
		// no resourceIncluder
	}
	
}

package org.openmrs.ui.framework.fragment;

import org.openmrs.api.context.Context;
import org.openmrs.ui.framework.MessagerImpl;
import org.openmrs.ui.framework.UiUtils;
import org.openmrs.ui.framework.extension.ExtensionManager;
import org.openmrs.ui.framework.formatter.FormatterService;
import org.springframework.context.MessageSource;
import org.springframework.core.convert.ConversionService;

public class FragmentActionUiUtils extends UiUtils {
	
	public FragmentActionUiUtils(MessageSource messageSource, ExtensionManager extensionManager,
	    ConversionService conversionService, FormatterService formatterService) {
        if (formatterService != null) {
            this.formatter = formatterService.getFormatter();
        }
        this.messager = new MessagerImpl(Context.getLocale(), messageSource);
        this.extensionManager = extensionManager;
        this.conversionService = conversionService;
        // no fragmentIncluder
        // no decoratable
        // no resourceIncluder
    }

}

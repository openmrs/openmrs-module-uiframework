package org.openmrs.ui2.core;

import java.util.Locale;

import org.openmrs.api.context.Context;
import org.springframework.context.MessageSource;

public class MessagerImpl implements Messager {
	
	private Locale locale;
	
	private MessageSource messageSource;
	
	public MessagerImpl(Locale locale, MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	@Override
	public String message(String code, Object... args) {
		Locale locale = this.locale != null ? this.locale : Context.getLocale();
		return messageSource.getMessage(code, args, locale);
	}
	
}

package org.openmrs.ui.framework.formatter;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.ui.framework.Formatter;
import org.openmrs.ui.framework.FormatterImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This service builds and configures a {@link Formatter}, which can be used in other parts of the
 * UI Framework. It supports letting implementations and modules define instances of
 * {@link FormatterFactory} to do custom formatting of specific classes.
 */
@Component("uiframework.formatterService")
public class FormatterService {
	
	private Handlebars handlebars;
	
	private MessageSource messageSource;
	
	private FormatterImpl formatter;
	
	@Autowired
	MessageSourceService messageSourceService;
	
	@Autowired
	@Qualifier("adminService")
	AdministrationService administrationService;
	
	@Autowired(required = false)
	List<FormatterFactory> classFormatters;
	
	public FormatterService() {
		handlebars = new Handlebars();
		handlebars.registerHelper("format", new Helper<Object>() {
			
			@Override
			public CharSequence apply(Object o, Options options) throws IOException {
				return formatter.format(o, Context.getLocale());
			}
		});
		handlebars.registerHelper("message", new Helper<String>() {
			
			@Override
			public CharSequence apply(String input, Options options) throws IOException {
				return messageSource.getMessage(input, null, Context.getLocale());
			}
		});
	}
	
	public Formatter getFormatter() {
		if (formatter == null) {
			createFormatterInstance();
		}
		return formatter;
	}
	
	private synchronized void createFormatterInstance() {
		if (formatter == null) {
			if (messageSource == null) {
				messageSource = messageSourceService.getActiveMessageSource();
			}
			formatter = new FormatterImpl(messageSource, administrationService);
			if (classFormatters != null) {
				// Per Spring's Ordered interface HIGHEST_PRECEDENCE is the lowest number, so we sort descending so the
				// lowest number is added first
				Collections.sort(classFormatters, new Comparator<FormatterFactory>() {
					
					@Override
					public int compare(FormatterFactory left, FormatterFactory right) {
						return right.getOrder().compareTo(left.getOrder());
					}
				});
				for (FormatterFactory classFormatter : classFormatters) {
					try {
						formatter.registerClassFormatter(classFormatter.getForClass(), classFormatter.createFormatter(this));
					}
					catch (Exception e) {
						throw new IllegalStateException("Error creating formatter for " + classFormatter.getForClass(), e);
					}
				}
			}
		}
	}
	
	/**
	 * Supports helpers for: * {{ format visitType }} -> recursively formats a property * {{ message
	 * 'some.code' }} -> looks up a code in messages.properties
	 * 
	 * @param template
	 * @return
	 * @throws IOException
	 */
	public Template compileHandlebarsTemplate(String template) throws IOException {
		return handlebars.compileInline(template);
	}
	
	/**
	 * Used for testing. Real usage is to instantiate a Spring bean implementing FormatterFactory,
	 * and it will be autowired.
	 * 
	 * @param classFormatter
	 */
	public synchronized void addClassFormatter(FormatterFactory classFormatter) {
		if (classFormatters == null) {
			classFormatters = new ArrayList<FormatterFactory>();
		}
		classFormatters.add(classFormatter);
	}
	
	/**
	 * Used for testing. Normally this is read from MessageSourceService.activeMessageSource
	 * 
	 * @param messageSource
	 */
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
}

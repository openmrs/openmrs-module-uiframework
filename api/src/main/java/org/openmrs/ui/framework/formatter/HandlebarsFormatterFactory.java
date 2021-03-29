package org.openmrs.ui.framework.formatter;

import com.github.jknack.handlebars.Template;
import org.openmrs.ui.framework.Formatter;

import java.io.IOException;
import java.util.Locale;

/**
 * Compiles template into a Handlebars template, with helpers as documented in
 * {@link FormatterService#compileHandlebarsTemplate(String)}
 */
public class HandlebarsFormatterFactory extends TemplateFormatterFactory {
	
	@Override
	public Formatter createFormatter(FormatterService service) throws IOException {
		return new HandlebarsFormatter(service.compileHandlebarsTemplate(template));
	}
	
	private class HandlebarsFormatter implements Formatter {
		
		private Template template;
		
		public HandlebarsFormatter(Template template) {
			this.template = template;
		}
		
		@Override
		public String format(Object o, Locale locale) {
			try {
				return template.apply(o);
			}
			catch (IOException e) {
				throw new IllegalStateException("Error applying handlebars template for " + getForClass(), e);
			}
		}
	}
}

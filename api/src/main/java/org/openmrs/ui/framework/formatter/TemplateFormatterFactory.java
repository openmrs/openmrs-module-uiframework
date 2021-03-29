package org.openmrs.ui.framework.formatter;

/**
 * Helper base class. Implementations should just override FormatterFactory.createFormatter and
 * build a formatter out of the text template.
 */
public abstract class TemplateFormatterFactory implements FormatterFactory {
	
	private String forClass;
	
	private Integer order = 0;
	
	protected String template;
	
	public String getForClass() {
		return forClass;
	}
	
	public void setForClass(String forClass) {
		this.forClass = forClass;
	}
	
	public Integer getOrder() {
		return order;
	}
	
	public void setOrder(Integer order) {
		this.order = order;
	}
	
	public String getTemplate() {
		return template;
	}
	
	public void setTemplate(String template) {
		this.template = template;
	}
	
}

package org.openmrs.ui.framework.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Order;
import org.openmrs.api.OrderService;
import org.openmrs.api.PatientService;
import org.openmrs.ui.framework.converter.util.ConversionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;

public class StringToOrderConverter implements Converter<String, Order> {
	
	@Autowired
	@Qualifier("orderService")
	private OrderService service;
	
	@Override
	public Order convert(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		} else if (ConversionUtil.onlyDigits(id)) {
			return service.getOrder(Integer.valueOf(id));
		} else {
			return service.getOrderByUuid(id);
		}
	}
}

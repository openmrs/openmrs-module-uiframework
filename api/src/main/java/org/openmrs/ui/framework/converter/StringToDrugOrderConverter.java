package org.openmrs.ui.framework.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.DrugOrder;
import org.openmrs.api.OrderService;
import org.openmrs.ui.framework.converter.util.ConversionUtil;
import org.openmrs.ui.framework.converter.StringToDrugOrderConverterHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.converter.Converter;

public class StringToDrugOrderConverter implements Converter<String, DrugOrder> {

    @Autowired
    @Qualifier("orderService")
    private OrderService service;

    @Override
    public DrugOrder convert(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        } else if (ConversionUtil.onlyDigits(id)) {
            return StringToDrugOrderConverterHelper.getDrugOrderDigits(service, id);
        } else {
            return StringToDrugOrderConverterHelper.getDrugOrderUuid(service, id);
        }
    }
}

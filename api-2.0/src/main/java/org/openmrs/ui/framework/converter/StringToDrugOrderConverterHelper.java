package org.openmrs.ui.framework.converter;

import org.openmrs.DrugOrder;
import org.openmrs.api.OrderService;
import org.springframework.stereotype.Component;
import org.openmrs.annotation.OpenmrsProfile;

@Component
@OpenmrsProfile(openmrsPlatformVersion = "2.0.0")
public class StringToDrugOrderConverterHelper {

    public static DrugOrder getDrugOrderDigits(OrderService service, String id) {
        return (DrugOrder) service.getOrder(Integer.valueOf(id));
    }
    
    public static DrugOrder getDrugOrderUuid(OrderService service, String id) {
        return (DrugOrder) service.getOrderByUuid(id);
    }
}
=======
package org.openmrs.ui.framework.converter;

import org.openmrs.DrugOrder;
import org.openmrs.api.OrderService;

public class StringToDrugOrderConverterHelper {

    public static DrugOrder getDrugOrderDigits(OrderService service, String id) {
        return (DrugOrder) service.getOrder(Integer.valueOf(id));
    }
    
    public static DrugOrder getDrugOrderUuid(OrderService service, String id) {
        return (DrugOrder) service.getOrderByUuid(id);
    }
}

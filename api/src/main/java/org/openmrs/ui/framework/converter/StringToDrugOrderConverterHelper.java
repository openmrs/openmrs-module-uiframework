package org.openmrs.ui.framework.converter;

import java.lang.reflect.Method;

import org.openmrs.DrugOrder;
import org.openmrs.api.OrderService;
import org.springframework.stereotype.Component;
import org.openmrs.annotation.OpenmrsProfile;

@Component
public class StringToDrugOrderConverterHelper {

    public static DrugOrder getDrugOrderDigits(OrderService service, String id) {
        try {
            // need to use reflection since the getOrder method has different parameters in different versions of openmrs
            Method getOrder = OrderService.class.getDeclaredMethod("getOrder");
            if (getOrder.getParameterCount() == 1) {
                return (DrugOrder) getOrder.invoke(service, Integer.valueOf(id));
            }
            else if (getOrder.getParameterCount() == 2) {
                return (DrugOrder) getOrder.invoke(service, Integer.valueOf(id), DrugOrder.class);
            }
            return null;
        }
        catch (Exception e) {
        	// fall through to just returning null if error
        }
        
        return null;
    }
    
    public static DrugOrder getDrugOrderUuid(OrderService service, String id) {
        return (DrugOrder) service.getOrderByUuid(id);
    }
}

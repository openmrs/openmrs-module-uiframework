package org.openmrs.ui.framework;

import java.util.Comparator;

import org.openmrs.activelist.ActiveListItem;
import org.openmrs.util.OpenmrsUtil;

public class ActiveListItemByStartDateComparator implements Comparator<ActiveListItem> {
	
	@Override
	public int compare(ActiveListItem o1, ActiveListItem o2) {
		return OpenmrsUtil.compare(o1.getStartDate(), o2.getStartDate());
	}
}

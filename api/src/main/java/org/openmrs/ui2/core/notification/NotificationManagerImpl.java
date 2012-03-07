package org.openmrs.ui2.core.notification;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.User;
import org.springframework.beans.factory.annotation.Autowired;

public class NotificationManagerImpl implements NotificationManager {
	
	@Autowired(required = false)
	List<NotificationProvider> notificationProviders;
	
	@Override
	public List<Notification> getAllNotifications(User forUser) {
		List<Notification> ret = new ArrayList<Notification>();
		if (notificationProviders != null) {
			for (NotificationProvider provider : notificationProviders) {
				ret.addAll(provider.getNotifications(forUser));
			}
		}
		return ret;
	}
	
}

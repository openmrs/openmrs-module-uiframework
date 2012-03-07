package org.openmrs.ui2.core.notification;

import java.util.List;

import org.openmrs.User;

public interface NotificationManager {
	
	public List<Notification> getAllNotifications(User forUser);
	
}

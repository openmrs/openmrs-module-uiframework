package org.openmrs.ui.framework.notification;

import java.util.List;

import org.openmrs.User;

public interface NotificationManager {
	
	public List<Notification> getAllNotifications(User forUser);
	
}

package org.openmrs.ui.framework.notification;

import java.util.Collection;

import org.openmrs.User;

public interface NotificationProvider {
	
	Collection<? extends Notification> getNotifications(User forUser);
	
}

package org.openmrs.ui2.core.notification;

import java.util.Collection;

import org.openmrs.User;

public interface NotificationProvider {
	
	Collection<? extends Notification> getNotifications(User forUser);
	
}

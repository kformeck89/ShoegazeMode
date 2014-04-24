package com.example.onthego.Notifications;

import android.app.NotificationManager;
import android.content.Context;

public abstract class BaseNotification {
	protected Context context;
	protected NotificationManager notificationManager;
	
	protected abstract int getId();
	public void startNotification(Context context, int type) {
		this.context = context;
		if (notificationManager == null) {
			notificationManager = (NotificationManager)context.getSystemService(
					Context.NOTIFICATION_SERVICE);
		}
		notificationManager.cancel(getId());
	}
	public void cancelNotification() {
		if (notificationManager != null) {
			notificationManager.cancel(getId());
		}
		if (context != null) {
			context = null;
		}
	}
}

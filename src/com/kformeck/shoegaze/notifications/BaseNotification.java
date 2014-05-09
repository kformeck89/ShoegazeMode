package com.kformeck.shoegaze.notifications;

import com.kformeck.shoegaze.MainActivity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;

public abstract class BaseNotification {
	protected final int ID = 81333378;
	protected Context context;
	protected NotificationManager notificationManager;
	
	protected PendingIntent getContentIntent() {
		Intent mainActivityIntent = new Intent(context, MainActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(mainActivityIntent);	
		
		return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
	}
	public void startNotification(Context context, int type) {
		this.context = context;
		if (notificationManager == null) {
			notificationManager = (NotificationManager)context.getSystemService(
					Context.NOTIFICATION_SERVICE);
		}
		notificationManager.cancel(ID);
	}
	public void cancelNotification() {
		if (notificationManager != null) {
			notificationManager.cancel(ID);
		}
		if (context != null) {
			context = null;
		}
	}
}

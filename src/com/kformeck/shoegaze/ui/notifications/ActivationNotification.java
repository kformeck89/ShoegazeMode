package com.kformeck.shoegaze.ui.notifications;

import com.example.onthego.R;
import com.kformeck.shoegaze.utilities.ShoegazeUtils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;

public class ActivationNotification extends BaseNotification {
	private static ActivationNotification instance;
	
	public static ActivationNotification getInstance() {
		if (instance == null) {
			instance = new ActivationNotification();
		}
		return instance;
	}
	private ActivationNotification() { }
	
	@Override
	public void startNotification(Context context, int type) {
		super.startNotification(context, type);	
		
		Notification.Builder builder = new Notification.Builder(context);
		builder.setTicker("Shoegaze Enabled")
			   .setContentTitle("Shoegaze Mode")
			   .setSmallIcon(R.drawable.ic_notification_stopped)
			   .setWhen(System.currentTimeMillis())
			   .setOngoing(type != 1)
			   .setPriority(Notification.PRIORITY_MAX)
			   .setContentIntent(getContentIntent());
		if (type == 1) {
			PendingIntent restartIntent = ShoegazeUtils.makeServiceIntent(
					context, context.getResources().getString(R.string.action_restart));
			builder.addAction(R.drawable.ic_launcher, "Restart", restartIntent);
		} else {
			builder.addAction(R.drawable.ic_start, "Start", 
					ShoegazeUtils.makeServiceIntent(
							context,
							context.getResources().getString(R.string.action_start)));
		}
		notificationManager.notify(ID, builder.build());
	}
}

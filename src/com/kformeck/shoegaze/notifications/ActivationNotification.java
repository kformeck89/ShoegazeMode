package com.kformeck.shoegaze.notifications;

import com.example.onthego.R;
import com.kformeck.shoegaze.receivers.ShoegazeReceiver;
import com.kformeck.shoegaze.utilities.ShoegazeUtils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;

public class ActivationNotification extends BaseNotification {
	private static ActivationNotification instance;
	private static final int ID = 81333398;
	
	public static ActivationNotification getInstance() {
		if (instance == null) {
			instance = new ActivationNotification();
		}
		return instance;
	}
	private ActivationNotification() { }
	
	@Override
	protected int getId() {
		return ID;
	}
	@Override
	public void startNotification(Context context, int type) {
		super.startNotification(context, type);	
		
		Notification.Builder builder = new Notification.Builder(context);
		
		// TODO: fix ticker icon
		builder.setTicker("Shoegaze Enabled")
			   .setContentTitle("Shoegaze Mode")
			   .setSmallIcon(R.drawable.ic_launcher)
			   .setWhen(System.currentTimeMillis())
			   .setOngoing(type != 1)
			   .setPriority(Notification.PRIORITY_MAX)
			   .setContentIntent(getContentIntent());
		if (type == 1) {
			PendingIntent restartIntent = ShoegazeUtils.makeServiceIntent(
					context, ShoegazeReceiver.ACTION_RESTART);
			builder.addAction(R.drawable.ic_launcher, "Restart", restartIntent);
		} else {
			builder.addAction(R.drawable.ic_start, "Start", 
					ShoegazeUtils.makeServiceIntent(context, ShoegazeReceiver.ACTION_START));
		}
		notificationManager.notify(ID, builder.build());
	}
}

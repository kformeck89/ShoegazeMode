package com.kformeck.shoegaze.notifications;

import com.example.onthego.R;
import com.kformeck.shoegaze.MainActivity;
import com.kformeck.shoegaze.ShoegazeUtilities;
import com.kformeck.shoegaze.receivers.ShoegazeReceiver;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;

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
		
		Intent mainActivityIntent = new Intent(context, MainActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(mainActivityIntent);		
		
		Notification.Builder builder = new Notification.Builder(context);
		
		// TODO: fix ticker icon
		builder.setTicker("Shoegaze Enabled")
			   .setContentTitle("Shoegaze Mode")
			   .setSmallIcon(R.drawable.ic_launcher)
			   .setWhen(System.currentTimeMillis())
			   .setOngoing(type != 1)
			   .setPriority(Notification.PRIORITY_MAX)
			   .setContentIntent(
					   stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT));
		if (type == 1) {
			PendingIntent restartIntent = ShoegazeUtilities.makeServiceIntent(
					context, ShoegazeReceiver.ACTION_RESTART);
			builder.addAction(R.drawable.ic_launcher, "Restart", restartIntent);
		} else {
			PendingIntent startIntent = ShoegazeUtilities.makeServiceIntent(
					context, ShoegazeReceiver.ACTION_START);
			PendingIntent optionsIntent = ShoegazeUtilities.makeServiceIntent(
					context, ShoegazeReceiver.ACTION_TOGGLE_OPTIONS);
			builder.addAction(R.drawable.ic_start, "Start", startIntent)
			       .addAction(R.drawable.ic_options, "Options", optionsIntent);
		}
		notificationManager.notify(ID, builder.build());
	}
}

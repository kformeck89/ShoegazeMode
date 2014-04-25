package com.example.onthego.Notifications;

import com.example.onthego.ShoegazeReceiver;
import com.example.onthego.ShoegazeUtilities;
import com.example.onthego.R;
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
			   .setOngoing(type != 1);
		if (type == 1) {
			PendingIntent restartIntent = ShoegazeUtilities.makeServiceIntent(
					context, ShoegazeReceiver.ACTION_RESTART);
			builder.addAction(R.drawable.ic_launcher, "Restart", restartIntent);
		} else {
			PendingIntent startIntent = ShoegazeUtilities.makeServiceIntent(
					context, ShoegazeReceiver.ACTION_START);
			
			// TODO: make this go to on_the_fly_settings
			PendingIntent optionsIntent = ShoegazeUtilities.makeServiceIntent(
					context, ShoegazeReceiver.ACTION_TOGGLE_OPTIONS);
			builder.addAction(R.drawable.ic_start, "Start", startIntent)
			       .addAction(R.drawable.ic_options, "Options", optionsIntent);
		}
		notificationManager.notify(ID, builder.build());
	}
}

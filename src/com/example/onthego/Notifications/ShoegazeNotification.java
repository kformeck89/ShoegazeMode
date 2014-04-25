package com.example.onthego.Notifications;

import com.example.onthego.ShoegazeReceiver;
import com.example.onthego.ShoegazeUtilities;
import com.example.onthego.R;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;

public class ShoegazeNotification extends BaseNotification {
	private static ShoegazeNotification instance;
	private static final int ID = 81333378;
	
	public static ShoegazeNotification getInstance() {
		if (instance == null) {
			instance = new ShoegazeNotification();
		}
		return instance;
	}
	private ShoegazeNotification() { }
	
	@Override
	protected int getId() {
		return ID;
	}
	@Override
	public void startNotification(Context context, int type) {
		super.startNotification(context, type);
		
		Notification.Builder builder = new Notification.Builder(context);
		builder.setTicker("Shoegaze Mode Active")
			   .setContentTitle("Shoegazing...")
			   .setSmallIcon(R.drawable.ic_launcher)
			   .setWhen(System.currentTimeMillis())
			   .setOngoing(type != 1);
		if (type == 1) {
			PendingIntent restartIntent = ShoegazeUtilities.makeServiceIntent(context, ShoegazeReceiver.ACTION_RESTART);
			builder.addAction(R.drawable.ic_launcher, "Restarting", restartIntent);
		} else {
			PendingIntent stopIntent = ShoegazeUtilities.makeServiceIntent(context, ShoegazeReceiver.ACTION_STOP);
			PendingIntent optionsIntent = ShoegazeUtilities.makeServiceIntent(context, ShoegazeReceiver.ACTION_TOGGLE_OPTIONS);
			builder.addAction(R.drawable.ic_stop, "Stop", stopIntent)
			       .addAction(R.drawable.ic_options, "Options", optionsIntent);
		}
		notificationManager.notify(ID, builder.build());
	}
}

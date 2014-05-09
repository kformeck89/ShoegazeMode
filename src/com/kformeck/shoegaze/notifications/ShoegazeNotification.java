package com.kformeck.shoegaze.notifications;

import com.example.onthego.R;
import com.kformeck.shoegaze.receivers.ShoegazeReceiver;
import com.kformeck.shoegaze.utilities.ShoegazeUtils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class ShoegazeNotification extends BaseNotification {
	private static ShoegazeNotification instance;
	private boolean isShoegazing;
	
	public static ShoegazeNotification getInstance() {
		if (instance == null) {
			instance = new ShoegazeNotification();
		}
		return instance;
	}
	private ShoegazeNotification() { isShoegazing = false; }
	
	@Override
	public void startNotification(Context context, int type) {
		super.startNotification(context, type);
		
		Notification.Builder builder = new Notification.Builder(context);
		builder.setTicker("Shoegaze Mode Active")
			   .setContentTitle("Shoegazing...")
			   .setSmallIcon(R.drawable.ic_launcher)
			   .setWhen(System.currentTimeMillis())
			   .setOngoing(type != 1)
			   .setPriority(Notification.PRIORITY_MAX)
			   .setContentIntent(getContentIntent());
		if (type == 1) {
			PendingIntent restartIntent = ShoegazeUtils.makeServiceIntent(context, ShoegazeReceiver.ACTION_RESTART);
			builder.addAction(R.drawable.ic_launcher, "Restarting", restartIntent);
		} else {			
			builder.addAction(
						R.drawable.ic_stop, "",
						ShoegazeUtils.makeServiceIntent(
								context, ShoegazeReceiver.ACTION_STOP))
			       .addAction(
			    		   R.drawable.ic_switch_camera, "",
			    		   PendingIntent.getBroadcast(
			    				   context, 0, 
			    				   new Intent(ShoegazeReceiver.ACTION_TOGGLE_CAMERA_MODE), 
			    				   PendingIntent.FLAG_CANCEL_CURRENT))
			       .addAction(
			    		   R.drawable.ic_options, "", 
			    		   ShoegazeUtils.makeServiceIntent(
			   					context, ShoegazeReceiver.ACTION_TOGGLE_OPTIONS));
		}
		notificationManager.notify(ID, builder.build());
		isShoegazing = true;
	}
	@Override
	public void cancelNotification() {
		super.cancelNotification();
		isShoegazing = false;
	}
	
	public boolean isShoegazing() {
		return isShoegazing;
	}
}

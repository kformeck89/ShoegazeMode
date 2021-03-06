package com.kformeck.shoegaze.ui.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.onthego.R;
import com.kformeck.shoegaze.utilities.ShoegazeUtils;

public class ShoegazeNotification extends BaseNotification {
	private static ShoegazeNotification instance;
	private boolean isShoegazing;
	private boolean isFlashOn;
	private Intent flashIntent;
	
	public static ShoegazeNotification getInstance() {
		if (instance == null) {
			instance = new ShoegazeNotification();
		}
		return instance;
	}
	private ShoegazeNotification() { 
		isShoegazing = false;
		isFlashOn = true;
	}
	
	private void instantiateFlashIntent() {
		flashIntent = new Intent(context.getResources().getString(
				R.string.action_toggle_flash));
		flashIntent.putExtra(
				context.getResources().getString(R.string.extra_flash_is_on), isFlashOn);
	}
	@Override
	public void startNotification(Context context, int type) {
		super.startNotification(context, type);		
		Notification.Builder builder = new Notification.Builder(context);
		builder.setTicker("Shoegaze Mode Active")
			   .setContentTitle("Shoegazing...")
			   .setSmallIcon(R.drawable.ic_notification_running)
			   .setWhen(System.currentTimeMillis())
			   .setOngoing(type != 1)
			   .setPriority(Notification.PRIORITY_MAX)
			   .setContentIntent(getContentIntent());
		if (type == 1) {
			PendingIntent restartIntent = ShoegazeUtils.makeServiceIntent(
					context, context.getResources().getString(R.string.action_restart));
			builder.addAction(R.drawable.ic_launcher, "Restarting", restartIntent);
		} else {
			instantiateFlashIntent();
			builder.addAction(
						R.drawable.ic_stop, "",
						ShoegazeUtils.makeServiceIntent(
								context, 
								context.getResources().getString(R.string.action_stop)))
			       .addAction(
			    		   R.drawable.ic_flash_on, "",
			    		   PendingIntent.getBroadcast(
			    				   context, 0, 
			    				   flashIntent,
			    				   PendingIntent.FLAG_CANCEL_CURRENT))
			       .addAction(
			    		   R.drawable.ic_options, "", 
			    		   ShoegazeUtils.makeServiceIntent(
			   					context, context.getResources().getString(
			   							R.string.action_toggle_options)));
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
	public void setFlashState(boolean isFlashOn) {
		this.isFlashOn = isFlashOn;
		flashIntent = null;
		instantiateFlashIntent();
		startNotification(context, 0);
	}
}

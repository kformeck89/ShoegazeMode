package com.kformeck.shoegaze.receivers;

import com.kformeck.shoegaze.OptionsDialog;
import com.kformeck.shoegaze.ShoegazeService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

public class ShoegazeReceiver extends BroadcastReceiver {
	public static final String ACTION_START = "com.kformeck.shoegaze.action.SHOEGAZE_START";
	public static final String ACTION_STOP = "com.kformeck.shoegaze.action.SHOEGAZE_STOP";
	public static final String ACTION_ALREADY_STOP = "com.kformeck.shoegaze.action.SHOEGAZE_ALREADY_STOP";
	public static final String ACTION_RESTART = "com.kformeck.shoegaze.action.SHOEGAZE_RESTART";
	public static final String ACTION_TOGGLE_ALPHA = "com.kformeck.shoegaze.action.SHOEGAZE_TOGGLE_ALPHA";
	public static final String ACTION_TOGGLE_OPTIONS = "com.kformeck.shoegaze.action.SHOEGAZE_TOGGLE_OPTIONS";
	public static final String ACTION_TOGGLE_LIGHT_SENSING_MODE = "com.kformeck.shoegaze.action.SHOEGAZE_TOGGLE_LIGHT_SENSING_MODE";
	public static final String ACTION_TOGGLE_AUTO_FLASHLIGHT_MODE = "com.kformeck.shoegaze.action.SHOEGAZE_TOGGLE_AUTOFLASHLIGHT_MODE";
	public static final String EXTRA_ALPHA = "com.kformeck.shoegaze.action.SHOEGAZE_EXTRA_ALPHA";
	public static final String EXTRA_LSM = "com.kformeck.shoegaze.action.SHOEGAZE_EXTRA_LSM";
	public static final String EXTRA_AUTO_FLASHLIGHT = "com.kformeck.shoegaze.action.SHOEGAZE_EXTRA_AUTO_FLASHLIGHT";

	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();
		Handler handler = new Handler();
		if (action != null && !action.isEmpty()) {
			final Intent serviceTriggerIntent = new Intent(context, ShoegazeService.class);
			if (action.equals(ACTION_START)) {
				context.startService(serviceTriggerIntent);
			} else if (action.equals(ACTION_STOP)) {
				context.stopService(serviceTriggerIntent);
				context.sendBroadcast(new Intent(ACTION_ALREADY_STOP));
			} else if (action.equals(ACTION_RESTART)) {
				context.stopService(serviceTriggerIntent);
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						//context.startService(serviceTriggerIntent);
					}}, 1000);
			} else if (action.equals(ACTION_TOGGLE_OPTIONS)) {
				OptionsDialog.getInstance(context).show();
			} else if (action.equals(ACTION_TOGGLE_AUTO_FLASHLIGHT_MODE)) {
				context.stopService(serviceTriggerIntent);
				context.sendBroadcast(new Intent(ACTION_TOGGLE_AUTO_FLASHLIGHT_MODE));
			}
		}
	}
}

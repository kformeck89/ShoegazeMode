package com.example.onthego;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

public class ShoegazeReceiver extends BroadcastReceiver {
	public static final String ACTION_START = "com.kformeck.onthego.action.ON_THE_GO_START";
	public static final String ACTION_STOP = "com.kformeck.onthego.action.ON_THE_GO_STOP";
	public static final String ACTION_ALREADY_STOP = "com.kformeck.onthego.action.ON_THE_GO_ALREADY_STOP";
	public static final String ACTION_RESTART = "com.kformeck.onthego.action.ON_THE_GO_RESTART";
	public static final String ACTION_TOGGLE_ALPHA = "com.kformeck.onthego.action.ON_THE_GO_TOGGLE_ALPHA";
	public static final String ACTION_TOGGLE_OPTIONS = "com.kformeck.onthego.action.ON_THE_GO_TOGGLE_OPTIONS";
	public static final String EXTRA_ALPHA = "com.kformeck.onthego.action.ON_THE_GO_EXTRA_ALPHA";

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
				Intent stopIntent = new Intent();
				stopIntent.setAction(ACTION_ALREADY_STOP);
				context.sendBroadcast(stopIntent);
			} else if (action.equals(ACTION_RESTART)) {
				context.stopService(serviceTriggerIntent);
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						//context.startService(serviceTriggerIntent);
					}}, 1000);
			} else if (action.equals(ACTION_TOGGLE_OPTIONS)) {
				OptionsDialog.getInstance(context).show();
			}
		}
	}
}

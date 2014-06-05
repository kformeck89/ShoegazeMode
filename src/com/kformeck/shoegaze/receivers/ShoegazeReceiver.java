package com.kformeck.shoegaze.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;

import com.example.onthego.R;
import com.kformeck.shoegaze.ui.OptionsDialog;
import com.kformeck.shoegaze.ui.ShoegazeService;

public class ShoegazeReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(final Context context, Intent intent) {
		final String action = intent.getAction();
		Handler handler = new Handler();
		Resources res = context.getResources();
		if (action != null && !action.isEmpty()) {
			final Intent serviceTriggerIntent = new Intent(context, ShoegazeService.class);
			if (action.equals(res.getString(R.string.action_start))) {
				context.startService(serviceTriggerIntent);
			} else if (action.equals(res.getString(R.string.action_stop))) {
				context.stopService(serviceTriggerIntent);
				context.sendBroadcast(new Intent(res.getString(R.string.action_already_stop)));
			} else if (action.equals(res.getString(R.string.action_restart))) {
				context.stopService(serviceTriggerIntent);
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						context.startService(serviceTriggerIntent);
					}}, 1000);	
			} else if (action.equals(res.getString(R.string.action_toggle_options))) {
				OptionsDialog.getInstance(context).show();
			} else if (action.equals(res.getString(R.string.action_toggle_flash))) {
				context.stopService(serviceTriggerIntent);
				context.sendBroadcast(new Intent(res.getString(R.string.action_toggle_flash)));
			}
		}
	}
}

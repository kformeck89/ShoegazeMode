package com.kformeck.shoegaze.receivers;

import com.example.onthego.R;
import com.kformeck.shoegaze.ui.notifications.ActivationNotification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class OnBootReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences prefs = context.getSharedPreferences(
				context.getResources().getString(R.string.shoegaze_prefs),
				Context.MODE_PRIVATE);
		SharedPreferences defaultPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		boolean appIsOn = 
				prefs.getBoolean(
						context.getResources().getString(R.string.pref_app_state), false) 
						&&
				defaultPrefs.getBoolean(
						context.getResources().getString(R.string.settings_key_onboot), false);
		if (appIsOn) {
			ActivationNotification.getInstance().startNotification(context, 0);
		}
	}
}

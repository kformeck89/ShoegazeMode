package com.kformeck.shoegaze.utilities;

import com.example.onthego.R;
import com.kformeck.shoegaze.receivers.ShoegazeReceiver;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class ShoegazeUtils {
	public static PendingIntent makeServiceIntent(Context context, String action) {
		Intent intent = new Intent(context, ShoegazeReceiver.class);
		intent.setAction(action);
		return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	}
	public static void saveSettings(
			SharedPreferences sharedPrefs,
			Context context,
			boolean isAutoModeOn,
			float userAlpha) {
		sharedPrefs.edit()
        		   .putBoolean(context.getResources().getString(R.string.pref_light_sensing_mode), isAutoModeOn)
        		   .apply();
		sharedPrefs.edit()
		   	 	   .putFloat(context.getResources().getString(R.string.pref_user_alpha), userAlpha)
		   	 	   .apply();
	}
}

package com.example.onthego;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class ShoegazeUtilities {
	public static PendingIntent makeServiceIntent(Context context, String action) {
		Intent intent = new Intent(context, ShoegazeReceiver.class);
		intent.setAction(action);
		return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	}
	public static void saveSettings(
			SharedPreferences sharedPrefs,
			Context context,
			boolean isAutoModeOn,
			boolean isAutoFlashOn,
			float userAlpha) {
		sharedPrefs.edit()
        		   .putBoolean(context.getResources().getString(R.string.pref_light_sensing_mode), isAutoModeOn)
        		   .commit();
		sharedPrefs.edit()
		   	       .putBoolean(context.getResources().getString(R.string.pref_auto_flashlight_mode), isAutoFlashOn)
		   	       .commit();
		sharedPrefs.edit()
		   	 	   .putFloat(context.getResources().getString(R.string.pref_user_alpha), userAlpha)
		   	 	   .commit();
	}
}

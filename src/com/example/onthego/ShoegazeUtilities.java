package com.example.onthego;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class ShoegazeUtilities {
	public static PendingIntent makeServiceIntent(Context context, String action) {
		Intent intent = new Intent(context, ShoegazeReceiver.class);
		intent.setAction(action);
		return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	}
}

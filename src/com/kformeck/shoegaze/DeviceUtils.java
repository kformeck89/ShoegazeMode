package com.kformeck.shoegaze;

import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class DeviceUtils {
	public static boolean isAutoBrightnessOn(Context context) {
		try {
			if (Settings.System.getInt(
					context.getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS_MODE)
				== Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
					return true;
				} else {
					return false;
				}
		} catch (SettingNotFoundException ex) { 
			return true; 
		}
	}
	public static void setAutoBrightness(Context context, boolean value) {
		if (value) {
			Settings.System.putInt(
					context.getContentResolver(), 
					Settings.System.SCREEN_BRIGHTNESS_MODE, 
					Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
		} else {
			Settings.System.putInt(
					context.getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS_MODE,
					Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
		}
	}
	public static int getBrightnessLevel(Context context) {
		try {
			return Settings.System.getInt(
					context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException e) { return 128; }
	}
	public static void setBrightnessLevel(Context context, int value) {
		Settings.System.putInt(
				context.getContentResolver(), 
				Settings.System.SCREEN_BRIGHTNESS, 
				value);
	}
	public static boolean deviceSupportsFrontCamera(Context context) {
		return context.getPackageManager()
				      .hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
	}
}

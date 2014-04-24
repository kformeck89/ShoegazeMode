package com.example.onthego;

import android.content.Context;
import android.content.pm.PackageManager;

public class DeviceUtils {
	public static boolean deviceSupportsFrontCamera(Context context) {
		return context.getPackageManager()
				      .hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
	}
}

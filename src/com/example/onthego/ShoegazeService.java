package com.example.onthego;

import java.io.IOException;

import com.example.onthego.Notifications.ActivationNotification;
import com.example.onthego.Notifications.ShoegazeNotification;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.view.TextureView;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

public class ShoegazeService extends Service implements FaceDetectionListener {
	private static final int CAMERA_BACK = 0;
	private static final int CAMERA_FRONT = 1;
	private static final int NOTIFICATION_STARTED = 0;
	
	public static final float ALPHA_MIN = 0.1f;
	public static final float ALPHA_VERY_LOW = 0.2f;
	public static final float ALPHA_LOW = 0.3f;
	public static final float ALPHA_MEDIUM_LOW = 0.4f;
	public static final float ALPHA_MEDIUM = 0.5f;
	public static final float ALPHA_MEDIUM_HIGH = 0.6f;
	public static final float ALPHA_HIGH = 0.7f;
	public static final float ALPHA_VERY_HIGH = 0.8f;
	public static final float ALPHA_MAX = 0.9f;
	
	private float userAlpha;
	private boolean isAutoModeOn;
	private boolean isAutoFlashOn;
	
	private Context context;
	private FrameLayout overlay;
	private Camera camera;
	private SharedPreferences sharedPrefs;
	private PowerManager powerManager;
	private WindowManager windowManager;
	
	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ShoegazeReceiver.ACTION_TOGGLE_ALPHA)) {
				final float intentAlpha = intent.getFloatExtra(ShoegazeReceiver.EXTRA_ALPHA, ALPHA_MEDIUM);
				toggleOnTheGoAlpha(intentAlpha);
			} else if (action.equals(ShoegazeReceiver.ACTION_TOGGLE_LIGHT_SENSING_MODE)) {
				setLightSensingModeActive(intent.getExtras().getBoolean(ShoegazeReceiver.EXTRA_LSM));
			} else if (action.equals(ShoegazeReceiver.ACTION_TOGGLE_AUTO_FLASHLIGHT_MODE)) {
				setAutoFlashlightModeActive(intent.getExtras().getBoolean(ShoegazeReceiver.EXTRA_AUTO_FLASHLIGHT));
			} else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
				resetViews();
			} else if (action.equals(Intent.ACTION_USER_PRESENT)) {
				setupViews();
			}
		}
	};
	
	@Override
	public void onCreate() {
		super.onCreate();
		context = this;
		
		powerManager = (PowerManager)this.getSystemService(POWER_SERVICE);
		windowManager = (WindowManager)this.getSystemService(WINDOW_SERVICE);
		
		sharedPrefs = context.getSharedPreferences(
				context.getResources().getString(R.string.shoegaze_prefs), Context.MODE_PRIVATE);
		userAlpha = sharedPrefs.getFloat(
				context.getResources().getString(R.string.pref_user_alpha), ALPHA_MEDIUM);
		isAutoModeOn = sharedPrefs.getBoolean(
				context.getResources().getString(R.string.pref_light_sensing_mode), false);
		isAutoFlashOn = sharedPrefs.getBoolean(
				context.getResources().getString(R.string.pref_auto_flashlight_mode), false);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(ShoegazeReceiver.ACTION_TOGGLE_ALPHA);
		filter.addAction(ShoegazeReceiver.ACTION_TOGGLE_LIGHT_SENSING_MODE);
		filter.addAction(ShoegazeReceiver.ACTION_TOGGLE_AUTO_FLASHLIGHT_MODE);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_USER_PRESENT);
		registerReceiver(broadcastReceiver, filter);
		
		setupViews();
	}
	@Override
	public void onDestroy() {
		if (broadcastReceiver != null) {
			unregisterReceiver(broadcastReceiver);
		}
		
		resetViews();
		ShoegazeNotification.getInstance().cancelNotification();
		ShoegazeUtilities.saveSettings(sharedPrefs, context, isAutoModeOn, isAutoFlashOn, userAlpha);
		
		super.onDestroy();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}
	
	private void toggleOnTheGoAlpha() {
		toggleOnTheGoAlpha(userAlpha);
	}
	private void toggleOnTheGoAlpha(float alpha) {
		sharedPrefs.edit().putFloat(
				context.getResources().getString(R.string.pref_user_alpha), 
				alpha).commit();
		if (overlay != null) {
			if (alpha > ALPHA_MAX) {
				overlay.setAlpha(ALPHA_MAX);
			} else if (alpha < ALPHA_MIN) {
				overlay.setAlpha(ALPHA_MIN);
			} else {
				overlay.setAlpha(alpha);	
			}
		}
	}
	private void getCameraInstance(int type) throws RuntimeException {
		releaseCamera();
		if (!DeviceUtils.deviceSupportsFrontCamera(context)) {
			camera = Camera.open();
			return;
		}
		switch (type) {
			default:
			case CAMERA_BACK:
				camera = Camera.open(0);
				break;
			case CAMERA_FRONT:
				final Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
				final int cameraCount = Camera.getNumberOfCameras();
				for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
					Camera.getCameraInfo(camIdx, cameraInfo);
					if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
						camera = Camera.open(camIdx);
					}
				}
				break;
		}
	}
	private void setupViews() {
		int cameraType = 0;  // TODO: retrieve saved camera type here
		boolean success = true;
		try {
			getCameraInstance(cameraType);
		} catch (RuntimeException ex) {
			Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
			success = false;
		}
		
		if (!success) {
			return;
		}
		
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
				WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
				WindowManager.LayoutParams.FLAG_FULLSCREEN |
				WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
				PixelFormat.TRANSLUCENT);
		
		final TextureView textureView = new TextureView(context);
		textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
			@Override
			public void onSurfaceTextureUpdated(SurfaceTexture surface) { }			
			@Override
			public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width,
					int height) { }			
			@Override
			public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
				releaseCamera();
				return false;
			}			
			@Override
			public void onSurfaceTextureAvailable(SurfaceTexture surface, int width,
					int height) {
				try {
					if (camera != null) {
						camera.setDisplayOrientation(90);
						camera.setPreviewTexture(surface);
						camera.startPreview();
						try {
							camera.startFaceDetection();
						} catch (IllegalArgumentException ilEx) {
						} catch (RuntimeException rtEx) {
							// TODO: catch errors here
						}
						camera.setFaceDetectionListener(ShoegazeService.this);
						  
					}
				} catch (IOException ignore) { 
				}
			}
		});
		
		if (isAutoModeOn) {
			setLightSensingModeActive(isAutoModeOn);
			setAutoFlashlightModeActive(isAutoFlashOn);
		}
		
		overlay = new FrameLayout(context);
		overlay.setLayoutParams(new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		overlay.addView(textureView);
		windowManager.addView(overlay, params);
		
		toggleOnTheGoAlpha();
		
		ActivationNotification.getInstance().cancelNotification();
		ShoegazeNotification.getInstance().startNotification(context, NOTIFICATION_STARTED);
	}
	private void resetViews() {
		if (overlay != null) {
			overlay.removeAllViews();
			windowManager.removeView(overlay);
			overlay = null;
			ActivationNotification.getInstance().startNotification(context, 0);
		}
	}
	private void releaseCamera() {
		if (camera != null) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}
	private void setUserActivity() {
		try {
			powerManager.userActivity(SystemClock.uptimeMillis(), true);
		} catch (RuntimeException ex) {
		}
	}	
	
	@Override
	public IBinder onBind(Intent intent) { return null; }
	
	public void setUserAlpha(float userAlpha) {
		this.userAlpha = userAlpha;
		sharedPrefs.edit().putFloat(context.getResources().getString(
				R.string.pref_user_alpha), userAlpha).commit();
	}
	public boolean getLightSensingModeActive() {
		return isAutoModeOn;
	}
	public void setLightSensingModeActive(boolean isActive) {
		isAutoModeOn = isActive;
		sharedPrefs.edit().putBoolean(context.getResources().getString(
				R.string.pref_light_sensing_mode), isActive).commit();
		if (isActive) {
			LightSensorManager.getInstance(context).start();
		} else {
			LightSensorManager.getInstance(context).cancel();
		}
	}
	public boolean getAutoFlashlightModeActive() {
		return isAutoFlashOn;
	}
	public void setAutoFlashlightModeActive(boolean isActive) {
		isAutoFlashOn = isActive;
		sharedPrefs.edit().putBoolean(context.getResources().getString(
				R.string.pref_auto_flashlight_mode), isActive).commit();
	}

	@Override
	public void onFaceDetection(Face[] faces, Camera camera) {
		if ((faces != null) && (faces.length > 0)) {
			if (!powerManager.isScreenOn() ) {
				powerManager.wakeUp(SystemClock.uptimeMillis());
			} else {
				setUserActivity();
			}
		}
	}
}

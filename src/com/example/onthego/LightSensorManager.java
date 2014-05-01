package com.example.onthego;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class LightSensorManager implements SensorEventListener {
	private static LightSensorManager instance;
	private Context context;
	private Sensor sensor;
	private SensorManager manager;
	
	public static LightSensorManager getInstance(Context context) {
		if (instance == null) {
			instance = new LightSensorManager(context);
		}
		return instance;
	}
	private LightSensorManager(Context context) { 
		this.context = context;
	}

	public void start() {
		manager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		sensor = (Sensor)manager.getDefaultSensor(Sensor.TYPE_LIGHT);
		manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
	}
	public void cancel() {
		if (manager != null) {
			manager.unregisterListener(this);
			manager = null;
		}
		if (sensor != null) {
			sensor = null;
		}
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
			int brightnessLevel = 0;
			float alphaExtra = 0.0f;
			Intent alphaChangedIntent = new Intent(ShoegazeReceiver.ACTION_TOGGLE_ALPHA);
			if (event.values[0] <= SensorManager.LIGHT_NO_MOON) {
				brightnessLevel = context.getResources().getInteger(R.integer.brightness_max);
				alphaExtra = ShoegazeService.ALPHA_MAX;
			} else if (event.values[0] > SensorManager.LIGHT_NO_MOON &&
					   event.values[0] <= SensorManager.LIGHT_FULLMOON) {
				brightnessLevel = context.getResources().getInteger(R.integer.brightness_very_high);
				alphaExtra = ShoegazeService.ALPHA_VERY_HIGH;
			} else if (event.values[0] > SensorManager.LIGHT_FULLMOON &&
					   event.values[0] <= SensorManager.LIGHT_CLOUDY) {
				brightnessLevel = context.getResources().getInteger(R.integer.brightness_high);
				alphaExtra = ShoegazeService.ALPHA_HIGH;
			} else if (event.values[0] > SensorManager.LIGHT_CLOUDY &&
					   event.values[0] <= SensorManager.LIGHT_SUNRISE) {
				brightnessLevel = context.getResources().getInteger(R.integer.brightness_medium_high);
				alphaExtra = ShoegazeService.ALPHA_MEDIUM_HIGH;
			} else if (event.values[0] > SensorManager.LIGHT_SUNRISE &&
					   event.values[0] <= SensorManager.LIGHT_OVERCAST) {
				brightnessLevel = context.getResources().getInteger(R.integer.brightness_medium);
				alphaExtra = ShoegazeService.ALPHA_MEDIUM;
			} else if (event.values[0] > SensorManager.LIGHT_OVERCAST &&
					   event.values[0] <= SensorManager.LIGHT_SHADE) {
				brightnessLevel = context.getResources().getInteger(R.integer.brightness_medium_low);
				alphaExtra = ShoegazeService.ALPHA_MEDIUM_LOW;
			} else if (event.values[0] > SensorManager.LIGHT_SHADE &&
					   event.values[0] <= SensorManager.LIGHT_SUNLIGHT) {
				brightnessLevel = context.getResources().getInteger(R.integer.brightness_low);
				alphaExtra = ShoegazeService.ALPHA_LOW;
			} else if (event.values[0] > SensorManager.LIGHT_SUNLIGHT &&
					   event.values[0] <= SensorManager.LIGHT_SUNLIGHT_MAX) {
				brightnessLevel = context.getResources().getInteger(R.integer.brightness_very_low);
				alphaExtra = ShoegazeService.ALPHA_VERY_LOW;
			} else if (event.values[0] > SensorManager.LIGHT_SUNLIGHT_MAX) {
				brightnessLevel = context.getResources().getInteger(R.integer.brightness_min);
				alphaExtra = ShoegazeService.ALPHA_MIN;
			}
			DeviceUtils.setBrightnessLevel(context, brightnessLevel);
			alphaChangedIntent.putExtra(ShoegazeReceiver.EXTRA_ALPHA, alphaExtra);
			context.sendBroadcast(alphaChangedIntent);
		}		
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}

package com.example.onthego;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class LightSensorManager {
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

	public void startLightManagement() {
		if (manager == null) {
			manager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		}
		if (sensor == null) {
			sensor = (Sensor)manager.getDefaultSensor(Sensor.TYPE_LIGHT);
		}
		manager.registerListener(new SensorEventListener() {
			@Override
			public void onAccuracyChanged(Sensor arg0, int arg1) { }
			@Override
			public void onSensorChanged(SensorEvent event) {
				if (event.sensor.equals(Sensor.TYPE_LIGHT)) {
					float alphaExtra = 0.0f;
					Intent alphaChangedIntent = new Intent(ShoegazeReceiver.ACTION_TOGGLE_ALPHA);
					if (event.values[0] <= SensorManager.LIGHT_NO_MOON) {
						
					} else if (event.values[0] > SensorManager.LIGHT_NO_MOON &&
							   event.values[0] <= SensorManager.LIGHT_FULLMOON) {
						
					} else if (event.values[0] > SensorManager.LIGHT_FULLMOON &&
							   event.values[0] <= SensorManager.LIGHT_CLOUDY) {	
						
					} else if (event.values[0] > SensorManager.LIGHT_CLOUDY &&
							   event.values[0] <= SensorManager.LIGHT_SUNRISE) {
						
					} else if (event.values[0] > SensorManager.LIGHT_SUNRISE &&
							   event.values[0] <= SensorManager.LIGHT_OVERCAST) {
						
					} else if (event.values[0] > SensorManager.LIGHT_OVERCAST &&
							   event.values[0] <= SensorManager.LIGHT_SHADE) {
						
					} else if (event.values[0] > SensorManager.LIGHT_SHADE &&
							   event.values[0] <= SensorManager.LIGHT_SUNLIGHT) {
						
					} else if (event.values[0] > SensorManager.LIGHT_SUNLIGHT &&
							   event.values[0] <= SensorManager.LIGHT_SUNLIGHT_MAX) {
						
					} else if (event.values[0] > SensorManager.LIGHT_SUNLIGHT_MAX) {
						
					} else {
					
					}
					alphaChangedIntent.putExtra(ShoegazeReceiver.EXTRA_ALPHA, alphaExtra);
					context.sendBroadcast(alphaChangedIntent);
				}
			}
			
		}, sensor, SensorManager.SENSOR_DELAY_NORMAL);
	}
}

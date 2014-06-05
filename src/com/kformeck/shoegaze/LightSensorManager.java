package com.kformeck.shoegaze;

import java.util.ArrayList;

import com.example.onthego.R;
import com.kformeck.shoegaze.receivers.ShoegazeReceiver;
import com.kformeck.shoegaze.ui.ShoegazeService;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class LightSensorManager implements SensorEventListener {
	private static LightSensorManager instance;
	private static final int SENSOR_HISTORY_SIZE = 10;
	private Context context;
	private Intent alphaChangedIntent;
	private Intent flashIntent;
	private Sensor sensor;
	private SensorManager manager;
	
	private class LightSensorStatManager {
		private ArrayList<Float> sensorHistory;
		public LightSensorStatManager() {
			sensorHistory = new ArrayList<Float>();
		}
		private float getAverage() {
			float sum = 0.0f;
			for (float value : sensorHistory) {
				sum += value;
			}
			return (sum / sensorHistory.size());
		}
		public boolean shouldFlashBeOn(float currentSensorValue) {
			if (sensorHistory.size() >= SENSOR_HISTORY_SIZE) {
				sensorHistory.remove(SENSOR_HISTORY_SIZE - 1);
				sensorHistory.add(0, currentSensorValue);
				if (getAverage() > 1) {
					return false;
				} else {
					return true;
				}
			} else {
				sensorHistory.add(currentSensorValue);
				return false;
			}
		}
	}
	private LightSensorStatManager statManager;
	
	public static LightSensorManager getInstance(Context context) {
		if (instance == null) {
			instance = new LightSensorManager(context);
		}
		return instance;
	}
	private LightSensorManager(Context context) { 
		this.context = context;
		statManager = new LightSensorStatManager();
		alphaChangedIntent = new Intent(ShoegazeReceiver.ACTION_TOGGLE_ALPHA);
		flashIntent = new Intent(context.getResources().getString(
				R.string.action_toggle_flash));
	}

	public void start() {
		manager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		sensor = (Sensor)manager.getDefaultSensor(Sensor.TYPE_LIGHT);
		manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
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
			float alphaExtra = 0.0f;
			if (event.values[0] <= SensorManager.LIGHT_NO_MOON) {
				alphaExtra = ShoegazeService.ALPHA_MAX;
			} else if (event.values[0] > SensorManager.LIGHT_NO_MOON &&
					   event.values[0] <= SensorManager.LIGHT_CLOUDY) {
				alphaExtra = ShoegazeService.ALPHA_HIGH;
			} else if (event.values[0] > SensorManager.LIGHT_CLOUDY &&
					   event.values[0] <= SensorManager.LIGHT_SUNRISE) {
				alphaExtra = ShoegazeService.ALPHA_MEDIUM_HIGH;
			} else if (event.values[0] > SensorManager.LIGHT_SUNRISE &&
					   event.values[0] <= SensorManager.LIGHT_OVERCAST) {
				alphaExtra = ShoegazeService.ALPHA_MEDIUM;
			} else if (event.values[0] > SensorManager.LIGHT_OVERCAST &&
					   event.values[0] <= SensorManager.LIGHT_SHADE) {
				alphaExtra = ShoegazeService.ALPHA_MEDIUM_LOW;
			} else if (event.values[0] > SensorManager.LIGHT_SHADE &&
					   event.values[0] <= SensorManager.LIGHT_SUNLIGHT_MAX) {
				alphaExtra = ShoegazeService.ALPHA_LOW;
			} else if (event.values[0] > SensorManager.LIGHT_SUNLIGHT_MAX) {
				alphaExtra = ShoegazeService.ALPHA_MIN;
			}
			alphaChangedIntent.putExtra(ShoegazeReceiver.EXTRA_ALPHA, alphaExtra);
			context.sendBroadcast(alphaChangedIntent);		
			
			if (statManager.shouldFlashBeOn(event.values[0])) {
				flashIntent.putExtra(
						context.getResources().getString(R.string.extra_flash_is_on), true);
				context.sendBroadcast(flashIntent);
			} else {
				flashIntent.putExtra(
						context.getResources().getString(R.string.extra_flash_is_on), false);
				context.sendBroadcast(flashIntent);
			}
		}		
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}

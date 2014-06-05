package com.kformeck.shoegaze;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.example.onthego.R;
import com.kformeck.shoegaze.ui.ShoegazeService;

public class LightSensorManager implements SensorEventListener {
	private static LightSensorManager instance;
	private Context context;
	private Intent alphaChangedIntent;
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
		alphaChangedIntent = new Intent(
				context.getResources().getString(R.string.action_toggle_alpha));
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
			alphaChangedIntent.putExtra(
					context.getResources().getString(R.string.extra_alpha), alphaExtra);
			context.sendBroadcast(alphaChangedIntent);
		}		
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}

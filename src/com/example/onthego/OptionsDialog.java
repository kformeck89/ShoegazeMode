package com.example.onthego;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;

public class OptionsDialog {
	private static OptionsDialog instance;
	private boolean autoBrightnessWasOn;
	private int prvBrightnessLevel;
	private Context context;
	private View overlay;
	private WindowManager windowManager;
	
	public static OptionsDialog getInstance(Context context) {
		if (instance == null) {
			instance = new OptionsDialog(context);
		}
		return instance;
	}
	private OptionsDialog(Context context) {
		this.context = context;
		retreiveBrightnessSettings();
	}
	
	public void show() {
		windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);		
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
				WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON |
				WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
				PixelFormat.TRANSLUCENT);
		params.gravity = Gravity.CENTER;
		
		overlay = ((LayoutInflater)context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.overlay_options, null, false);
		overlay.setBackground(context.getResources().getDrawable(
				R.drawable.options_overlay_shape));
		overlay.setAlpha(1);
		
		final Switch lsmSwitch = (Switch)overlay.findViewById(R.id.switchLightSensingMode);
		lsmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Intent toggleLsmIntent = new Intent(ShoegazeReceiver.ACTION_TOGGLE_LIGHT_SENSING_MODE);
				toggleLsmIntent.putExtra(ShoegazeReceiver.EXTRA_LSM, isChecked);
				if (isChecked) {
					retreiveBrightnessSettings();
					DeviceUtils.setAutoBrightness(context, false);					
				} else {
					if (autoBrightnessWasOn) {
						DeviceUtils.setAutoBrightness(context, true);
					} else {
						DeviceUtils.setBrightnessLevel(context, prvBrightnessLevel);
					}
				}
				context.sendBroadcast(toggleLsmIntent);
			}	
		});
		
		final Switch autoFlashSwitch = (Switch)overlay.findViewById(R.id.switchAutoflash);
		autoFlashSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Intent toggleAutoFlashIntent = new Intent(ShoegazeReceiver.ACTION_TOGGLE_AUTO_FLASHLIGHT_MODE);
			    toggleAutoFlashIntent.putExtra(ShoegazeReceiver.EXTRA_AUTO_FLASHLIGHT, isChecked);
			    context.sendBroadcast(toggleAutoFlashIntent);
			}
		});
		
		final SeekBar alphaSlider = (SeekBar)overlay.findViewById(R.id.sliderAlpha);
		final float value = 0.5f; //TODO: retrieve this from settings instead
		final int progress = (int)(value * 100);
		alphaSlider.setProgress(progress);
		alphaSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				sendAlphaBroadcast(String.valueOf(progress + 10));
			}
			@Override
			public void onStartTrackingTouch(SeekBar arg0) { }
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {	}
		});
		
		overlay.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					if (windowManager != null) {
						windowManager.removeView(overlay);
						windowManager = null;
					}
				}
				return false;
			}
		});
		
		windowManager.addView(overlay, params);
	}	
	private void sendAlphaBroadcast(String i) {
		final float value = (Float.parseFloat(i) / 100);
		final Intent alphaBroadcast = new Intent();
		alphaBroadcast.setAction(ShoegazeReceiver.ACTION_TOGGLE_ALPHA);
		alphaBroadcast.putExtra(ShoegazeReceiver.EXTRA_ALPHA, value);
		context.sendBroadcast(alphaBroadcast);
	}
	private void retreiveBrightnessSettings() {
		autoBrightnessWasOn = DeviceUtils.isAutoBrightnessOn(context);
		prvBrightnessLevel = DeviceUtils.getBrightnessLevel(context);
	}
}

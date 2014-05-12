package com.kformeck.shoegaze.ui;

import com.example.onthego.R;
import com.kformeck.shoegaze.receivers.ShoegazeReceiver;
import com.kformeck.shoegaze.utilities.DeviceUtils;
import com.kformeck.shoegaze.utilities.ShoegazeUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;

public class OptionsDialog {
	private static OptionsDialog instance;
	
	private boolean autoBrightnessWasOn;
	private boolean isAutoModeOn;
	private boolean isAutoFlashOn;
	private int prvBrightnessLevel;
	private float userAlpha;
	
	private Context context;
	private SharedPreferences sharedPrefs;
	private View overlay;
	private Switch autoFlashSwitch;
	private SeekBar alphaSlider;
	private WindowManager windowManager;
	
	private enum UiMode {
		AUTO,
		MANUAL
	};
	
	public static OptionsDialog getInstance(Context context) {
		if (instance == null) {
			instance = new OptionsDialog(context);
		}
		return instance;
	}
	private OptionsDialog(Context context) {
		this.context = context;
		retreiveBrightnessSettings();
		
		sharedPrefs = context.getSharedPreferences(
				context.getResources().getString(R.string.shoegaze_prefs), Context.MODE_PRIVATE);
	}
	
	public void show() {
		userAlpha = sharedPrefs.getFloat(
				context.getResources().getString(R.string.pref_user_alpha),
				context.getResources().getInteger(R.integer.brightness_medium));
		isAutoModeOn = sharedPrefs.getBoolean(
				context.getResources().getString(R.string.pref_light_sensing_mode), false);
		isAutoFlashOn = sharedPrefs.getBoolean(
				context.getResources().getString(R.string.pref_auto_flashlight_mode), false);
		
		windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);		
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				(int)context.getResources().getDimension(R.dimen.options_width),
				(int)context.getResources().getDimension(R.dimen.options_height),
				WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
				WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON |
				WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
				PixelFormat.TRANSLUCENT);
		params.gravity = Gravity.CENTER;
		
		overlay = ((LayoutInflater)context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.overlay_options, null, false);
		overlay.setBackground(context.getResources().getDrawable(
				android.R.drawable.dialog_holo_dark_frame));
		overlay.setAlpha(1);
		
		Switch lsmSwitch = (Switch)overlay.findViewById(R.id.switchLightSensingMode);
		Button btnSave = (Button)overlay.findViewById(R.id.btnSave);
		Button btnCancel = (Button)overlay.findViewById(R.id.btnCancel);
		autoFlashSwitch = (Switch)overlay.findViewById(R.id.switchAutoflash);
		alphaSlider = (SeekBar)overlay.findViewById(R.id.sliderAlpha);
		
		lsmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					isAutoModeOn = true;
					rearrangeUi(UiMode.AUTO);
					retreiveBrightnessSettings();
					DeviceUtils.setAutoBrightness(context, false);					
				} else {
					isAutoModeOn = false;
					isAutoFlashOn = false;
					rearrangeUi(UiMode.MANUAL);
					if (autoBrightnessWasOn) {
						DeviceUtils.setAutoBrightness(context, true);
					} else {
						DeviceUtils.setBrightnessLevel(context, prvBrightnessLevel);
					}					
				}
				sendLsmBroadcast(isChecked);
			}	
		});			
		autoFlashSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isAutoFlashOn = isChecked;
				Intent toggleAutoFlashIntent = new Intent(ShoegazeReceiver.ACTION_TOGGLE_AUTO_FLASHLIGHT_MODE);
			    toggleAutoFlashIntent.putExtra(ShoegazeReceiver.EXTRA_AUTO_FLASHLIGHT, isChecked);
			    context.sendBroadcast(toggleAutoFlashIntent);
			}
		});		
		alphaSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				userAlpha = (float)(progress);
				sendAlphaBroadcast(String.valueOf(userAlpha));
			}
			@Override
			public void onStartTrackingTouch(SeekBar arg0) { }
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {	}
		});
		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ShoegazeUtils.saveSettings(sharedPrefs, context, isAutoModeOn, isAutoFlashOn, userAlpha);
				close();
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				close();
			}
		});
		
		rearrangeUi(isAutoModeOn ? UiMode.AUTO : UiMode.MANUAL);
		lsmSwitch.setChecked(isAutoModeOn);
		alphaSlider.setProgress((int)userAlpha);
		
		
		
		windowManager.addView(overlay, params);
	}	
	private void rearrangeUi(UiMode mode) {
		if (mode == UiMode.AUTO) {
			alphaSlider.setVisibility(View.GONE);
			autoFlashSwitch.setVisibility(View.VISIBLE);
		} else {
			autoFlashSwitch.setVisibility(View.GONE);
			alphaSlider.setVisibility(View.VISIBLE);
		}
	}
	private void sendAlphaBroadcast(String i) {
		final float value = (Float.parseFloat(i) / 100);
		final Intent alphaBroadcast = new Intent();
		alphaBroadcast.setAction(ShoegazeReceiver.ACTION_TOGGLE_ALPHA);
		alphaBroadcast.putExtra(ShoegazeReceiver.EXTRA_ALPHA, value);
		context.sendBroadcast(alphaBroadcast);
	}
	private void sendLsmBroadcast(boolean value) {
		Intent lsmBroadcast = new Intent();
		lsmBroadcast.setAction(ShoegazeReceiver.ACTION_TOGGLE_LIGHT_SENSING_MODE);
		lsmBroadcast.putExtra(ShoegazeReceiver.EXTRA_LSM, value);
		context.sendBroadcast(lsmBroadcast);
	}
	private void retreiveBrightnessSettings() {
		autoBrightnessWasOn = DeviceUtils.isAutoBrightnessOn(context);
		prvBrightnessLevel = DeviceUtils.getBrightnessLevel(context);
	}
	private void close() {
		if (windowManager != null) {
			windowManager.removeView(overlay);
			windowManager = null;
		}
	}
}

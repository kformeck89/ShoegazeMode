package com.kformeck.shoegaze.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
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

import com.example.onthego.R;
import com.kformeck.shoegaze.utilities.ShoegazeUtils;

public class OptionsDialog {
	private static OptionsDialog instance;
	
	private boolean isAutoModeOn;
	private float userAlpha;
	
	private Context context;
	private Resources res;
	private SharedPreferences sharedPrefs;
	private View overlay;
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
		sharedPrefs = context.getSharedPreferences(
				context.getResources().getString(R.string.shoegaze_prefs), Context.MODE_PRIVATE);
		res = context.getResources();
	}
	
	public void show() {
		userAlpha = sharedPrefs.getFloat(
				context.getResources().getString(R.string.pref_user_alpha),
				context.getResources().getInteger(R.integer.brightness_medium));
		isAutoModeOn = sharedPrefs.getBoolean(
				context.getResources().getString(R.string.pref_light_sensing_mode), false);
		
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
		overlay.setBackground(res.getDrawable(android.R.drawable.dialog_holo_dark_frame));
		overlay.setAlpha(1);
		
		Switch lsmSwitch = (Switch)overlay.findViewById(R.id.switchLightSensingMode);
		Button btnSave = (Button)overlay.findViewById(R.id.btnSave);
		Button btnCancel = (Button)overlay.findViewById(R.id.btnCancel);
		alphaSlider = (SeekBar)overlay.findViewById(R.id.sliderAlpha);
		
		lsmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					isAutoModeOn = true;
					rearrangeUi(UiMode.AUTO);					
				} else {
					isAutoModeOn = false;
					rearrangeUi(UiMode.MANUAL);					
				}
				sendLsmBroadcast(isChecked);
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
				ShoegazeUtils.saveSettings(sharedPrefs, context, isAutoModeOn, userAlpha);
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
		alphaSlider.setThumb(res.getDrawable(R.drawable.scrollbar_thumb));
		alphaSlider.setProgressDrawable(res.getDrawable(R.drawable.scrollbar_track));
		
		windowManager.addView(overlay, params);
	}	
	private void rearrangeUi(UiMode mode) {
		alphaSlider.setEnabled(mode == UiMode.MANUAL);
	}
	private void sendAlphaBroadcast(String i) {
		final float value = (Float.parseFloat(i) / 100);
		final Intent alphaBroadcast = new Intent();
		alphaBroadcast.setAction(context.getResources().getString(R.string.action_toggle_alpha));
		alphaBroadcast.putExtra(res.getString(R.string.extra_alpha), value);
		context.sendBroadcast(alphaBroadcast);
	}
	private void sendLsmBroadcast(boolean value) {
		Intent lsmBroadcast = new Intent();
		lsmBroadcast.setAction(res.getString(R.string.action_toggle_lsm));
		lsmBroadcast.putExtra(res.getString(R.string.extra_lsm), value);
		context.sendBroadcast(lsmBroadcast);
	}
	private void close() {
		if (windowManager != null) {
			windowManager.removeView(overlay);
			windowManager = null;
		}
	}
}

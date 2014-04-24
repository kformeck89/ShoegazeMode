package com.example.onthego;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;

public class OptionsDialog {
	private static OptionsDialog instance;
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
	}
	
	public void show() {
		windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);		
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON |
				WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
				PixelFormat.TRANSLUCENT);
		params.gravity = Gravity.CENTER;
		
		overlay = ((LayoutInflater)context.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.overlay_options, null, false);
		overlay.setBackground(context.getResources().getDrawable(R.drawable.options_overlay_shape));
		
		final SeekBar alphaSlider = (SeekBar)overlay.findViewById(R.id.sliderAlpha);
		final float value = 0.5f; //TODO: retrieve this from settings instead
		final int progress = (int)(value * 100);
		alphaSlider.setProgress(progress);
		alphaSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
				sendAlphaBroadcast(String.valueOf(i + 10));
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
		alphaBroadcast.setAction(OnTheGoReceiver.ACTION_TOGGLE_ALPHA);
		alphaBroadcast.putExtra(OnTheGoReceiver.EXTRA_ALPHA, value);
		context.sendBroadcast(alphaBroadcast);
	}
}

package com.example.onthego;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.widget.SeekBar;

public class OptionsDialog {
	private static OptionsDialog instance;
	private Context context;
	
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
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.dialog_options);
		dialog.setTitle("Shoegaze Options");
		
		final SeekBar alphaSlider = (SeekBar)dialog.findViewById(R.id.sliderAlpha);
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
		
		dialog.show();
	}	
	private void sendAlphaBroadcast(String i) {
		final float value = (Float.parseFloat(i) / 100);
		final Intent alphaBroadcast = new Intent();
		alphaBroadcast.setAction(OnTheGoReceiver.ACTION_TOGGLE_ALPHA);
		alphaBroadcast.putExtra(OnTheGoReceiver.EXTRA_ALPHA, value);
		context.sendBroadcast(alphaBroadcast);
	}
}

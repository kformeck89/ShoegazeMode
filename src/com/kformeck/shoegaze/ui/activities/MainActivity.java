package com.kformeck.shoegaze.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

import com.example.onthego.R;
import com.kformeck.shoegaze.notifications.ActivationNotification;
import com.kformeck.shoegaze.notifications.ShoegazeNotification;
import com.kformeck.shoegaze.receivers.ShoegazeReceiver;
import com.kformeck.shoegaze.ui.fragments.SettingsFragment;

public class MainActivity extends Activity {
	private Switch masterSwitch;
	private Intent switchToggledIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction()
		                    .replace(android.R.id.content, new SettingsFragment())
		                    .commit();		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		
		final SharedPreferences prefs = getSharedPreferences(
				getResources().getString(R.string.shoegaze_prefs), MODE_PRIVATE);
		
		getActionBar().setIcon(R.drawable.ic_action_bar);
		
		masterSwitch = (Switch)menu.findItem(
				R.id.actionBarSwitch).getActionView().findViewById(R.id.masterSwitch);
		masterSwitch.setChecked(
				prefs.getBoolean(getResources().getString(R.string.pref_app_state), false));
		masterSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {
				switchToggledIntent = new Intent(
						getResources().getString(R.string.action_shoegaze_state_toggled));
				switchToggledIntent.putExtra(
						getResources().getString(R.string.extra_app_state), checked);
				prefs.edit()
					 .putBoolean(getResources().getString(R.string.pref_app_state), checked)
					 .apply();
				if (checked) {
					if (!ShoegazeNotification.getInstance().isShoegazing()) {
						ActivationNotification.getInstance().startNotification(MainActivity.this, 0);
					}
				} else {
					if (ShoegazeNotification.getInstance().isShoegazing()) {
						Intent stopIntent = new Intent(MainActivity.this, ShoegazeReceiver.class);
						stopIntent.setAction(ShoegazeReceiver.ACTION_STOP);
						sendBroadcast(stopIntent);
					}
					ShoegazeNotification.getInstance().cancelNotification();
				}
				sendBroadcast(switchToggledIntent);
			}	
		});		
		return super.onCreateOptionsMenu(menu);
	}
}

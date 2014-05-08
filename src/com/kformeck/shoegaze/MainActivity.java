package com.kformeck.shoegaze;

import com.example.onthego.R;
import com.kformeck.shoegaze.notifications.ActivationNotification;
import com.kformeck.shoegaze.notifications.ShoegazeNotification;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

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
		
		switchToggledIntent = new Intent(
				getResources().getString(R.string.action_shoegaze_state_toggled));
		
		masterSwitch = (Switch)menu.findItem(
				R.id.actionBarSwitch).getActionView().findViewById(R.id.masterSwitch);
		masterSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {
				switchToggledIntent.putExtra(
						getResources().getString(R.string.extra_app_state), checked);
				prefs.edit()
					 .putBoolean(getResources().getString(R.string.pref_app_state), checked)
					 .apply();
				sendBroadcast(switchToggledIntent);
				if (checked) {
					if (!ShoegazeNotification.getInstance().IsShoegazing()) {
						ActivationNotification.getInstance().startNotification(MainActivity.this, 0);
					}
				} else {
					ActivationNotification.getInstance().cancelNotification();
				}
			}	
		});
		masterSwitch.setChecked(
				prefs.getBoolean(getResources().getString(R.string.pref_app_state), false));
		
		switchToggledIntent.putExtra(
				getResources().getString(R.string.extra_app_state), masterSwitch.isChecked());
		sendBroadcast(switchToggledIntent);
		
		return super.onCreateOptionsMenu(menu);
	}
}

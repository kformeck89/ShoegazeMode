package com.example.onthego;

import com.example.onthego.Notifications.ActivationNotification;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

public class MainActivity extends Activity {

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
		Switch masterSwitch = (Switch)menu.findItem(R.id.actionBarSwitch).getActionView().findViewById(R.id.masterSwitch);
		masterSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {
				if (checked) {
					ActivationNotification.getInstance().startNotification(MainActivity.this, 0);
				} else {
					ActivationNotification.getInstance().cancelNotification();
				}
			}	
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
}

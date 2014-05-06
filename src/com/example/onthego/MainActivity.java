package com.example.onthego;

import com.example.onthego.Notifications.ActivationNotification;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
/*		setContentView(R.layout.activity_main);
		ToggleButton toggleButton = (ToggleButton)findViewById(R.id.toggleButton);
		toggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean checked) {
				if (checked) {
					ActivationNotification.getInstance().startNotification(MainActivity.this, 0);
				} else {
					ActivationNotification.getInstance().cancelNotification();
				}
			}	
		});*/
		
		getFragmentManager().beginTransaction()
		                    .replace(android.R.id.content, new SettingsFragment())
		                    .commit();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_help) {
			// TODO: Write help preferences activity
		}
		return super.onOptionsItemSelected(item);
	}
}

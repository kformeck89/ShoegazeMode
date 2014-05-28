package com.kformeck.shoegaze.ui.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;

import com.example.onthego.R;

public class AboutActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.ic_action_bar);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setTitle(getResources().getString(R.string.settings_about_title));
	}
}

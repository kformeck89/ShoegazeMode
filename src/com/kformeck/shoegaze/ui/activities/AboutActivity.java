package com.kformeck.shoegaze.ui.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.onthego.R;

public class AboutActivity extends Activity {
	private static final String URL_GPLUS_ME = "https://plus.google.com/u/0/+KyleFormeck/posts";
	private static final String URL_GPLUS_FILIP = "https://plus.google.com/u/0/+FilipCernak/about";
	private static final String THIS_PACKAGE = "com.example.onthego";
	private LinearLayout userMe;
	private LinearLayout userFilip;
	
	private void startGooglePlus(String googlePlusUrl) {
		Intent gPlusIntent = new Intent(Intent.ACTION_VIEW);
		gPlusIntent.setData(Uri.parse(googlePlusUrl));
		startActivity(gPlusIntent);
	}
	private void toggleCardBackgroundColor(View view, boolean hasFocus) {
		if (hasFocus) {
			view.setBackgroundColor(getResources().getColor(
					R.color.bg_card_container));
		} else {
			view.setBackgroundColor(getResources().getColor(
					android.R.color.white));
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		ActionBar actionBar = getActionBar();
		actionBar.setIcon(R.drawable.ic_action_bar);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setTitle(getResources().getString(R.string.settings_about_title));
		
		TextView txtVersion = (TextView)findViewById(R.id.txtVersion);
		try {
			txtVersion.setText("v" + getPackageManager().getPackageInfo(
					THIS_PACKAGE, 0).versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		userMe = (LinearLayout)findViewById(R.id.user_me);
		userFilip = (LinearLayout)findViewById(R.id.user_filip);
		
		userMe.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startGooglePlus(URL_GPLUS_ME);
			}
		});
		userMe.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					toggleCardBackgroundColor(view, true);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					toggleCardBackgroundColor(view, false);
				}
				return false;
			}
		});
		userFilip.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startGooglePlus(URL_GPLUS_FILIP);
			}
		});
		userFilip.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					toggleCardBackgroundColor(view, true);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					toggleCardBackgroundColor(view, false);
				}
				return false;
			}
		});
	}
}

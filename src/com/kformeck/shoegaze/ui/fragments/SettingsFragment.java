package com.kformeck.shoegaze.ui.fragments;

import com.example.onthego.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {
	IntentFilter filter;
	final BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			boolean shoegazeState = intent.getBooleanExtra(getResources().getString(
					R.string.extra_app_state), false);
			getPreferenceScreen().findPreference(
					getResources().getString(R.string.settings_key_onboot))
									.setEnabled(shoegazeState);
		}
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings_screen);
		
		filter = new IntentFilter(getResources().getString(
				R.string.action_shoegaze_state_toggled));
	}
	@Override
	public void onStart() {
		super.onStart();
		getActivity().registerReceiver(receiver, filter);
	}
	@Override
	public void onPause() {
		super.onPause();
		getActivity().unregisterReceiver(receiver);
	}
	@Override
	public void onDestroy() {
		if (filter != null) {
			filter = null;
		}
		super.onDestroy();
	}
}

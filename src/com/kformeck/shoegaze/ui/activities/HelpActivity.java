package com.kformeck.shoegaze.ui.activities;

import java.util.ArrayList;
import java.util.List;

import com.example.onthego.R;
import com.kformeck.shoegaze.ui.fragments.GettingStartedFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class HelpActivity extends FragmentActivity {
	private List<Fragment> fragmentList;
	
	private class HelpPagerAdapter extends FragmentPagerAdapter {
		public HelpPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		@Override
		public int getCount() { return 3; }
		@Override
		public Fragment getItem(int position) {
			return fragmentList.get(position);
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		fragmentList = new ArrayList<Fragment>();
		fragmentList.add(new GettingStartedFragment());
		
		ViewPager pager = (ViewPager)findViewById(R.id.pager);
		pager.setAdapter(new HelpPagerAdapter(getSupportFragmentManager()));
	}
}

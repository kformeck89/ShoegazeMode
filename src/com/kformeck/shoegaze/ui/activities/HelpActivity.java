package com.kformeck.shoegaze.ui.activities;

import java.util.ArrayList;
import java.util.List;

import com.example.onthego.R;
import com.kformeck.shoegaze.ui.fragments.GettingStartedFragment;
import com.kformeck.shoegaze.ui.fragments.HelpFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

public class HelpActivity extends FragmentActivity {	
	private class ViewPagerContainer {
		private Fragment fragment;
		private String title;
		
		public ViewPagerContainer(Fragment fragment, String title) {
			this.fragment = fragment;
			this.title = title;
		}
		
		public Fragment getFragment() {
			return fragment;
		}
		public String getTitle() {
			return title;
		}
	}
	private class HelpPagerAdapter extends FragmentStatePagerAdapter {
		private List<ViewPagerContainer> fragmentContainer;
	
		public HelpPagerAdapter(FragmentManager fragmentManager, List<ViewPagerContainer> fragmentContainer) {
			super(fragmentManager);
			this.fragmentContainer = fragmentContainer;
		}
		@Override
		public String getPageTitle(int position) {
			return fragmentContainer.get(position).getTitle();
		}
		@Override
		public int getCount() {
			return fragmentContainer.size();
		}
		@Override
		public Fragment getItem(int position) {
			return fragmentContainer.get(position).getFragment();
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		
		List<ViewPagerContainer> fragmentContainer = new ArrayList<ViewPagerContainer>();
		fragmentContainer.add(new ViewPagerContainer(
				new GettingStartedFragment(), "Getting Started"));
		fragmentContainer.add(new ViewPagerContainer(new HelpFragment(), "Help"));
		
		ViewPager pager = (ViewPager)findViewById(R.id.pager);
		pager.setOffscreenPageLimit(2);
		pager.setAdapter(new HelpPagerAdapter(
				getSupportFragmentManager(), 
				fragmentContainer));
	}
}

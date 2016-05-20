package com.gpsmobitrack.gpstracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gpstracker.pro.R;
import com.viewpagerindicator.CirclePageIndicator;

public class MainActivity extends Activity {

	ViewPager viewPager;
	MyPagerAdapter myPagerAdapter;
	CirclePageIndicator mIndicator;
	private SharedPreferences prefs;
	private SharedPreferences.Editor editor;
	ImageView skip;
	private int totalCount;
	int NumberOfPages = 5;
	int[] res = { 
			R.drawable.first_screen,
			R.drawable.sec_screen,
			R.drawable.third_screen,
			R.drawable.fourth_screen,
			R.drawable.fith_screen
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		prefs = getPreferences(Context.MODE_PRIVATE);
		editor = prefs.edit();
		setContentView(R.layout.activity_main_pager);
		viewPager = (ViewPager)findViewById(R.id.myviewpager);
		skip = (ImageView)findViewById(R.id.imageButton_skip);
		mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
		myPagerAdapter = new MyPagerAdapter();
		viewPager.setAdapter(myPagerAdapter);
		mIndicator.setViewPager(viewPager);
		mIndicator.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				if(position == 4){
					finish();
					Intent intent = new Intent(MainActivity.this,SplashScreen.class);
					startActivity(intent);
				}
			}
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}
			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
		skip.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				Intent intent = new Intent(MainActivity.this,SplashScreen.class);
				startActivity(intent);
			}
		});
		count();
	}
	public void count(){
		totalCount = prefs.getInt("counter", 0);
		totalCount++;
		editor.putInt("counter", totalCount);
		editor.commit();
		if(totalCount>1){
			finish();
			Intent intent = new Intent(MainActivity.this,SplashScreen.class);
			startActivity(intent);
		}
	}
	private class MyPagerAdapter extends PagerAdapter{
		ViewHolderItem viewHolder;
		LayoutInflater mLayoutInflater;
		public MyPagerAdapter(){
			mLayoutInflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public int getCount() {
			return NumberOfPages;
		}
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((RelativeLayout) object);
		}
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View itemView = mLayoutInflater.inflate(R.layout.viewpager_image, container, false);
			viewHolder = new ViewHolderItem();
			viewHolder.imageView=(ImageView)itemView.findViewById(R.id.imageView1);
			viewHolder.imageView.setImageResource(res[position]);
			itemView.setTag(viewHolder);
			container.addView(itemView);
			return itemView;
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((RelativeLayout)object);
		}
	}
	class ViewHolderItem {
		ImageView imageView;
	}
}
package com.chang.news;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chang.news.fragment.ThreeFragment;
import com.chang.news.fragment.SecondFragment;
import com.chang.news.fragment.FourFragment;
import com.chang.news.fragment.FirstFragment;
import com.imooc.tab03.R;

public class MainActivity extends FragmentActivity implements OnClickListener
{
	private ViewPager mViewPager;
	private FragmentPagerAdapter mAdapter;
	private List<Fragment> mFragments;

	private LinearLayout mTabInform;
	private LinearLayout mTabPsychology;
	private LinearLayout mTabLecture;
	private LinearLayout mTabNews;
	
	private TextView mTextViewInform;
	private TextView mTextViewPhychology;
	private TextView mTextViewLecture;
	private TextView mTextViewNews;

	private View mViewInform;
	private View mViewPhychology;
	private View mViewLecture;
	private View mViewNews;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		initView();
		
		setSelect(0);
	}

	
	private void initView()
	{
		mViewPager = (ViewPager) findViewById(R.id.id_viewpager);

		mTabInform = (LinearLayout) findViewById(R.id.id_tab_inform);
		mTabPsychology = (LinearLayout) findViewById(R.id.id_psychology);
		mTabLecture = (LinearLayout) findViewById(R.id.id_tab_lecture);
		mTabNews = (LinearLayout) findViewById(R.id.id_tab_news);
		
		mTextViewInform = (TextView) findViewById(R.id.id_tab_inform_tv);
		mTextViewPhychology = (TextView) findViewById(R.id.id_tab_psychology_tv);
		mTextViewLecture = (TextView) findViewById(R.id.id_tab_lecture_tv);
		mTextViewNews = (TextView) findViewById(R.id.id_tab_news_tv);

		mViewInform = (View) findViewById(R.id.id_tab_inform_view);
		mViewPhychology = (View) findViewById(R.id.id_tab_psychology_view);
		mViewLecture = (View) findViewById(R.id.id_tab_lecture_view);
		mViewNews = (View) findViewById(R.id.id_tab_news_view);

		mFragments = new ArrayList<Fragment>();
		Fragment mTab01 = new FirstFragment();
		Fragment mTab02 = new SecondFragment();
		Fragment mTab03 = new ThreeFragment();
		Fragment mTab04 = new FourFragment();
		
		mFragments.add(mTab01);
		mFragments.add(mTab02);
		mFragments.add(mTab03);
		mFragments.add(mTab04);  
		
		mTabInform.setOnClickListener(this);
		mTabPsychology.setOnClickListener(this);
		mTabLecture.setOnClickListener(this);
		mTabNews.setOnClickListener(this);
		
		//设置viewpager保留多少个显示界面
        mViewPager.setOffscreenPageLimit(3);

		mAdapter = new FragmentPagerAdapter(getSupportFragmentManager())
		{

			@Override
			public int getCount()
			{
				return mFragments.size();
			}

			@Override
			public Fragment getItem(int arg0)
			{
				return mFragments.get(arg0);
			}
		};
		mViewPager.setAdapter(mAdapter);
		
		mViewPager.setOnPageChangeListener(new OnPageChangeListener()
		{
			
			@Override
			public void onPageSelected(int arg0)
			{
				int currentItem = mViewPager.getCurrentItem();
				setTab(currentItem);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0)
			{
				// TODO Auto-generated method stub
				
			}
		});
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.id_tab_inform:
			setSelect(0);
			break;
		case R.id.id_psychology:
			setSelect(1);
			break;
		case R.id.id_tab_lecture:
			setSelect(2);
			break;
		case R.id.id_tab_news:
			setSelect(3);
			break;

		default:
			break;
		}
	}

	private void setSelect(int i)
	{
		setTab(i);
		mViewPager.setCurrentItem(i);
	}

	private void setTab(int i)
	{
		resetView();
		// 设置标题的下划线
		// 切换内容区域
		switch (i)
		{
		case 0:
			mViewInform.setVisibility(View.VISIBLE);
			mTextViewInform.setTextColor(Color.parseColor("#4D4DFF"));
			break;
		case 1:
			mViewPhychology.setVisibility(View.VISIBLE);
			mTextViewPhychology.setTextColor(Color.parseColor("#4D4DFF"));
			break;
		case 2:
			mViewLecture.setVisibility(View.VISIBLE);
			mTextViewLecture.setTextColor(Color.parseColor("#4D4DFF"));
			break;
		case 3:
			mViewNews.setVisibility(View.VISIBLE);
			mTextViewNews.setTextColor(Color.parseColor("#4D4DFF"));
			break;
		}
	}

	/**
	 * 隐藏所有标题的下划线
	 */
	private void resetView()
	{
		mViewInform.setVisibility(View.INVISIBLE);
		mViewPhychology.setVisibility(View.INVISIBLE);
		mViewLecture.setVisibility(View.INVISIBLE);
		mViewNews.setVisibility(View.INVISIBLE);
		
		mTextViewInform.setTextColor(Color.parseColor("#FFFFFF"));
		mTextViewPhychology.setTextColor(Color.parseColor("#FFFFFF"));
		mTextViewLecture.setTextColor(Color.parseColor("#FFFFFF"));
		mTextViewNews.setTextColor(Color.parseColor("#FFFFFF"));
	}

}

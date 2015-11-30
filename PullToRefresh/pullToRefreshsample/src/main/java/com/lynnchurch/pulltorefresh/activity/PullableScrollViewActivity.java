package com.lynnchurch.pulltorefresh.activity;

import com.lynnchurch.pulltorefresh.MyPullListener;
import com.lynnchurch.pulltorefresh.R;
import com.jingchen.pulltorefresh.PullToRefreshLayout;

import android.app.Activity;
import android.os.Bundle;

public class PullableScrollViewActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scrollview);
		((PullToRefreshLayout) findViewById(R.id.refresh_view))
		.setOnPullListener(new MyPullListener());
	}
}

package com.jingchen.pulltorefresh.activity;

import com.jingchen.pulltorefresh.MyPullListener;
import com.jingchen.pulltorefresh.R;
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

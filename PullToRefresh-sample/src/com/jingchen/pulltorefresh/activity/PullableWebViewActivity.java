package com.jingchen.pulltorefresh.activity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.jingchen.pulltorefresh.MyPullListener;
import com.jingchen.pulltorefresh.R;
import com.jingchen.pulltorefresh.PullToRefreshLayout;

public class PullableWebViewActivity extends Activity
{
	WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		((PullToRefreshLayout) findViewById(R.id.refresh_view))
				.setOnPullListener(new MyPullListener());
		webView = (WebView) findViewById(R.id.content_view);
		webView.loadUrl("http://blog.csdn.net/zhongkejingwang");
	}
}

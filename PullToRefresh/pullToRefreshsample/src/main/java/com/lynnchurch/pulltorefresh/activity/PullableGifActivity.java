package com.lynnchurch.pulltorefresh.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import com.lynnchurch.pulltorefresh.MyAdapter;
import com.lynnchurch.pulltorefresh.MyPullListener;
import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.lynnchurch.pulltorefresh.R;

import android.app.Activity;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class PullableGifActivity extends Activity
{

	private ListView listView;
	private PullToRefreshLayout ptrl;
	private boolean isFirstIn=true;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listview);
		ptrl = ((PullToRefreshLayout) findViewById(R.id.refresh_view));
		ptrl.setOnPullListener(new MyPullListener());
		// 设置带gif动画的上拉头与下拉头
		try
		{
			ptrl.setGifRefreshView(new GifDrawable(getResources(), R.drawable.anim));
			ptrl.setGifLoadmoreView(new GifDrawable(getResources(), R.drawable.anim));
			
		} catch (NotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		listView = (ListView) ptrl.getPullableView();
		initListView();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
		// 第一次进入自动刷新
		if (isFirstIn)
		{
			ptrl.autoRefresh();
			isFirstIn = false;
		}
	}

	/**
	 * ListView初始化方法
	 */
	private void initListView()
	{
		List<String> items = new ArrayList<String>();
		for (int i = 0; i < 30; i++)
		{
			items.add("这里是item " + i);
		}
		MyAdapter adapter = new MyAdapter(this, items);
		listView.setAdapter(adapter);
		listView.setOnItemLongClickListener(new OnItemLongClickListener()
		{

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				Toast.makeText(
						PullableGifActivity.this,
						"LongClick on "
								+ parent.getAdapter().getItemId(position),
						Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				Toast.makeText(PullableGifActivity.this,
						" Click on " + parent.getAdapter().getItemId(position),
						Toast.LENGTH_SHORT).show();
			}
		});
	}

}

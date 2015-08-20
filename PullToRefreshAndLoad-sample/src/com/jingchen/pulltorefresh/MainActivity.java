package com.jingchen.pulltorefresh;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.jingchen.pulltorefresh.PullToRefreshLayout.OnPullProcessListener;
import com.jingchen.pulltorefresh.activity.PullableGifActivity;
import com.jingchen.pulltorefresh.activity.PullableListViewActivity;
import com.jingchen.pulltorefresh.PullToRefreshLayout;

/**
 * 更多详解见博客http://blog.csdn.net/zhongkejingwang/article/details/38868463
 * 
 * @author 陈靖
 * 
 */
public class MainActivity extends Activity {
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		PullToRefreshLayout pullToRefreshLayout = ((PullToRefreshLayout) findViewById(R.id.refresh_view));
		pullToRefreshLayout.setOnPullListener(new MyPullListener());
		pullToRefreshLayout.setOnRefreshProcessListener(new MyOnPullProcessListener());
		listView = (ListView) pullToRefreshLayout.getPullableView();
		initListView();
	}

	/**
	 * ListView初始化方法
	 */
	private void initListView() {
		List<String> items = new ArrayList<String>();
		items.add("可下拉刷新上拉加载的ListView");
		items.add("带Gif动画的可下拉刷新上拉加载的ListView");
		items.add("可下拉刷新上拉加载的GridView");
		items.add("可下拉刷新上拉加载的ExpandableListView");
		items.add("可下拉刷新上拉加载的SrcollView");
		items.add("可下拉刷新上拉加载的WebView");
		items.add("可下拉刷新上拉加载的ImageView");
		items.add("可下拉刷新上拉加载的TextView");
		MyAdapter adapter = new MyAdapter(this, items);
		listView.setAdapter(adapter);
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(
						MainActivity.this,
						" LongClick on "
								+ parent.getAdapter().getItemId(position),
						Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent intent = new Intent();
				switch (position) {
				case 0:
					intent.setClass(MainActivity.this,
							PullableListViewActivity.class);
					startActivity(intent);
					break;
				case 1:
					intent.setClass(MainActivity.this,
							PullableGifActivity.class);
					startActivity(intent);
					break;
				case 2:
					// intent.setClass(MainActivity.this,
					// PullableGridViewActivity.class);
					break;
				case 3:
					// intent.setClass(MainActivity.this,
					// PullableExpandableListViewActivity.class);
					break;
				case 4:
					// intent.setClass(MainActivity.this,
					// PullableScrollViewActivity.class);
					break;
				case 5:
					// intent.setClass(MainActivity.this,
					// PullableWebViewActivity.class);
					break;
				case 6:
					// intent.setClass(MainActivity.this,
					// PullableImageViewActivity.class);
					break;
				case 7:
					// intent.setClass(MainActivity.this,
					// PullableTextViewActivity.class);
					break;

				default:
					break;
				}
			}
		});
	}


	public class MyOnPullProcessListener implements OnPullProcessListener {

		@Override
		public void onPrepare(View v,int which) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStart(View v,int which) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onHandling(View v,int which) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFinish(View v,int which) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPull(View v,float pullDistance,int which) {
			// TODO Auto-generated method stub
			Log.e("pullDistance", "" + pullDistance);
		}

	}
}

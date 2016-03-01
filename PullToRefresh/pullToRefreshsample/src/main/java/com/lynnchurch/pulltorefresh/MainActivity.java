package com.lynnchurch.pulltorefresh;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.jingchen.pulltorefresh.PullToRefreshLayout.OnPullProcessListener;
import com.lynnchurch.pulltorefresh.activity.PullableExpandableListViewActivity;
import com.lynnchurch.pulltorefresh.activity.PullableGifActivity;
import com.lynnchurch.pulltorefresh.activity.PullableGridViewActivity;
import com.lynnchurch.pulltorefresh.activity.PullableImageViewActivity;
import com.lynnchurch.pulltorefresh.activity.PullableListViewActivity;
import com.lynnchurch.pulltorefresh.activity.PullableRecyclerViewActivity;
import com.lynnchurch.pulltorefresh.activity.PullableScrollViewActivity;
import com.lynnchurch.pulltorefresh.activity.PullableTextViewActivity;
import com.lynnchurch.pulltorefresh.activity.PullableWebViewActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 更多详解见博客http://blog.csdn.net/zhongkejingwang/article/details/38868463
 *
 * @author 陈靖
 */
public class MainActivity extends Activity
{
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
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
    private void initListView()
    {
        List<String> items = new ArrayList<>();
        items.add(getString(R.string.listview));
        items.add(getString(R.string.recyclerview));
        items.add(getString(R.string.gif_listview));
        items.add(getString(R.string.grid_view));
        items.add(getString(R.string.expandable_listview));
        items.add(getString(R.string.scrollview));
        items.add(getString(R.string.webview));
        items.add(getString(R.string.imageview));
        items.add(getString(R.string.textview));
        MyAdapter adapter = new MyAdapter(this, items);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new OnItemLongClickListener()
        {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id)
            {
                Toast.makeText(
                        MainActivity.this,
                        " LongClick on "
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

                Intent intent = new Intent();
                switch (position)
                {
                    case 0:
                        intent.setClass(MainActivity.this,
                                PullableListViewActivity.class);
                        break;
                    case 1:
                        intent.setClass(MainActivity.this,
                                PullableRecyclerViewActivity.class);
                        break;
                    case 2:
                        intent.setClass(MainActivity.this,
                                PullableGifActivity.class);
                        break;
                    case 3:
                        intent.setClass(MainActivity.this,
                                PullableGridViewActivity.class);
                        break;
                    case 4:
                        intent.setClass(MainActivity.this,
                                PullableExpandableListViewActivity.class);
                        break;
                    case 5:
                        intent.setClass(MainActivity.this,
                                PullableScrollViewActivity.class);
                        break;
                    case 6:
                        intent.setClass(MainActivity.this,
                                PullableWebViewActivity.class);
                        break;
                    case 7:
                        intent.setClass(MainActivity.this,
                                PullableImageViewActivity.class);
                        break;
                    case 8:
                        intent.setClass(MainActivity.this,
                                PullableTextViewActivity.class);
                        break;

                    default:
                        break;
                }
                startActivity(intent);
            }
        });
    }


    public class MyOnPullProcessListener implements OnPullProcessListener
    {

        @Override
        public void onPrepare(View v, int which)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStart(View v, int which)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void onHandling(View v, int which)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void onFinish(View v, int which)
        {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPull(View v, float pullDistance, int which)
        {
            // TODO Auto-generated method stub
        }

    }
}

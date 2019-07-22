# DEPRECATED 建议选择https://github.com/CymChad/BaseRecyclerViewAdapterHelper

# PullToRefresh
一个可下拉刷新与上拉加载更多的库（可轻松实现滑动到底部自动加载的功能），可对RecyclerView、ListView、GridView、WebView、ScrollView等几乎所有常用的View类型进行此操作，能够自动下拉刷新，并且还提供了GIF动画的上拉与下拉头，也可自定义上拉头与下拉头。（本库基于https://github.com/jingchenUSTC/PullToRefreshAndLoad ，特此感谢）

##效果图

![image](https://github.com/lynnchurch/PullToRefresh/blob/master/images/sample_picture.gif)

##使用示例代码

布局文件
```xml
<com.jingchen.pulltorefresh.PullToRefreshLayout         
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/refresh_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#FFF" >

    <com.jingchen.pulltorefresh.PullableListView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:divider="#AAA"
        android:dividerHeight="5dp" />

</com.jingchen.pulltorefresh.PullToRefreshLayout>
```

使用代码
```java
package com.jingchen.pulltorefresh.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import com.jingchen.pulltorefresh.MyAdapter;
import com.jingchen.pulltorefresh.MyPullListener;
import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.jingchen.pulltorefresh.R;
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
        // 此处设置下拉刷新或上拉加载更多监听器
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

 /**
 * 下拉刷新与上拉加载更多监听器
 */
 public class MyPullListener implements OnPullListener {

     @Override
     public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
        // 下拉刷新操作
         new Handler() {
             @Override
             public void handleMessage(Message msg) {
                 // 千万别忘了告诉控件刷新完毕了哦！
                 pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
             }
         }.sendEmptyMessageDelayed(0, 5000);
     }

     @Override
     public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
         // 加载更多操作
         new Handler() {
             @Override
             public void handleMessage(Message msg) {
                 // 千万别忘了告诉控件加载完毕了哦！
                 pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
             }
         }.sendEmptyMessageDelayed(0, 5000);
     }
 }
}
```

package com.lynnchurch.pulltorefresh.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.jingchen.pulltorefresh.PullToRefreshLayout;
import com.jingchen.pulltorefresh.PullableRecyclerView;
import com.jingchen.pulltorefresh.WrapRecyclerView;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.lynnchurch.pulltorefresh.MyPullListener;
import com.lynnchurch.pulltorefresh.R;
import com.lynnchurch.pulltorefresh.RecyclerAdapter;

import java.util.ArrayList;

public class PullableRecyclerViewActivity extends Activity
{
    private WrapRecyclerView recycler_view;
    private View mFootView;
    CircleProgressBar mCircleProgressBar;
    private PullToRefreshLayout ptrl;
    private ArrayList<String> mData = new ArrayList<>();
    private RecyclerAdapter mAdapter;
    private boolean isFirstIn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        ptrl = ((PullToRefreshLayout) findViewById(R.id.refresh_view));
        mFootView = LayoutInflater.from(this).inflate(R.layout.foot_loadmore, null);
        mCircleProgressBar = (CircleProgressBar) mFootView.findViewById(R.id.progressBar);
        ptrl.setPullUpEnable(false);
        ((PullableRecyclerView) ptrl.getPullableView()).setOnScrollUpListener(new PullableRecyclerView.OnScrollUpListener()
        {
            @Override
            public void onScrollUp(int position)
            {
                if (position == mData.size()-1)
                {
                    mCircleProgressBar.setVisibility(View.VISIBLE);
                    new Handler()
                    {
                        @Override
                        public void handleMessage(Message msg)
                        {
                            // 千万别忘了告诉控件加载完毕了哦！
                            int size = mData.size();
                            for (int i = size; i < size + 10; i++)
                            {
                                mData.add("这里是item " + i);
                            }
                            mCircleProgressBar.setVisibility(View.GONE);
                        }
                    }.sendEmptyMessageDelayed(0, 3000);
                    return;
                }

            }
        });
        ptrl.setOnPullListener(new MyPullListener());
        recycler_view = (WrapRecyclerView) ptrl.getPullableView();
        recycler_view.addFootView(mFootView);
        initRecyclerView();
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
    private void initRecyclerView()
    {
        for (int i = 0; i < 10; i++)
        {
            mData.add("这里是item " + i);
        }
        // 设置列表
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RecyclerAdapter(this, mData);
        mAdapter.setmOnItemClickListener(new RecyclerAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                Toast.makeText(PullableRecyclerViewActivity.this,
                        " Click on " + mData.get(position),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLoginClick(View view, int position)
            {
                Toast.makeText(
                        PullableRecyclerViewActivity.this,
                        "LongClick on "
                                + mData.get(position),
                        Toast.LENGTH_SHORT).show();
            }
        });
        recycler_view.setAdapter(mAdapter);
        ptrl.autoRefresh();
    }


}

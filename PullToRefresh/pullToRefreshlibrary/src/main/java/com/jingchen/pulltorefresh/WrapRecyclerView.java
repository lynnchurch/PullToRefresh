package com.jingchen.pulltorefresh;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class WrapRecyclerView extends RecyclerView
{

    private ArrayList<View> mHeaderViews = new ArrayList<>();

    private ArrayList<View> mFootViews = new ArrayList<>();

    private Adapter mAdapter;

    public WrapRecyclerView(Context context)
    {
        super(context);
    }

    public WrapRecyclerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public WrapRecyclerView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void addHeaderView(View view)
    {
        mHeaderViews.clear();
        mHeaderViews.add(view);
        if (mAdapter != null)
        {
            if (!(mAdapter instanceof RecyclerWrapAdapter))
            {
                mAdapter = new RecyclerWrapAdapter(mHeaderViews, mFootViews, mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void addFootView(View view)
    {
        mFootViews.clear();
        mFootViews.add(view);
        if (mAdapter != null)
        {
            if (!(mAdapter instanceof RecyclerWrapAdapter))
            {
                mAdapter = new RecyclerWrapAdapter(mHeaderViews, mFootViews, mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void setAdapter(Adapter adapter)
    {

        if (mHeaderViews.isEmpty() && mFootViews.isEmpty())
        {
            super.setAdapter(adapter);
        } else
        {
            adapter = new RecyclerWrapAdapter(mHeaderViews, mFootViews, adapter);
            super.setAdapter(adapter);
        }
        mAdapter = adapter;
    }

    /**
     * 获取页眉的高度
     *
     * @return
     */
    public int getHeaderHeight()
    {
        int height = 0;
        if (!mHeaderViews.isEmpty())
        {
            for (int i = 0; i < mHeaderViews.size(); i++)
            {
                mHeaderViews.get(i).measure(0, 0);
                height += mHeaderViews.get(i).getHeight();
            }
        }
        return height;
    }
}

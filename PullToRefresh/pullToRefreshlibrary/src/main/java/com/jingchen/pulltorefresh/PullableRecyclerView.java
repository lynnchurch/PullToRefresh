package com.jingchen.pulltorefresh;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;


public class PullableRecyclerView extends WrapRecyclerView implements Pullable
{
    public int mFirstVisiblePosition = -1;
    public int mLastVisiblePosition = -1;

    public PullableRecyclerView(Context context)
    {
        this(context, null, 0);
    }

    public PullableRecyclerView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public PullableRecyclerView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean canPullDown()
    {
        LayoutManager lm = getLayoutManager();
        mFirstVisiblePosition = getFirstVisibleItemPosition();
        int count = getAdapter().getItemCount();
        if (0 == count)
        {
            // 没有item的时候也可以下拉刷新
            return true;
        } else if (lm.findViewByPosition(mFirstVisiblePosition).getTop() == 0 && mFirstVisiblePosition == 0)
        {
            // 滑到ListView的顶部了
            return true;
        } else
            return false;
    }

    @Override
    public boolean canPullUp()
    {
        LayoutManager lm = getLayoutManager();
        mFirstVisiblePosition = getFirstVisibleItemPosition();
        mLastVisiblePosition = getLastVisibleItemPosition();
        int count = getAdapter().getItemCount();
        if (0 == count)
        {
            // 没有item的时候也可以上拉加载
            return true;
        } else if (mLastVisiblePosition == (count - 1))
        {
            // 滑到底部了
            if (lm.findViewByPosition(count - 1).getBottom() <= getMeasuredHeight())
            {
                return true;
            }
        }
        return false;
    }


    /**
     * 获取顶部可见项的位置
     *
     * @return
     */
    private int getFirstVisibleItemPosition()
    {
        LayoutManager lm = getLayoutManager();
        int firstVisibleItemPosition = 0;
        if (lm instanceof LinearLayoutManager)
        {
            firstVisibleItemPosition = ((LinearLayoutManager) lm).findFirstVisibleItemPosition();
        } else if (lm instanceof GridLayoutManager)
        {
            firstVisibleItemPosition = ((GridLayoutManager) lm).findFirstVisibleItemPosition();
        } else if (lm instanceof StaggeredGridLayoutManager)
        {
            int positions[] = new int[1];
            ((StaggeredGridLayoutManager) lm).findFirstVisibleItemPositions(positions);
            firstVisibleItemPosition = positions[0];
        }
        return firstVisibleItemPosition;
    }

    /**
     * 获取底部可见项的位置
     *
     * @return
     */
    private int getLastVisibleItemPosition()
    {
        LayoutManager lm = getLayoutManager();
        int lastVisibleItemPosition = 0;
        if (lm instanceof LinearLayoutManager)
        {
            lastVisibleItemPosition = ((LinearLayoutManager) lm).findLastVisibleItemPosition();
        } else if (lm instanceof GridLayoutManager)
        {
            lastVisibleItemPosition = ((GridLayoutManager) lm).findLastVisibleItemPosition();
        } else if (lm instanceof StaggeredGridLayoutManager)
        {
            int positions[] = new int[1];
            ((StaggeredGridLayoutManager) lm).findFirstVisibleItemPositions(positions);
            ((StaggeredGridLayoutManager) lm).findLastVisibleItemPositions(positions);
            lastVisibleItemPosition = positions[0];
        }
        return lastVisibleItemPosition;
    }

    public int getFirstVisiblePosition()
    {
        return mFirstVisiblePosition;
    }

    public int getLastVisiblePosition()
    {
        return mLastVisiblePosition;
    }
}

package com.jingchen.pulltorefresh;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class RecyclerWrapAdapter extends RecyclerView.Adapter implements WrapperAdapter
{

    private RecyclerView.Adapter mAdapter;

    private ArrayList<View> mHeaderViews;

    private ArrayList<View> mFootViews;

    static final ArrayList<View> EMPTY_INFO_LIST =
            new ArrayList<>();


    public RecyclerWrapAdapter(ArrayList<View> headerViews, ArrayList<View> footViews, RecyclerView.Adapter adapter)
    {
        mAdapter = adapter;
        if (null == headerViews)
        {
            mHeaderViews = EMPTY_INFO_LIST;
        } else
        {
            mHeaderViews = headerViews;
        }
        if (null == footViews)
        {
            mFootViews = EMPTY_INFO_LIST;
        } else
        {
            mFootViews = footViews;
        }
    }

    public int getHeadersCount()
    {
        return mHeaderViews.size();
    }

    public int getFootersCount()
    {
        return mFootViews.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if (viewType == RecyclerView.INVALID_TYPE)
        {
            mHeaderViews.get(0).setLayoutParams(parent.getLayoutParams());
            return new HeaderViewHolder(mHeaderViews.get(0));
        } else if (viewType == RecyclerView.INVALID_TYPE - 1)
        {
            mFootViews.get(0).setLayoutParams(parent.getLayoutParams());
            return new HeaderViewHolder(mFootViews.get(0));
        }
        return mAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        int numHeaders = getHeadersCount();
        if (position < numHeaders)
        {
            return;
        }
        int adjPosition = position - numHeaders;
        int adapterCount = 0;
        if (mAdapter != null)
        {
            adapterCount = mAdapter.getItemCount();
            if (adjPosition < adapterCount)
            {
                mAdapter.onBindViewHolder(holder, adjPosition);
                return;
            }
        }
    }

    @Override
    public int getItemCount()
    {
        if (mAdapter != null)
        {
            return getHeadersCount() + getFootersCount() + mAdapter.getItemCount();
        } else
        {
            return getHeadersCount() + getFootersCount();
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        int numHeaders = getHeadersCount();
        if (position < numHeaders)
        {
            return RecyclerView.INVALID_TYPE;
        }
        int adjPosition = position - numHeaders;
        int adapterCount = 0;
        if (mAdapter != null)
        {
            adapterCount = mAdapter.getItemCount();
            if (adjPosition < adapterCount)
            {
                return mAdapter.getItemViewType(adjPosition);
            }
        }
        return RecyclerView.INVALID_TYPE - 1;
    }


    @Override
    public long getItemId(int position)
    {
        int numHeaders = getHeadersCount();
        if (mAdapter != null && position >= numHeaders)
        {
            int adjPosition = position - numHeaders;
            int adapterCount = mAdapter.getItemCount();
            if (adjPosition < adapterCount)
            {
                return mAdapter.getItemId(adjPosition);
            }
        }
        return -1;
    }


    @Override
    public RecyclerView.Adapter getWrappedAdapter()
    {
        return mAdapter;
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder
    {
        public HeaderViewHolder(View itemView)
        {
            super(itemView);
        }
    }
}

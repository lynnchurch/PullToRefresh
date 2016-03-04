package com.lynnchurch.pulltorefresh;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final int NORMAL_ITEM = 0;
    private static final int BOTTOM_ITEM = 1;
    private Context mActivity;
    private ArrayList<String> mData;
    private OnItemClickListener mOnItemClickListener;
    private LoadmoreViewHolder mLoadmore;
    private OnLoadmoreListener mOnLoadmoreListener;
    private int mLastPosition;


    public RecyclerAdapter(Context context, ArrayList<String> data)
    {
        mActivity = context;
        mData = data;
        mData.add("");
        mLastPosition = mData.size() - 2;
    }


    @Override
    public int getItemViewType(int position)
    {
        if (mData.size() - 1 == position)
        {
            return BOTTOM_ITEM;
        }
        return NORMAL_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if (NORMAL_ITEM == viewType)
        {
            return new NormalViewHolder(LayoutInflater.from(
                    mActivity).inflate(R.layout.recyclerview_list_item, parent,
                    false));
        } else
        {
            mLoadmore = new LoadmoreViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.loadmore, parent, false));
            return mLoadmore;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {
        if (holder instanceof NormalViewHolder)
        {
            final NormalViewHolder viewHolder = (NormalViewHolder) holder;
            if (null != mOnItemClickListener)
            {
                viewHolder.itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        mOnItemClickListener.onItemClick(viewHolder.itemView, position);
                    }
                });
                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        mOnItemClickListener.onItemClick(viewHolder.itemView, position);
                        return false;
                    }
                });
            }
            viewHolder.tv_name.setText(mData.get(position));
        }

        if (position > mLastPosition && null != mOnLoadmoreListener)
        {
            mOnLoadmoreListener.onLoadmore();
            mLastPosition = mData.size() - 1;
        }
    }

    @Override
    public int getItemCount()
    {
        return mData.size();
    }


    class NormalViewHolder extends RecyclerView.ViewHolder
    {
        View itemView;
        TextView tv_name;

        public NormalViewHolder(View v)
        {
            super(v);
            itemView = v;
            tv_name = (TextView) v.findViewById(R.id.tv_name);
        }
    }

    /**
     * 加载更多
     */
    class LoadmoreViewHolder extends RecyclerView.ViewHolder
    {
        View itemView;
        TextView tv_hint;
        CircleProgressBar progressBar;

        public LoadmoreViewHolder(View v)
        {
            super(v);
            itemView = v;
            tv_hint = (TextView) v.findViewById(R.id.tv_hint);
            progressBar = (CircleProgressBar) v.findViewById(R.id.progressBar);
        }
    }

    /**
     * 当加载失败
     */
    public void onFailed()
    {
        mLoadmore.progressBar.setVisibility(View.GONE);
        mLoadmore.tv_hint.setVisibility(View.VISIBLE);
        mLoadmore.tv_hint.setText("点击重试");
        mLoadmore.tv_hint.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (null != mOnLoadmoreListener)
                {
                    mLoadmore.progressBar.setVisibility(View.VISIBLE);
                    mLoadmore.tv_hint.setVisibility(View.GONE);
                    mOnLoadmoreListener.onLoadmore();
                }
            }
        });
    }

    /**
     * 当没有更多
     */
    public void onNothing()
    {
        mLoadmore.progressBar.setVisibility(View.GONE);
        mLoadmore.tv_hint.setVisibility(View.VISIBLE);
        mLoadmore.tv_hint.setText("没有更多");
        mLoadmore.tv_hint.setOnClickListener(null);
    }


    /**
     * 设置Item监听器
     *
     * @param onItemClickListener
     */
    public void setmOnItemClickListener(OnItemClickListener onItemClickListener)
    {
        mOnItemClickListener = onItemClickListener;
    }

    /**
     * Item点击监听
     */
    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);

        void onItemLoginClick(View view, int position);
    }

    /**
     * 设置加载更多监听
     *
     * @param listener
     */
    public void setOnLoadmoreListener(OnLoadmoreListener listener)
    {
        mOnLoadmoreListener = listener;
    }

    /**
     * 加载更多监听
     */
    public interface OnLoadmoreListener
    {
        void onLoadmore();
    }

}
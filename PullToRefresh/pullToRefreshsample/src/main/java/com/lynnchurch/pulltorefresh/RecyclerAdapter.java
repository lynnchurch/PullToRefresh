package com.lynnchurch.pulltorefresh;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private Context mActivity;
    private ArrayList<String> mData;
    private OnItemClickListener mOnItemClickListener;


    public RecyclerAdapter(Context context, ArrayList<String> data)
    {
        mActivity = context;
        mData = data;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        RecyclerView.ViewHolder holder = null;
        holder = new BaseViewHolder(LayoutInflater.from(
                mActivity).inflate(R.layout.recyclerview_list_item, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position)
    {
        final BaseViewHolder viewHolder = (BaseViewHolder) holder;
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

    @Override
    public int getItemCount()
    {
        return mData.size();
    }


    class BaseViewHolder extends RecyclerView.ViewHolder
    {
        View itemView;
        TextView tv_name;

        public BaseViewHolder(View v)
        {
            super(v);
            itemView = v;
            tv_name = (TextView) v.findViewById(R.id.tv_name);
        }
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
}
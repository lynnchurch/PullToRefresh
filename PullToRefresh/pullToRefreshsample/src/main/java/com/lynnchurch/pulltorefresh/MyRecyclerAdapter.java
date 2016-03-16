package com.lynnchurch.pulltorefresh;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jingchen.pulltorefresh.WrapRecyclerView;

import java.util.ArrayList;

public class MyRecyclerAdapter extends BaseAutoLoadMoreAdapter<String>
{


    public MyRecyclerAdapter(Context context, WrapRecyclerView recyclerView, ArrayList<String> data)
    {
        super(context, recyclerView, data);
    }

    @Override
    public BaseViewHolder onCreateBaseViewHolder(ViewGroup parent, int viewType)
    {
        return new MyBaseViewHolder(LayoutInflater.from(
                mContext).inflate(R.layout.recyclerview_list_item, parent,
                false));

    }

    @Override
    public int getItemHeight(RecyclerView.ViewHolder holder)
    {
        holder.itemView.measure(0, 0);
        return holder.itemView.getMeasuredHeight();
    }

    @Override
    public void onBindBaseViewHolder(BaseViewHolder holder, int position)
    {
        MyBaseViewHolder myHolder = (MyBaseViewHolder) holder;
        myHolder.tv_name.setText(mData.get(position));
    }

    class MyBaseViewHolder extends BaseViewHolder
    {
        TextView tv_name;

        public MyBaseViewHolder(View v)
        {
            super(v);
            tv_name = (TextView) v.findViewById(R.id.tv_name);
        }
    }
}
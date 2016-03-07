package com.lynnchurch.pulltorefresh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MyRecyclerAdapter extends BaseAutoLoadMoreAdapter<String>
{


    public MyRecyclerAdapter(Context context, ArrayList<String> data)
    {
        super(context, data);
    }

    @Override
    public NormalViewHolder onCreateNormalViewHolder(ViewGroup parent, int viewType)
    {
        return new MyNormalViewHolder(LayoutInflater.from(
                mContext).inflate(R.layout.recyclerview_list_item, parent,
                false));

    }

    @Override
    public void onBindNormalViewHolder(NormalViewHolder holder, int position)
    {
        MyNormalViewHolder myHolder = (MyNormalViewHolder) holder;
        myHolder.tv_name.setText(mData.get(position));
    }

    class MyNormalViewHolder extends NormalViewHolder
    {
        TextView tv_name;

        public MyNormalViewHolder(View v)
        {
            super(v);
            tv_name = (TextView) v.findViewById(R.id.tv_name);
        }
    }
}
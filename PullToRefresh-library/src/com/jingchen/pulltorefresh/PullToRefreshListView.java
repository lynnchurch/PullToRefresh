package com.jingchen.pulltorefresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

public class PullToRefreshListView extends PullToRefreshLayout
{
	private PullableListView listView;
	private int dividerHeight;
	private Drawable divider;

	public PullToRefreshListView(Context context)
	{
		this(context, null, 0);
		// TODO Auto-generated constructor stub
	}

	public PullToRefreshListView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public PullToRefreshListView(Context context, AttributeSet attrs,
			int defStyle)
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected View addPullableView(PullToRefreshLayout parentView,
			AttributeSet attrs, int defStyle)
	{
		listView = (PullableListView) LayoutInflater.from(getContext())
				.inflate(R.layout.listview, parentView, false);

		if (null != attrs)
		{
			TypedArray a = getContext().obtainStyledAttributes(attrs,
					R.styleable.PullToRefreshListView, defStyle, 0);
			dividerHeight = (int) a.getDimensionPixelSize(
					R.styleable.PullToRefreshListView_android_dividerHeight, 1);
			divider = a
					.getDrawable(R.styleable.PullToRefreshListView_android_divider);
			a.recycle();
			if (null != divider)
			{
				listView.setDivider(divider);
			}
			listView.setDividerHeight(dividerHeight);
		}
		parentView.addView(listView);
		return listView;
	}

}

package com.jingchen.pulltorefresh;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifDrawable;

/**
 * 自定义的布局，用来管理三个子控件，其中一个是下拉头，一个是包含内容的pullableView（可以是实现Pullable接口的的任何View），
 * 还有一个上拉头，更多详解见博客http://blog.csdn.net/zhongkejingwang/article/details/38868463
 *
 * @author 陈靖
 */
public class PullToRefreshLayout extends RelativeLayout
{
    public static final String TAG = "PullToRefreshLayout";
    // 初始状态
    public static final int INIT = 0;
    // 释放刷新
    public static final int RELEASE_TO_REFRESH = 1;
    // 正在刷新
    public static final int REFRESHING = 2;
    // 释放加载
    public static final int RELEASE_TO_LOAD = 3;
    // 正在加载
    public static final int LOADING = 4;
    // 操作完毕
    public static final int DONE = 5;
    // 当前状态
    private int state = INIT;
    // 刷新回调接口
    private OnPullListener mListener;
    // 刷新成功
    public static final int SUCCEED = 0;
    // 刷新失败
    public static final int FAIL = 1;
    // 按下Y坐标，上一个事件点Y坐标
    private float downY, lastY;

    // 下拉的距离。注意：pullDownY和pullUpY不可能同时不为0
    public float pullDownY = 0;
    // 上拉的距离
    private float pullUpY = 0;

    // 释放刷新的距离
    private float refreshDist = 200;
    // 释放加载的距离
    private float loadmoreDist = 200;

    private MyTimer timer;
    // 回滚速度
    public float mMoveSpeed = 8;
    // 第一次执行布局
    private boolean isLayout = false;
    // 在刷新过程中滑动操作
    private boolean isTouch = false;
    // 手指滑动距离与下拉头的滑动距离比，中间会随正切函数变化
    private float radio = 2;

    // 下拉箭头的转180°动画
    private RotateAnimation reverseUpAnimation;
    private RotateAnimation reverseDownAnimation;
    // 均匀旋转动画
    private RotateAnimation refreshingAnimation;

    // 默认下拉头
    private View defaultRefreshView;
    // 下拉的箭头
    private View pullDownView;
    // 正在刷新的图标
    private View refreshingView;
    // 刷新结果图标
    private View refreshStateImageView;
    // 刷新结果：成功或失败
    private TextView refreshStateTextView;

    // 默认上拉头
    private View defaultLoadmoreView;
    // // 上拉的箭头
    private View pullUpView;
    // 正在加载的图标
    private View loadingView;
    // 加载结果图标
    private View loadStateImageView;
    // 加载结果：成功或失败
    private TextView loadStateTextView;

    // 实现了Pullable接口的View
    private View pullableView;
    // 过滤多点触碰
    private int mEvents;
    // 这两个变量用来控制pull的方向，如果不加控制，当情况满足可上拉又可下拉时没法下拉
    private boolean mCanPullDown = true;
    private boolean mCanPullUp = true;

    private boolean mPullDownEnable = true;
    private boolean mPullUpEnable = true;

    // 执行自动回滚的handler
    private Handler updateHandler;

    // 自定义下拉头
    private View customRefreshView;
    // 自定义上拉头
    private View customLoadmoreView;
    // 下拉刷新过程监听器
    private OnPullProcessListener mOnRefreshProcessListener;
    // 上拉加载更多过程监听器
    private OnPullProcessListener mOnLoadmoreProcessListener;

    // 下拉头
    private View refreshView;
    // 上拉头
    private View loadmoreView;

    // 是否已经准备下拉
    private boolean mPreparedPullDown;
    // 是否已经准备上拉
    private boolean mPreparedPullUp;

    public PullToRefreshLayout(Context context)
    {
        this(context, null, 0);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initView(context, attrs, defStyle);
    }

    private void initView(Context context, AttributeSet attrs, int defStyle)
    {
        updateHandler = new UpdateHandler(this);
        timer = new MyTimer(updateHandler);
        reverseUpAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                context, R.anim.reverse_up_anim);
        reverseDownAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                context, R.anim.reverse_down_anim);
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                context, R.anim.rotating_anim);
        // 添加匀速转动动画
        LinearInterpolator lir = new LinearInterpolator();
        reverseUpAnimation.setInterpolator(lir);
        refreshingAnimation.setInterpolator(lir);

        // 添加上拉头和下拉头
        LayoutInflater inflater = LayoutInflater.from(context);
        defaultRefreshView = inflater.inflate(R.layout.refresh_head, this,
                false);
        refreshView = defaultRefreshView;
        defaultLoadmoreView = inflater.inflate(R.layout.load_more, this, false);
        loadmoreView = defaultLoadmoreView;
        addView(defaultRefreshView);
        addView(defaultLoadmoreView);
    }

    /**
     * 获取可拉取的视图
     *
     * @return
     */
    public View getPullableView()
    {
        for (int i = 0; i < getChildCount(); i++)
        {
            View v = getChildAt(i);
            if (v instanceof Pullable)
            {
                pullableView = v;
                return pullableView;
            }
        }
        return pullableView;
    }

    /**
     * 设置自定义下拉头
     *
     * @param v
     */
    public void setCustomRefreshView(View v)
    {
        customRefreshView = v;
        removeView(defaultRefreshView);
        addView(customRefreshView);
        refreshView = customRefreshView;
    }

    /**
     * 设置自定义上拉头
     *
     * @param v
     */
    public void setCustomLoadmoreView(View v)
    {
        customLoadmoreView = v;
        removeView(defaultLoadmoreView);
        addView(customLoadmoreView);
        loadmoreView = customLoadmoreView;
    }

    /**
     * 设置下拉刷新gif动画头
     *
     * @param headGifDrawable
     */
    public void setGifRefreshView(GifDrawable headGifDrawable)
    {
        // 设置下拉头
        GifHeadView headView = new GifHeadView(getContext());
        headView.setGifAnim(headGifDrawable);
        setCustomRefreshView(headView);
        setOnRefreshProcessListener(new GifOnPullProcessListener(
                headView.getDrawable()));
    }

    /**
     * 设置上拉加载更多gif动画头
     *
     * @param footGifDrawable
     */
    public void setGifLoadmoreView(GifDrawable footGifDrawable)
    {
        // 设置上拉头
        GifHeadView footView = new GifHeadView(getContext());
        footView.setGifAnim(footGifDrawable);
        setCustomLoadmoreView(footView);
        setOnLoadmoreProcessListener(new GifOnPullProcessListener(
                footView.getDrawable()));
    }

    private void hide()
    {
        timer.schedule(5);
    }

    /**
     * 完成刷新操作，显示刷新结果。注意：刷新完成后一定要调用这个方法
     */
    /**
     * @param refreshResult PullToRefreshLayout.SUCCEED代表成功，PullToRefreshLayout.FAIL代表失败
     */
    public void refreshFinish(final int refreshResult)
    {
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (null != mOnRefreshProcessListener)
                {
                    mOnRefreshProcessListener.onFinish(refreshView,
                            OnPullProcessListener.REFRESH);
                }
                if (null == customRefreshView)
                {
                    refreshingView.clearAnimation();
                    refreshingView.setVisibility(View.GONE);
                }
                switch (refreshResult)
                {
                    case SUCCEED:
                        // 刷新成功
                        if (null == customRefreshView)
                        {
                            refreshStateImageView.setVisibility(View.VISIBLE);
                            refreshStateTextView.setText(R.string.refresh_succeed);
                            refreshStateImageView
                                    .setBackgroundResource(R.drawable.refresh_succeed);
                        }
                        break;
                    case FAIL:
                    default:
                        // 刷新失败
                        if (null == customRefreshView)
                        {
                            refreshStateImageView.setVisibility(View.VISIBLE);
                            refreshStateTextView.setText(R.string.refresh_fail);
                            refreshStateImageView
                                    .setBackgroundResource(R.drawable.refresh_failed);
                        }
                        break;
                }
                if (pullDownY > 0)
                {
                    // 刷新结果停留1秒
                    new RemainHandler(PullToRefreshLayout.this).sendEmptyMessageDelayed(0, 1000);
                } else
                {
                    changeState(DONE);
                    hide();
                }
            }
        }, 2400);
    }

    /**
     * 加载完毕，显示加载结果。注意：加载完成后一定要调用这个方法
     *
     * @param refreshResult PullToRefreshLayout.SUCCEED代表成功，PullToRefreshLayout.FAIL代表失败
     */
    public void loadmoreFinish(int refreshResult)
    {
        if (null != mOnLoadmoreProcessListener)
        {
            mOnLoadmoreProcessListener.onFinish(loadmoreView,
                    OnPullProcessListener.LOADMORE);
        }
        if (null == customLoadmoreView)
        {
            loadingView.clearAnimation();
            loadingView.setVisibility(View.GONE);
        }

        switch (refreshResult)
        {
            case SUCCEED:
                // 加载成功
                if (null == customLoadmoreView)
                {
                    loadStateImageView.setVisibility(View.VISIBLE);
                    loadStateTextView.setText(R.string.load_succeed);
                    loadStateImageView
                            .setBackgroundResource(R.drawable.load_succeed);
                }
                break;
            case FAIL:
            default:
                // 加载失败
                if (null == customLoadmoreView)
                {
                    loadStateImageView.setVisibility(View.VISIBLE);
                    loadStateTextView.setText(R.string.load_fail);
                    loadStateImageView
                            .setBackgroundResource(R.drawable.load_failed);
                }
                break;
        }
        if (pullUpY < 0)
        {
            // 刷新结果停留1秒
            new RemainHandler(this).sendEmptyMessageDelayed(0, 1000);
        } else
        {
            changeState(DONE);
            hide();
        }
    }

    private void changeState(int to)
    {
        state = to;
        switch (state)
        {
            case INIT:
                mPreparedPullDown = false;
                mPreparedPullUp = false;
                // 下拉布局初始状态
                if (null == customRefreshView)
                {
                    pullDownView.startAnimation(reverseDownAnimation);
                    refreshStateImageView.setVisibility(View.GONE);
                    refreshStateTextView.setText(R.string.pull_to_refresh);
                    pullDownView.setVisibility(View.VISIBLE);
                }
                // 上拉布局初始状态
                if (null == customLoadmoreView)
                {
                    pullUpView.startAnimation(reverseDownAnimation);
                    loadStateImageView.setVisibility(View.GONE);
                    loadStateTextView.setText(R.string.pullup_to_load);
                    pullUpView.setVisibility(View.VISIBLE);
                }
                break;
            case RELEASE_TO_REFRESH:
                if (null != mOnRefreshProcessListener)
                {
                    mOnRefreshProcessListener.onStart(refreshView,
                            OnPullProcessListener.REFRESH);
                }
                // 释放刷新状态
                if (null == customRefreshView)
                {
                    refreshStateTextView.setText(R.string.release_to_refresh);
                    pullDownView.startAnimation(reverseUpAnimation);
                }
                break;
            case REFRESHING:
                if (null != mOnRefreshProcessListener)
                {
                    mOnRefreshProcessListener.onHandling(refreshView,
                            OnPullProcessListener.REFRESH);
                }
                // 正在刷新状态
                if (null == customRefreshView)
                {
                    pullDownView.clearAnimation();
                    refreshingView.setVisibility(View.VISIBLE);
                    pullDownView.setVisibility(View.INVISIBLE);
                    refreshingView.startAnimation(refreshingAnimation);
                    refreshStateTextView.setText(R.string.refreshing);
                }
                break;
            case RELEASE_TO_LOAD:
                if (null != mOnLoadmoreProcessListener)
                {
                    mOnLoadmoreProcessListener.onStart(loadmoreView,
                            OnPullProcessListener.LOADMORE);
                }
                // 释放加载状态
                if (null == customLoadmoreView)
                {
                    loadStateTextView.setText(R.string.release_to_load);
                    pullUpView.startAnimation(reverseUpAnimation);
                }
                break;
            case LOADING:
                // 正在加载状态
                if (null != mOnLoadmoreProcessListener)
                {
                    mOnLoadmoreProcessListener.onHandling(loadmoreView,
                            OnPullProcessListener.LOADMORE);
                }
                if (null == customLoadmoreView)
                {
                    pullUpView.clearAnimation();
                    loadingView.setVisibility(View.VISIBLE);
                    pullUpView.setVisibility(View.INVISIBLE);
                    loadingView.startAnimation(refreshingAnimation);
                    loadStateTextView.setText(R.string.loading);
                } else
                {
                    customLoadmoreView.setVisibility(View.VISIBLE);
                }
                break;
            case DONE:
                refreshView.setVisibility(View.VISIBLE);
                loadmoreView.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 不限制上拉或下拉
     */
    private void releasePull()
    {
        mCanPullDown = true;
        mCanPullUp = true;
    }

    /**
     * 设置是否可下拉刷新
     *
     * @param pullDownEnable
     */
    public void setPullDownEnable(boolean pullDownEnable)
    {
        mPullDownEnable = pullDownEnable;
    }

    /**
     * 设置是否可上拉刷新
     */
    public void setPullUpEnable(boolean pullUpEnable)
    {
        mPullUpEnable = pullUpEnable;
    }

    /*
     * （非 Javadoc）由父控件决定是否分发事件，防止事件冲突
     *
     * @see android.view.ViewGroup#dispatchTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        switch (ev.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
                downY = ev.getY();
                lastY = downY;
                timer.cancel();
                mEvents = 0;
                releasePull();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                // 过滤多点触碰
                mEvents = -1;
                break;
            case MotionEvent.ACTION_MOVE:
                if (null != mOnRefreshProcessListener && pullDownY > 0)
                {
                    if (!mPreparedPullDown)
                    {
                        mPreparedPullDown = true;
                        if (null != mOnRefreshProcessListener)
                        {
                            mOnRefreshProcessListener.onPrepare(refreshView,
                                    OnPullProcessListener.REFRESH);
                        }
                    }
                    mOnRefreshProcessListener.onPull(refreshView, pullDownY,
                            OnPullProcessListener.REFRESH);
                }
                if (null != mOnLoadmoreProcessListener && pullUpY < 0)
                {
                    if (!mPreparedPullUp)
                    {
                        mPreparedPullUp = true;
                        if (null != mOnLoadmoreProcessListener)
                        {
                            mOnLoadmoreProcessListener.onPrepare(loadmoreView,
                                    OnPullProcessListener.LOADMORE);
                        }
                    }
                    mOnLoadmoreProcessListener.onPull(loadmoreView, pullUpY,
                            OnPullProcessListener.LOADMORE);
                }
                if (mEvents == 0)
                {
                    if (pullDownY > 0
                            || (((Pullable) pullableView).canPullDown()
                            && mCanPullDown && mPullDownEnable && state != LOADING))
                    {
                        // 可以下拉，正在加载时不能下拉
                        // 对实际滑动距离做缩小，造成用力拉的感觉
                        pullDownY = pullDownY + (ev.getY() - lastY) / radio;
                        if (pullDownY < 0)
                        {
                            pullDownY = 0;
                            mCanPullDown = false;
                            mCanPullUp = true;
                        }
                        if (pullDownY > getMeasuredHeight())
                            pullDownY = getMeasuredHeight();
                        if (state == REFRESHING)
                        {
                            // 正在刷新的时候触摸移动
                            isTouch = true;
                        }
                    } else if (pullUpY < 0
                            || (((Pullable) pullableView).canPullUp() && mCanPullUp
                            && mPullUpEnable && state != REFRESHING))
                    {
                        // 可以上拉，正在刷新时不能上拉
                        pullUpY = pullUpY + (ev.getY() - lastY) / radio;
                        if (pullUpY > 0)
                        {
                            pullUpY = 0;
                            mCanPullDown = true;
                            mCanPullUp = false;
                        }
                        if (pullUpY < -getMeasuredHeight())
                            pullUpY = -getMeasuredHeight();
                        if (state == LOADING)
                        {
                            // 正在加载的时候触摸移动
                            isTouch = true;
                        }
                    } else
                        releasePull();
                } else
                    mEvents = 0;
                lastY = ev.getY();
                // 根据下拉距离改变比例
                radio = (float) (2 + 2 * Math.tan(Math.PI / 2 / getMeasuredHeight()
                        * (pullDownY + Math.abs(pullUpY))));
                if (pullDownY > 0 || pullUpY < 0)
                    requestLayout();
                if (pullDownY > 0)
                {
                    if (pullDownY <= refreshDist
                            && (state == RELEASE_TO_REFRESH || state == DONE))
                    {
                        // 如果下拉距离没达到刷新的距离且当前状态是释放刷新，改变状态为下拉刷新
                        changeState(INIT);
                    }
                    if (pullDownY >= refreshDist && state == INIT)
                    {
                        // 如果下拉距离达到刷新的距离且当前状态是初始状态刷新，改变状态为释放刷新
                        changeState(RELEASE_TO_REFRESH);
                    }
                } else if (pullUpY < 0)
                {
                    // 下面是判断上拉加载的，同上，注意pullUpY是负值
                    if (-pullUpY <= loadmoreDist
                            && (state == RELEASE_TO_LOAD || state == DONE))
                    {
                        changeState(INIT);
                    }
                    // 上拉操作
                    if (-pullUpY >= loadmoreDist && state == INIT)
                    {
                        changeState(RELEASE_TO_LOAD);
                    }

                }
                // 因为刷新和加载操作不能同时进行，所以pullDownY和pullUpY不会同时不为0，因此这里用(pullDownY +
                // Math.abs(pullUpY))就可以不对当前状态作区分了
                if ((pullDownY + Math.abs(pullUpY)) > 8)
                {
                    // 防止下拉过程中误触发长按事件和点击事件
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (pullDownY > refreshDist || -pullUpY > loadmoreDist)
                // 正在刷新时往下拉（正在加载时往上拉），释放后下拉头（上拉头）不隐藏
                {
                    isTouch = false;
                }
                if (state == RELEASE_TO_REFRESH)
                {
                    changeState(REFRESHING);
                    // 刷新操作
                    if (mListener != null)
                        mListener.onRefresh(this);
                } else if (state == RELEASE_TO_LOAD)
                {
                    changeState(LOADING);
                    // 加载操作
                    if (mListener != null)
                        mListener.onLoadMore(this);
                }
                hide();
            default:
                break;
        }
        // 事件分发交给父类
        super.dispatchTouchEvent(ev);
        return true;
    }

    /**
     * @author chenjing 自动模拟手指滑动的task
     */
    private class AutoRefreshAndLoadTask extends
            AsyncTask<Integer, Float, String>
    {

        @Override
        protected String doInBackground(Integer... params)
        {
            while (pullDownY < 4 / 3 * refreshDist)
            {
                pullDownY += mMoveSpeed;
                publishProgress(pullDownY);
                try
                {
                    Thread.sleep(params[0]);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result)
        {
            changeState(REFRESHING);
            // 刷新操作
            if (mListener != null)
                mListener.onRefresh(PullToRefreshLayout.this);
            if (null != mOnRefreshProcessListener)
            {
                mOnRefreshProcessListener.onStart(refreshView,
                        OnPullProcessListener.REFRESH);
            }
            hide();
        }

        @Override
        protected void onProgressUpdate(Float... values)
        {
            if (pullDownY > refreshDist)
                changeState(RELEASE_TO_REFRESH);
            requestLayout();
        }

    }

    /**
     * 自动刷新
     */
    public void autoRefresh()
    {
        loadmoreView.setVisibility(View.GONE);
        AutoRefreshAndLoadTask task = new AutoRefreshAndLoadTask();
        task.execute(2);
    }

    /**
     * 自动加载
     */
    public void autoLoad()
    {
        refreshView.setVisibility(View.GONE);
        pullUpY = -loadmoreDist;
        requestLayout();
        changeState(LOADING);
        // 加载操作
        if (mListener != null)
            mListener.onLoadMore(this);
    }

    private void initView()
    {
        // 初始化下拉布局
        if (null == customRefreshView)
        {
            pullDownView = defaultRefreshView.findViewById(R.id.pull_icon);
            refreshStateTextView = (TextView) defaultRefreshView
                    .findViewById(R.id.state_tv);
            refreshingView = defaultRefreshView
                    .findViewById(R.id.refreshing_icon);
            refreshStateImageView = defaultRefreshView
                    .findViewById(R.id.state_iv);
        }
        // 初始化上拉布局
        if (null == customLoadmoreView)
        {
            pullUpView = defaultLoadmoreView.findViewById(R.id.pullup_icon);
            loadStateTextView = (TextView) defaultLoadmoreView
                    .findViewById(R.id.loadstate_tv);
            loadingView = defaultLoadmoreView.findViewById(R.id.loading_icon);
            loadStateImageView = defaultLoadmoreView
                    .findViewById(R.id.loadstate_iv);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        if (!isLayout)
        {
            // 这里是第一次进来的时候做一些初始化
            getPullableView();
            isLayout = true;
            initView();
            refreshView.measure(0, 0);
            refreshDist = refreshView.getMeasuredHeight();
            loadmoreView.measure(0, 0);
            loadmoreDist = loadmoreView.getMeasuredHeight();
        }
        // 改变子控件的布局，这里直接用(pullDownY + pullUpY)作为偏移量，这样就可以不对当前状态作区分
        refreshView.layout(0,
                (int) (pullDownY + pullUpY) - refreshView.getMeasuredHeight(),
                refreshView.getMeasuredWidth(), (int) (pullDownY + pullUpY));
        pullableView.layout(0, (int) (pullDownY + pullUpY),
                pullableView.getMeasuredWidth(), (int) (pullDownY + pullUpY)
                        + pullableView.getMeasuredHeight());
        loadmoreView.layout(0,
                (int) (pullDownY + pullUpY) + pullableView.getMeasuredHeight(),
                loadmoreView.getMeasuredWidth(),
                (int) (pullDownY + pullUpY) + pullableView.getMeasuredHeight()
                        + loadmoreView.getMeasuredHeight());
    }

    class MyTimer
    {
        private Handler handler;
        private Timer timer;
        private MyTask mTask;

        public MyTimer(Handler handler)
        {
            this.handler = handler;
            timer = new Timer();
        }

        public void schedule(long period)
        {
            if (mTask != null)
            {
                mTask.cancel();
                mTask = null;
            }
            mTask = new MyTask(handler);
            timer.schedule(mTask, 0, period);
        }

        public void cancel()
        {
            if (mTask != null)
            {
                mTask.cancel();
                mTask = null;
            }
        }

        class MyTask extends TimerTask
        {
            private Handler handler;

            public MyTask(Handler handler)
            {
                this.handler = handler;
            }

            @Override
            public void run()
            {
                handler.obtainMessage().sendToTarget();
            }

        }
    }

    /**
     * 刷新结果停留的handler
     */
    static class RemainHandler extends Handler
    {
        private WeakReference<PullToRefreshLayout> mLayout;

        public RemainHandler(PullToRefreshLayout layout)
        {
            mLayout = new WeakReference<>(layout);
        }

        @Override
        public void handleMessage(Message msg)
        {
            PullToRefreshLayout layout = mLayout.get();
            if (null != layout)
            {
                layout.changeState(DONE);
                layout.hide();
            }
        }
    }

    /**
     * 执行自动回滚的handler
     */
    static class UpdateHandler extends Handler
    {
        private WeakReference<PullToRefreshLayout> mLayout;

        public UpdateHandler(PullToRefreshLayout layout)
        {
            mLayout = new WeakReference<>(layout);
        }

        @Override
        public void handleMessage(Message msg)
        {
            PullToRefreshLayout layout = mLayout.get();
            if (null != layout)
            {
                // 回弹速度随下拉距离moveDeltaY增大而增大
                layout.mMoveSpeed = (float) (8 + 5 * Math.tan(Math.PI / 2
                        / layout.getMeasuredHeight()
                        * (layout.pullDownY + Math.abs(layout.pullUpY))));
                if (!layout.isTouch)
                {
                    // 正在刷新，且没有往上推的话则悬停，显示"正在刷新..."
                    if (layout.state == REFRESHING
                            && layout.pullDownY <= layout.refreshDist)
                    {
                        layout.pullDownY = layout.refreshDist;
                        layout.timer.cancel();
                    } else if (layout.state == LOADING
                            && -layout.pullUpY <= layout.loadmoreDist)
                    {
                        layout.pullUpY = -layout.loadmoreDist;
                        layout.timer.cancel();
                    }

                }
                if (layout.pullDownY > 0)
                    layout.pullDownY -= layout.mMoveSpeed;
                else if (layout.pullUpY < 0)
                    layout.pullUpY += layout.mMoveSpeed;
                if (layout.pullDownY < 0)
                {
                    // 已完成回弹
                    layout.pullDownY = 0;
                    if (null == layout.customRefreshView)
                    {
                        layout.pullDownView.clearAnimation();
                    }
                    // 隐藏下拉头时有可能还在刷新，只有当前状态不是正在刷新时才改变状态
                    if (layout.state != REFRESHING && layout.state != LOADING)
                        layout.changeState(INIT);
                    layout.timer.cancel();
                    layout.requestLayout();
                }
                if (layout.pullUpY > 0)
                {
                    // 已完成回弹
                    layout.pullUpY = 0;
                    if (null == layout.customLoadmoreView)
                    {
                        layout.pullUpView.clearAnimation();
                    }
                    // 隐藏上拉头时有可能还在刷新，只有当前状态不是正在刷新时才改变状态
                    if (layout.state != REFRESHING && layout.state != LOADING)
                        layout.changeState(INIT);
                    layout.timer.cancel();
                    layout.requestLayout();
                }
                // 刷新布局,会自动调用onLayout
                layout.requestLayout();
                // 没有拖拉或者回弹完成
                if (layout.pullDownY + Math.abs(layout.pullUpY) == 0)
                    layout.timer.cancel();
            }
        }

    }

    ;

    public void setOnPullListener(OnPullListener listener)
    {
        mListener = listener;
    }

    /**
     * 设置下拉刷新过程监听器
     *
     * @param onPullProcessListener
     */
    public void setOnRefreshProcessListener(
            OnPullProcessListener onPullProcessListener)
    {
        mOnRefreshProcessListener = onPullProcessListener;
    }

    /**
     * 设置上拉加载更多过程监听器
     *
     * @param onPullProcessListener
     */
    public void setOnLoadmoreProcessListener(
            OnPullProcessListener onPullProcessListener)
    {
        mOnLoadmoreProcessListener = onPullProcessListener;
    }

    /**
     * 刷新加载回调接口
     *
     * @author chenjing
     */
    public interface OnPullListener
    {
        /**
         * 刷新操作
         */
        void onRefresh(PullToRefreshLayout pullToRefreshLayout);

        /**
         * 加载操作
         */
        void onLoadMore(PullToRefreshLayout pullToRefreshLayout);
    }

    /**
     * 下拉刷新或上拉加载更多过程监听器
     *
     * @author LynnChurch
     */
    public interface OnPullProcessListener
    {
        int REFRESH = 1; // 刷新

        int LOADMORE = 2; // 加载更多

        /**
         * 准备 （提示下拉刷新或上拉加载更多）
         *
         * @param v
         * @param which 刷新或加载更多
         */
        void onPrepare(View v, int which);

        /**
         * 开始 （提示释放刷新或释放加载更多）
         *
         * @param v
         * @param which 刷新或加载更多
         */
        void onStart(View v, int which);

        /**
         * 处理中
         *
         * @param v
         * @param which 刷新或加载更多
         */
        void onHandling(View v, int which);

        /**
         * 完成
         *
         * @param v
         * @param which 刷新或加载更多
         */
        void onFinish(View v, int which);

        /**
         * 用于获取拉取的距离
         *
         * @param v
         * @param pullDistance
         * @param which        刷新或加载更多
         */
        void onPull(View v, float pullDistance, int which);
    }

    /**
     * 用于gif动画的控制
     *
     * @author LynnChurch
     */
    public class GifOnPullProcessListener implements OnPullProcessListener
    {
        private GifDrawable mGifDrawable;
        private int lastFramePosition; // 上一帧的位置

        public GifOnPullProcessListener(GifDrawable gifDrawable)
        {
            mGifDrawable = gifDrawable;
        }

        @Override
        public void onPrepare(View v, int which)
        {
            // TODO Auto-generated method stub
            mGifDrawable.stop();
        }

        @Override
        public void onStart(View v, int which)
        {
            // TODO Auto-generated method stub
        }

        @Override
        public void onHandling(View v, int which)
        {
            // TODO Auto-generated method stub
            mGifDrawable.start();
        }

        @Override
        public void onFinish(View v, int which)
        {
            // TODO Auto-generated method stub
            mGifDrawable.stop();
        }

        @Override
        public void onPull(View v, float pullDistance, int which)
        {
            // TODO Auto-generated method stub
            // 动画总帧数
            int frames = mGifDrawable.getNumberOfFrames();
            RelativeLayout headView = (RelativeLayout) v
                    .findViewById(R.id.head_view);
            int headViewHeight = headView.getHeight();
            // 算出下拉过程中对应的动画进度
            float progress = Math.abs(pullDistance % headViewHeight
                    / headViewHeight);
            // 当前播放帧
            int currentFrame = (int) (frames * progress) + 1;

            // 当前帧与上一帧不同时才进行播放，保证流畅度
            if (lastFramePosition != currentFrame)
            {
                // 需要进行动画重置，否则不能够自由地定位到每一帧
                mGifDrawable.reset();
                mGifDrawable.seekToFrame(currentFrame);
                lastFramePosition = currentFrame;
            }
        }

    }
}

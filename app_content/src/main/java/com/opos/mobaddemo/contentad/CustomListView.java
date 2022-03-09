package com.opos.mobaddemo.contentad;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


public class CustomListView extends ListView implements OnScrollListener, View.OnTouchListener {
    private LinearLayout mTopView;
    private ImageView mTopImageView;
    private ImageView mTopLoadingImageView;
    private TextView mTopDescTV;
    private int mTopViewHeight;
    //
    private LinearLayout mBottomView;
    private ImageView mBottomImageView;
    private int mBottomViewHeight;
    //
    private Animation mUpArrowAni;
    private Animation mDownArrowAni;
    //
    private int mLastVisibleItem = 0;
    private int mTotalItemCount = 0;
    private boolean mIsLoadingMore = false;
    //
    private static final int STATE_DOWN_PULL_REFRESH = 0;
    private static final int STATE_RELEASE_REFRESH = 1;
    private static final int STATE_REFRESHING = 2;
    private int mCurrentState = STATE_DOWN_PULL_REFRESH;
    //
    private ILoadStatusListener mILoadStatusListener;

    public CustomListView(Context context) {
        super(context);
        initView();
    }

    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CustomListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        mUpArrowAni = AnimationUtils.loadAnimation(getContext(), R.anim.animation_arrow_up);
        mDownArrowAni = AnimationUtils.loadAnimation(getContext(), R.anim.animation_arrow_down);

        mTopView = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.item_top_loading, null);
        mTopImageView = mTopView.findViewById(R.id.content_ad_top_arrow_iv);
        mTopLoadingImageView = mTopView.findViewById(R.id.content_ad_top_loading_iv);
        mTopDescTV = mTopView.findViewById(R.id.content_ad_top_loading_tv);
        //
        mBottomView = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.item_bottom_loading, null);
        mBottomImageView = mBottomView.findViewById(R.id.content_ad_bottom_loading_iv);
        //
        mTopView.measure(0, 0);
        mBottomView.measure(0, 0);
        //
        mTopViewHeight = mTopView.getMeasuredHeight();
        mBottomViewHeight = mBottomView.getMeasuredHeight();
        //
        mTopView.setPadding(0, -mTopViewHeight, 0, 0);
        mBottomView.setPadding(0, -mBottomViewHeight, 0, 0);
        //
        this.setOnScrollListener(this);
        this.setOnTouchListener(this);
        //
        this.addHeaderView(mTopView);
        this.addFooterView(mBottomView);
    }

    public void setLoadListener(ILoadStatusListener iLoadStatusListener) {
        mILoadStatusListener = iLoadStatusListener;
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (!mIsLoadingMore && mCurrentState != STATE_REFRESHING && scrollState == SCROLL_STATE_IDLE && mLastVisibleItem == mTotalItemCount) {
            mBottomView.setPadding(0, 0, 0, 0);//显示出footerView
            startAnimation(mBottomImageView);
            this.setSelection(this.getCount());//让listview最后一条显示出来，在页面完全显示出底布局
            if (null != mILoadStatusListener) {
                mILoadStatusListener.onBottomLoadMore();
            }
            mIsLoadingMore = true;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mLastVisibleItem = firstVisibleItem + visibleItemCount;
        mTotalItemCount = totalItemCount;
    }

    float downY, moveY, offsetY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = ev.getRawY();
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if (mCurrentState == STATE_REFRESHING || mIsLoadingMore) {
                    return super.onTouchEvent(event);
                }
                moveY = event.getY();
                offsetY = moveY - downY;
                //下滑距离大于0，且当前第一个可见位置为0
                if (offsetY > 0 && getFirstVisiblePosition() == 0) {
                    int paddingTop = (int) (-mTopViewHeight + offsetY);
                    if (paddingTop > mTopViewHeight) {
                        paddingTop = mTopViewHeight;
                    }
                    mTopView.setPadding(0, paddingTop, 0, 0);
                    if (paddingTop > 0 && mCurrentState != STATE_RELEASE_REFRESH) {
                        mCurrentState = STATE_RELEASE_REFRESH;
                        updateTopView();
                    } else if (paddingTop <= 0 && mCurrentState != STATE_DOWN_PULL_REFRESH) {
                        mCurrentState = STATE_DOWN_PULL_REFRESH;
                        updateTopView();
                    }
                    return true;//消费当前事件
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mCurrentState == STATE_DOWN_PULL_REFRESH) {
                    mTopView.setPadding(0, -mTopViewHeight, 0, 0);
                } else if (mCurrentState == STATE_RELEASE_REFRESH) {
                    mCurrentState = STATE_REFRESHING;
                    mTopView.setPadding(0, 0, 0, 0);
                    updateTopView();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void updateTopView() {
        switch (mCurrentState) {
            case STATE_DOWN_PULL_REFRESH:
                mTopImageView.setVisibility(View.VISIBLE);
                mTopImageView.startAnimation(mDownArrowAni);
                mTopDescTV.setText(R.string.content_ad_pull_refresh_text);
                break;
            case STATE_RELEASE_REFRESH:
                mTopImageView.startAnimation(mUpArrowAni);
                mTopDescTV.setText(R.string.content_ad_refresh_release_text);
                break;
            case STATE_REFRESHING:
                mTopImageView.setVisibility(View.GONE);
                mTopImageView.clearAnimation();
                startAnimation(mTopLoadingImageView);
                mTopLoadingImageView.setVisibility(View.VISIBLE);
                mTopDescTV.setText(R.string.content_ad_loading_text);
                //
                if (null != mILoadStatusListener) {
                    mILoadStatusListener.onTopRefresh();
                }
                break;
        }
    }

    /*
     * 结束动画
     * */
    public void completeRefresh() {
        if (mIsLoadingMore) {
            //重置BottomView状态
            StopAnimation(mBottomImageView);
            mBottomView.setPadding(0, -mBottomViewHeight, 0, 0);
            mIsLoadingMore = false;
        } else if (mCurrentState == STATE_REFRESHING) {
            //重置TopView状态
            StopAnimation(mTopLoadingImageView);
            mTopLoadingImageView.setVisibility(GONE);
            mTopImageView.setVisibility(View.VISIBLE);
            mTopDescTV.setText(getContext().getResources().getString(R.string.content_ad_pull_refresh_text));
            mTopView.setPadding(0, -mTopViewHeight, 0, 0);
            mCurrentState = STATE_DOWN_PULL_REFRESH;
        }
    }


    private void startAnimation(ImageView imageView) {
        if (null != imageView) {
            if (imageView.getDrawable() instanceof AnimationDrawable) {
                AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
                animationDrawable.start();
            }
        }
    }

    private void StopAnimation(ImageView imageView) {
        if (null != imageView) {
            if (imageView.getDrawable() instanceof AnimationDrawable) {
                AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
                animationDrawable.stop();
            }
        }
    }

    public interface ILoadStatusListener {
        void onTopRefresh();

        void onBottomLoadMore();
    }

}

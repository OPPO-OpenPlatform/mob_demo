package com.opos.mobaddemo.hotsplash;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import com.heytap.msp.mobad.api.ad.HotSplashAd;
import com.heytap.msp.mobad.api.listener.IHotSplashListener;
import com.heytap.msp.mobad.api.params.SplashAdParams;

public class HotSplashInstance implements IHotSplashListener {
    /**
     * 从请求广告到广告展示出来最大耗时时间，只能在[3000,5000]ms之内。
     */

    private static final int FETCH_TIME_OUT = 3000;
    private volatile static HotSplashInstance mInstance;

    public static final HotSplashInstance getInstance() {
        if (null != mInstance) {
            return mInstance;
        }
        synchronized (HotSplashInstance.class) {
            HotSplashInstance temp = mInstance;
            if (null == temp) {
                temp = mInstance = new HotSplashInstance();
            }
            return mInstance;
        }
    }

    private HotSplashAd mHotSplashAd;
    private LoadListener mLoadListener;
    private ShowListener mShowListener;

    private HotSplashInstance() {
    }

    public void reset(Activity activity, LoadListener loadListener, String posId) {
        if (null != mHotSplashAd) {
            mHotSplashAd.destroyAd();
        }
        mLoadListener = loadListener;
        LayoutInflater inflate = LayoutInflater.from(activity);
        View bottomArea = inflate.inflate(R.layout.hot_splash_bottom_area, null);
        //自定义跳过按钮样式结束
        SplashAdParams splashAdParams = new SplashAdParams.Builder()
                .setFetchTimeout(FETCH_TIME_OUT)
                .setShowPreLoadPage(true)
                .setBottomArea(bottomArea)
                .build();
        mHotSplashAd = new HotSplashAd(activity, posId, this, splashAdParams);
    }

    public void showAd(Activity activity, ShowListener showListener) {
        mShowListener = showListener;
        mHotSplashAd.showAd(activity);
    }

    public void destroy() {
        HotSplashAd hotSplashAd = mHotSplashAd;
        if (null != hotSplashAd) {
            hotSplashAd.destroyAd();
        }
        mShowListener = null;
    }

    @Override
    public void onAdReady() {
        if (null != mLoadListener) {
            mLoadListener.onAdReady();
        }
    }

    @Override
    public void onAdDismissed() {
        if (null != mShowListener) {
            mShowListener.onAdDismissed();
        }
    }

    @Override
    public void onAdShow(String transportData) {
        if (null != mShowListener) {
            mShowListener.onAdShow(transportData);
        }
    }

    @Override
    public void onAdFailed(int code, String errMsg) {
        if (null != mLoadListener) {
            mLoadListener.onAdFailed(code, errMsg);
        }
    }

    @Override
    public void onAdClick() {
        if (null != mShowListener) {
            mShowListener.onAdClick();
        }
    }

    public interface LoadListener {
        public void onAdReady();

        public void onAdFailed(int code, String errMsg);
    }

    public interface ShowListener {
        public void onAdDismissed();

        public void onAdShow(String transportData);

        public void onAdClick();

        public void onAdFailed(int code, String errMsg);
    }
}

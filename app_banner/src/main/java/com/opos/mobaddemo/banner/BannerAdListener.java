package com.opos.mobaddemo.banner;

import android.util.Log;

import com.heytap.msp.mobad.api.listener.IBannerAdListener;


public class BannerAdListener implements IBannerAdListener {

    private static final String TAG="BannerAdListener";

    @Override
    public void onAdShow() {
        /**
         *广告展示
         */
        Log.d(TAG, "onAdShow");
    }

    @Deprecated
    @Override
    public void onAdFailed(String errMsg) {
       // 已废弃，使用onAdFailed(int i, String s)
        // Deprecated , do nothing
    }

    @Override
    public void onAdFailed(int i, String s) {
        /**
         *请求广告失败
         */
        Log.d(TAG, "onAdFailed:code:" + i + ",msg:" + s);
    }

    @Override
    public void onAdReady() {

        /**
         *请求广告成功
         */
        Log.d(TAG, "onAdReady");
    }

    @Override
    public void onAdClick() {
        /**
         *广告被点击
         */
        Log.d(TAG, "onAdClick");
    }

    @Override
    public void onAdClose() {
        /**
         *广告被关闭
         */
        Log.d(TAG, "onAdClose");
    }
}

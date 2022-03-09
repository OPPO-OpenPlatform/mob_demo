package com.opos.mobaddemo.interstitial;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.heytap.msp.mobad.api.ad.InterstitialVideoAd;
import com.heytap.msp.mobad.api.listener.IInterstitialVideoAdListener;
import com.opos.mobaddemo.posid.Constants;

/*
* 插屏全屏视频
* */
public class InterstitialVideoActivity extends Activity implements IInterstitialVideoAdListener {
    private static final String TAG = "IVideoActivity";
    private InterstitialVideoAd mInterstitialVideoAd;

    public static final String INTERSTITIAL_ORI = "inter_ori";
    public static final String INTERSTITIAL_POS_ID = "inter_pos_id";
    public static final int ORI_VERTICAL = 0;
    public static final int ORI_HORIZONTAL = 1;
    public String mPosId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial_video);
        initPosId();
        init();
    }

    private void initPosId() {
        Intent intent = getIntent();
        int ori = intent.getIntExtra(INTERSTITIAL_ORI, ORI_VERTICAL);
        mPosId = intent.getStringExtra(INTERSTITIAL_POS_ID);
        setRequestedOrientation(ori == ORI_VERTICAL ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    protected void onDestroy() {
        /**
         * 在退出页面时调用destroyAd来释放广告资源
         */
        if (null != mInterstitialVideoAd) {
            mInterstitialVideoAd.destroyAd();
        }
        super.onDestroy();
    }

    private void init() {
        ((Button) findViewById(R.id.show_ad_bn)).setEnabled(false);
        /**
         * 构造 InterstitialAd.
         */
        mInterstitialVideoAd = new InterstitialVideoAd(this, mPosId, this);
        /**
         * 调用 loadAd() 方法请求广告.
         */
        mInterstitialVideoAd.loadAd();
    }

    public void onBnClick(View view) {
        if (view.getId() == R.id.load_ad_bn) {
            mInterstitialVideoAd.loadAd();
        } else if (view.getId() == R.id.show_ad_bn) {
            mInterstitialVideoAd.showAd();
        } else if (view.getId() == R.id.destroy_ad_bn) {
            /**
             *销毁广告；注意：调用destroyAd方法以后，在调用该InterstitialAd对象的任何方法将没有效果
             */
            mInterstitialVideoAd.destroyAd();
            Toast.makeText(InterstitialVideoActivity.this,"广告销毁：本次不能再请求广告",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAdShow() {
        /**
         *广告展示
         */
        Log.d(TAG, "onAdShow");
    }

    @Override
    public void onAdFailed(String errMsg) {
        /**
         *请求广告失败
         */
        Log.d(TAG, "onAdFailed:errMsg=" + (null != errMsg ? errMsg : ""));
    }

    @Override
    public void onAdReady() {

        /**
         *请求广告成功
         */
        Log.d(TAG, "onAdReady");
        /**
         *  调用sowAd方法展示插屏广告
         *  注意：只有请求广告回调onAdReady以后，调用loadAd方法才会展示广告，如果是onAdFailed，则即使调用了showAd，也不会展示广告
         *
         */
        ((Button) findViewById(R.id.show_ad_bn)).setEnabled(true);
        Toast.makeText(InterstitialVideoActivity.this.getApplicationContext(), "广告加载成功", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onAdFailed(int code, String errMsg) {
        Log.d(TAG, "onAdFailed code:" + code + ", msg:" + errMsg);
        Toast.makeText(InterstitialVideoActivity.this,"插屏视频广告加载失败，错误码： "+code+" ,错误信息： " + (null != errMsg ? errMsg : ""),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onVideoPlayComplete() {
        Log.d(TAG, "onVideoPlayComplete");
    }
}

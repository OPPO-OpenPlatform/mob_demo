package com.opos.mobaddemo.interstitial;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.heytap.msp.mobad.api.ad.InterstitialAd;
import com.heytap.msp.mobad.api.listener.IInterstitialAdListener;

/*
 * 插屏广告位，注意区分非插屏全屏视频广告位
 * */
public class InterstitialActivity extends Activity implements IInterstitialAdListener {
    private static final String TAG = "InterstitialActivity";
    private InterstitialAd mInterstitialAd;

    public static final String INTERSTITIAL_ORI = "inter_ori";
    public static final String INTERSTITIAL_POS_ID = "inter_pos_id";
    public static final int ORI_VERTICAL = 0;
    public static final int ORI_HORIZONTAL = 1;
    public String mPosId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);
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
        if (null != mInterstitialAd) {
            mInterstitialAd.destroyAd();
        }
        super.onDestroy();
    }

    private void init() {
        ((Button) findViewById(R.id.show_ad_bn)).setEnabled(false);
        /**
         * 构造 InterstitialAd.
         */
        mInterstitialAd = new InterstitialAd(this, mPosId);
        /**
         * 设置插屏广告行为监听器.
         */
        mInterstitialAd.setAdListener(this);
        /**
         * 调用 loadAd() 方法请求广告.
         */
        mInterstitialAd.loadAd();
    }

    public void onBnClick(View view) {
        int id = view.getId();
        if (id == R.id.load_ad_bn) {
            mInterstitialAd.loadAd();
        } else if (id == R.id.show_ad_bn) {
            mInterstitialAd.showAd();
        } else if (id == R.id.destroy_ad_bn) {/**
         *销毁广告；注意：调用destroyAd方法以后，在调用该InterstitialAd对象的任何方法将没有效果
         */
            mInterstitialAd.destroyAd();
            Toast.makeText(InterstitialActivity.this, "广告销毁：本次不能再请求广告", Toast.LENGTH_SHORT).show();
        }
    }

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
        /**
         *请求广告失败，已废弃，请使用onAdFailed(int i, String s)
         */
        // Deprecated , do nothing
    }

    @Override
    public void onAdFailed(int i, String s) {
        Log.d(TAG, "onAdFailed:code=" + i + ", msg:" + s);
        Toast.makeText(InterstitialActivity.this,"插屏广告加载失败，错误码："+i+" ,错误信息：" + (null != s ? s : ""),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdReady() {

        /**
         *请求广告成功
         */
        Log.d(TAG, "onAdReady");
        Toast.makeText(InterstitialActivity.this.getApplicationContext(), "广告加载成功", Toast.LENGTH_SHORT).show();
        ((Button) findViewById(R.id.show_ad_bn)).setEnabled(true);
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

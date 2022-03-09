package com.opos.mobaddemo.mix;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.heytap.msp.mobad.api.ad.BannerAd;
import com.heytap.msp.mobad.api.ad.InterstitialAd;
import com.heytap.msp.mobad.api.listener.IBannerAdListener;
import com.heytap.msp.mobad.api.listener.IInterstitialAdListener;
import com.opos.mobaddemo.posid.Constants;

public class MixActivity extends Activity implements IInterstitialAdListener {
    private static final String TAG = "MixActivity";
    //
    private RelativeLayout mAdContainer;
    private BannerAd mBannerAd;
    //
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mix);
        init();
    }


    @Override
    protected void onDestroy() {
        if (null != mBannerAd) {
            mBannerAd.destroyAd();
        }
        if (null != mInterstitialAd) {
            mInterstitialAd.destroyAd();
        }
        super.onDestroy();
    }

    private void init() {
        initBannerAd();
        initInterstitialAd();
    }

    private void initBannerAd() {
        mAdContainer = (RelativeLayout) findViewById(R.id.ad_container);
        //update 2018-03-29
        mBannerAd = new BannerAd(this, Constants.MIX_BANNER_POS_ID);
        //
        mBannerAd.setAdListener(new IBannerAdListener() {
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
                // deprecated do nothing;
            }

            @Override
            public void onAdFailed(int i, String s) {
                Log.d(TAG, "onAdFailed:code:" + i + ",msg:" + s);
                Toast.makeText(MixActivity.this,"Banner广告加载失败，错误码："+i+" ,错误信息：" + (null != s ? s : ""),Toast.LENGTH_SHORT).show();            }

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
        });

        View adView = mBannerAd.getAdView();

        if (null != adView) {
            mAdContainer.addView(adView);
        }

        mBannerAd.loadAd();
    }

    private void initInterstitialAd() {
        //update 2018-03-29
        mInterstitialAd = new InterstitialAd(this, Constants.MIX_INTERSTITIAL_POS_ID);
        //
        mInterstitialAd.setAdListener(this);

        mInterstitialAd.loadAd();
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
         *请求广告失败,已废弃，请使用onAdFailed(int i, String s)
         */
        // Deprecated do nothing
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
        mInterstitialAd.showAd();
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
    public void onAdFailed(int i, String s) {
        Log.d(TAG, "onAdFailed:code:" + i + ",msg:" + s);
        Toast.makeText(MixActivity.this,"插屏广告加载失败，错误码："+i+" ,错误信息：" + (null != s ? s : ""),Toast.LENGTH_SHORT).show();
    }
}

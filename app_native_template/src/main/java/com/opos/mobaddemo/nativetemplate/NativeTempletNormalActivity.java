package com.opos.mobaddemo.nativetemplate;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.heytap.msp.mobad.api.ad.NativeTempletAd;
import com.heytap.msp.mobad.api.listener.INativeTempletAdListener;
import com.heytap.msp.mobad.api.params.INativeTempletAdView;
import com.heytap.msp.mobad.api.params.NativeAdError;
import com.heytap.msp.mobad.api.params.NativeAdSize;


import java.util.List;


public class NativeTempletNormalActivity extends Activity implements INativeTempletAdListener {
    private static final String TAG = "NTempletNormalActivity";
    public static final String EXTRA_KEY_POS_ID = "posId";
    /**
     * 广告容器
     */
    private ViewGroup mAdContainer;

    private EditText mAdWidthET;
    private EditText mAdHeightET;
    /**
     * 原生模板广告对象
     */
    private NativeTempletAd mNativeTempletAd;
    /**
     * 原生模板广告View对象，在请求广告成功以后返回
     */
    private INativeTempletAdView mINativeTempletAdView;
    //default =0.
    private int mWidth = 0;
    private int mHeight = 0;
    //
    private String mPosId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_templet_normal);
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        destroyAd();
        super.onDestroy();
    }

    private void initView() {
        mAdContainer = (ViewGroup) findViewById(R.id.ad_container);
        mAdWidthET = (EditText) findViewById(R.id.width_et);
        mAdHeightET = (EditText) findViewById(R.id.height_et);
    }

    private void initData() {
        mPosId = getIntent().getStringExtra(EXTRA_KEY_POS_ID);
    }

    public void onBnClick(View view) {
        int id = view.getId();
        if (id == R.id.load_ad_bn) {
            loadAd();
        } else if (id == R.id.destroy_ad_bn) {
            destroyAd();
        }
    }

    private void loadAd() {
        if (null == mNativeTempletAd || checkEditTextChanged()) {
            if (!checkEditTextEmpty()) {
                mWidth = Integer.valueOf(mAdWidthET.getText().toString());
                mHeight = Integer.valueOf(mAdHeightET.getText().toString());
            }
            /**
             * 通过构造NativeAdSize对象，在NativeTempletAd初始化时传入、可以指定原生模板广告的大小，单位为dp
             * 在接入、调试模板广告的过程中，可以利用这个字段调整广告View的大小，找到与自己的App需求适合的最佳广告位尺寸
             * 确定最佳尺寸后，应该把这个ADSize固定下来，并在构造NativeExpressAD的时候传入
             * 也可以传入null，展示默认的大小
             */
            NativeAdSize nativeAdSize = new NativeAdSize.Builder()
                    .setWidthInDp(mWidth)
                    .setHeightInDp(mHeight)
                    .build();
            mNativeTempletAd = new NativeTempletAd(this, mPosId, nativeAdSize, this);
        }
        /**
         *调用loadAd方法请求原生模板广告
         */
        mNativeTempletAd.loadAd();
        hideSoftInput();
    }

    private void destroyAd() {
        hideSoftInput();
        /**
         * 每一个mINativeTempletAdView使用完以后都要调用destroy释放
         */
        if (null != mINativeTempletAdView) {
            mINativeTempletAdView.destroy();
        }
        /**
         * mNativeTempletAd调用destroyAd方法释放相关资源
         */
        if (null != mNativeTempletAd) {
            mNativeTempletAd.destroyAd();
        }
    }

    @Override
    public void onAdSuccess(List<INativeTempletAdView> iNativeTempletAdViewList) {
        Log.d(TAG, "onAdSuccess size=" + (null != iNativeTempletAdViewList ? iNativeTempletAdViewList.size() : "null"));
        if (null != iNativeTempletAdViewList && iNativeTempletAdViewList.size() > 0) {
            /**
             * 释放前一个INativeTempletAdView对对象资源
             */
            if (null != mINativeTempletAdView) {
                mINativeTempletAdView.destroy();
            }
            /**
             * 将广告“容器”置于可见状态，否则将无法产生有效曝光
             */
            if (View.VISIBLE != mAdContainer.getVisibility()) {
                mAdContainer.setVisibility(View.VISIBLE);
            }
            /**
             * 如果容器已经有广告、则移除
             */
            if (mAdContainer.getChildCount() > 0) {
                mAdContainer.removeAllViews();
            }
            /**
             * 获取广告View
             */
            mINativeTempletAdView = iNativeTempletAdViewList.get(0);
            View adView = mINativeTempletAdView.getAdView();
            if (null != adView) {
                /**
                 * 添加广告View到广告“容器”
                 */
                mAdContainer.addView(adView);
                /**
                 * 调用render方法渲染广告
                 */
                mINativeTempletAdView.render();
            }
        }
    }

    @Override
    public void onAdFailed(NativeAdError nativeAdError) {
        Log.d(TAG, "onAdFailed nativeAdError=" + (null != nativeAdError ? nativeAdError.toString() : "null"));
        Toast.makeText(NativeTempletNormalActivity.this, "load native templet ad error,error msg：" + (null != nativeAdError ? nativeAdError.toString() : "null"), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAdClick(INativeTempletAdView iNativeTempletAdView) {
        Log.d(TAG, "onAdClick iNativeTempletAdView=" + (null != iNativeTempletAdView ? iNativeTempletAdView : "null"));
    }

    @Override
    public void onAdShow(INativeTempletAdView iNativeTempletAdView) {
        Log.d(TAG, "onAdShow iNativeTempletAdView=" + (null != iNativeTempletAdView ? iNativeTempletAdView : "null"));
    }

    @Override
    public void onAdClose(INativeTempletAdView iNativeTempletAdView) {
        Log.d(TAG, "onAdClose iNativeTempletAdView=" + (null != iNativeTempletAdView ? iNativeTempletAdView : "null"));
        /**
         *当广告模板中的关闭按钮被点击时，广告将不再展示。INativeTempletAdView也会被Destroy，不再可用
         */
        if (null != mAdContainer && mAdContainer.getChildCount() > 0) {
            mAdContainer.removeAllViews();
            mAdContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRenderSuccess(INativeTempletAdView iNativeTempletAdView) {
        Log.d(TAG, "onRenderSuccess iNativeTempletAdView=" + (null != iNativeTempletAdView ? iNativeTempletAdView : "null"));
    }

    @Override
    public void onRenderFailed(NativeAdError nativeAdError, INativeTempletAdView iNativeTempletAdView) {
        Log.d(TAG, "onRenderFailed nativeAdError=" + (null != nativeAdError ? nativeAdError.toString() : "null") + ",iNativeTempletAdView=" + (null != iNativeTempletAdView ? iNativeTempletAdView : "null"));
        Toast.makeText(NativeTempletNormalActivity.this, "render native templet ad error,error msg：" + (null != nativeAdError ? nativeAdError.toString() : "null"), Toast.LENGTH_LONG).show();
    }

    private boolean checkEditTextEmpty() {
        String width = mAdWidthET.getText().toString();
        String height = mAdHeightET.getText().toString();
        if (TextUtils.isEmpty(width) || TextUtils.isEmpty(height)) {
            return true;
        }
        return false;
    }

    private boolean checkEditTextChanged() {
        if (!checkEditTextEmpty()) {
            return Integer.valueOf(mAdWidthET.getText().toString()) != mWidth
                    || Integer.valueOf(mAdHeightET.getText().toString()) != mHeight;
        }
        return false;
    }

    private void hideSoftInput() {
        View view =  NativeTempletNormalActivity.this.getCurrentFocus();
        if(null == null){
            return;
        }
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
}

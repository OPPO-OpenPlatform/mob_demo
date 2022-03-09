package com.opos.mobaddemo.nativead;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.heytap.msp.mobad.api.ad.NativeAdvanceAd;
import com.heytap.msp.mobad.api.listener.INativeAdvanceInteractListener;
import com.heytap.msp.mobad.api.listener.INativeAdvanceLoadListener;
import com.heytap.msp.mobad.api.listener.INativeAdvanceMediaListener;
import com.heytap.msp.mobad.api.params.INativeAdvanceData;
import com.heytap.msp.mobad.api.params.MediaView;
import com.heytap.msp.mobad.api.params.NativeAdvanceContainer;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.opos.mobaddemo.posid.Constants;

import java.util.ArrayList;
import java.util.List;

public class NativeMixActivity extends Activity {
    private static final String TAG = "NativeMixActivity";
    private NativeAdvanceAd mNativeAdvanceAd;
    /**
     * 原生广告数据对象。
     */
    private INativeAdvanceData mINativeAdData;
    private AQuery mAQuery;
    private NativeAdvanceContainer mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_advance_mix);
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        initData();
        initView();
    }

    private void initData() {
        /**
         *构造NativeAd对象。
         */
        mNativeAdvanceAd = new NativeAdvanceAd(this, Constants.NATIVE_ADVANCE_MIX_TEXT_IMG_POS_ID, mINativeAdvanceLoadListener);
    }

    private void initView() {
        mRootView = (NativeAdvanceContainer) findViewById(R.id.native_ad_container);
        mRootView.setVisibility(View.GONE);
        //
        mAQuery = new AQuery(this);
        mAQuery.id(R.id.load_native_ad_bn).clicked(this, "loadAd");
        mAQuery.id(R.id.show_native_ad_bn).clicked(this, "showAd").enabled(false);
    }


    public void loadAd() {
        if (null != mNativeAdvanceAd) {
            /**
             *调用loadAd方法加载原生广告。
             */
            mNativeAdvanceAd.loadAd();
        }
    }

    public void showAd() {
        /**
         *在展示原生广告前调用isAdValid判断当前广告是否有效，否则即使展示了广告，也是无效曝光、点击，不计费的
         *注意：每个INativeAdData对象只有一次有效曝光、一次有效点击；多次曝光，多次点击都只扣一次费。
         */
        if (null != mINativeAdData && mINativeAdData.isAdValid()) {
            mRootView.removeAllViews();
            findViewById(R.id.native_ad_container).setVisibility(View.VISIBLE);
            //
            ViewGroup adView = NativeAdapter.createViewByData(NativeMixActivity.this, mRootView, mINativeAdData, mICloseListener);
            if (null == adView) {
                Log.d(TAG, "null view");
                return;
            }
            mRootView.addView(adView);
            adView.setVisibility(View.VISIBLE);
            //
            mINativeAdData.setInteractListener(new INativeAdvanceInteractListener() {
                @Override
                public void onClick() {
                    Log.i(TAG, "MobListener=> onClick");
                    Toast.makeText(NativeMixActivity.this.getApplicationContext(), "原生广告点击", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onShow() {
                    Log.i(TAG, "MobListener=> onAdShow");
                    Toast.makeText(NativeMixActivity.this.getApplicationContext(), "原生广告展示", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(int code, String msg) {
                    Log.i(TAG, "MobListener=> onError code =" + code + ",msg =" + msg);
                    Toast.makeText(NativeMixActivity.this.getApplicationContext(), "原生广告出错，ret:" + code + ",msg:" + msg, Toast.LENGTH_SHORT).show();
                }
            });
            //
            List<View> clickViewList = new ArrayList<>();
            /**
             * 响应广告点击事件的按钮
             */
            clickViewList.add(findViewById(R.id.click_bn));
            /**
             * 绑定广告点击事件与点击按钮
             * 原生广告的渲染内容必须渲染在NativeAdvanceContainer里面
             */
            mINativeAdData.bindToView(NativeMixActivity.this, mRootView, clickViewList);
            //
            //
            if (mINativeAdData.getCreativeType() == INativeAdvanceData.CREATIVE_TYPE_NATIVE_VIDEO) {
                /*
                 * 绑定广告展示的视频View,请在bindMediaView之前调用bindToView
                 */
                MediaView mediaContainer = (MediaView) adView.findViewById(R.id.video_container);
                mINativeAdData.bindMediaView(adView.getContext(), mediaContainer, new INativeAdvanceMediaListener() {
                    @Override
                    public void onVideoPlayStart() {
                        Log.i("nativeVideo", "MobListener=> onVideoPlayStart");
                    }

                    @Override
                    public void onVideoPlayComplete() {
                        Log.i("nativeVideo", "MobListener=> onVideoPlayComplete");
                    }

                    @Override
                    public void onVideoPlayError(int errorCode, String msg) {
                        Log.i("nativeVideo", "MobListener=> onVideoPlayError :code = " + errorCode + ",msg = " + msg);
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        /**
         *銷毀NativeAd、INativeAdData对象，释放资源。
         */
        if (null != mNativeAdvanceAd) {
            mNativeAdvanceAd.destroyAd();
            mNativeAdvanceAd = null;
        }
        if (null != mINativeAdData) {
            mINativeAdData.release();
            mINativeAdData = null;
        }
        NativeAdapter.exit();
        super.onDestroy();
    }

    private INativeAdvanceLoadListener mINativeAdvanceLoadListener = new INativeAdvanceLoadListener() {

        @Override
        public void onAdSuccess(List<INativeAdvanceData> dataList) {
            Log.i(TAG, "MobListener=> onAdSuccess");
            if (null != dataList && dataList.size() > 0) {
                mINativeAdData = dataList.get(0);
                if (null != mINativeAdData) {
                    mAQuery.id(R.id.show_native_ad_bn).enabled(true);
                    Toast.makeText(NativeMixActivity.this.getApplicationContext(), "加载原生广告成功", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onAdFailed(int ret, String msg) {
            Log.i(TAG, "MobListener=> onAdFailed ret =" + ret + ",msg =" + msg);
            Toast.makeText(NativeMixActivity.this.getApplicationContext(), "加载原生广告失败,错误码：" + ret + ",msg:" + msg, Toast.LENGTH_LONG).show();
        }
    };

    private NativeAdapter.ICloseListener mICloseListener = new NativeAdapter.ICloseListener() {
        @Override
        public void onClose() {
            mAQuery.id(R.id.show_native_ad_bn).enabled(false);
        }
    };
}

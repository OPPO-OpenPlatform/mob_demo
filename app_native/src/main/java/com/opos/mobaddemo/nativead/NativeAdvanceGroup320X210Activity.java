package com.opos.mobaddemo.nativead;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.heytap.msp.mobad.api.ad.NativeAdvanceAd;
import com.heytap.msp.mobad.api.listener.INativeAdvanceInteractListener;
import com.heytap.msp.mobad.api.listener.INativeAdvanceLoadListener;
import com.heytap.msp.mobad.api.params.INativeAdFile;
import com.heytap.msp.mobad.api.params.INativeAdvanceData;
import com.heytap.msp.mobad.api.params.NativeAdvanceContainer;
import com.opos.mobaddemo.posid.Constants;


import java.util.ArrayList;
import java.util.List;

public class NativeAdvanceGroup320X210Activity extends Activity implements INativeAdvanceLoadListener {

    private static final String TAG = "NativeAdvanceGroup320X210Activity";

    private NativeAdvanceAd mNativeAdvanceAd;
    /**
     * 原生广告数据对象。
     */
    private INativeAdvanceData mINativeAdData;
    //
    private AQuery mAQuery;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_advance_group_text_img_320_210);
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        initView();
        initData();
    }

    private void initView() {
        mAQuery = new AQuery(this);
        findViewById(R.id.native_ad_container).setVisibility(View.GONE);
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
            findViewById(R.id.native_ad_container).setVisibility(View.VISIBLE);
            /**
             *展示主图、主图由三张小图的组合而成、小图的大小为320X210。
             */
            if (null != mINativeAdData.getImgFiles() && mINativeAdData.getImgFiles().size() == 3) {
                INativeAdFile iNativeAdFile1 = (INativeAdFile) mINativeAdData.getImgFiles().get(0);
                showImage(iNativeAdFile1.getUrl(), (ImageView) findViewById(R.id.img_1_iv));
                //
                INativeAdFile iNativeAdFile2 = (INativeAdFile) mINativeAdData.getImgFiles().get(1);
                showImage(iNativeAdFile2.getUrl(), (ImageView) findViewById(R.id.img_2_iv));
                //
                INativeAdFile iNativeAdFile3 = (INativeAdFile) mINativeAdData.getImgFiles().get(2);
                showImage(iNativeAdFile3.getUrl(), (ImageView) findViewById(R.id.img_3_iv));
            }
            /**
             * 判断是否需要展示“广告”Logo标签
             */
            if (null != mINativeAdData.getLogoFile()) {
                showImage(mINativeAdData.getLogoFile().getUrl(), (ImageView) findViewById(R.id.logo_iv));
            }
            mAQuery.id(R.id.title_tv).text(null != mINativeAdData.getTitle() ? mINativeAdData.getTitle() : "");
            mAQuery.id(R.id.desc_tv).text(null != mINativeAdData.getDesc() ? mINativeAdData.getDesc() : "");
            /**
             * 处理“关闭”按钮交互行为
             */
            mAQuery.id(R.id.close_iv).clicked(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findViewById(R.id.native_ad_container).setVisibility(View.GONE);
                    mAQuery.id(R.id.show_native_ad_bn).enabled(false);
                }
            });
            mAQuery.id(R.id.click_bn).text(null != mINativeAdData.getClickBnText() ? mINativeAdData.getClickBnText() : "");
            mINativeAdData.setInteractListener(new INativeAdvanceInteractListener() {

                @Override
                public void onClick() {
                    Toast.makeText(NativeAdvanceGroup320X210Activity.this, "原生广告点击", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onShow() {
                    Toast.makeText(NativeAdvanceGroup320X210Activity.this, "原生广告展示", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(int code, String msg) {
                    Toast.makeText(NativeAdvanceGroup320X210Activity.this, "原生广告出错，ret:" + code + ",msg:" + msg, Toast.LENGTH_SHORT).show();
                }
            });
            /**
             *原生广告的渲染内容必须渲染在NativeAdvanceContainer里面
             */
            NativeAdvanceContainer container = (NativeAdvanceContainer) findViewById(R.id.native_ad_container);
            List<View> clickViewList = new ArrayList<>();
            /**
             * 响应广告点击事件的按钮
             */
            clickViewList.add(findViewById(R.id.click_bn));
            /**
             * 绑定广告点击事件与点击按钮
             */
            mINativeAdData.bindToView(this, container, clickViewList);
        }
    }

    private void showImage(String url, ImageView imageView) {
        ImageLoader.getInstance().displayImage(url, imageView);
    }

    private void initData() {
        /**
         *构造NativeAd对象。
         */
        mNativeAdvanceAd = new NativeAdvanceAd(this, Constants.NATIVE_ADVANCE_GROUP_320X210_TEXT_IMG_POS_ID, this);
    }

    @Override
    protected void onDestroy() {
        if (null != mNativeAdvanceAd) {
            /**
             *銷毀NativeAd对象，释放资源。
             */
            mNativeAdvanceAd.destroyAd();
        }
        super.onDestroy();
    }

    /**
     * 广告数据加载成功
     * @param dataList
     */
    @Override
    public void onAdSuccess(List<INativeAdvanceData> dataList) {
        if (null != dataList && dataList.size() > 0) {
            mINativeAdData = dataList.get(0);
            mAQuery.id(R.id.show_native_ad_bn).enabled(true);
            Toast.makeText(this, "加载原生广告成功", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAdFailed(int ret, String msg) {
        Toast.makeText(this, "加载原生广告失败,错误码：" + ret + ",msg:" + msg, Toast.LENGTH_LONG).show();
    }
}

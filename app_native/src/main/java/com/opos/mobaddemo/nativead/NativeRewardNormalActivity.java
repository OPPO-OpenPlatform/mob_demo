package com.opos.mobaddemo.nativead;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.androidquery.AQuery;
import com.heytap.msp.mobad.api.ad.NativeAd;
import com.heytap.msp.mobad.api.listener.INativeRewardAdListener;
import com.heytap.msp.mobad.api.params.INativeAdData;
import com.heytap.msp.mobad.api.params.INativeAdFile;
import com.heytap.msp.mobad.api.params.NativeAdError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

public class NativeRewardNormalActivity extends Activity implements INativeRewardAdListener {

    private static final String TAG = "NativeRewardNormalActivity";
    /**
     * TODO 激励广告前置引导提示，主要用于提示用户如何操作才能获取奖励。目前原生支持两种激励场景：1、点击下载安装完成获取激励；2、点击下载安装后打开获取激励。应用在接入的时候，需要根据自己应用场景，对用户进行一定的引导。
     */
    private static final String REWARD_TIPS_INSTALL_COMPLETED = "注意：点击下载并安装完成可获取奖励";
    private static final String REWARD_TIPS_LAUNCH_APP = "注意：点击下载，安装完成后点击打开可以获取奖励";
    //
    private static final String REWARD_TV_TEXT = "+50积分";
    /**
     * TODO 该文案主要用于当收到应用安装完成通知后、将点击按钮的“下载”文案改成“打开”使用。
     */
    private static final String CLICK_BN_TEXT_LAUNCH_APP = "打开";
    //
    private static final String REWARD_TOAST_TEXT = "完成任务、恭喜获取";
    private static final String ACCOUNT_TOTAL_CREDIT_TEXT = "账户积分：";

    private String mPosId;

    private NativeAd mNativeAd;
    /**
     * 原生广告数据对象。
     */
    private INativeAdData mINativeAdData;
    //
    private AQuery mAQuery;
    /**
     * TODO 目前原生支持两种激励场景：1、点击下载安装完成获取激励；2、点击下载安装后打开获取激励。具体使用哪种激励场景由应用来决策、上面两个激励场景只能选择一种场景激励、不能两个场景都激励。
     */
    private int mRewardScene;

    private AlertDialog mAlertDialog;

    private int mTotalCredit = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_reward_normal);
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        mRewardScene = getIntent().getIntExtra(NativeRewardActivity.EXTRA_KEY_REWARD_SCENE, 0);
        mPosId = getIntent().getStringExtra(NativeRewardActivity.EXTRA_KEY_POS_ID);
        initView();
        initData();
    }

    private void initView() {
        mAQuery = new AQuery(this);
        mAQuery.id(R.id.load_native_ad_bn).clicked(this, "loadAd");
        mAQuery.id(R.id.show_native_ad_bn).clicked(this, "showAd").enabled(false);
    }

    public void loadAd() {
        if (null != mNativeAd) {
            /**
             *调用loadAd方法加载原生广告。
             */
            mNativeAd.loadAd();
        }
    }

    public void showAd() {
        /**
         * TODO 注意：目前只有素材大小为512X512的原生广告位才支持激励广告，请应用在申请原生广告位的时候选择素材为512X512规格的广告位。
         */
        /**
         *在展示原生广告前调用isAdValid判断当前广告是否有效，否则即使展示了广告，也是无效曝光、点击，不计费的
         *注意：每个INativeAdData对象只有一次有效曝光、一次有效点击；多次曝光，多次点击都只扣一次费。
         */
        if (null != mINativeAdData && mINativeAdData.isAdValid()) {
            findViewById(R.id.native_ad_container).setVisibility(View.VISIBLE);
            /**
             *展示推广应用的ICON，大小为512X512。
             */
            if (null != mINativeAdData.getIconFiles() && mINativeAdData.getIconFiles().size() > 0) {
                INativeAdFile iNativeAdFile = (INativeAdFile) mINativeAdData.getIconFiles().get(0);
                showImage(iNativeAdFile.getUrl(),(ImageView)findViewById(R.id.icon_iv));
            }
            /**
             * 判断是否需要展示“广告”Logo标签
             */
            if (null != mINativeAdData.getLogoFile()) {
                showImage(mINativeAdData.getLogoFile().getUrl(),(ImageView)findViewById(R.id.logo_iv));
            }
            mAQuery.id(R.id.title_tv).text(null != mINativeAdData.getTitle() ? mINativeAdData.getTitle() : "");
            mAQuery.id(R.id.desc_tv).text(null != mINativeAdData.getDesc() ? mINativeAdData.getDesc() : "");
            mAQuery.id(R.id.action_bn).text(null != mINativeAdData.getClickBnText() ? mINativeAdData.getClickBnText() : "").clicked(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /**
                     *原生广告被点击时必须调用onAdClick方法通知SDK进行点击统计；
                     * 注意：onAdClick方法必须在onAdShow方法之后再调用才有效，否则是无效点击。
                     */
                    mINativeAdData.onAdClick(v);
                }
            });
            /**
             *TODO 展示激励广告前置引导，这里只是demo简单的引导示例效果、应用可以结合自己的游戏场景自定义即可。
             */
            if (NativeRewardActivity.REWARD_SCENE_INSTALL_COMPLETE == mRewardScene) {
                mAQuery.id(R.id.reward_tips_tv).text(REWARD_TIPS_INSTALL_COMPLETED);
            } else if (NativeRewardActivity.REWARD_SCENE_LAUNCH_APP == mRewardScene) {
                mAQuery.id(R.id.reward_tips_tv).text(REWARD_TIPS_LAUNCH_APP);
            }
            mAQuery.id(R.id.reward_tips_tv).visibility(View.VISIBLE);
            mAQuery.id(R.id.reward_tv).text(REWARD_TV_TEXT).visibility(View.VISIBLE);
            mAQuery.id(R.id.credit_tv).text(ACCOUNT_TOTAL_CREDIT_TEXT + mTotalCredit).visibility(View.VISIBLE);
            /**
             * 原生广告曝光时必须调用onAdShow方法通知SDK进行曝光统计，否则就没有曝光数据。
             */
            mINativeAdData.onAdShow(findViewById(R.id.native_ad_container));
        }
    }


    private void showImage(String url, ImageView imageView) {
        ImageLoader.getInstance().displayImage(url, imageView);
    }

    private void initData() {
        /**
         *TODO 构造NativeAd对象,注意:构造原生激励广告的时候、需要传入激励场景【NativeAd.REWARD_SCENE_INSTALL_COMPLETE-安装完成激励，NativeAd.REWARD_SCENE_LAUNCH_APP-安装完成打开激励】，Listener也是INativeRewardAdListener对象。
         */
        if (NativeRewardActivity.REWARD_SCENE_INSTALL_COMPLETE == mRewardScene) {
            mNativeAd = new NativeAd(this, mPosId, NativeAd.REWARD_SCENE_INSTALL_COMPLETE, this);
        } else if (NativeRewardActivity.REWARD_SCENE_LAUNCH_APP == mRewardScene) {
            mNativeAd = new NativeAd(this, mPosId, NativeAd.REWARD_SCENE_LAUNCH_APP, this);
        }
        /**
         * TODO 户获取奖励提示框、这只是demo效果，应用结合自己的游戏场景自定义即可。
         */
        mAlertDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("获取奖励")
                .setMessage(REWARD_TOAST_TEXT + REWARD_TV_TEXT)
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
    }

    @Override
    protected void onDestroy() {
        if (null != mNativeAd) {
            /**
             *銷毀NativeAd对象，释放资源。
             */
            mNativeAd.destroyAd();
        }
        super.onDestroy();
    }

    /**
     * 原生广告加载成功，在onAdSuccess回调广告数据
     *
     * @param iNativeAdDataList
     */
    @Override
    public void onAdSuccess(List iNativeAdDataList) {
        if (null != iNativeAdDataList && iNativeAdDataList.size() > 0) {
            mINativeAdData = (INativeAdData) iNativeAdDataList.get(0);
            mAQuery.id(R.id.show_native_ad_bn).enabled(true);
            Toast.makeText(NativeRewardNormalActivity.this, "加载原生广告成功", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAdFailed(NativeAdError nativeAdError) {
        Toast.makeText(NativeRewardNormalActivity.this, "加载原生广告失败,错误码：" + nativeAdError.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAdError(NativeAdError nativeAdError, INativeAdData iNativeAdData) {
        Toast.makeText(NativeRewardNormalActivity.this, "调用原生广告统计方法出错,错误码：" + nativeAdError.toString(), Toast.LENGTH_LONG).show();
    }

    /**
     * @param pkgName 下载安装完成回调，pkgName-安装完成的应用包名
     */
    @Override
    public void onInstallCompleted(String pkgName) {
        /**
         * 在收到应用安装完成回调以后，先调用isCurrentApp方法判断当前在展示的广告是不是刚才安装的应用，如果是，才可以将按钮文案从“下载”更改成“打开”，并且将按钮点击行为更改成调用launchApp方法打开应用。
         */
        if (mINativeAdData.isCurrentApp(pkgName)) {
            mAQuery.id(R.id.action_bn).text(CLICK_BN_TEXT_LAUNCH_APP).clicked(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mINativeAdData.launchApp()) {
                        Toast.makeText(NativeRewardNormalActivity.this, "打开应用成功！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NativeRewardNormalActivity.this, "打开应用失败！", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * 激励回调
     *
     * @param objects
     */
    @Override
    public void onReward(Object... objects) {
        /**
         * TODO 在这里给予用户对应的奖励。
         */
        mTotalCredit += 50;
        mAQuery.id(R.id.credit_tv).text(ACCOUNT_TOTAL_CREDIT_TEXT + mTotalCredit);
        mAQuery.id(R.id.reward_tv).visibility(View.GONE);
        mAlertDialog.show();
    }

    @Override
    public void onRewardFail(Object... objects) {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("获取奖励失败")
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }
}

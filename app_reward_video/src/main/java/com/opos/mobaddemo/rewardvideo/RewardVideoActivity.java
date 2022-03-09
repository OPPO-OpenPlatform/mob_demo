package com.opos.mobaddemo.rewardvideo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.heytap.msp.mobad.api.ad.RewardVideoAd;
import com.heytap.msp.mobad.api.listener.IRewardVideoAdListener;
import com.heytap.msp.mobad.api.params.RewardVideoAdParams;
import com.opos.mobaddemo.posid.Constants;


public class RewardVideoActivity extends Activity implements IRewardVideoAdListener {
    private static final String TAG = "RewardVideoActivity";
    /**
     * add 2018-10-25
     * TODO 激励视频广告前置引导提示，主要用于提示用户如何操作才能获取奖励。目前支持三种种激励场景：1、视频播放完成；2、点击下载安装完成获取激励；3、点击下载安装后打开获取激励。应用在接入的时候，需要根据自己应用场景，对用户进行一定的引导。
     */
    private static final String REWARD_SCENE_PLAY_COMPLETE_TIPS = "视频播放完成可以获取一次免费复活机会";
    private static final String REWARD_SCENE_INSTALL_COMPLETE_TIPS = "应用安装完成可以获取一次免费复活机会";
    private static final String REWARD_SCENE_LAUNCH_APP_TIPS = "应用安装完成点击打开可以获取一次免费复活机会";
    private static final String REWARD_SCENE_INTERACT_TIPS = "观看并互动获取奖励";
    private static final String REWARD_SCENE_AD_CLICK = "点击转化按钮领取奖励";
    //
    private static final String REWARD_TOAST_TEXT = "完成任务、恭喜成功获取一次免费复活机会";
    //
    private TextView mStatusTv;
    private RewardVideoAd mRewardVideoAd;
    //add 2018-10-25
    private String mRewardTips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_video);
        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        destroyVideo();
        super.onDestroy();
    }

    private void initView() {
        mStatusTv = (TextView) findViewById(R.id.status_tv);
        mStatusTv.setMovementMethod(new ScrollingMovementMethod());
    }

    private void initData() {
        /**
         * 构造激励视频广告对象
         */
        mRewardVideoAd = new RewardVideoAd(this, Constants.REWARD_VIDEO_POS_ID, this);
        printStatusMsg("初始化视频广告.");
    }

    public void onBnClick(View view) {
        if (view.getId() == R.id.req_video_ad) {
            loadVideo();
        }
    }

    private void loadVideo() {
        /**
         * 调用loadAd方法请求激励视频广告;通过RewardVideoAdParams.setFetchTimeout方法可以设置请求
         * 视频广告最大超时时间，单位毫秒；
         */
        RewardVideoAdParams rewardVideoAdParams = new RewardVideoAdParams.Builder()
                .setFetchTimeout(3000)
                .build();
        mRewardVideoAd.loadAd(rewardVideoAdParams);
        printStatusMsg("请求加载视频广告.");
    }

    private void playVideo() {
        /**
         * TODO 在播放广告时候，先调用isReady方法判断当前是否有广告可以播放；如果有、再调用showAd方法播放激励视频广告。
         */
        if (mRewardVideoAd.isReady()) {
            mRewardVideoAd.showAd();
            printStatusMsg("播放视频广告.");
        }
    }

    private void destroyVideo() {
        /**
         * 销毁激励视频广告
         */
        mRewardVideoAd.destroyAd();
        printStatusMsg("释放视频广告资源.");
    }

    private void printStatusMsg(String txt) {
        if (null != txt) {
            Log.d(TAG, txt);
            mStatusTv.setText(mStatusTv.getText() + "\n" + txt);
        }
    }

    @Override
    public void onAdSuccess() {
        /**
         *TODO 请求视频广告成功、展示播放视频的入口Dialog、根据getRewardScene方法返回的激励场景、提示引导用户如何操作才能获取奖励。这里只是Demo效果、应用可以结合自己的游戏场景来引导。
         */
        switch (mRewardVideoAd.getRewardScene()) {
            case RewardVideoAd.REWARD_SCENE_PLAY_COMPLETE:
                mRewardTips = REWARD_SCENE_PLAY_COMPLETE_TIPS;
                break;
            case RewardVideoAd.REWARD_SCENE_INSTALL_COMPLETE:
                mRewardTips = REWARD_SCENE_INSTALL_COMPLETE_TIPS;
                break;
            case RewardVideoAd.REWARD_SCENE_LAUNCH_APP:
                mRewardTips = REWARD_SCENE_LAUNCH_APP_TIPS;
                break;
            case RewardVideoAd.REWARD_SCENE_PLAY_INTERACTION:
                mRewardTips = REWARD_SCENE_INTERACT_TIPS;
                break;
            case RewardVideoAd.REWARD_SCENE_AD_CLICK:
                mRewardTips = REWARD_SCENE_AD_CLICK;
                break;
            default:
                mRewardTips = "";
        }
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("获取奖励提示")
                .setMessage(mRewardTips)
                .setPositiveButton("播放", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        playVideo();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        alertDialog.show();
        printStatusMsg("请求视频广告成功.");
    }


    @Deprecated
    @Override
    public void onAdFailed(String msg) {
        /**
         * 已废弃，使用onAdFailed(int i, String s)
         */
        // Deprecated do nothing
    }

    @Override
    public void onAdFailed(int i, String s) {
        /**
         * 请求广告失败、不展示播放视频的入口Dialog
         */
        printStatusMsg("请求视频广告失败. code:" +i + ",msg:"+ s);
    }

    @Override
    public void onAdClick(long currentPosition) {
        printStatusMsg("视频广告被点击，当前播放进度 = " + currentPosition + " 秒");
    }

    @Override
    public void onVideoPlayStart() {
        printStatusMsg("视频开始播放.");
    }

    @Override
    public void onVideoPlayComplete() {
        /**
         * TODO 注意：从SDK 3.0.1版本开始，不能在激励视频广告播放完成回调-onVideoPlayComplete里做任何激励操作，统一在onReward回调里给予用户激励。
         */
        printStatusMsg("视频广告播放完成.");
    }

    @Override
    public void onVideoPlayError(String msg) {
        printStatusMsg("视频播放错误，错误信息=" + msg);
    }

    @Override
    public void onVideoPlayClose(long currentPosition) {
        printStatusMsg("视频广告被关闭，当前播放进度 = " + currentPosition + " 秒");
    }

    @Override
    public void onLandingPageOpen() {
        printStatusMsg("视频广告落地页打开.");
    }

    @Override
    public void onLandingPageClose() {
        printStatusMsg("视频广告落地页关闭.");
    }

    /**
     * add 2018-10-25
     *
     * @param objects
     */
    @Override
    public void onReward(Object... objects) {
        /**
         * TODO 注意：只能在收到onReward回调的时候才能给予用户奖励。
         */
        printStatusMsg("给用户发放奖励.");
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("获取奖励提示")
                .setMessage(REWARD_TOAST_TEXT)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        alertDialog.show();
    }
}

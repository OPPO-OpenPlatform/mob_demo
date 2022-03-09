package com.opos.mobaddemo.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.heytap.msp.mobad.api.InitParams;
import com.heytap.msp.mobad.api.MobAdManager;
import com.heytap.msp.mobad.api.listener.IInitListener;
import com.opos.mobaddemo.R;
import com.opos.mobaddemo.posid.Constants;

import com.heytap.msp.mobad.api.ad.LandSplashAd;
import com.heytap.msp.mobad.api.listener.ISplashAdListener;
import com.heytap.msp.mobad.api.params.SplashAdParams;
import java.util.ArrayList;
import java.util.List;

public class LandSplashActivity extends Activity implements ISplashAdListener {
    private static final String TAG = "LandSplashActivity";
    private static final int REQUEST_PERMISSIONS_CODE = 100;
    /**
     * 从请求广告到广告展示出来最大耗时时间，只能在[3000,5000]ms之内。
     */
    private static final int FETCH_TIME_OUT = 3000;
    //
    /**
     * 闪屏广告是半屏广告，广告下面半屏是: 应用ICON+应用标题+应用描述，
     * 应用标题和应用描述由应用在SplashAd构造函数里传入，
     * 应用标题限制最多不超过个 8 个汉字，应用描述限制不超过 13 个汉字。
     */
    private static final String APP_TITLE = "HEYTAP广告联盟";
    private static final String APP_DESC = "让天下没有难做的广告";
    //
    private List<String> mNeedRequestPMSList = new ArrayList<>();
    //
    private LandSplashAd mLandSplashAd;
    /**
     * 判断是否可以立刻跳转应用主页面。
     */
    private boolean mCanJump = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
        super.onCreate(savedInstanceState);
        //
        setContentView(R.layout.activity_splash);
        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        //
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            /**
             * 如果你的targetSDKVersion >= 23，就要主动申请好权限。如果您的App没有适配到Android6.0（即targetSDKVersion < 23），那么只需要在这里直接调用fetchSplashAd方法。
             *
             */
            checkAndRequestPermissions();
        } else {
            /**
             * 如果是Android6.0以下的机器，默认在安装时获得了所有权限，可以直接调用SDK。
             */
            fetchSplashAd();
        }
    }


    private void fetchSplashAd() {
        try {
            /**
             * SplashAd初始化参数、这里可以设置获取广告最大超时时间，
             */
            //
            SplashAdParams splashAdParams = new SplashAdParams.Builder()
                    .setFetchTimeout(FETCH_TIME_OUT)
                    .setTitle(APP_TITLE)
                    .setDesc(APP_DESC)
                    .build();
            /**
             * 构造SplashAd对象
             * 注意：构造函数传入的几个形参都不能为空，否则将抛出NullPointerException异常。
             */
            mLandSplashAd = new LandSplashAd(this, Constants.LAND_SPLASH_POS_ID, this, splashAdParams);
        } catch (Exception e) {
            Log.w(TAG, "", e);
            /**
             *  出错，直接finish(),跳转应用主页面。
             */
            goMainActivity();
        }
    }

    /**
     * 结束闪屏页面，跳转主页面。
     */
    private void next() {
        if (mCanJump) {
            goMainActivity();
        } else {
            mCanJump = true;
        }
    }

    @Override
    public void onAdShow() {
        Log.d(TAG, "onAdShow");
    }

    @Override
    public void onAdShow(String s) {
        Log.d(TAG, "onAdShow:"+s);
    }

    @Override
    public void onAdFailed(String errMsg) {
        // 已废弃，使用onAdFailed(int i, String s)
    }

    @Override
    public void onAdFailed(int i, String s) {
        /**
         * 如果加载广告失败，直接finish(),跳转应用主页面。
         */
        Log.d(TAG, "onAdFailed code:" + i + ",msg:" + s);
        goMainActivity();
    }

    @Override
    public void onAdClick() {
        Log.d(TAG, "onAdClick");
    }

    @Override
    public void onAdDismissed() {
        /**
         *广告播放完毕或者用户点击“跳过”按钮，跳转应用主页面。
         */
        Log.d(TAG, "onAdDismissed");
        next();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        /**
         * 这里包含对于点击闪屏广告以后、然后返回闪屏广告页面立刻跳转应用主页面的处理。
         */
        if (mCanJump) {
            next();
        }
        mCanJump = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        /**
         * 这里包含对于点击闪屏广告以后、然后返回闪屏广告页面立刻跳转应用主页面的处理。
         */
        mCanJump = false;
    }

    @Override
    protected void onDestroy() {
        if (null != mLandSplashAd) {
            mLandSplashAd.destroyAd();
        }
        super.onDestroy();
    }

    /**
     * 开屏页一定要禁止用户对返回按钮的控制，否则将可能导致用户手动退出了App而广告无法正常曝光和计费。
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 申请SDK运行需要的权限
     * 注意：在Android Q以下READ_PHONE_STATE权限是必须权限，没有这个权限SDK无法正常获得广告。
     * WRITE_EXTERNAL_STORAGE 、ACCESS_FINE_LOCATION 是可选权限；没有不影响SDK获取广告；但是如果应用申请到该权限，会显著提升应用的广告收益。
     */
    private void checkAndRequestPermissions() {
        /**
         * 在Android Q以下READ_PHONE_STATE 权限是必须权限，没有这个权限SDK无法正常获得广告。
         */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            mNeedRequestPMSList.add(Manifest.permission.READ_PHONE_STATE);
        }
        /**
         * WRITE_EXTERNAL_STORAGE 是可选权限；没有不影响SDK获取广告；但是如果应用申请到该权限，会显著提升应用的广告收益。
         */
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            mNeedRequestPMSList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        /**
         * ACCESS_FINE_LOCATION 是可选权限；没有不影响SDK获取广告；但是如果应用申请到该权限，会显著提升应用的广告收益。
         */
        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            mNeedRequestPMSList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        //
        if (0 == mNeedRequestPMSList.size()) {
            /**
             * 权限都已经有了，那么直接调用SDK请求广告。
             */
            fetchSplashAd();
        } else {
            /**
             * 有权限需要申请，主动申请。
             */
            String[] temp = new String[mNeedRequestPMSList.size()];
            mNeedRequestPMSList.toArray(temp);
            ActivityCompat.requestPermissions(this, temp, REQUEST_PERMISSIONS_CODE);
        }
    }

    /**
     * 处理权限申请的结果
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            /**
             *处理SDK申请权限的结果。
             */
            case REQUEST_PERMISSIONS_CODE:
                if (hasNecessaryPMSGranted()) {
                    /*
                     * 获取权限成功之后需初始化，可解决第一次打开应用未获取权限导致SDK初始化失败的问题
                     */
                    InitParams initParams = new InitParams.Builder()
                            .setDebug(true)//true打开SDK日志，当应用发布Release版本时，必须注释掉这行代码的调用，或者设为false
                            .build();
                    /**
                     * 调用这行代码初始化广告SDK
                     */
                    MobAdManager.getInstance().init(this, Constants.APP_ID, initParams, new IInitListener() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "IInitListener onSuccess");
                        }

                        @Override
                        public void onFailed(String reason) {
                            Log.d(TAG, "IInitListener onFailed");
                        }
                    });
                    /**
                     * 应用已经获得SDK运行必须的READ_PHONE_STATE权限，直接请求广告。
                     */
                    fetchSplashAd();
                } else {
                    goMainActivity();
                }
//                    /**
//                     * 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
//                     */
//                    Toast.makeText(this, "应用缺少SDK运行必须的READ_PHONE_STATE权限！请点击\"应用权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                    intent.setData(Uri.parse("package:" + getPackageName()));
//                    startActivity(intent);
//                    finish();
                break;
            default:
                break;
        }
    }

    public void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 判断应用是否已经获得SDK运行必须的READ_PHONE_STATE权限。
     *
     * @return
     */
    private boolean hasNecessaryPMSGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q || PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            return true;
        }
        return false;
    }
}

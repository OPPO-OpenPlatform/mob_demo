package com.opos.mobaddemo.hotsplash;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.opos.mobaddemo.posid.Constants;

public class HotSplashControlActivity extends Activity {
    private final static String TAG = "HotSplashControlActivity";
    private CheckBox mPortChb;
    private boolean isReady;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ((ViewGroup) this.getWindow().getDecorView().findViewById(android.R.id.content)).setForceDarkAllowed(false);
        }
        setContentView(R.layout.activity_hot_control_splash);
        mPortChb = findViewById(R.id.chb_hot_splash_port);
        mPortChb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }
        });
    }

    public void doReset(View view) {
        HotSplashInstance.getInstance().reset(this, new HotSplashInstance.LoadListener() {
            @Override
            public void onAdReady() {
                Log.i(TAG, "MobListener=> onAdReady");
                isReady = true;
                Toast.makeText(HotSplashControlActivity.this, "ad ready", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailed(int code, String errMsg) {
                Log.i(TAG, "MobListener=> onAdFailed =" + code + ",msg =" + errMsg);
                isReady = false;
                Toast.makeText(HotSplashControlActivity.this, "ad fail " + code + ":" + errMsg, Toast.LENGTH_SHORT).show();
            }
        }, getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ? Constants.SPLASH_POS_ID : Constants.LAND_SPLASH_POS_ID);
    }

    public void doShow(View view) {
        if (!isReady) {
            Toast.makeText(HotSplashControlActivity.this, "ad had not load", Toast.LENGTH_SHORT).show();
            return;
        }
        HotSplashInstance.getInstance().showAd(this, mShowListener);
    }

    @Override
    protected void onDestroy() {
        HotSplashInstance.getInstance().destroy();
        super.onDestroy();
    }


    private HotSplashInstance.ShowListener mShowListener = new HotSplashInstance.ShowListener() {
        private static final String TAG = "HotSplash";

        @Override
        public void onAdClick() {
            Log.i(TAG, "MobListener=> onAdClick");
        }

        @Override
        public void onAdDismissed() {
            Log.i(TAG, "MobListener=> onAdDismissed");
            /**
             *广告播放完毕或者用户点击“跳过”按钮，跳转应用主页面。
             */
            HotSplashInstance.getInstance().destroy();
        }

        @Override
        public void onAdShow(String transportData) {
            Log.i(TAG, "MobListener=> onAdShow = " + transportData);
        }


        @Override
        public void onAdFailed(int code, String errMsg) {
            Log.i(TAG, "MobListener=> onAdFailed code:" + code + ",msg:" + errMsg);
            /**
             * 如果加载广告失败，直接finish(),跳转应用主页面。
             */
            Toast.makeText(HotSplashControlActivity.this, "广告加载失败", Toast.LENGTH_SHORT).show();
        }
    };
}

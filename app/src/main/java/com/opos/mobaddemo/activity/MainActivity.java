package com.opos.mobaddemo.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import android.view.View;
import android.widget.Toast;

import com.heytap.msp.mobad.api.MobAdManager;
import com.opos.mobaddemo.R;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 判断应用是否已经获得SDK运行必须的READ_PHONE_STATE、WRITE_EXTERNAL_STORAGE两个权限。
     * Q以上可以不用READ_PHONE_STATE权限
     *
     * @return
     */
    private boolean hasNecessaryPMSGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q || PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            return true;
        }
        return false;
    }

    public void onBnClick(View view) {
        switch (view.getId()) {
            case R.id.banner_bn:
                startBannerActivity();
                break;
            case R.id.interstitial_bn:
                startInterstitialActivity();
                break;
            case R.id.interstitial_video_bn:
                startInterstitialVideoActivity();
                break;
            case R.id.mix_bn:
                startMixActivity();
                break;
            case R.id.native_bn:
                startNativeActivity();
                break;
            case R.id.native_advance_bn:
                startNativeAdvanceActivity();
                break;
            //add 2018-04-09、启动原生模板广告
            case R.id.native_templet_bn:
                startNativeTempletActivity();
                break;
            //add 2018-04-15、启动激励视频广告
            case R.id.reward_video_bn:
                startRewardVideoActivity();
                break;
            //add 2018-10-18、启动原生激励广告
            case R.id.native_reward_bn:
                startNativeRewardActivity();
                break;
            //add 2020/4/9 启动资讯广告
            case R.id.contentad_btn:
                startContentAdActivity();
                break;
            case R.id.hot_splash_bn:
                startHotSplashAdActivity();
                break;
        }
    }

    private void startHotSplashAdActivity() {
        Class activityClass;
        try {
            activityClass = Class.forName("com.opos.mobaddemo.hotsplash.HotSplashControlActivity");
        } catch (Exception e) {
            Toast.makeText(this.getApplicationContext(), "module error", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(activityClass);
    }


    private void startBannerActivity() {
        Class activityClass;
        try {
            activityClass = Class.forName("com.opos.mobaddemo.banner.BannerActivity");
        } catch (Exception e) {
            Toast.makeText(this.getApplicationContext(), "module error", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(activityClass);
    }


    private void startInterstitialActivity() {
        Class activityClass;
        try {
            activityClass = Class.forName("com.opos.mobaddemo.interstitial.InterstitialChooseActivity");
        } catch (Exception e) {
            Toast.makeText(this.getApplicationContext(), "module error", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(activityClass);
    }

    private void startInterstitialVideoActivity() {
        Class activityClass;
        try {
            activityClass = Class.forName("com.opos.mobaddemo.interstitial.InterstitialVideoChooseActivity");
        } catch (Exception e) {
            Toast.makeText(this.getApplicationContext(), "module error", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(activityClass);
    }

    private void startMixActivity() {
        Class activityClass;
        try {
            activityClass = Class.forName("com.opos.mobaddemo.mix.MixActivity");
        } catch (Exception e) {
            Toast.makeText(this.getApplicationContext(), "module error", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(activityClass);
    }

    private void startNativeAdvanceActivity() {
        Class activityClass;
        try {
            activityClass = Class.forName("com.opos.mobaddemo.nativead.NativeAdvanceActivity");
        } catch (Exception e) {
            Toast.makeText(this.getApplicationContext(), "module error", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(activityClass);
    }

    private void startNativeActivity() {
        Class activityClass;
        try {
            activityClass = Class.forName("com.opos.mobaddemo.nativead.NativeActivity");
        } catch (Exception e) {
            Toast.makeText(this.getApplicationContext(), "module error", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(activityClass);
    }

    //add 2018-04-09
    private void startNativeTempletActivity() {
        Class activityClass;
        try {
            activityClass = Class.forName("com.opos.mobaddemo.nativetemplate.NativeTempletActivity");
        } catch (Exception e) {
            Toast.makeText(this.getApplicationContext(), "module error", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(activityClass);
    }

    //add 2018-04-15
    private void startRewardVideoActivity() {
        Class activityClass;
        try {
            activityClass = Class.forName("com.opos.mobaddemo.rewardvideo.RewardVideoActivity");
        } catch (Exception e) {
            Toast.makeText(this.getApplicationContext(), "module error", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(activityClass);
    }

    //add 2018-10-18
    private void startNativeRewardActivity() {
        Class activityClass;
        try {
            activityClass = Class.forName("com.opos.mobaddemo.nativead.NativeRewardActivity");
        } catch (Exception e) {
            Toast.makeText(this.getApplicationContext(), "module error", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(activityClass);
    }

    private void startContentAdActivity() {
        Class activityClass;
        try {
            activityClass = Class.forName("com.opos.mobaddemo.contentad.ContentAdActivity");
        } catch (Exception e) {
            Toast.makeText(this.getApplicationContext(), "module error", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(activityClass);
    }

    @Override
    public void onBackPressed() {
        /**
         * 在你的应用程序进程退出时，调用该方法释放SDK 资源
         * */

        MobAdManager.getInstance().exit(this);
        //
        super.onBackPressed();
        try {
            /**
             * 退出应用进程
             */
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startActivity(Class activityClass) {
        if (!hasNecessaryPMSGranted()) {
            Toast.makeText(this.getApplicationContext(), "没有 READ_PHONE_STATE 或 WRITE_EXTERNAL_STORAGE 权限，SDK无法正常运行!!!", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }
}

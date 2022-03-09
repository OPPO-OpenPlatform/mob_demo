package com.opos.mobaddemo;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import androidx.multidex.MultiDex;

import com.heytap.msp.mobad.api.InitParams;
import com.heytap.msp.mobad.api.MobAdManager;
import com.heytap.msp.mobad.api.listener.IInitListener;
import com.opos.mobaddemo.activity.MainActivity;
import com.opos.mobaddemo.activity.SplashHotStartActivity;
import com.opos.mobaddemo.posid.Constants;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class MobAdDemoApp extends Application {
    /**
     * 注意：此页面的冷热启动处理，在横竖屏切换重走 Activity 也会被认为是热启动
     * 横竖屏切换不想被判断为冷热启动则，在activity配置文件中加 android:configChanges="orientation|keyboardHidden|screenSize"
     * 或者写死 Activity 的横竖屏属性
     */
    /***
     * 非必需（不判断冷热启动不用这个参数）
     * 判断是否冷启动 true 正在冷启动  ，false 已经冷启动
     **/
    private boolean isColdStart = true;
    /***
     * 非必需（不判断冷热启动不用这个参数）
     * 1.activityStartNum 为 1 且 isColdStart = false 为热启动
     * 2.activityStartNum 为 0 判断为离开当前应用或者关闭当前应用
     **/
    private int activityStartNum = 0;

    /**
     * 离开app的时间点
     */
    private long leaveAppTime = 0;

    /**
     * 已经跳转到主页面，代表着冷启动展示广告过程已结束，广告过程包括跳转中间页
     */
    private boolean hasJumpMainActivity = false;

    /**
     * 若HotSplashActivity由于跳转中间页，不能再拉取新广告
     */
    private boolean canShowHotSplashActivity = true;

    //
    public long getLeaveAppTime() {
        return leaveAppTime;
    }

    /***
     * 非必需（不判断冷热启动不用这个参数）
     * 声明一个监听Activity们生命周期的接口
     */
    private ActivityLifecycleCallbacks activityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
        /**
         * application下的每个Activity声明周期改变时，都会触发以下的函数。
         */
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
            ++activityStartNum;
            if (!hasJumpMainActivity && activity instanceof MainActivity) {
                hasJumpMainActivity = true;
            }
            /***
             * 处理全局热启动
             * 热启动时如果用户离开当前 app 达到自定义条件，且不在开屏页面就跳转开屏页
             */
            if (isHotStart() && customCondition()) {
                if (!(activity instanceof SplashHotStartActivity) && canShowHotSplashActivity) {
                    Intent intent = new Intent(activity.getApplicationContext(), SplashHotStartActivity.class);
                    activity.startActivity(intent);
                    canShowHotSplashActivity = false;
                }
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
            if (isColdStart) {
                isColdStart = false;
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
            --activityStartNum;
            //记录离开app的时间点
            if (activityStartNum == 0) {
                leaveAppTime = System.currentTimeMillis();
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            if (activity instanceof SplashHotStartActivity) {
                canShowHotSplashActivity = true;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 应用必须加入这行代码，初始化广告SDK
         */
        initSdk();
        /**
         * 解决Android9.0以上出现Detected problems with API compatibility的弹框
         */
        closeAndroidPDialog();
        /**
         * 非必需（不做冷热启动判断不需要这行）
         * 注册一个监听所有 Activity 生命周期的接口回调
         */
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    private void initSdk() {
        InitParams initParams = new InitParams.Builder()
                .setDebug(true)//true打开SDK日志，当应用发布Release版本时，必须注释掉这行代码的调用，或者设为false
                .build();
        /**
         * 调用这行代码初始化广告SDK
         */
        MobAdManager.getInstance().init(this, Constants.APP_ID, initParams, new IInitListener() {
            @Override
            public void onSuccess() {
                Log.d("MobAdDemoApp", "IInitListener onSuccess");
            }

            @Override
            public void onFailed(String s) {
                Log.d("MobAdDemoApp", "IInitListener onFailed");
            }
        });
    }

    private void closeAndroidPDialog() {
        try {
            Class<?> cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 非必需（不判断冷热启动不用这个方法）
     * 热启动判断 在Activity生命周期 onStart() 或者
     * onResume() 的 super.onResume()之前中调用才有效
     *
     * @return true 热启动
     */
    public boolean isHotStart() {
        if (1 == activityStartNum && !isColdStart && hasJumpMainActivity) {
            return true;
        }
        return false;
    }

    /**
     * 开发者可以自定义该方法去判断用户离开 app 后，再返回 app 时否显示广告的条件
     * * 非必须，不做热启动判断不用该参数
     * * 用来做热启动判断
     * 用户可自定义条件
     * 这里以用户离开qpp超过 2 分钟为例
     * 判断时间 = 当前时间点 - 冷启动完毕时间点
     *
     * @return true 超过2分钟(120000毫秒), false 不超过2分钟
     */
    private boolean customCondition() {
        return System.currentTimeMillis() - getLeaveAppTime() > 120000;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}

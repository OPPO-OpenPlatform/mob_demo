package com.opos.mobaddemo.nativead;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.opos.mobaddemo.posid.Constants;


public class NativeRewardActivity extends Activity {
    /**
     * 激励场景、有两种：1、下载完成后激励；2、下载完成打开后激励；
     * 应用在接入的时候只能选择其中一种激励场景；
     */
    public static final String EXTRA_KEY_REWARD_SCENE = "rewardScene";
    public static final String EXTRA_KEY_POS_ID = "posId";
    public static final int REWARD_SCENE_INSTALL_COMPLETE = 1;
    public static final int REWARD_SCENE_LAUNCH_APP = REWARD_SCENE_INSTALL_COMPLETE + 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        setContentView(R.layout.activity_native_reward);
    }

    public void onBnClick(View view) {
        int id = view.getId();
        if (id == R.id.native_reward_normal_install_bn) {
            startNormalActivity(REWARD_SCENE_INSTALL_COMPLETE);
        } else if (id == R.id.native_reward_normal_launch_bn) {
            startNormalActivity(REWARD_SCENE_LAUNCH_APP);
        } else if (id == R.id.native_reward_recycler_view_install_bn) {
            startRecyclerViewActivity(REWARD_SCENE_INSTALL_COMPLETE);
        } else if (id == R.id.native_reward_recycler_view_launch_bn) {
            startRecyclerViewActivity(REWARD_SCENE_LAUNCH_APP);
        }
    }

    /**
     * 普通激励场景
     */
    private void startNormalActivity(int rewardScene) {
        Intent intent = new Intent(this, NativeRewardNormalActivity.class);
        intent.putExtra(EXTRA_KEY_REWARD_SCENE, rewardScene);
        intent.putExtra(EXTRA_KEY_POS_ID, Constants.NATIVE_REWARD_NORMAL_POS_ID);
        startActivity(intent);
    }

    /**
     * 列表形式的激励场景
     */
    private void startRecyclerViewActivity(int rewardScene) {
        Intent intent = new Intent(this, NativeRewardRecyclerViewActivity.class);
        intent.putExtra(EXTRA_KEY_REWARD_SCENE, rewardScene);
        intent.putExtra(EXTRA_KEY_POS_ID, Constants.NATIVE_REWARD_RECYCLER_VIEW_POS_ID);
        startActivity(intent);
    }

}

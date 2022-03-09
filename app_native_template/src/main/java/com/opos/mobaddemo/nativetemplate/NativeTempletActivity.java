package com.opos.mobaddemo.nativetemplate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.opos.mobaddemo.posid.Constants;


public class NativeTempletActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        setContentView(R.layout.activity_native_templet);
    }

    public void onBnClick(View view) {
        int id = view.getId();
        if (id == R.id.native_temple_640X320_normal_bn || id == R.id.native_temple_320X210_normal_bn || id == R.id.native_temple_group_320X210_normal_bn) {
            startNativeNormalActivity(getPosId(view.getId()));
        } else if (id == R.id.native_temple_640X320_recycler_bn || id == R.id.native_temple_320X210_recycler_bn || id == R.id.native_temple_group_320X210_recycler_bn) {
            startNativeRecyclerViewActivity(getPosId(view.getId()));
        }
    }

    /**
     * 启动普通的原生模板广告
     *
     * @param posId
     */
    private void startNativeNormalActivity(String posId) {
        Intent intent = new Intent(this, NativeTempletNormalActivity.class);
        intent.putExtra(NativeTempletNormalActivity.EXTRA_KEY_POS_ID, posId);
        startActivity(intent);
    }

    /**
     * 启动内嵌在List里面的原生模板广告
     *
     * @param posId
     */
    private void startNativeRecyclerViewActivity(String posId) {
        Intent intent = new Intent(this, NativeTempletRecyclerViewActivity.class);
        intent.putExtra(NativeTempletRecyclerViewActivity.EXTRA_KEY_POS_ID, posId);
        startActivity(intent);
    }

    private String getPosId(int viewId) {
        String posId = "";
        if (viewId == R.id.native_temple_640X320_normal_bn || viewId == R.id.native_temple_640X320_recycler_bn) {
            posId = Constants.NATIVE_TEMPLET_640X320_POS_ID;
        } else if (viewId == R.id.native_temple_320X210_normal_bn || viewId == R.id.native_temple_320X210_recycler_bn) {
            posId = Constants.NATIVE_TEMPLET_320X210_POS_ID;
        } else if (viewId == R.id.native_temple_group_320X210_normal_bn || viewId == R.id.native_temple_group_320X210_recycler_bn) {
            posId = Constants.NATIVE_TEMPLET_GROUP_320X210_POS_ID;
        }
        return posId;
    }
}

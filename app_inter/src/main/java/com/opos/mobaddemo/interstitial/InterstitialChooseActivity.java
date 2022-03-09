package com.opos.mobaddemo.interstitial;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.opos.mobaddemo.posid.Constants;

/*
* 使用新插屏样式广告位注意区分广告方向属性，横屏广告位展示在竖屏应用可能会有样式异常
* */
public class InterstitialChooseActivity extends Activity {
    private EditText mVerticalET;
    private EditText mHorizontalET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial_chooice);
        mVerticalET = (EditText) this.findViewById(R.id.inter_vertical_posid_et);
        mVerticalET.setText(Constants.INTERSTITIAL_VERTICAL_POS_ID);
        mHorizontalET = (EditText) this.findViewById(R.id.inter_horizontal_posid_et);
        mHorizontalET.setText(Constants.INTERSTITIAL_HORIZONTAL_POS_ID);
    }

    public void onBnClick(View view) {
        Intent intent = new Intent(this, InterstitialActivity.class);
        String posId;
        if (view.getId() == R.id.inter_vertical_bn) {//竖屏插屏
            intent.putExtra(InterstitialActivity.INTERSTITIAL_ORI, InterstitialActivity.ORI_VERTICAL);
            if (null == mVerticalET.getText() || TextUtils.isEmpty(mVerticalET.getText().toString())) {
                posId = Constants.INTERSTITIAL_VERTICAL_POS_ID;
            } else {
                posId = mVerticalET.getText().toString();
            }
        } else {//横屏插屏
            intent.putExtra(InterstitialActivity.INTERSTITIAL_ORI, InterstitialActivity.ORI_HORIZONTAL);
            if (null == mHorizontalET.getText() || TextUtils.isEmpty(mHorizontalET.getText().toString())) {
                posId = Constants.INTERSTITIAL_HORIZONTAL_POS_ID;
            } else {
                posId = mHorizontalET.getText().toString();
            }
        }
        intent.putExtra(InterstitialActivity.INTERSTITIAL_POS_ID, posId);
        this.startActivity(intent);
    }
}

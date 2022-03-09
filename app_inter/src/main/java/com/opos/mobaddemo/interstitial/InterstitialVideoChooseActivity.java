package com.opos.mobaddemo.interstitial;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.opos.mobaddemo.posid.Constants;

public class InterstitialVideoChooseActivity extends Activity {
    private EditText mVerticalET;
    private EditText mHorizontalET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial_video_chooice);
        mVerticalET = (EditText) this.findViewById(R.id.inter_vertical_posid_et);
        mVerticalET.setText(Constants.INTERSTITIAL_VIDEO_VERTICAL_POS_ID);
        mHorizontalET = (EditText) this.findViewById(R.id.inter_horizontal_posid_et);
        mHorizontalET.setText(Constants.INTERSTITIAL_VIDEO_HORIZONTAL_POS_ID);
    }

    public void onBnClick(View view){
        Intent intent = new Intent(this, InterstitialVideoActivity.class);
        String posId;
        if (view.getId() == R.id.inter_vertical_bn){//竖屏插屏
            intent.putExtra(InterstitialVideoActivity.INTERSTITIAL_ORI, InterstitialVideoActivity.ORI_VERTICAL);
            if (null == mVerticalET.getText() || TextUtils.isEmpty(mVerticalET.getText().toString())) {
                posId = Constants.INTERSTITIAL_VIDEO_VERTICAL_POS_ID;
            } else {
                posId = mVerticalET.getText().toString();
            }
        } else {//横屏插屏
            intent.putExtra(InterstitialVideoActivity.INTERSTITIAL_ORI, InterstitialVideoActivity.ORI_HORIZONTAL);
            if (null == mHorizontalET.getText() || TextUtils.isEmpty(mHorizontalET.getText().toString())) {
                posId = Constants.INTERSTITIAL_VIDEO_HORIZONTAL_POS_ID;
            } else {
                posId = mHorizontalET.getText().toString();
            }
        }
        intent.putExtra(InterstitialVideoActivity.INTERSTITIAL_POS_ID, posId);
        this.startActivity(intent);
    }
}

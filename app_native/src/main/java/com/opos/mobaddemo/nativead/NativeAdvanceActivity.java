package com.opos.mobaddemo.nativead;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;




public class NativeAdvanceActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        setContentView(R.layout.activity_native_advance);
    }

    public void onBnClick(View view) {
        if(view.getId() == R.id.native_512X512_text_icon_bn){
            startNative512X512Activity();
        }else if(view.getId() == R.id.native_640X320_text_img_bn){
            startNative640X320Activity();
        }else if(view.getId() == R.id.native_320X210_text_img_bn){
            startNative320X210Activity();
        }else if(view.getId() == R.id.native_group_320X210_text_img_bn){
            startNativeGroup320X210Activity();
        }else if (view.getId() == R.id.native_video_text_bn) {
            startNativeVideoActivity();
        } else if (view.getId() == R.id.native_recycle_view_video_text_bn) {
            startNativeVideoRecycleActivity();
        } else if (view.getId() == R.id.native_mix_bn){
            startNativeMixActivity();
        }
    }

    /**
     * 启动素材规格为512X512大小的图文混合的原生广告
     */
    private void startNative512X512Activity() {
        Intent intent = new Intent(this, NativeAdvance512X512Activity.class);
        startActivity(intent);
    }

    /**
     * 启动素材规格为640X320大小的图文混合的原生广告
     */
    private void startNative640X320Activity() {
        Intent intent = new Intent(this, NativeAdvance640X320Activity.class);
        startActivity(intent);
    }

    /**
     * 启动素材规格为320X210大小的图文混合的原生广告
     */
    private void startNative320X210Activity() {
        Intent intent = new Intent(this, NativeAdvance320X210Activity.class);
        startActivity(intent);
    }

    /**
     * 启动素材规格为三张320X210大小的图文混合的原生广告
     */
    private void startNativeGroup320X210Activity() {
        Intent intent = new Intent(this, NativeAdvanceGroup320X210Activity.class);
        startActivity(intent);
    }

    /**
     * 启动素材规格视频的原生广告
     */
    private void startNativeVideoActivity() {
        Intent intent = new Intent(this, NativeAdvanceVideoActivity.class);
        startActivity(intent);
    }

    /**
     * 启动素材规格视频列表的原生广告
     */
    private void startNativeVideoRecycleActivity() {
        Intent intent = new Intent(this, NativeAdvanceVideoRecyclerViewActivity.class);
        startActivity(intent);
    }

    /**
     * 启动素材规格混出的原生广告
     */
    private void startNativeMixActivity() {
        Intent intent = new Intent(this, NativeMixActivity.class);
        startActivity(intent);
    }


}

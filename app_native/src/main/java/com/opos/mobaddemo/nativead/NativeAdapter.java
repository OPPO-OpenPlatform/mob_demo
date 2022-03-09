package com.opos.mobaddemo.nativead;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.heytap.msp.mobad.api.params.INativeAdFile;
import com.heytap.msp.mobad.api.params.INativeAdvanceData;
import com.nostra13.universalimageloader.core.ImageLoader;

public class NativeAdapter {
    private static final String TAG = "NativeAdapter";

    private static ViewGroup sIconViewGroup;
    private static ViewGroup sIcon640X320ViewGroup;
    private static ViewGroup sIcon320X210ViewGroup;
    private static ViewGroup sIconGroupViewGroup;
    private static ViewGroup sVideoViewGroup;

    public static ViewGroup createViewByData(Context context, ViewGroup container, INativeAdvanceData iNativeAdvanceData, final ICloseListener iCloseListener) {
        context = context.getApplicationContext();
        int creativeType = iNativeAdvanceData.getCreativeType();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        ViewGroup rootView = null;
        switch (creativeType) {
            case INativeAdvanceData.CREATIVE_TYPE_NATIVE_VIDEO:
                rootView = createNativeVideoView(layoutInflater, iNativeAdvanceData, container, iCloseListener);
                break;
            case INativeAdvanceData.CREATIVE_TYPE_TEXT_ICON:
                rootView = createIconView(layoutInflater, iNativeAdvanceData, container);
                break;
            case INativeAdvanceData.CREATIVE_TYPE_TEXT_ICON_320X210:
                rootView = createIcon320X210View(layoutInflater, iNativeAdvanceData, container, iCloseListener);
                break;
            case INativeAdvanceData.CREATIVE_TYPE_TEXT_ICON_640X320:
                rootView = createIcon640X320View(layoutInflater, iNativeAdvanceData, container, iCloseListener);
                break;
            case INativeAdvanceData.CREATIVE_TYPE_TEXT_ICON_GROUP_320X210:
                rootView = createIconGroup320X210View(layoutInflater, iNativeAdvanceData, container, iCloseListener);
                break;
            default:
                Toast.makeText(context, "error creative type =" + creativeType, Toast.LENGTH_SHORT).show();
                break;
        }
        return rootView;
    }

    private static ViewGroup createNativeVideoView(LayoutInflater layoutInflater, final INativeAdvanceData iNativeAdvanceData, ViewGroup container, final ICloseListener iCloseListener) {
        if (null == sVideoViewGroup) {
            sVideoViewGroup = (ViewGroup) layoutInflater.inflate(R.layout.activity_native_advance_text_video_item, container, false);
        }
        Log.d(TAG, "MobListener=>createNativeVideoView" + sVideoViewGroup);
        /**
         * 判断是否需要展示“广告”Logo标签
         */
        if (null != iNativeAdvanceData.getLogoFile() && null != iNativeAdvanceData.getLogoFile().getUrl()) {
            showImage(iNativeAdvanceData.getLogoFile().getUrl(), (ImageView) sVideoViewGroup.findViewById(R.id.logo_iv));
        }
        ((TextView) sVideoViewGroup.findViewById(R.id.title_tv)).setText(null != iNativeAdvanceData.getTitle() ? iNativeAdvanceData.getTitle() : "");
        ((TextView) sVideoViewGroup.findViewById(R.id.desc_tv)).setText(null != iNativeAdvanceData.getDesc() ? iNativeAdvanceData.getDesc() : "");
        /**
         * 处理“关闭”按钮交互行为
         */
        ((ImageView) sVideoViewGroup.findViewById(R.id.close_iv)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sVideoViewGroup.setVisibility(View.GONE);
                if (null != iCloseListener) iCloseListener.onClose();
                /*
                 * 注意释放视频资源，否则视频资源可能一直占用
                 * */
                if (null != iNativeAdvanceData) {
                    iNativeAdvanceData.release();
                }
            }
        });
        ((TextView) sVideoViewGroup.findViewById(R.id.click_bn)).setText(null != iNativeAdvanceData.getClickBnText() ? iNativeAdvanceData.getClickBnText() : "");
        return sVideoViewGroup;
    }

    private static ViewGroup createIconGroup320X210View(LayoutInflater layoutInflater, INativeAdvanceData iNativeAdvanceData, ViewGroup container, final ICloseListener iCloseListener) {
        if (null == sIconGroupViewGroup) {
            sIconGroupViewGroup = (ViewGroup) layoutInflater.inflate(R.layout.activity_native_advance_group_text_img_320_210_item, container, false);
        }
        Log.d(TAG, "MobListener=>createIconGroup320X210View " + sIconGroupViewGroup);
        /**
         *展示主图、主图由三张小图的组合而成、小图的大小为320X210。
         */
        if (null != iNativeAdvanceData.getImgFiles() && iNativeAdvanceData.getImgFiles().size() == 3) {
            INativeAdFile iNativeAdFile1 = (INativeAdFile) iNativeAdvanceData.getImgFiles().get(0);
            showImage(iNativeAdFile1.getUrl(), (ImageView) sIconGroupViewGroup.findViewById(R.id.img_1_iv));
            //
            INativeAdFile iNativeAdFile2 = (INativeAdFile) iNativeAdvanceData.getImgFiles().get(1);
            showImage(iNativeAdFile2.getUrl(), (ImageView) sIconGroupViewGroup.findViewById(R.id.img_2_iv));
            //
            INativeAdFile iNativeAdFile3 = (INativeAdFile) iNativeAdvanceData.getImgFiles().get(2);
            showImage(iNativeAdFile3.getUrl(), (ImageView) sIconGroupViewGroup.findViewById(R.id.img_3_iv));
        }
        /**
         * 判断是否需要展示“广告”Logo标签
         */
        if (null != iNativeAdvanceData.getLogoFile()) {
            showImage(iNativeAdvanceData.getLogoFile().getUrl(), (ImageView) sIconGroupViewGroup.findViewById(R.id.logo_iv));
        }
        ((TextView) sIconGroupViewGroup.findViewById(R.id.title_tv)).setText(null != iNativeAdvanceData.getTitle() ? iNativeAdvanceData.getTitle() : "");
        ((TextView) sIconGroupViewGroup.findViewById(R.id.desc_tv)).setText(null != iNativeAdvanceData.getDesc() ? iNativeAdvanceData.getDesc() : "");
        /**
         * 处理“关闭”按钮交互行为
         */
        ((ImageView) sIconGroupViewGroup.findViewById(R.id.close_iv)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sIconGroupViewGroup.setVisibility(View.GONE);
                if (null != iCloseListener) iCloseListener.onClose();
            }
        });
        ((TextView) sIconGroupViewGroup.findViewById(R.id.click_bn)).setText(null != iNativeAdvanceData.getClickBnText() ? iNativeAdvanceData.getClickBnText() : "");
        return sIconGroupViewGroup;
    }

    private static ViewGroup createIcon320X210View(LayoutInflater layoutInflater, INativeAdvanceData iNativeAdvanceData, ViewGroup container, final ICloseListener iCloseListener) {
        if (null == sIcon320X210ViewGroup) {
            sIcon320X210ViewGroup = (ViewGroup) layoutInflater.inflate(R.layout.activity_native_advance_text_img_320_210_item, container, false);
        }
        Log.d(TAG, "MobListener=>createIcon320X210View " + sIcon320X210ViewGroup);
        /**
         *展示主图、大小为320X210。
         */
        if (null != iNativeAdvanceData.getImgFiles() && iNativeAdvanceData.getImgFiles().size() > 0) {
            INativeAdFile iNativeAdFile = (INativeAdFile) iNativeAdvanceData.getImgFiles().get(0);
            showImage(iNativeAdFile.getUrl(), (ImageView) sIcon320X210ViewGroup.findViewById(R.id.img_iv));
        }
        /**
         * 判断是否需要展示“广告”Logo标签
         */
        if (null != iNativeAdvanceData.getLogoFile()) {
            showImage(iNativeAdvanceData.getLogoFile().getUrl(), (ImageView) sIcon320X210ViewGroup.findViewById(R.id.logo_iv));
        }
        ((TextView) sIcon320X210ViewGroup.findViewById(R.id.title_tv)).setText(null != iNativeAdvanceData.getTitle() ? iNativeAdvanceData.getTitle() : "");
        ((TextView) sIcon320X210ViewGroup.findViewById(R.id.desc_tv)).setText(null != iNativeAdvanceData.getDesc() ? iNativeAdvanceData.getDesc() : "");
        /**
         * 处理“关闭”按钮交互行为
         */
        ((ImageView) sIcon320X210ViewGroup.findViewById(R.id.close_iv)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sIcon320X210ViewGroup.setVisibility(View.GONE);
                if (null != iCloseListener) iCloseListener.onClose();
            }
        });
        ((Button) sIcon320X210ViewGroup.findViewById(R.id.click_bn)).setText(null != iNativeAdvanceData.getClickBnText() ? iNativeAdvanceData.getClickBnText() : "");
        return sIcon320X210ViewGroup;
    }

    private static ViewGroup createIcon640X320View(LayoutInflater layoutInflater, INativeAdvanceData iNativeAdvanceData, ViewGroup container, final ICloseListener iCloseListener) {
        if (null == sIcon640X320ViewGroup) {
            sIcon640X320ViewGroup = (ViewGroup) layoutInflater.inflate(R.layout.activity_native_advance_text_img_640_320_item, container, false);
        }
        Log.d(TAG, "MobListener=>createIcon640X320View " + sIcon640X320ViewGroup);
        /**
         *展示主图、大小为640X320。
         */
        if (null != iNativeAdvanceData.getImgFiles() && iNativeAdvanceData.getImgFiles().size() > 0) {
            INativeAdFile iNativeAdFile = (INativeAdFile) iNativeAdvanceData.getImgFiles().get(0);
            showImage(iNativeAdFile.getUrl(), (ImageView) sIcon640X320ViewGroup.findViewById(R.id.img_iv));
        }
        /**
         * 判断是否需要展示“广告”Logo标签
         */
        if (null != iNativeAdvanceData.getLogoFile()) {
            showImage(iNativeAdvanceData.getLogoFile().getUrl(), (ImageView) sIcon640X320ViewGroup.findViewById(R.id.logo_iv));
        }
        ((TextView) sIcon640X320ViewGroup.findViewById(R.id.title_tv)).setText(null != iNativeAdvanceData.getTitle() ? iNativeAdvanceData.getTitle() : "");
        ((TextView) sIcon640X320ViewGroup.findViewById(R.id.desc_tv)).setText(null != iNativeAdvanceData.getDesc() ? iNativeAdvanceData.getDesc() : "");
        /**
         * 处理“关闭”按钮交互行为
         */
        ((ImageView) sIcon640X320ViewGroup.findViewById(R.id.close_iv)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sIcon640X320ViewGroup.setVisibility(View.GONE);
                if (null != iCloseListener) iCloseListener.onClose();
            }
        });
        ((Button) sIcon640X320ViewGroup.findViewById(R.id.click_bn)).setText(null != iNativeAdvanceData.getClickBnText() ? iNativeAdvanceData.getClickBnText() : "");
        return sIcon640X320ViewGroup;
    }

    private static ViewGroup createIconView(LayoutInflater layoutInflater, INativeAdvanceData iNativeAdvanceData, ViewGroup container) {
        if (null == sIconViewGroup) {
            sIconViewGroup = (ViewGroup) layoutInflater.inflate(R.layout.activity_native_advance_text_icon_512_512_item, container, false);
        }
        Log.d(TAG, "MobListener=>createIconView " + sIconViewGroup);
        /**
         *展示推广应用的ICON，大小为512X512。
         */
        if (null != iNativeAdvanceData.getIconFiles() && iNativeAdvanceData.getIconFiles().size() > 0) {
            INativeAdFile iNativeAdFile = (INativeAdFile) iNativeAdvanceData.getIconFiles().get(0);
            showImage(iNativeAdFile.getUrl(), (ImageView) sIconViewGroup.findViewById(R.id.icon_iv));
        }
        /**
         * 判断是否需要展示“广告”Logo标签
         */
        if (null != iNativeAdvanceData.getLogoFile()) {
            showImage(iNativeAdvanceData.getLogoFile().getUrl(), (ImageView) sIconViewGroup.findViewById(R.id.logo_iv));
        }
        ((TextView) sIconViewGroup.findViewById(R.id.title_tv)).setText(null != iNativeAdvanceData.getTitle() ? iNativeAdvanceData.getTitle() : "");
        ((TextView) sIconViewGroup.findViewById(R.id.desc_tv)).setText(null != iNativeAdvanceData.getDesc() ? iNativeAdvanceData.getDesc() : "");
        ((Button) sIconViewGroup.findViewById(R.id.click_bn)).setText(null != iNativeAdvanceData.getClickBnText() ? iNativeAdvanceData.getClickBnText() : "");
        return sIconViewGroup;
    }

    private static void showImage(String url, ImageView imageView) {
        ImageLoader.getInstance().displayImage(url, imageView);
    }

    public static void exit() {
        sIcon320X210ViewGroup = null;
        sIcon640X320ViewGroup = null;
        sIconGroupViewGroup = null;
        sIconViewGroup = null;
        sVideoViewGroup = null;
    }

    public static interface ICloseListener {
        void onClose();
    }
}

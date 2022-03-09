package com.opos.mobaddemo.contentad;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;



public class AdMultiImgHolder {
    public static final AdMultiImgHolder create(View convertView, View parentView) {
        AdMultiImgHolder adMultiImgHolder;
        if (null == convertView) {
            convertView = LayoutInflater.from(parentView.getContext()).inflate(R.layout.item_ad_multi_img, null);
            adMultiImgHolder = new AdMultiImgHolder(convertView);
            convertView.setTag(adMultiImgHolder);
        } else {
            adMultiImgHolder = (AdMultiImgHolder) convertView.getTag();
        }
        return adMultiImgHolder;
    }

    final View convertView;
    final TextView titleTxt;
    final TextView tagTxt;
    final LinearLayout picLayout;
    final TextView clickBtn;

    private AdMultiImgHolder(View convertView) {
        this.convertView = convertView;
        titleTxt = convertView.findViewById(R.id.item_ad_multi_img_title);
        tagTxt = convertView.findViewById(R.id.item_ad_multi_img_tag);
        picLayout = convertView.findViewById(R.id.item_ad_multi_img_pic_container);
        clickBtn = convertView.findViewById(R.id.item_ad_multi_img_btn);
    }
}

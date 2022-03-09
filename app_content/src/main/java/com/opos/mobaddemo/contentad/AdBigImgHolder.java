package com.opos.mobaddemo.contentad;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class AdBigImgHolder {
    public static final AdBigImgHolder create(View convertView, View parentView) {
        AdBigImgHolder adBigImgHolder = null;
        if (null == convertView) {
            convertView = LayoutInflater.from(parentView.getContext()).inflate(R.layout.item_ad_big_img, null);
            adBigImgHolder = new AdBigImgHolder(convertView);
            convertView.setTag(adBigImgHolder);
        } else {
            adBigImgHolder = (AdBigImgHolder) convertView.getTag();
        }
        return adBigImgHolder;
    }

    final View convertView;
    final TextView titleTxt;
    final TextView tagTxt;
    final ImageView picImg;
    final TextView clickBtn;

    private AdBigImgHolder(View convertView) {
        this.convertView = convertView;
        titleTxt = convertView.findViewById(R.id.item_ad_big_img_title);
        tagTxt = convertView.findViewById(R.id.item_ad_big_img_tag);
        picImg = convertView.findViewById(R.id.item_ad_big_img_pic);
        clickBtn = convertView.findViewById(R.id.item_ad_big_img_btn);
    }
}

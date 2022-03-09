package com.opos.mobaddemo.contentad;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;



public class AdSmallImgHolder {
    public static final AdSmallImgHolder create(View convertView, View parentView) {
        AdSmallImgHolder adSmallImgHolder = null;
        if (null == convertView) {
            convertView = LayoutInflater.from(parentView.getContext()).inflate(R.layout.item_ad_small_img, null);
            adSmallImgHolder = new AdSmallImgHolder(convertView);
            convertView.setTag(adSmallImgHolder);
        } else {
            adSmallImgHolder = (AdSmallImgHolder) convertView.getTag();
        }
        return adSmallImgHolder;
    }

    final View convertView;
    final TextView titleTxt;
    final TextView tagTxt;
    final ImageView picImg;
    final TextView clickBtn;

    private AdSmallImgHolder(View convertView) {
        this.convertView = convertView;
        titleTxt = convertView.findViewById(R.id.item_ad_small_img_title);
        tagTxt = convertView.findViewById(R.id.item_ad_small_img_tag);
        picImg = convertView.findViewById(R.id.item_ad_small_img_pic);
        clickBtn = convertView.findViewById(R.id.item_ad_small_img_btn);
    }
}

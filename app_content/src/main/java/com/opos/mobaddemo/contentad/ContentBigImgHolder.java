package com.opos.mobaddemo.contentad;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;



public class ContentBigImgHolder {
    public static final ContentBigImgHolder create(View convertView, View parentView) {
        ContentBigImgHolder contentBigImgHolder = null;
        if (null == convertView) {
            convertView = LayoutInflater.from(parentView.getContext()).inflate(R.layout.item_content_big_img, null);
            contentBigImgHolder = new ContentBigImgHolder(convertView);
            convertView.setTag(contentBigImgHolder);
        } else {
            contentBigImgHolder = (ContentBigImgHolder) convertView.getTag();
        }
        return contentBigImgHolder;
    }

    final View convertView;
    final TextView titleTxt;
    final ImageView picImg;
    final TextView tagTxt;

    private ContentBigImgHolder(View convertView) {
        this.convertView = convertView;
        titleTxt = convertView.findViewById(R.id.item_content_big_img_title);
        picImg = convertView.findViewById(R.id.item_content_big_img_pic);
        tagTxt = convertView.findViewById(R.id.item_content_big_img_tag);
    }
}

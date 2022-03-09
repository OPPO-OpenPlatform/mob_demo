package com.opos.mobaddemo.contentad;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class ContentTxtImgHolder {
    public static final ContentTxtImgHolder create(View convertView, View parentView) {
        ContentTxtImgHolder contentTxtImgHolder = null;
        if (null == convertView) {
            convertView = LayoutInflater.from(parentView.getContext()).inflate(R.layout.item_content_txt_img, null);
            contentTxtImgHolder = new ContentTxtImgHolder(convertView);
            convertView.setTag(contentTxtImgHolder);
        } else {
            contentTxtImgHolder = (ContentTxtImgHolder) convertView.getTag();
        }
        return contentTxtImgHolder;
    }

    final View convertView;
    final TextView titleTxt;
    final ImageView picImg;

    final TextView tagTxt;

    private ContentTxtImgHolder(View convertView) {
        this.convertView = convertView;
        titleTxt = convertView.findViewById(R.id.item_content_txt_img_title);
        picImg = convertView.findViewById(R.id.item_content_txt_img_pic);

        tagTxt = convertView.findViewById(R.id.item_content_txt_img_tag);
    }
}

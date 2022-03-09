package com.opos.mobaddemo.contentad;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


public class ContentMultiImgHolder {
    public static final ContentMultiImgHolder create(View convertView, View parentView) {
        ContentMultiImgHolder contentMultiImgHolder = null;
        if (null == convertView) {
            convertView = LayoutInflater.from(parentView.getContext()).inflate(R.layout.item_content_multi_img, null);
            contentMultiImgHolder = new ContentMultiImgHolder(convertView);
            convertView.setTag(contentMultiImgHolder);
        } else {
            contentMultiImgHolder = (ContentMultiImgHolder) convertView.getTag();
        }
        return contentMultiImgHolder;
    }

    final View convertView;
    final TextView titleTxt;
    final LinearLayout containerLayout;

    final TextView tagTxt;

    private ContentMultiImgHolder(View convertView) {
        this.convertView = convertView;
        titleTxt = convertView.findViewById(R.id.item_content_multi_img_title);
        containerLayout = convertView.findViewById(R.id.item_content_multi_img_pic_container);

        tagTxt = convertView.findViewById(R.id.item_content_multi_img_tag);
    }
}

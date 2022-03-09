package com.opos.mobaddemo.contentad;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;



class ContentTxtHolder {

    public static final ContentTxtHolder create(View convertView, View parentView) {
        ContentTxtHolder contentTxtHolder = null;
        if (null == convertView) {
            convertView = LayoutInflater.from(parentView.getContext()).inflate(R.layout.item_content_txt, null);
            contentTxtHolder = new ContentTxtHolder(convertView);
            convertView.setTag(contentTxtHolder);
        } else {
            contentTxtHolder = (ContentTxtHolder) convertView.getTag();
        }
        return contentTxtHolder;
    }

    final View convertView;
    final TextView titleTxt;
    final TextView contentTxt;
    final TextView tagTxt;

    private ContentTxtHolder(View convertView) {
        this.convertView = convertView;
        titleTxt = convertView.findViewById(R.id.item_content_txt_title);
        contentTxt = convertView.findViewById(R.id.item_content_txt_content);
        tagTxt = convertView.findViewById(R.id.item_content_txt_tag);
    }

}

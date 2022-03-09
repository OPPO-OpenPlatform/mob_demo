package com.opos.mobaddemo.contentad;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.heytap.msp.mobad.api.params.MediaView;


public class AdVideoHolder {
    public static final AdVideoHolder create(View convertView, View parentView) {
        AdVideoHolder adVideoHolder;
        if (null == convertView) {
            convertView = LayoutInflater.from(parentView.getContext()).inflate(R.layout.item_ad_video, null);
            adVideoHolder = new AdVideoHolder(convertView);
            convertView.setTag(adVideoHolder);
        } else {
            adVideoHolder = (AdVideoHolder) convertView.getTag();
        }
        return adVideoHolder;
    }

    final View convertView;
    final TextView titleTxt;
    final TextView tagTxt;
    final MediaView mediaView;
    final TextView clickBtn;

    private AdVideoHolder(View convertView) {
        this.convertView = convertView;
        titleTxt = convertView.findViewById(R.id.item_ad_video_title);
        tagTxt = convertView.findViewById(R.id.item_ad_video_tag);
        mediaView = convertView.findViewById(R.id.item_ad_video_mediaView);
        clickBtn = convertView.findViewById(R.id.item_ad_video_btn);
    }
}

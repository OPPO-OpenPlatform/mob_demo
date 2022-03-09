package com.opos.mobaddemo.contentad;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.heytap.msp.mobad.api.params.MediaView;

public class ContentVideoHolder {

    public static final ContentVideoHolder create(View convertView, View parentView) {
        ContentVideoHolder contentVideoHolder;
        if (null == convertView) {
            convertView = LayoutInflater.from(parentView.getContext()).inflate(R.layout.item_content_video, null);
            contentVideoHolder = new ContentVideoHolder(convertView);
            convertView.setTag(contentVideoHolder);
        } else {
            contentVideoHolder = (ContentVideoHolder) convertView.getTag();
        }
        return contentVideoHolder;
    }

    final View convertView;
    final TextView titleTxt;
    final MediaView mediaView;
    final View controlView;
    final View playView;
    final TextView durationTxt;
    final ImageView hoverImg;

    final TextView tagTxt;

    private ContentVideoHolder(View convertView) {
        this.convertView = convertView;
        titleTxt = convertView.findViewById(R.id.item_content_video_title);
        mediaView = convertView.findViewById(R.id.item_content_video_mediaView);
        controlView = convertView.findViewById(R.id.item_content_video_control);
        playView = convertView.findViewById(R.id.item_content_video_play);
        hoverImg = convertView.findViewById(R.id.item_content_video_hover);

        tagTxt = convertView.findViewById(R.id.item_content_video_tag);
        durationTxt = convertView.findViewById(R.id.item_content_video_duration);
    }
}

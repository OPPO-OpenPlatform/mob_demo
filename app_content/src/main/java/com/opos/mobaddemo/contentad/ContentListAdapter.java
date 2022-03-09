package com.opos.mobaddemo.contentad;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.heytap.msp.mobad.api.listener.IContentInteractListener;
import com.heytap.msp.mobad.api.listener.IContentMediaListener;
import com.heytap.msp.mobad.api.params.ContentContainer;
import com.heytap.msp.mobad.api.params.IContentAdData;
import com.heytap.msp.mobad.api.params.IContentBaseData;
import com.heytap.msp.mobad.api.params.IContentData;

import java.util.ArrayList;
import java.util.List;

public class ContentListAdapter extends BaseAdapter {

    private static final int TYPE_UNKNOWN = 0;
    private static final int TYPE_CONTENT_TXT = TYPE_UNKNOWN + 1;
    private static final int TYPE_CONTENT_BIG_IMG = TYPE_CONTENT_TXT + 1;
    private static final int TYPE_CONTENT_TXT_IMG = TYPE_CONTENT_BIG_IMG + 1;
    private static final int TYPE_CONTENT_MULTI_IMG = TYPE_CONTENT_TXT_IMG + 1;
    private static final int TYPE_CONTENT_VIDEO = TYPE_CONTENT_MULTI_IMG + 1;

    private static final int TYPE_AD_SMALL_IMG = TYPE_CONTENT_VIDEO + 1;
    private static final int TYPE_AD_BIG_IMG = TYPE_AD_SMALL_IMG + 1;
    private static final int TYPE_AD_MULTI_IMG = TYPE_AD_BIG_IMG + 1;
    private static final int TYPE_AD_VIDEO = TYPE_AD_MULTI_IMG + 1;

    private static final int TYPE_NUM = TYPE_AD_VIDEO + 1;

    private boolean isPlayContentVideo = true;
    private List<IContentBaseData> mDataList;

    private int mVideoPlayPosition = -1;

    public ContentListAdapter(List<IContentBaseData> dataList) {
        mDataList = dataList;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public IContentBaseData getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public int getItemViewType(int position) {
        IContentBaseData data = getItem(position);
        if (data instanceof IContentAdData) {
            IContentAdData adData = (IContentAdData) data;
            switch (adData.getCreativeType()) {
                case IContentAdData.TYPE_MULTI_IMG:
                    return TYPE_AD_MULTI_IMG;
                case IContentAdData.TYPE_BIG_IMG:
                    return TYPE_AD_BIG_IMG;
                case IContentAdData.TYPE_SMALL_IMG:
                    return TYPE_AD_SMALL_IMG;
                case IContentAdData.TYPE_VIDEO:
                    return TYPE_AD_VIDEO;
                default:
                    return TYPE_AD_SMALL_IMG;
            }
        } else if (data instanceof IContentData) {
            IContentData contentData = (IContentData) data;
            switch (contentData.getCreativeType()) {
                case IContentData.TYPE_TEXT:
                    return TYPE_CONTENT_TXT;
                case IContentData.TYPE_TEXT_BIG_IMG:
                    return TYPE_CONTENT_BIG_IMG;
                case IContentData.TYPE_TEXT_IMG:
                    return TYPE_CONTENT_TXT_IMG;
                case IContentData.TYPE_TEXT_MULTI_IMG:
                    return TYPE_CONTENT_MULTI_IMG;
                case IContentData.TYPE_VIDEO:
                    return TYPE_CONTENT_VIDEO;
                default:
                    return TYPE_CONTENT_TXT;

            }
        }
        return TYPE_UNKNOWN;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_NUM;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO
        IContentBaseData data = getItem(position);
        int type = getItemViewType(position);
        switch (type) {
            case TYPE_CONTENT_TXT:
                return getContentTxtView((IContentData) data, convertView, parent);
            case TYPE_CONTENT_BIG_IMG:
                return getContentBigImgView((IContentData) data, convertView, parent);
            case TYPE_CONTENT_TXT_IMG:
                return getContentTxtImgView((IContentData) data, convertView, parent);
            case TYPE_CONTENT_MULTI_IMG:
                return getContentMultiImgView((IContentData) data, convertView, parent);
            case TYPE_CONTENT_VIDEO:
                return getContentVideoView(position, (IContentData) data, convertView, parent);
            case TYPE_AD_SMALL_IMG:
                return getAdSmallImgView((IContentAdData) data, convertView, parent);
            case TYPE_AD_BIG_IMG:
                return getAdBigImgView((IContentAdData) data, convertView, parent);
            case TYPE_AD_MULTI_IMG:
                return getAdMultiImgView((IContentAdData) data, convertView, parent);
            case TYPE_AD_VIDEO:
                return getAdVideoView((IContentAdData) data, convertView, parent);
        }
        return getUnKnownView(position, convertView, parent);
    }

    private View getUnKnownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new TextView(parent.getContext());
        }
        ((TextView) convertView).setText("unknown data int position:" + position);
        return convertView;
    }

    private View getContentTxtView(IContentData contentData, View convertView, ViewGroup parent) {
        ContentTxtHolder contentTxtHolder = ContentTxtHolder.create(convertView, parent);
        convertView = contentTxtHolder.convertView;

        showTag(contentTxtHolder.tagTxt, contentData.getTagList(), contentData.getPublishTime());
        contentTxtHolder.titleTxt.setText(contentData.getTitle());
        contentTxtHolder.contentTxt.setText(contentData.getDesc());
        List<View> clickView = new ArrayList<>();
        clickView.add(convertView);
        contentData.bindToView(parent.getContext(), (ContentContainer) convertView, clickView);
        return convertView;
    }

    private View getContentBigImgView(IContentData contentData, View convertView, ViewGroup parent) {
        ContentBigImgHolder contentBigImgHolder = ContentBigImgHolder.create(convertView, parent);
        convertView = contentBigImgHolder.convertView;
        showTag(contentBigImgHolder.tagTxt, contentData.getTagList(), contentData.getPublishTime());

        contentBigImgHolder.titleTxt.setText(contentData.getTitle());
        if (null != contentData.getImgUrlList() && contentData.getImgUrlList().size() > 0) {
            showImage(contentData.getImgUrlList().get(0), contentBigImgHolder.picImg);
        }
        List<View> clickView = new ArrayList<>();
        clickView.add(convertView);
        contentData.bindToView(parent.getContext(), (ContentContainer) convertView, clickView);

        return convertView;
    }

    private View getContentTxtImgView(IContentData contentData, View convertView, ViewGroup parent) {
        ContentTxtImgHolder contentTxtImgHolder = ContentTxtImgHolder.create(convertView, parent);
        convertView = contentTxtImgHolder.convertView;
        showTag(contentTxtImgHolder.tagTxt, contentData.getTagList(), contentData.getPublishTime());

        contentTxtImgHolder.titleTxt.setText(contentData.getTitle());
        if (null != contentData.getImgUrlList() && contentData.getImgUrlList().size() > 0) {
            showImage(contentData.getImgUrlList().get(0), contentTxtImgHolder.picImg);
        }
        List<View> clickView = new ArrayList<>();
        clickView.add(convertView);
        contentData.bindToView(parent.getContext(), (ContentContainer) convertView, clickView);
        return convertView;
    }

    private View getContentMultiImgView(IContentData contentData, View convertView, ViewGroup parent) {
        ContentMultiImgHolder contentMultiImgHolder = ContentMultiImgHolder.create(convertView, parent);
        convertView = contentMultiImgHolder.convertView;
        showTag(contentMultiImgHolder.tagTxt, contentData.getTagList(), contentData.getPublishTime());

        // show multi img
        if (null == contentData.getImgUrlList() || contentData.getImgUrlList().size() <= 0) {
            contentMultiImgHolder.containerLayout.setVisibility(View.GONE);
        } else {
            contentMultiImgHolder.containerLayout.setVisibility(View.VISIBLE);
            List<ImageView> imgList = new ArrayList<>();
            imgList.add((ImageView) convertView.findViewById(R.id.item_content_multi_img_pic_1));
            imgList.add((ImageView) convertView.findViewById(R.id.item_content_multi_img_pic_2));
            imgList.add((ImageView) convertView.findViewById(R.id.item_content_multi_img_pic_3));
            int length = Math.min(contentData.getImgUrlList().size(), imgList.size());
            for (int i = 0; i < length; i++) {
                imgList.get(i).setVisibility(View.VISIBLE);
                showImage(contentData.getImgUrlList().get(i), imgList.get(i));
            }
            for (int i = 0; i < imgList.size() - length; i++) {
                imgList.get(length + i).setVisibility(View.GONE);
            }
        }
        contentMultiImgHolder.titleTxt.setText(contentData.getTitle());
        List<View> clickView = new ArrayList<>();
        clickView.add(convertView);
        contentData.bindToView(parent.getContext(), (ContentContainer) convertView, clickView);
        return convertView;
    }

    private View getContentVideoView(final int position, final IContentData contentData, View convertView, final ViewGroup parent) {
        final ContentVideoHolder contentVideoHolder = ContentVideoHolder.create(convertView, parent);
        convertView = contentVideoHolder.convertView;
        showTag(contentVideoHolder.tagTxt, contentData.getTagList(), contentData.getPublishTime());
        setDuration(contentVideoHolder.durationTxt, contentData.getVideoDuration());

        contentVideoHolder.titleTxt.setText(contentData.getTitle());
        List<View> clickView = new ArrayList<>();
        clickView.add(convertView);
        contentData.bindToView(parent.getContext(), (ContentContainer) convertView, clickView);
        if (isPlayContentVideo) {
            contentData.bindMediaVIew(contentVideoHolder.mediaView, new IContentMediaListener() {
                @Override
                public void onVideoPlayStart() {
                    Log.d("contentlist", "content video play start");
                    mVideoPlayPosition = position;
                    contentVideoHolder.playView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onVideoPlayStop() {
                    Log.d("contentlist", "content video play stop");
                    mVideoPlayPosition = -1;
                    contentVideoHolder.playView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onVideoPlayComplete() {
                    Log.d("contentlist", "content video play complete");
                    mVideoPlayPosition = -1;
                    contentVideoHolder.playView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onVideoPlayError(String msg) {
                    Log.d("contentlist", "content video play error:" + msg);
                    mVideoPlayPosition = -1;
                    contentVideoHolder.playView.setVisibility(View.INVISIBLE);
                }
            });
            contentVideoHolder.controlView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position != mVideoPlayPosition) {
                        contentData.resume();
                    } else {
                        contentData.pause();
                    }

                }
            });
            if (null != contentData.getImgUrlList() && contentData.getImgUrlList().size() > 0) {
                showImage(contentData.getImgUrlList().get(0), contentVideoHolder.hoverImg);
            }
        }
        if (mVideoPlayPosition == position) {
            contentVideoHolder.playView.setVisibility(View.INVISIBLE);
        } else {
            contentVideoHolder.playView.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private void setDuration(TextView durationTxt, int duration) {
        StringBuilder stringBuilder = new StringBuilder();
        duration = duration / 1000;
        int m = duration / 60;
        int s = duration % 60;
        stringBuilder.append(String.format("%02d", m));
        stringBuilder.append(":");
        stringBuilder.append(String.format("%02d", s));
        durationTxt.setText(stringBuilder);

    }

    private View getAdSmallImgView(IContentAdData adData, View convertView, ViewGroup parent) {
        AdSmallImgHolder adSmallImgHolder = AdSmallImgHolder.create(convertView, parent);
        convertView = adSmallImgHolder.convertView;
        adSmallImgHolder.titleTxt.setText(adData.getDesc());
        showAdTag(adSmallImgHolder.tagTxt, adData);
        adSmallImgHolder.clickBtn.setText(adData.getClickBnText());
        if (null != adData.getImgFiles() && adData.getImgFiles().size() > 0) {
            showImage(adData.getImgFiles().get(0).getUrl(), adSmallImgHolder.picImg);
        }
        adData.setInteractListener(new AdInteractionListener(adData));
        List<View> clickView = new ArrayList<>();
        clickView.add(convertView);
        adData.bindToView(parent.getContext(), (ContentContainer) convertView, clickView);
        return convertView;
    }

    private View getAdBigImgView(IContentAdData adData, View convertView, ViewGroup parent) {
        AdBigImgHolder adBigImgHolder = AdBigImgHolder.create(convertView, parent);
        convertView = adBigImgHolder.convertView;
        adBigImgHolder.titleTxt.setText(adData.getDesc());
        adBigImgHolder.clickBtn.setText(adData.getClickBnText());
        if (null != adData.getImgFiles() && adData.getImgFiles().size() > 0) {
            showImage(adData.getImgFiles().get(0).getUrl(), adBigImgHolder.picImg);
        }

        showAdTag(adBigImgHolder.tagTxt, adData);
        adData.setInteractListener(new AdInteractionListener(adData));
        List<View> clickView = new ArrayList<>();
        clickView.add(convertView);
        adData.bindToView(parent.getContext(), (ContentContainer) convertView, clickView);
        return convertView;
    }

    private View getAdMultiImgView(IContentAdData adData, View convertView, ViewGroup parent) {
        AdMultiImgHolder adMultiImgHolder = AdMultiImgHolder.create(convertView, parent);
        convertView = adMultiImgHolder.convertView;
        // show multi img
        if (null == adData.getImgFiles() || adData.getImgFiles().size() <= 0) {
            adMultiImgHolder.picLayout.setVisibility(View.GONE);
        } else {
            adMultiImgHolder.picLayout.setVisibility(View.VISIBLE);
            List<ImageView> imgList = new ArrayList<>();
            imgList.add((ImageView) convertView.findViewById(R.id.item_ad_multi_img_pic_1));
            imgList.add((ImageView) convertView.findViewById(R.id.item_ad_multi_img_pic_2));
            imgList.add((ImageView) convertView.findViewById(R.id.item_ad_multi_img_pic_3));
            int length = Math.min(adData.getImgFiles().size(), imgList.size());
            for (int i = 0; i < length; i++) {
                imgList.get(i).setVisibility(View.VISIBLE);
                showImage(adData.getImgFiles().get(i).getUrl(), imgList.get(i));
            }
            for (int i = 0; i < imgList.size() - length; i++) {
                imgList.get(length + i).setVisibility(View.GONE);
            }
        }
        adData.setInteractListener(new AdInteractionListener(adData));
        adMultiImgHolder.clickBtn.setText(adData.getClickBnText());
        adMultiImgHolder.titleTxt.setText(adData.getTitle());
        showAdTag(adMultiImgHolder.tagTxt, adData);

        List<View> clickView = new ArrayList<>();
        clickView.add(convertView);
        adData.bindToView(parent.getContext(), (ContentContainer) convertView, clickView);
        return convertView;
    }

    private void showAdTag(TextView tagTxt, IContentAdData adData) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("广告");
        if (!TextUtils.isEmpty(adData.getTitle())) {
            stringBuilder.append(" ");
            stringBuilder.append(adData.getTitle());
        }
        tagTxt.setText(stringBuilder);
    }

    private View getAdVideoView(IContentAdData adData, View convertView, ViewGroup parent) {
        AdVideoHolder adVideoHolder = AdVideoHolder.create(convertView, parent);
        convertView = adVideoHolder.convertView;
        adVideoHolder.clickBtn.setText(adData.getClickBnText());
        adVideoHolder.titleTxt.setText(adData.getDesc());
        showAdTag(adVideoHolder.tagTxt, adData);

        adData.setInteractListener(new AdInteractionListener(adData));
        List<View> clickView = new ArrayList<>();
        clickView.add(convertView);
        adData.bindToView(parent.getContext(), (ContentContainer) convertView, clickView);
        adData.bindMediaView(adVideoHolder.mediaView, new IContentMediaListener() {
            @Override
            public void onVideoPlayStart() {
                logDebug("ad video play start");
            }

            @Override
            public void onVideoPlayStop() {
                logDebug("ad video play stop");
            }

            @Override
            public void onVideoPlayComplete() {
                logDebug("ad video play complet");
            }

            @Override
            public void onVideoPlayError(String msg) {
                logDebug("ad video play error");
            }
        });
        return convertView;
    }

    private static final int MAX_TAG_AMOUNT = 3;

    private void showTag(TextView textView, List<String> tagList, long publishTime) {
        if (null == tagList || tagList.size() <= 0) {
            textView.setVisibility(View.GONE);
            return;
        }
        textView.setVisibility(View.VISIBLE);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < tagList.size() && i < MAX_TAG_AMOUNT; i++) {
            if (i != 0) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(tagList.get(i));
        }

        stringBuilder.append("    ");
        long timeInternal = System.currentTimeMillis() - publishTime;
        if (timeInternal <= 0 || timeInternal >= 24 * 60 * 60 * 1000) {
            stringBuilder.append(DateFormat.format("yyyy:MM:dd", publishTime));
        } else if (timeInternal <= 60 * 1000) {
            stringBuilder.append(timeInternal / 1000 + "秒前");
        } else if (timeInternal <= 60 * 60 * 1000) {
            stringBuilder.append(timeInternal / (60 * 1000) + "分钟前");
        } else {
            stringBuilder.append(timeInternal / (60 * 60 * 1000) + "小时前");
        }

        textView.setText(stringBuilder);
    }

    private void showImage(String url, final ImageView imageView) {
        if (null != imageView.getTag() && imageView.getTag().equals(url)) {
            return;
        }
        imageView.setImageDrawable(null);
        ImageLoader.getInstance().displayImage(url, imageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                imageView.setTag(null);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                if (null != imageView.getTag() && imageView.getTag().equals(s)) {
                    imageView.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
        imageView.setTag(url);
    }

    private static final void logDebug(String msg) {
        Log.d(ContentAdActivity.TAG, msg);
    }

    private static class AdInteractionListener implements IContentInteractListener {
        private IContentAdData mAdData;

        public AdInteractionListener(IContentAdData adData) {
            mAdData = adData;
        }

        @Override
        public void onClick() {
            logDebug("onClick :" + mAdData);
        }

        @Override
        public void onShow() {
            logDebug("onShow :" + mAdData);
        }

        @Override
        public void onError(int code, String msg) {
            logDebug("onError :" + "code:" + code + ",msg:" + msg + "," + mAdData);
        }
    }
}

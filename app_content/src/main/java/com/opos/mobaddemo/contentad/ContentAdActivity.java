package com.opos.mobaddemo.contentad;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.heytap.msp.mobad.api.ad.ContentAd;
import com.heytap.msp.mobad.api.listener.IContentLoadListener;
import com.heytap.msp.mobad.api.params.IContentBaseData;
import com.heytap.msp.mobad.api.params.IContentData;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.opos.mobaddemo.posid.Constants;

import java.util.ArrayList;
import java.util.List;

public class ContentAdActivity extends Activity {
    public static final String TAG = "ContentAdTag";
    private CustomListView mListView;
    private List<IContentBaseData> mDataList;
    private BaseAdapter mAdapter;
    private ContentAd mContentAd;
    private boolean mIsLoadingMore = false;
    private boolean isResetContentAd = false;
    //

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contentad);
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
        mListView = findViewById(R.id.contentad_list);
        mDataList = new ArrayList<>();
        mAdapter = new ContentListAdapter(mDataList);
        mListView.setAdapter(mAdapter);
        mListView.setLoadListener(new CustomListView.ILoadStatusListener() {
            @Override
            public void onTopRefresh() {
                resetContentAd();
            }

            @Override
            public void onBottomLoadMore() {
                if (mIsLoadingMore) {
                    return;
                }
                if (null != mContentAd) {
                    mIsLoadingMore = true;
                    mContentAd.loadAd();
                }
            }
        });
        resetContentAd();
    }

    private void resetContentAd() {
        mContentAd = new ContentAd(this, Constants.CONTENT_AD_POS_ID, "12");
        mContentAd.setLoadListener(new IContentLoadListener() {
            @Override
            public void onAdSuccess(List<IContentBaseData> resultList) {
                mListView.completeRefresh();
                //
                logDebug("load succ size:" + (null != resultList ? resultList.size() : 0));
                if (null == resultList || resultList.size() <= 0) {
                    // end of list
                    Toast.makeText(ContentAdActivity.this.getApplicationContext(), "已经加载到最后一页，请重新刷新", Toast.LENGTH_SHORT).show();
                    return;
                }
                clearDataList();
                mDataList.addAll(resultList);
                mAdapter.notifyDataSetChanged();
                //
                mIsLoadingMore = false;
            }

            @Override
            public void onAdFailed(int code, String msg) {
                mListView.completeRefresh();
                //
                logDebug("fail with code:" + code + ",msg:" + msg);
                //
                isResetContentAd = false;
                mIsLoadingMore = false;
                Toast.makeText(ContentAdActivity.this.getApplicationContext(), "fail with code:" + code + ",msg:" + msg, Toast.LENGTH_SHORT).show();
            }
        });
        isResetContentAd = true;
        mContentAd.loadAd();
    }

    private void clearDataList() {
        if (isResetContentAd) {//解决由于动画效果触发清楚dataList立即刷新的问题
            mDataList.clear();
            isResetContentAd = false;
        }
    }

    private void logDebug(String msg) {
        Log.d(TAG, msg);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        //释放资源
        if (null != mDataList && mDataList.size() > 0) {
            for (IContentBaseData baseData : mDataList) {
                if (baseData instanceof IContentData) {
                    ((IContentData) baseData).release();
                }
            }
        }
        super.onDestroy();
    }
}

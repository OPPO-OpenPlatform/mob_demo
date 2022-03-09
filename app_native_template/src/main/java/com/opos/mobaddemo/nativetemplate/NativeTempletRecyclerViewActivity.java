package com.opos.mobaddemo.nativetemplate;

import android.app.Activity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.heytap.msp.mobad.api.ad.NativeTempletAd;
import com.heytap.msp.mobad.api.listener.INativeTempletAdListener;
import com.heytap.msp.mobad.api.params.INativeTempletAdView;
import com.heytap.msp.mobad.api.params.NativeAdError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class NativeTempletRecyclerViewActivity extends Activity implements INativeTempletAdListener {

    private static final String TAG = "NativeTempletRecyclerViewActivity";
    //
    public static final String EXTRA_KEY_POS_ID = "posId";
    /**
     * 列表Item个数
     */
    private static final int MAX_ITEMS = 30;
    /**
     * 第一个广告展示的位置
     */
    private static final int FIRST_AD_POSITION = 3;
    /**
     * 每隔10个展示一个广告
     */
    private static final int ITEMS_PER_AD = 10;
    //
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private CustomAdapter mAdapter;
    private List<NormalItem> mNormalDataList = new ArrayList<NormalItem>();
    /**
     * 原生模板广告对象
     */
    private NativeTempletAd mNativeTempletAd;
    /**
     * 请求原生广告成功后返回的原生广告视图对象列表
     */
    private List<INativeTempletAdView> mINativeTempletAdViewList;
    /**
     * 记录当前广告在列表中展示的位置
     */
    private HashMap<INativeTempletAdView, Integer> mAdViewPositionMap = new HashMap<INativeTempletAdView, Integer>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        setContentView(R.layout.activity_native_templet_recycler_view);
        //
        initView();
        //
        initData();
    }

    @Override
    protected void onDestroy() {
        destroyAd();
        super.onDestroy();
    }


    private void destroyAd() {
        try {
            /**
             * 每一个INativeTempletAdView对象使用完以后都要释放资源
             */
            if (null != mINativeTempletAdViewList && mINativeTempletAdViewList.size() > 0) {
                for (INativeTempletAdView iNativeTempletAdView : mINativeTempletAdViewList) {
                    iNativeTempletAdView.destroy();
                }
            }
            /**
             * mNativeTempletAd调用destroyAd方法释放相关资源
             */
            if (null != mNativeTempletAd) {
                mNativeTempletAd.destroyAd();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.ad_rv);
        mRecyclerView.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
    }

    private void initData() {
        for (int i = 0; i < MAX_ITEMS; ++i) {
            mNormalDataList.add(new NormalItem("No." + i + " Normal Data"));
        }
        mAdapter = new CustomAdapter(mNormalDataList);
        mRecyclerView.setAdapter(mAdapter);
        //
        initNativeTempletAd();
    }

    private void initNativeTempletAd() {
        String posId = getIntent().getStringExtra(EXTRA_KEY_POS_ID);
        /**
         * 通过构造NativeAdSize对象，在NativeTempletAd初始化时传入、可以指定原生模板广告的大小，单位为dp
         * 也可以传入null，展示默认的大小
         */
        mNativeTempletAd = new NativeTempletAd(this, posId, null, this);
        /**
         * 调用loadAd方法请求原生模板广告
         */
        mNativeTempletAd.loadAd();
    }

    @Override
    public void onAdSuccess(List<INativeTempletAdView> iNativeTempletAdViewList) {
        Log.d(TAG, "onAdSuccess size=" + (null != iNativeTempletAdViewList ? iNativeTempletAdViewList.size() : "null"));
        if (null != iNativeTempletAdViewList && iNativeTempletAdViewList.size() > 0) {
            mINativeTempletAdViewList = iNativeTempletAdViewList;
            for (int i = 0; i < mINativeTempletAdViewList.size(); i++) {
                int position = FIRST_AD_POSITION + ITEMS_PER_AD * i;
                if (position < mNormalDataList.size()) {
                    mAdViewPositionMap.put(mINativeTempletAdViewList.get(i), position); // 把每个广告在列表中位置记录下来
                    mAdapter.addAdViewToPosition(position, mINativeTempletAdViewList.get(i));
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAdFailed(NativeAdError nativeAdError) {
        Log.d(TAG, "onAdFailed nativeAdError=" + (null != nativeAdError ? nativeAdError.toString() : "null"));
        Toast.makeText(NativeTempletRecyclerViewActivity.this, "load native templet ad error,error msg：" + (null != nativeAdError ? nativeAdError.toString() : "null"), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAdClick(INativeTempletAdView iNativeTempletAdView) {
        Log.d(TAG, "onAdClick iNativeTempletAdView=" + (null != iNativeTempletAdView ? iNativeTempletAdView : "null"));
    }

    @Override
    public void onAdShow(INativeTempletAdView iNativeTempletAdView) {
        Log.d(TAG, "onAdShow iNativeTempletAdView=" + (null != iNativeTempletAdView ? iNativeTempletAdView : "null"));
    }

    @Override
    public void onAdClose(INativeTempletAdView iNativeTempletAdView) {
        Log.d(TAG, "onAdClose iNativeTempletAdView=" + (null != iNativeTempletAdView ? iNativeTempletAdView : "null"));
        if (mAdapter != null) {
            int removedPosition = mAdViewPositionMap.get(iNativeTempletAdView);
            mAdapter.removeAdView(removedPosition);
        }
    }

    @Override
    public void onRenderSuccess(INativeTempletAdView iNativeTempletAdView) {
        Log.d(TAG, "onRenderSuccess iNativeTempletAdView=" + (null != iNativeTempletAdView ? iNativeTempletAdView : "null"));
    }

    @Override
    public void onRenderFailed(NativeAdError nativeAdError, INativeTempletAdView iNativeTempletAdView) {
        Log.d(TAG, "onRenderFailed nativeAdError=" + (null != nativeAdError ? nativeAdError.toString() : "null") + ",iNativeTempletAdView=" + (null != iNativeTempletAdView ? iNativeTempletAdView : "null"));
        Toast.makeText(NativeTempletRecyclerViewActivity.this, "render native templet ad error,error msg：" + (null != nativeAdError ? nativeAdError.toString() : "null"), Toast.LENGTH_LONG).show();
    }

    public class NormalItem {
        private String title;

        public NormalItem(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }


    public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

        static final int TYPE_DATA = 0;
        static final int TYPE_AD = 1;
        private List<Object> mData;

        public CustomAdapter(List list) {
            mData = list;
        }

        /**
         * 把返回的INativeTempletAdView添加到数据集里面去
         *
         * @param position
         * @param iNativeTempletAdView
         */
        public void addAdViewToPosition(int position, INativeTempletAdView iNativeTempletAdView) {
            if (position >= 0 && position < mData.size() && iNativeTempletAdView != null) {
                mData.add(position, iNativeTempletAdView);
            }
        }


        /**
         * 移除INativeTempletAdView的时候是一条一条移除的
         *
         * @param position
         */
        public void removeAdView(int position) {
            mData.remove(position);
            mAdapter.notifyItemRemoved(position);
            mAdapter.notifyItemRangeChanged(0, mData.size() - 1);
        }

        @Override
        public int getItemCount() {
            if (mData != null) {
                return mData.size();
            } else {
                return 0;
            }
        }

        @Override
        public int getItemViewType(int position) {
            return mData.get(position) instanceof INativeTempletAdView ? TYPE_AD : TYPE_DATA;
        }

        @Override
        public void onBindViewHolder(final CustomViewHolder customViewHolder, final int position) {
            int type = getItemViewType(position);
            if (TYPE_AD == type) {
                final INativeTempletAdView iNativeTempletAdView = (INativeTempletAdView) mData.get(position);
                mAdViewPositionMap.put(iNativeTempletAdView, position); // 广告在列表中的位置是可以被更新的
                View adView = iNativeTempletAdView.getAdView();
                if (customViewHolder.container.getChildCount() > 0
                        && customViewHolder.container.getChildAt(0) == adView) {
                    return;
                }

                if (customViewHolder.container.getChildCount() > 0) {
                    customViewHolder.container.removeAllViews();
                }

                if (adView.getParent() != null) {
                    ((ViewGroup) adView.getParent()).removeView(adView);
                }

                customViewHolder.container.addView(adView);
                /**
                 * 调用render方法渲染广告
                 */
                iNativeTempletAdView.render();
            } else {
                customViewHolder.title.setText(((NormalItem) mData.get(position)).getTitle());
            }
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            int layoutId = (viewType == TYPE_AD) ? R.layout.item_native_templet_ad : R.layout.item_native_templet_data;
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, null);
            CustomViewHolder viewHolder = new CustomViewHolder(view);
            return viewHolder;
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView title;
            public ViewGroup container;

            public CustomViewHolder(View view) {
                super(view);
                title = (TextView) view.findViewById(R.id.native_templet_item_title_tv);
                container = (ViewGroup) view.findViewById(R.id.native_templet_item_ad_container);
            }
        }
    }

}

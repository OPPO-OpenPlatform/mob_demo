package com.opos.mobaddemo.nativead;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.androidquery.AQuery;
import com.heytap.msp.mobad.api.ad.NativeAdvanceAd;
import com.heytap.msp.mobad.api.listener.INativeAdvanceInteractListener;
import com.heytap.msp.mobad.api.listener.INativeAdvanceLoadListener;
import com.heytap.msp.mobad.api.listener.INativeAdvanceMediaListener;
import com.heytap.msp.mobad.api.params.INativeAdData;
import com.heytap.msp.mobad.api.params.INativeAdvanceData;
import com.heytap.msp.mobad.api.params.MediaView;
import com.heytap.msp.mobad.api.params.NativeAdvanceContainer;
import com.opos.mobaddemo.posid.Constants;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;

public class NativeAdvanceVideoRecyclerViewActivity extends Activity implements INativeAdvanceLoadListener {
    private static final String TAG = "NAVideoRecyclerView";
    /**
     * 每个列表Item个数
     */
    private static final int MAX_ITEMS = 30;
    /**
     * 第一个广告展示的位置
     */
    private static final int FIRST_AD_POSITION = 1;
    /**
     * 每隔15个展示一个广告
     */
    private int ITEMS_PER_AD = 15;
    //
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private CustomAdapter mAdapter;
    private List<NormalItem> mNormalDataList = new ArrayList<NormalItem>();
    private NativeAdvanceAd mNativeAdvanceAd;
    /**
     * 请求原生广告成功后返回的原生广告视图对象列表
     */
    private List<INativeAdvanceData> mINativeAdvanceDataList = new ArrayList<>();
    /**
     * 记录当前广告在列表中展示的位置
     */
//    private HashMap<INativeAdvanceData, Integer> mAdDataPositionMap = new HashMap<INativeAdvanceData, Integer>();

    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    private boolean mIsLoading = false;
    private boolean mIsRefresh = false;
    //
    private AtomicBoolean mIsNeedClearData = new AtomicBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        setContentView(R.layout.activity_native_advance_text_video_recycler_view);
        //
        initView();
        //
        initData();
    }

    private void initView() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.ad_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!mIsLoading && !mIsRefresh) {
                    mIsRefresh = true;
                    // do something, such as re-request from server or other
                    Log.d(TAG, "onRefresh: ");
                    initNativeAdvanceAd();
                }
            }
        });
        //
        mRecyclerView = (RecyclerView) findViewById(R.id.ad_rv);
        mRecyclerView.setHasFixedSize(true);
        mLinearLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (!mIsLoading && !mIsRefresh && newState == SCROLL_STATE_IDLE && isVisibleBottom(recyclerView)) {
                    mIsLoading = true;
                    Log.d(TAG, "onScrollStateChanged: ");
                    //
                    mNativeAdvanceAd.loadAd();
                }
            }
        });
    }

    private void initData() {
        mAdapter = new CustomAdapter(mNormalDataList);
        mRecyclerView.setAdapter(mAdapter);
        //
        initNativeAdvanceAd();
    }

    private void initNativeAdvanceAd() {
        /**
         * 通过构造NativeAdSize对象，在NativeTempletAd初始化时传入、可以指定原生模板广告的大小，单位为dp
         * 也可以传入null，展示默认的大小
         */
        mNativeAdvanceAd = new NativeAdvanceAd(this, Constants.NATIVE_ADVANCE_GROUP_VIDEO_LIST_POS_ID, this);
        /**
         * 调用loadAd方法请求原生模板广告
         */
        mNativeAdvanceAd.loadAd();
        //
        mIsNeedClearData.compareAndSet(false, true);
    }

    @Override
    public void onAdSuccess(final List<INativeAdvanceData> dataList) {
        if (mIsLoading) {
            mIsLoading = false;
        }
        if (null != dataList && dataList.size() > 0) {
            mINativeAdvanceDataList.addAll(dataList);
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    addNewPage(dataList);
                }
            });
        }
        if (mIsRefresh) {
            mSwipeRefreshLayout.setRefreshing(false);
            mIsRefresh = false;
        }
    }

    @Override
    public void onAdFailed(int ret, String msg) {
        Toast.makeText(this.getApplicationContext(), "加载原生广告失败,错误码：" + ret + ",msg:" + msg, Toast.LENGTH_SHORT).show();
        if (mIsLoading) {
            mIsLoading = false;
        }
        if (mIsRefresh) {
            mSwipeRefreshLayout.setRefreshing(false);
            mIsRefresh = false;
        }
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
        static final int TYPE_FOOT_DATA = 2;
        private List<Object> mData;

        public CustomAdapter(List list) {
            mData = list;
        }

        /**
         * 把返回的INativeAdvanceData添加到数据集里面去
         *
         * @param position
         * @param iNativeAdvanceData
         */
        public void addAdDataToPosition(int position, INativeAdvanceData iNativeAdvanceData) {
            if (position >= 0 && position < mData.size() && iNativeAdvanceData != null) {
                mData.add(position, iNativeAdvanceData);
            }
        }

        /**
         * 移除INativeAdvanceData的时候是一条一条移除的
         *
         * @param position
         */
        public void removeAdView(int position) {
            if (mData.get(position) != null && mData.get(position) instanceof INativeAdvanceData) {
                ((INativeAdvanceData) mData.get(position)).release();
            }
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
            return mData.get(position) instanceof INativeAdvanceData ? TYPE_AD : TYPE_DATA;
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            int layoutId = (viewType == TYPE_AD) ? R.layout.item_native_advance_ad : R.layout.item_native_advance_data;
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, null);
            CustomViewHolder viewHolder = new CustomViewHolder(view, viewType);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final CustomViewHolder holder, final int position) {
            int type = getItemViewType(position);
            switch (type) {
                case TYPE_AD:
                    final INativeAdvanceData iNativeAdvanceData = (INativeAdvanceData) mData.get(position);
                    if (null != iNativeAdvanceData && iNativeAdvanceData.isAdValid()) {
//                    mAdDataPositionMap.put(iNativeAdvanceData, position); // 广告在列表中的位置是可以被更新的
                        AQuery logoAQ = holder.logoAQ;
                        if (null != iNativeAdvanceData.getLogoFile()) {
                            logoAQ.id(R.id.logo_iv).image(TextUtils.isEmpty(iNativeAdvanceData.getLogoFile().getUrl()) ? "" : iNativeAdvanceData.getLogoFile().getUrl(), false, true);
                        }

                        holder.title.setText(null != iNativeAdvanceData.getTitle() ? iNativeAdvanceData.getTitle() : "");
                        holder.desc.setText(null != iNativeAdvanceData.getDesc() ? iNativeAdvanceData.getDesc() : "");
                        holder.clickBn.setText(null != iNativeAdvanceData.getClickBnText() ? iNativeAdvanceData.getClickBnText() : "");
                        /**
                         * 处理“关闭”按钮交互行为
                         */
                        View.OnClickListener onClickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (v == holder.closeIv) {
                                    removeAdView(position);
                                }
                            }
                        };
                        holder.closeIv.setOnClickListener(onClickListener);
                        /**
                         *原生广告的渲染内容必须渲染在NativeAdvanceContainer里面
                         */
                        NativeAdvanceContainer container = holder.nativeAdvanceContainer;
                        MediaView mediaContainer = holder.mediaView;
                        List<View> clickViewList = new ArrayList<>();
                        /**
                         * 响应广告点击事件的按钮
                         */
                        clickViewList.add(holder.clickBn);
                        /**
                         * 绑定广告点击事件与点击按钮
                         */
                        iNativeAdvanceData.bindToView(NativeAdvanceVideoRecyclerViewActivity.this, container, clickViewList);
                        setAdListener(holder, iNativeAdvanceData);
                    }
                    break;
                case TYPE_DATA:
                    holder.title.setText(((NormalItem) mData.get(position)).getTitle());
                    break;
            }
        }


        private void setAdListener(final CustomViewHolder holder, final INativeAdvanceData adData) {
            adData.setInteractListener(new INativeAdvanceInteractListener() {

                @Override
                public void onClick() {
                    Toast.makeText(NativeAdvanceVideoRecyclerViewActivity.this.getApplicationContext(), "原生广告点击", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onShow() {
//                    Toast.makeText(NativeAdvanceVideoRecyclerViewActivity.this.getApplicationContext(), "原生广告展示", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(int code, String msg) {
                    Toast.makeText(NativeAdvanceVideoRecyclerViewActivity.this.getApplicationContext(), "原生广告出错，ret:" + code + ",msg:" + msg, Toast.LENGTH_SHORT).show();
                }
            });
            /**
             * 广告素材为视频类型
             */
            if (adData.getCreativeType() == INativeAdData.CREATIVE_TYPR_NATIVE_VIDEO) {
                /*
                 *绑定广告展示的视频View
                 */
                adData.bindMediaView(NativeAdvanceVideoRecyclerViewActivity.this, holder.mediaView, new INativeAdvanceMediaListener() {
                    @Override
                    public void onVideoPlayStart() {
                        Log.d(TAG, "onVideoPlayStart");
//                        Toast.makeText(NativeAdvanceVideoRecyclerViewActivity.this.getApplicationContext(), "onVideoPlayStart", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onVideoPlayComplete() {
                        Log.d(TAG, "onVideoPlayComplete");
//                        Toast.makeText(NativeAdvanceVideoRecyclerViewActivity.this.getApplicationContext(), "onVideoPlayComplete", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onVideoPlayError(int errorCode, String msg) {
                        Log.d(TAG, "onVideoPlayError :code = " + errorCode + ",msg = " + msg);
//                        Toast.makeText(NativeAdvanceVideoRecyclerViewActivity.this.getApplicationContext(), "onVideoPlayError :code = " + errorCode + ",msg = " + msg, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(NativeAdvanceVideoRecyclerViewActivity.this.getApplicationContext(), "is not video ad type", Toast.LENGTH_SHORT).show();
            }
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {
            public NativeAdvanceContainer nativeAdvanceContainer;
            public MediaView mediaView;
            public TextView desc;
            public ImageView logo;
            public ImageView closeIv;
            //
            public TextView title;
            public Button clickBn;
            //
            public AQuery logoAQ;
            //
            public ImageView loadingIV;

            public CustomViewHolder(View view, int viewType) {
                super(view);
                switch (viewType) {
                    case TYPE_AD:
                        nativeAdvanceContainer = (NativeAdvanceContainer) view.findViewById(R.id.native_ad_container);
                        mediaView = (MediaView) view.findViewById(R.id.video_container);
                        desc = (TextView) view.findViewById(R.id.desc_tv);
                        logo = (ImageView) view.findViewById(R.id.logo_iv);
                        closeIv = (ImageView) view.findViewById(R.id.close_iv);
                        title = (TextView) view.findViewById(R.id.title_tv);
                        clickBn = (Button) view.findViewById(R.id.click_bn);
                        logoAQ = new AQuery(view);
                        break;
                    case TYPE_DATA:
                        title = (TextView) view.findViewById(R.id.native_advance_item_title_tv);
                        break;
                }
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mINativeAdvanceDataList) {
            /**
             *释放视频资源。
             */
            for (INativeAdvanceData iNativeAdvanceData : mINativeAdvanceDataList) {
                iNativeAdvanceData.release();
            }
        }
        if (null != mNativeAdvanceAd) {
            /**
             *銷毀NativeAd对象，释放资源。
             */
            mNativeAdvanceAd.destroyAd();
        }
        mAdapter = null;
        mRecyclerView = null;
    }

    private void addNewPage(List<INativeAdvanceData> dataList) {
        if (null != dataList && dataList.size() > 0) {
            if (mIsNeedClearData.compareAndSet(true, false)) {
                mNormalDataList.clear();
            }
            if (null == mAdapter){
                return;
            }
            int count = mAdapter.getItemCount();
            for (int i = 0; i < MAX_ITEMS; ++i) {
                mNormalDataList.add(new NormalItem("No." + (count + i) + " Normal Data"));
            }

            for (int i = 0; i < dataList.size(); i++) {
                int position = count + FIRST_AD_POSITION + ITEMS_PER_AD * i;
                if (position < mNormalDataList.size()) {
//                    mAdDataPositionMap.put(mINativeAdvanceDataList.get(i), position); // 把每个广告在列表中位置记录下来
                    mAdapter.addAdDataToPosition(position, dataList.get(i));
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }


    public static boolean isVisibleBottom(RecyclerView recyclerView){
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //屏幕中最后一个可见子项的position
        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
        //当前屏幕所看到的子项个数
        int visibleCount = layoutManager.getChildCount();
        //当前所有子项个数
        int totalCount = layoutManager.getItemCount();
        //RecyclerView的滑动状态
        if(visibleCount > 0 && lastVisiblePosition == totalCount - 1 ){
            return true;
        }else {
            return false;
        }
    }

}
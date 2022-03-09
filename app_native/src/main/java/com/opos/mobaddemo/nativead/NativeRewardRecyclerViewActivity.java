package com.opos.mobaddemo.nativead;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.heytap.msp.mobad.api.ad.NativeAd;
import com.heytap.msp.mobad.api.listener.INativeRewardAdListener;
import com.heytap.msp.mobad.api.params.INativeAdData;
import com.heytap.msp.mobad.api.params.INativeAdFile;
import com.heytap.msp.mobad.api.params.NativeAdError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NativeRewardRecyclerViewActivity extends Activity implements INativeRewardAdListener {

    private static final String TAG = "NativeRewardRecyclerViewActivity";
    /**
     * TODO 激励广告前置引导提示，主要用于提示用户如何操作才能获取奖励。目前原生支持两种激励场景：1、点击下载安装完成获取激励；2、点击下载安装后打开获取激励。应用在接入的时候，需要根据自己应用场景，对用户进行一定的引导。
     */
    private static final String REWARD_TIPS_INSTALL_COMPLETED = "注意：点击下载并安装完成可获取奖励";
    private static final String REWARD_TIPS_LAUNCH_APP = "注意：点击下载，安装完成后点击打开可以获取奖励";
    /**
     * TODO 该文案主要用于当收到应用安装完成通知后、将点击按钮的“下载”文案改成“打开”使用。
     */
    private static final String CLICK_BN_TEXT_LAUNCH_APP = "打开";
    //
    private static final String REWARD_TV_TEXT = "+50积分";
    private static final String REWARD_TOAST_TEXT = "完成任务、恭喜获取";

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

    private String mPosId;

    private ImageLoader mImageLoader;
    /**
     * 原生广告对象
     */
    private NativeAd mNativeAd;
    /**
     * TODO 目前原生支持两种激励场景：1、点击下载安装完成获取激励；2、点击下载安装后打开获取激励。具体使用哪种激励场景由应用来决策、上面两个激励场景只能选择一种场景激励、不能两个场景都激励。
     */
    private int mRewardScene;

    private AlertDialog mAlertDialog;
    /**
     * 请求原生广告成功后返回的原生广告对象列表
     */
    private List<INativeAdData> mINativeAdDataList;
    /**
     * 记录点击下载的广告对象和所在列表的位置。
     */
    private HashMap<Integer, INativeAdData> mAdDataPositionMap = new HashMap<Integer, INativeAdData>();
    /**
     * 记录已经安装的文件位置,K为已安装位置，V为安装位置是否打开过
     */
    private HashMap<Integer, Boolean> mInstallPositionMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        setContentView(R.layout.activity_native_reward_recycler_view);
        //
        mRewardScene = getIntent().getIntExtra(NativeRewardActivity.EXTRA_KEY_REWARD_SCENE, 0);
        mPosId = getIntent().getStringExtra(NativeRewardActivity.EXTRA_KEY_POS_ID);
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
             * mNativeAd调用destroyAd方法释放相关资源
             */
            if (null != mNativeAd) {
                mNativeAd.destroyAd();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        /**
         * 采用开源的ImageLoader框架来下载和加载图片资源。
         */
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(this));
        mRecyclerView = (RecyclerView) findViewById(R.id.ad_rv);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(0);
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
        initNativeAd();
    }

    private void initNativeAd() {
        /**
         *TODO 构造NativeAd对象,注意:构造原生激励广告的时候、需要传入激励场景【NativeAd.REWARD_SCENE_INSTALL_COMPLETE-安装完成激励，NativeAd.REWARD_SCENE_LAUNCH_APP-安装完成打开激励】，Listener也是INativeRewardAdListener对象。
         */
        if (NativeRewardActivity.REWARD_SCENE_INSTALL_COMPLETE == mRewardScene) {
            mNativeAd = new NativeAd(this, mPosId, NativeAd.REWARD_SCENE_INSTALL_COMPLETE, this);
        } else if (NativeRewardActivity.REWARD_SCENE_LAUNCH_APP == mRewardScene) {
            mNativeAd = new NativeAd(this, mPosId, NativeAd.REWARD_SCENE_LAUNCH_APP, this);
        }
        /**
         * 调用loadAd方法请求原生广告
         */
        mNativeAd.loadAd();
        /**
         * TODO 户获取奖励提示框、这只是demo效果，应用结合自己的游戏场景自定义即可。
         */
        mAlertDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("获取奖励")
                .setMessage(REWARD_TOAST_TEXT + REWARD_TV_TEXT)
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
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

        /**
         * TODO 三种Item类型、header用来展示激励引导提示；data是普通数据；ad是广告；
         */
        static final int TYPE_HEADER = 0;
        static final int TYPE_DATA = 1;
        static final int TYPE_AD = 2;
        private List<Object> mData;

        public CustomAdapter(List list) {
            mData = list;
        }

        /**
         * 把返回的iNativeAdData添加到数据集里面去
         *
         * @param position
         * @param iNativeAdData
         */
        public void addAdDataToPosition(int position, INativeAdData iNativeAdData) {
            if (position >= 0 && position < mData.size() && iNativeAdData != null) {
                mData.add(position, iNativeAdData);
            }
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
            if (0 == position) {
                return TYPE_HEADER;
            } else {
                return mData.get(position) instanceof INativeAdData ? TYPE_AD : TYPE_DATA;
            }
        }

        @Override
        public void onBindViewHolder(final CustomViewHolder customViewHolder, @SuppressLint("RecyclerView") final int position) {
            int type = getItemViewType(position);
            if (TYPE_HEADER == type) {
                /**
                 *TODO 展示激励广告前置引导，这里只是demo简单的引导示例效果、应用可以结合自己的游戏场景自定义即可。
                 */
                if (NativeRewardActivity.REWARD_SCENE_INSTALL_COMPLETE == mRewardScene) {
                    customViewHolder.tips.setText(REWARD_TIPS_INSTALL_COMPLETED);
                } else if (NativeRewardActivity.REWARD_SCENE_LAUNCH_APP == mRewardScene) {
                    customViewHolder.tips.setText(REWARD_TIPS_LAUNCH_APP);
                }
            } else if (TYPE_AD == type) {
                final INativeAdData iNativeAdData = (INativeAdData) mData.get(position);
                customViewHolder.title.setText(iNativeAdData.getTitle());
                customViewHolder.desc.setText(iNativeAdData.getDesc());
                /**
                 *展示推广应用的ICON，大小为512X512。
                 */
                if (null != iNativeAdData.getIconFiles() && iNativeAdData.getIconFiles().size() > 0) {
                    INativeAdFile iNativeAdFile = (INativeAdFile) iNativeAdData.getIconFiles().get(0);
                    //
                    mImageLoader.displayImage(iNativeAdFile.getUrl(), customViewHolder.icon);
                }
                /**
                 * 判断是否需要展示“广告”Logo标签
                 */
                if (null != iNativeAdData.getLogoFile()) {
                    mImageLoader.displayImage(iNativeAdData.getLogoFile().getUrl(), customViewHolder.logo);
                }

                /**
                 * 判断广告是否安装过，对应控制按钮文案
                 */
                boolean isRewarded = false;
                boolean isInstalled = false;
                if (mInstallPositionMap.size() > 0) {
                    Iterator iterator = mInstallPositionMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<Integer, Boolean> entry = (Map.Entry) iterator.next();
                        if (entry.getKey() == position) {
                            isRewarded = entry.getValue();
                            isInstalled = true;
                        }
                    }
                }

                if (isInstalled) {
                    if (NativeRewardActivity.REWARD_SCENE_INSTALL_COMPLETE == mRewardScene) {//安装完成激励直接去除reward
                        customViewHolder.reward.setVisibility(View.GONE);
                    } else if (NativeRewardActivity.REWARD_SCENE_LAUNCH_APP == mRewardScene && !isRewarded) {
                        customViewHolder.reward.setVisibility(View.VISIBLE);
                    } else if (NativeRewardActivity.REWARD_SCENE_LAUNCH_APP == mRewardScene && isRewarded) {//打开完成激励需判断是否已经奖励过
                        customViewHolder.reward.setVisibility(View.GONE);
                    }
                    customViewHolder.action.setText(CLICK_BN_TEXT_LAUNCH_APP);
                    customViewHolder.action.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (iNativeAdData.launchApp()) {
                                Toast.makeText(NativeRewardRecyclerViewActivity.this, "打开应用成功！", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(NativeRewardRecyclerViewActivity.this, "打开应用失败！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    customViewHolder.action.setText(iNativeAdData.getClickBnText());
                    customViewHolder.reward.setVisibility(View.VISIBLE);
                    customViewHolder.reward.setText(REWARD_TV_TEXT);
                    customViewHolder.action.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /**
                             * 原生广告曝光时必须调用onAdShow方法通知SDK进行曝光统计，否则就没有曝光数据。
                             */
                            iNativeAdData.onAdClick(v);
                            /**
                             * 记录点击下载的广告对象和在列表中的位置。
                             */
                            mAdDataPositionMap.put(position, iNativeAdData);
                        }
                    });
                }
                //

                iNativeAdData.onAdShow(customViewHolder.adContainer);
            } else {
                customViewHolder.title.setText(((NormalItem) mData.get(position)).getTitle());
            }
        }

        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            int layoutId = R.layout.item_native_reward_data;
            if (viewType == TYPE_AD) {
                layoutId = R.layout.item_native_reward_ad;
            } else if (viewType == TYPE_HEADER) {
                layoutId = R.layout.item_native_reward_header;
            }
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, null);
            CustomViewHolder viewHolder = new CustomViewHolder(view);
            return viewHolder;
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView tips;
            public RelativeLayout adContainer;
            public TextView title;
            public TextView desc;
            public Button action;
            public TextView reward;
            public ImageView icon;
            public ImageView logo;

            public CustomViewHolder(View view) {
                super(view);
                tips = (TextView) view.findViewById(R.id.tips_tv);
                adContainer = (RelativeLayout) view.findViewById(R.id.ad_container);
                title = (TextView) view.findViewById(R.id.title_tv);
                desc = (TextView) view.findViewById(R.id.desc_tv);
                action = (Button) view.findViewById(R.id.action_bn);
                reward = (TextView) view.findViewById(R.id.reward_tv);
                icon = (ImageView) view.findViewById(R.id.icon_iv);
                logo = (ImageView) view.findViewById(R.id.logo_iv);
            }
        }
    }


    /**
     * 原生广告加载成功，在onAdSuccess回调广告数据
     *
     * @param iNativeAdDataList
     */
    @Override
    public void onAdSuccess(List iNativeAdDataList) {
        if (null != iNativeAdDataList && iNativeAdDataList.size() > 0) {
            mINativeAdDataList = iNativeAdDataList;
            for (int i = 0; i < mINativeAdDataList.size(); i++) {
                int position = FIRST_AD_POSITION + ITEMS_PER_AD * i;
                if (position < mNormalDataList.size()) {
                    mAdapter.addAdDataToPosition(position, mINativeAdDataList.get(i));
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAdFailed(NativeAdError nativeAdError) {
        Toast.makeText(NativeRewardRecyclerViewActivity.this, "加载原生广告失败,错误码：" + nativeAdError.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAdError(NativeAdError nativeAdError, INativeAdData iNativeAdData) {
        Toast.makeText(NativeRewardRecyclerViewActivity.this, "调用原生广告统计方法出错,错误码：" + nativeAdError.toString(), Toast.LENGTH_LONG).show();
    }

    /**
     * @param pkgName 下载安装完成回调，pkgName-安装完成的应用包名
     */
    @Override
    public void onInstallCompleted(String pkgName) {

        /**
         * 在收到应用安装完成回调以后，先调用isCurrentApp方法判断当前在展示的广告是不是刚才安装的应用，如果是，才可以将按钮文案从“下载”更改成“打开”，并且将按钮点击行为更改成调用launchApp方法打开应用。
         */
        if (mAdDataPositionMap.size() > 0) {
            Iterator iterator = mAdDataPositionMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, INativeAdData> entry = (Map.Entry) iterator.next();
                final INativeAdData iNativeAdData = entry.getValue();
                //
                if (iNativeAdData.isCurrentApp(pkgName)) {
                    int position = entry.getKey();
                    //
                    mInstallPositionMap.put(position, false);

                    final CustomAdapter.CustomViewHolder customViewHolder = (CustomAdapter.CustomViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position); //得到要更新的item的view

                    //
                    if (null != customViewHolder) {
                        customViewHolder.action.setText(CLICK_BN_TEXT_LAUNCH_APP);
                        customViewHolder.action.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (iNativeAdData.launchApp()) {
                                    Toast.makeText(NativeRewardRecyclerViewActivity.this, "打开应用成功！", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(NativeRewardRecyclerViewActivity.this, "打开应用失败！", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    /**
     * 激励回调
     *
     * @param objects
     */
    @Override
    public void onReward(Object... objects) {
        /**
         * TODO 在这里给予用户对应的奖励。
         */
        try {
            String pkgName = (String) objects[0];
            if (mAdDataPositionMap.size() > 0) {
                Iterator iterator = mAdDataPositionMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Integer, INativeAdData> entry = (Map.Entry) iterator.next();
                    final INativeAdData iNativeAdData = entry.getValue();
                    //
                    if (iNativeAdData.isCurrentApp(pkgName)) {
                        int position = entry.getKey();
                        //
                        if (NativeRewardActivity.REWARD_SCENE_LAUNCH_APP == mRewardScene) {
                            mInstallPositionMap.put(position, true);
                        }
                        final CustomAdapter.CustomViewHolder customViewHolder = (CustomAdapter.CustomViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position); //得到要更新的item的view
                        if (null != customViewHolder) {
                            customViewHolder.reward.setVisibility(View.GONE);
                        }
                    }
                }
            }
            mAlertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRewardFail(Object... objects) {
        try {
            String pkgName = (String) objects[0];
            if (mAdDataPositionMap.size() > 0) {
                Iterator iterator = mAdDataPositionMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Integer, INativeAdData> entry = (Map.Entry) iterator.next();
                    final INativeAdData iNativeAdData = entry.getValue();
                    //
                    if (iNativeAdData.isCurrentApp(pkgName)) {
                        int position = entry.getKey();
                        //
//                        if (NativeRewardActivity.REWARD_SCENE_LAUNCH_APP == mRewardScene) {
                        mInstallPositionMap.put(position, true);
//                        }
                        final CustomAdapter.CustomViewHolder customViewHolder = (CustomAdapter.CustomViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position); //得到要更新的item的view
                        if (null != customViewHolder) {
                            customViewHolder.reward.setVisibility(View.GONE);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("获取奖励失败")
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }
}

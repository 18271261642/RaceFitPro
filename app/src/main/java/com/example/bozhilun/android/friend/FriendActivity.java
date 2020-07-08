package com.example.bozhilun.android.friend;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bozhilun.android.Commont;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.friend.bean.FriendMyFriendListBean;
import com.example.bozhilun.android.friend.bean.TodayRankBean;
import com.example.bozhilun.android.friend.mutilbind.FrendAdapter;
import com.example.bozhilun.android.friend.mutilbind.TodayRankAdapter;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.example.bozhilun.android.util.ToastUtil;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 好友首页
 * @aboutContent:
 * @author： An
 * @crateTime: 2018/3/9 18:07
 * @mailBox: an.****.life@gmail.com
 * @company: 东莞速成科技有限公司
 */

public class FriendActivity extends WatchBaseActivity implements RequestView, TabLayout.OnTabSelectedListener, FrendAdapter.OnItemListenter {

    private static final String TAG = "FriendActivity";

    @BindView(R.id.toolbar_normal)
    Toolbar mNormalToolbar;
    @BindView(R.id.recycler_frend)
    RecyclerView recyclerViewFrend;
    @BindView(R.id.recycler_unfrend)
    RecyclerView recyclerViewUnFrend;
    @BindView(R.id.m_tablayout)
    TabLayout mTabLayout;

    @BindView(R.id.myFriendPositionTv)
    TextView myFriendPositionTv;
    @BindView(R.id.myFriendMineHeadImg)
    CircleImageView myFriendMineHeadImg;
    @BindView(R.id.myFriendMineNameTv)
    TextView myFriendMineNameTv;
    @BindView(R.id.myFriendMineStepTv)
    TextView myFriendMineStepTv;
    @BindView(R.id.myFriendMinZanImg)
    ImageView myFriendMinZanImg;
    @BindView(R.id.myFriendMineZanCountTv)
    TextView myFriendMineZanCountTv;
    //显示好友列表的布局
    @BindView(R.id.myFriendMineLin)
    LinearLayout myFriendMineLin;
    private int pageNumber = 0;//记录当前页码

    private RequestPressent requestPressent;
    String userId = "";


    //我的好友列表
    private List<FriendMyFriendListBean> myFriendList;
    private FrendAdapter frendAdapter;

    //用户世界排行
    private TodayRankAdapter rankAdapter;
    private List<TodayRankBean.RankListBean> rankList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fredens);
        ButterKnife.bind(this);
        inEdit();


        initViews();


    }

    private void initViews() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewFrend.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerViewFrend.setLayoutManager(linearLayoutManager);
        myFriendList = new ArrayList<>();
        frendAdapter = new FrendAdapter(this, myFriendList);
        recyclerViewFrend.setAdapter(frendAdapter);
        frendAdapter.setmOnItemListenter(this);


        LinearLayoutManager lin2 = new LinearLayoutManager(this);
        lin2.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewUnFrend.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerViewUnFrend.setLayoutManager(lin2);
        rankList = new ArrayList<>();
        rankAdapter = new TodayRankAdapter(this, rankList);
        recyclerViewUnFrend.setAdapter(rankAdapter);


    }

    @Override
    protected void onResume() {
        super.onResume();
        userId = (String) SharedPreferencesUtils.readObject(this, "userId");
        myFriendList.clear();
        rankList.clear();
        if (pageNumber == 0) {
            myFriendMineLin.setVisibility(View.VISIBLE);
            recyclerViewUnFrend.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(userId)) {
                showLoadingDialog(getResources().getString(R.string.dlog));
                findFrendList(userId);
                findNewApplyFrend(userId);
            }
        } else {
            myFriendMineLin.setVisibility(View.GONE);
            recyclerViewUnFrend.setVisibility(View.VISIBLE);
            showLoadingDialog(getResources().getString(R.string.dlog));
            findUnFrendList();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        pageNumber = 0;
        if (requestPressent != null)
            requestPressent.detach();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void inEdit() {
        requestPressent = new RequestPressent();
        requestPressent.attach(this);

        mTabLayout.setVisibility(View.VISIBLE);
        mTabLayout.addTab(mTabLayout.newTab().setText(getResources().getString(R.string.string_frend)));//好友
        mTabLayout.addTab(mTabLayout.newTab().setText(getResources().getString(R.string.string_world)));//世界
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.addOnTabSelectedListener(this);

        //替换三个点
        mNormalToolbar.setOverflowIcon(getResources().getDrawable(R.mipmap.image_add));
        mNormalToolbar.setNavigationIcon(R.mipmap.backs);
        setSupportActionBar(mNormalToolbar);


    }


    /**
     * 让菜单同时显示图标和文字
     *
     * @param featureId
     * @param menu
     * @return
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.frend_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_add_new_frend) {//添加好友
            startActivity(new Intent(this, AddNewFriendActivity.class));
            return true;
        }
        if (id == R.id.action_new_frend_apply) {//好友申请
            startActivity(new Intent(this, NewFriendApplyActivity.class));
            return true;
        }

        finish();
        return super.onOptionsItemSelected(item);
    }

    /************************************************/
/**
 *
 * 可以重复添加好友,添加之后删除一条后,再删除相同的返回013
 * 添加好友返回成功，但是好友列表没有数据
 * 不过自己搜索添加自己能成功，而且删除提示013
 */

    /**
     * 查找好友列表
     *
     * @param userId
     */
    public void findFrendList(String userId) {
        String sleepUrl = Commont.FRIEND_BASE_URL + Commont.Findlist;
        String saveMac = WatchUtils.getSherpBleMac(FriendActivity.this);
        if (WatchUtils.isEmpty(saveMac))
            return;
        JSONObject sleepJson = new JSONObject();
        try {
            sleepJson.put("userId", userId);
            sleepJson.put("mac", saveMac);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        if (requestPressent != null) {
            Log.e(TAG, "---------获取好友列表参数=" + sleepJson.toString());
            requestPressent.getRequestJSONObject(0x01, sleepUrl, FriendActivity.this, sleepJson.toString(), 0);
        }
    }

    /**
     * 删除好友
     *
     * @param userId
     */
    public void deleteFrenditem(String userId, String applicant) {
        String sleepUrl = Commont.FRIEND_BASE_URL + Commont.DeleteFrendItem;
        JSONObject sleepJson = new JSONObject();
        try {
            sleepJson.put("userId", userId);
            sleepJson.put("applicant", applicant);

            Log.d("-----------朋友--", " 删除好友获取参数--" + sleepJson.toString());
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(0x03, sleepUrl, FriendActivity.this, sleepJson.toString(), 0);
        }
    }

    /**
     * 给好友点赞 （好友之间一天只能点赞一次，不能取消赞）
     *
     * @param userId
     * @param applicant 被赞人
     */
    public void awesomeFrenditem(String userId, String applicant) {
        String sleepUrl = Commont.FRIEND_BASE_URL + Commont.FrendAwesome;
        JSONObject sleepJson = new JSONObject();
        try {
            sleepJson.put("userId", userId);
            sleepJson.put("applicant", applicant);
            Log.d("-----------朋友--", " 给好友点赞获取参数--" + sleepJson.toString());
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(0x04, sleepUrl, FriendActivity.this, sleepJson.toString(), 0);
        }
    }

    /**
     * 查找非好友列表，世界排行
     */
    public void findUnFrendList() {
        String sleepUrl = Commont.FRIEND_BASE_URL + Commont.TodayRank;
        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(0x02, sleepUrl, FriendActivity.this, "", 0);
        }
    }


    /**
     * 查找朋友的新申请
     *
     * @param userId
     */
    public void findNewApplyFrend(String userId) {
        String sleepUrl = Commont.FRIEND_BASE_URL + Commont.FindNewFrend;
        JSONObject sleepJson = new JSONObject();
        try {
            sleepJson.put("userId", userId);
            Log.d("-----------朋友--", " 查找新申请的朋友参数--" + sleepJson.toString());
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        if (requestPressent != null) {
            requestPressent.getRequestJSONObject(0x05, sleepUrl, this, sleepJson.toString(), 0);
        }
    }


   /* Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            try {
                switch (message.what) {
                    case 0x05:
                        Log.d("----------新申请朋友列表返回--", message.obj.toString());
                        NewFrendApplyBean newFrendApplyBean = new Gson().fromJson(message.obj.toString(), NewFrendApplyBean.class);
                        if (newFrendApplyBean != null && newFrendApplyBean.getResultCode().equals("001")) {
                            List<NewFrendApplyBean.ApplyListBean> applyList = newFrendApplyBean.getApplyList();
                            if (applyList != null && !applyList.isEmpty()) {
                                //替换三个点
                                mNormalToolbar.setOverflowIcon(getResources().getDrawable(R.mipmap.ic_new_add_frend));
                                setSupportActionBar(mNormalToolbar);
                            } else {
                                //替换三个点
                                mNormalToolbar.setOverflowIcon(getResources().getDrawable(R.mipmap.image_add));
                                setSupportActionBar(mNormalToolbar);
                            }

                        }
                        break;
                }
            } catch (Exception error) {
                error.printStackTrace();
            }

            return false;
        }
    });
*/

    @Override
    public void showLoadDialog(int what) {
        showLoadingDialog(getResources().getString(R.string.dlog));
    }

    @Override
    public void successData(int what, Object object, int daystag) {
        closeLoadingDialog();
        if (object == null || TextUtils.isEmpty(object.toString().trim()) || object.toString().contains("<html>"))
            return;
        Log.e(TAG, "--------succ="+what+"---"+object.toString());

        try {
            JSONObject jsonObject = new JSONObject(object.toString());
            switch (what) {
                case 0x01:  //好友列表
                    analysisFriendListData(jsonObject);
                    break;
                case 0x02:  //世界排行
                    analysisWorldRankData(jsonObject);
                    break;
                case 0x03:      //删除好友
                    analysisDelFriendData(jsonObject);
                    break;
                case 0x04:  //好友点赞
                    if(jsonObject.getInt("code") == 200){   //点赞成功
                        findFrendList(userId);
                    }else{
                        ToastUtil.showToast(FriendActivity.this,jsonObject.getString("msg"));
                    }
                    break;
                case 0x05:  //查找新好友的申请
                    analysisNewFriendApp(jsonObject);
                    break;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //是否有好友申请
    private void analysisNewFriendApp(JSONObject jsonObject) {
        try {
            if(jsonObject.getInt("code") == 200){
                String data = jsonObject.getString("data");
                if(data != null && !data.equals("[]")){
                    //替换三个点
                    mNormalToolbar.setOverflowIcon(getResources().getDrawable(R.mipmap.ic_new_add_frend));
                    setSupportActionBar(mNormalToolbar);
                }else{
                    //替换三个点
                    mNormalToolbar.setOverflowIcon(getResources().getDrawable(R.mipmap.image_add));
                    setSupportActionBar(mNormalToolbar);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    Exception e;
    @Override
    public void failedData(int what, Throwable e) {
        closeLoadingDialog();
    }

    @Override
    public void closeLoadDialog(int what) {
        closeLoadingDialog();
    }


    //展示好友列表
    private void analysisFriendListData(JSONObject friendListStr) {

        try {
            myFriendList.clear();
            frendAdapter.notifyDataSetChanged();
            if (friendListStr.getInt("code") == 200) {
                JSONObject dataJsonStr = friendListStr.getJSONObject("data");

                //显示个人信息的bean
                FriendMyFriendListBean tmpBean = new Gson().fromJson(dataJsonStr.getString("userInfo"),
                        FriendMyFriendListBean.class);
                showMineUserData(tmpBean);

                //好友列表
                List<FriendMyFriendListBean> tempFriendList = new Gson().fromJson(dataJsonStr.getString("myfriends"),
                        new TypeToken<List<FriendMyFriendListBean>>() {
                        }.getType());
                Collections.sort(tempFriendList, new Comparator<FriendMyFriendListBean>() {
                    @Override
                    public int compare(FriendMyFriendListBean o1, FriendMyFriendListBean o2) {
                        return o2.getLevel()-o1.getLevel();
                    }
                });
                myFriendList.addAll(tempFriendList);
                frendAdapter.notifyDataSetChanged();
                return;
            }
            frendAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //显示个人信息
    @SuppressLint("SetTextI18n")
    private void showMineUserData(FriendMyFriendListBean tmpBean) {
        myFriendPositionTv.setText(getResources().getString(R.string.string_mine));
        myFriendMineNameTv.setText(tmpBean.getNickname() + "");
        myFriendMineStepTv.setText(getResources().getString(R.string.step) + ":" + tmpBean.getStepNumber());
        Glide.with(FriendActivity.this).load(tmpBean.getImage())
                .into(myFriendMineHeadImg);
        int isThumbs = tmpBean.getTodayThumbs();
        myFriendMineZanCountTv.setText(isThumbs + "");
        if (isThumbs == 0) {
            myFriendMinZanImg.setEnabled(true);
            myFriendMinZanImg.setBackgroundResource(R.mipmap.ic_on_like);
        } else {
            myFriendMinZanImg.setEnabled(false);
            myFriendMinZanImg.setBackgroundResource(R.mipmap.ic_un_like);
        }
    }


    //世界排行
    private void analysisWorldRankData(JSONObject jsonObject) {
        rankList.clear();
        try {
            if(jsonObject.getInt("code") == 200){
                String rankStr = jsonObject.getString("data");
                List<TodayRankBean.RankListBean> tmpRankList = new Gson().fromJson(rankStr, new TypeToken<List<TodayRankBean.RankListBean>>() {
                }.getType());
                rankList.addAll(tmpRankList);
                rankAdapter.notifyDataSetChanged();
            }else{
                rankAdapter.notifyDataSetChanged();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //删除好友
    private void analysisDelFriendData(JSONObject jsonObject) {
        try {
            if (jsonObject.getInt("code") == 200) {   //删除成功，刷新界面
                findFrendList(userId);
                findNewApplyFrend(userId);
            }else{
                ToastUtil.showToast(FriendActivity.this,jsonObject.getString("msg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * TabLayout 改变监听
     *
     * @param tab
     */
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        pageNumber = tab.getPosition();
        userId = (String) SharedPreferencesUtils.readObject(this, "userId");
        if (pageNumber == 0) {
            myFriendMineLin.setVisibility(View.VISIBLE);
            recyclerViewUnFrend.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(userId)) {
                findFrendList(userId);
            }
        } else {
            myFriendMineLin.setVisibility(View.GONE);
            recyclerViewUnFrend.setVisibility(View.VISIBLE);
            findUnFrendList();
        }
        if (!TextUtils.isEmpty(userId)) findNewApplyFrend(userId);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    /**
     * 好友列表点击事件
     *
     * @param view
     * @param applicant   朋友UserID
     * @param stepNumber  步数
     * @param frendHeight 身高
     */
    @Override
    public void ItemOnClick(View view, String applicant, int stepNumber, String frendHeight, int postion, String bleMac) {
        //去朋友数据界面
        startActivity(FrendDataActivity.class, new String[]{"applicant", "stepNumber", "frendHeight", "bleMac"},
                new String[]{applicant, stepNumber + "", frendHeight, bleMac});
    }

    /**
     * 点击我的条目--- 去咱我的人的界面
     *
     * @param postion
     */
    @Override
    public void ItemOnClickMine(int postion) {

    }


    /**
     * 好友点赞
     *
     * @param view
     */
    @Override
    public void ItemLoveOnClick(View view, String applicant) {
        userId = (String) SharedPreferencesUtils.readObject(FriendActivity.this, "userId");
        if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(applicant)) {
            awesomeFrenditem(userId, applicant);
        }
    }


    /**
     * 好友列表长按事件-------删除好友
     *
     * @param view
     */
    @Override
    public void ItemOnLongClick(View view, final String applicant) {

        userId = (String) SharedPreferencesUtils.readObject(FriendActivity.this, "userId");
        if (!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(applicant)) {
            deleteFrenditem(userId, applicant);
        }
    }

    @OnClick(R.id.myFriendMineLin)
    public void onClick() {
        startActivity(FrendLoveMineActivity.class);
    }
}

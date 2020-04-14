package com.example.bozhilun.android.recommend;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.adpter.FragmentAdapter;
import com.example.bozhilun.android.siswatch.view.LazyFragment;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Admin
 * Date 2020/4/1
 */
public class RecommendFragment extends LazyFragment implements RequestView {

    private static final String TAG = "RecommendFragment";


    View recommView;
    @BindView(R.id.recommendTabLayout)
    TabLayout recommendTabLayout;
    @BindView(R.id.recommViewPager)
    ViewPager recommViewPager;
    Unbinder unbinder;

    private List<Fragment> fragmentList;
    private FragmentAdapter fragmentAdapter;
    private String[] titleStr = new String[]{"广场","我的"};

    @BindView(R.id.recommendBanner)
    Banner recommendBanner;

    private List<ImgBean> urlList;

    private List<String> tmpList;

    private RequestPressent requestPressent;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPressent = new RequestPressent();
        requestPressent.attach(this);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        recommView = inflater.inflate(R.layout.fragment_recommend_home_layout, container, false);
        unbinder = ButterKnife.bind(this, recommView);

        initViews();

        initBanner();


        return recommView;
    }


    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
        if(isVisible){
            String url = "http://122.51.27.32/data/banner.json";
            requestPressent.getRequestJSONObject(0x01,url,getActivity(),0);
        }
    }

    private void initBanner() {

    }

    private void showDetail(String imgUrl) {
        Intent intent = new Intent(getActivity(),BannerDetailActivity.class);
        intent.putExtra("img_url",imgUrl);
        startActivity(intent);
    }

    private void initViews() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new SquareFragment());
        fragmentList.add(new RecommendMineFragment());
        for(int i = 0;i<titleStr.length;i++){
            recommendTabLayout.addTab(recommendTabLayout.newTab(),i);
        }
        recommendTabLayout.setupWithViewPager(recommViewPager,false);

        fragmentAdapter = new FragmentAdapter(getActivity().getSupportFragmentManager(),fragmentList);
        recommViewPager.setAdapter(fragmentAdapter);
        recommViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                recommendTabLayout.getTabAt(i).setText(titleStr[i]);
                recommViewPager.setCurrentItem(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        for(int i = 0;i<titleStr.length;i++){
            recommendTabLayout.getTabAt(i).setText(titleStr[i]);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    private void showBannerView(final List<ImgBean> list){
        recommendBanner.setImageLoader(new ImgBannerHolder());
        recommendBanner.setImages(list);
        recommendBanner.start();


        recommendBanner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                showDetail(list.get(position).getDetailUrl());

            }
        });
    }



    @Override
    public void showLoadDialog(int what) {

    }

    @Override
    public void successData(int what, Object object, int daystag) {
        if(what != 0x01)
            return;
        if(object == null)
            return;
        List<ImgBean> imgBeanList = new Gson().fromJson(object.toString(),new TypeToken<List<ImgBean>>(){}.getType());
        if(imgBeanList == null)
            return;
        showBannerView(imgBeanList);
    }

    @Override
    public void failedData(int what, Throwable e) {

    }

    @Override
    public void closeLoadDialog(int what) {

    }
}

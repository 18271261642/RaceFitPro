package com.example.bozhilun.android.mvp.fragment;


import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.bozhilun.android.mvp.BaseMvpPresent;
import com.example.bozhilun.android.mvp.IBaseView;
import com.example.bozhilun.android.siswatch.LazyFragment;

/**
 * Created by Admin
 * Date 2020/5/22
 */
public abstract class BaseMvpFragment<V extends IBaseView,P extends BaseMvpPresent> extends LazyFragment implements IBaseView , IFragment,View.OnClickListener {

    protected P mPresent;
    private View rootView;
    private Dialog dialog;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       if(rootView == null)
           rootView = inflater.inflate(getLayout(),container,false);
        ViewGroup partView = (ViewGroup) rootView.getRootView();
        if(partView != null)
            partView.removeView(rootView);
        if(mPresent == null)
            mPresent = createPresenter();
        mPresent.attachView(this);
        initView();
        initData(savedInstanceState);

        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mPresent != null)
            mPresent.deachView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected abstract P createPresenter();
}

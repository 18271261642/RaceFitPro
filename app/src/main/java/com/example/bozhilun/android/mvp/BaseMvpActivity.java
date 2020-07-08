package com.example.bozhilun.android.mvp;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;

/**
 * Created by Admin
 * Date 2020/5/14
 */
public abstract class BaseMvpActivity <V extends IBaseView,P extends BaseMvpPresent> extends WatchBaseActivity implements IBaseView{

    protected P mPresenter;

    private Dialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(mPresenter == null)
            mPresenter = createPresenter();
        mPresenter.attachView((V) this);
    }

    protected abstract P createPresenter();

    @Override
    public Dialog showLoadDialog() {
        return null;
    }

    @Override
    public void cancleDialog() {

    }


}

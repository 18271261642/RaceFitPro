package com.example.bozhilun.android.mvp;

import java.lang.ref.WeakReference;

/**
 * Created by Admin
 * Date 2020/5/14
 */
public class BaseMvpPresent <V extends IBaseView> {

    protected V view;

    private WeakReference<V> weakReference;

    private V getView() {
        if(view == null)
            throw new IllegalStateException("view is not attach");
        return view;
    }


    public void attachView(V view){
        if(view != null){
            weakReference = new WeakReference<>(view);
            this.view = weakReference.get();
        }
    }

    public void deachView(){
        weakReference.clear();
        weakReference = null;
        view = null;
    }
}

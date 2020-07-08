package com.example.bozhilun.android.b30;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @Description TODO(所有Fragment基类，延迟加载)
 * @author mango
 * @Date 2018/2/23 17:49
 */
public abstract class B30BaseFragment extends Fragment {
    private String TAG = B30BaseFragment.class.getSimpleName();

    private View mRoot;

    /**
     * 是否执行了lazyLoad方法
     */
    private boolean isLoaded;
    /**
     * 是否创建了View
     */
    private boolean isCreateView;

    /**
     * 当从另一个activity回到fragment所在的activity
     * 当fragment回调onResume方法的时候，可以通过这个变量判断fragment是否可见，来决定是否要刷新数据
     */
    public boolean isVisible;

    /*
    * 此方法在viewpager嵌套fragment时会回调
    * 查看FragmentPagerAdapter源码中instantiateItem和setPrimaryItem会调用此方法
    * 在所有生命周期方法前调用
    * 这个基类适用于在viewpager嵌套少量的fragment页面
    * 该方法是第一个回调，可以将数据放在这里处理（viewpager默认会预加载一个页面）
    * 只在fragment可见时加载数据，加快响应速度
    * */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            onVisible();
        } else {
            onInvisible();
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /*
    * 防止view的重复加载 与FragmentPagerAdapter 中destroyItem方法取消调用父类的效果是一样的
    * */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(mRoot == null){
            mRoot = createView(inflater,container,savedInstanceState);
            isCreateView = true;
            initView(mRoot);
            initListener();
            onVisible();
        }
        return mRoot;
    }

    protected void onVisible() {

        isVisible = true;

        if(isLoaded){
            refreshLoad();
        }
        if (!isLoaded && isCreateView && getUserVisibleHint()) {
            isLoaded = true;
            lazyLoad();
        }
    }

    protected void onInvisible() {
        isVisible = false;
    }

    protected abstract View createView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState);
    protected abstract void initView(View root);
    protected abstract void initListener();

    /**
     * fragment第一次可见的时候回调此方法
     */
    protected abstract void lazyLoad();

    /**
     * 去除setUserVisibleHint()多余的回调场景，保证只有当fragment可见状态发生变化时才回调
     * 回调时机在view创建完后，所以支持ui操作，解决在setUserVisibleHint()里进行ui操作有可能报null异常的问题
     *
     * 可在该回调方法里进行一些ui显示与隐藏，比如加载框的显示和隐藏
     *
     * @param isVisible true  不可见 -> 可见
     *                  false 可见  -> 不可见
     */
    protected void onFragmentVisibleChange(boolean isVisible) {

    }

    /**
     * 在Fragment第一次可见加载以后，每次Fragment滑动可见的时候会回调这个方法，
     * 子类可以重写这个方法做数据刷新操作
     */
    protected void refreshLoad(){}

}
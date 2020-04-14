package com.example.bozhilun.android.util;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.activity.wylactivity.wyl_util.ScreenShot;

import java.io.File;
import java.util.HashMap;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.google.GooglePlus;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

import static com.mob.tools.utils.Strings.getString;

public class ShareSDKUtils {

    private static final String TAG = "ShareSDKUtils";
    private static LoginListenter mLoginListenter = null;
    private static ShareSDKUtils mInstance;


    public static ShareSDKUtils getInstance(LoginListenter mLoginListenter) {

        if (null == mInstance) {
            synchronized (ShareSDKUtils.class) {
                if (null == mInstance) {
                    mInstance = new ShareSDKUtils(mLoginListenter);
                }
            }
        }
        return mInstance;
    }


    private ShareSDKUtils(LoginListenter mLoginListenter) {
        ShareSDKUtils.mLoginListenter = mLoginListenter;
    }


    /**
     * 登陆
     */
    public void loginShow(String AppName) {
        Platform platform = ShareSDK.getPlatform(AppName);
        Log.e(TAG, "---------platform=" + (platform == null ? "platform=null" : platform.toString()));
        if (platform == null) return;
        if (platform.isAuthValid()) {
            Log.e(TAG, "---------platform=" + "已经授权，删除授权");
            //判断是否已经存在授权状态，可以根据自己的登录逻辑设置
            platform.removeAccount(true);//如果授权就删除授权资料
        }
        platform.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Log.e(TAG, "---------platform=" + "已经授权成功：" + (platform == null ? "platform=null" : platform.toString()));
                mLoginListenter.OnLoginListenter(platform, hashMap);
                return;
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Log.e(TAG, "---------platform=" + "已经授权失败：" + (platform == null ? "platform=null" : platform.toString()));
                mLoginListenter.onError(platform, i, throwable);
                return;
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Log.e(TAG, "---------platform=" + "已经授权取消：" + (platform == null ? "platform=null" : platform.toString()));
                mLoginListenter.onCancel(platform, i);
                return;
            }
        });
        //　SSO是一种统一认证和授权机制，指访问同一服务器不同应用中的受保护资源的同一用户，
        // 只需要登录一次，即通过一个应用中的安全验证后，再访问其他应用中的受保护资源时，不再需要重新登录验证。
        //使用SSO授权。有客户端的都会优先启用客户端授权，没客户端的则任然使用网页版进行授权  com.bzl.map.bzlsharedsdk
//        if (AppName.equals(SinaWeibo.NAME)) {
//            platform.SSOSetting(true); // true表示不使用SSO方式授权
//            authorize(platform);
//            return;
//        }
        if (platform.isClientValid()) {
            platform.SSOSetting(false); // true表示不使用SSO方式授权
        } else {
            platform.SSOSetting(true); // true表示不使用SSO方式授权
        }
        authorize(platform);
    }

    /**
     * 授权
     *
     * @param platform
     */
    private void authorize(Platform platform) {
        if (platform == null) {
            return;
        }
        //platform.authorize();	//要功能，不要数据
        // 参数null表示获取当前授权用户资料
        platform.showUser(null); //授权并获取用户信息
    }


    /**
     * onekeyshare分享调用九宫格方法-----快捷分享
     */
    public static void showShare(Context context, String imgurl) {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("分享标题");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath(imgurl);//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(context);
    }


    /**
     * 自定义分享 ---- 需要自定义分享界面
     *
     * @param activity
     * @param AppName     分享的平台
     * @param share_title 分享标题
     * @param share_text  分享内容
     */
    public static void SharedType(Activity activity, String AppName, String share_title, String share_text) {
        Platform platform = ShareSDK.getPlatform(AppName);
        if (platform == null) return;
        if (platform.isClientValid()) {// 如果有新浪微博客户端，每次都可以重新选择或添加分享账号
            String filePath = Environment.getExternalStorageDirectory() + "/DCIM/" + System.currentTimeMillis() + ".png";
            ScreenShot.shoot(activity, new File(filePath));

            Platform.ShareParams sp = new Platform.ShareParams();
            sp.setShareType(Platform.SHARE_WEBPAGE);// 一定要设置分享属性
            sp.setTitle("RaceFitPro");
            sp.setTitleUrl("https://mall.jd.com/index-173284.html");
//            if (!TextUtils.isEmpty(share_title)) sp.setTitle(share_title);
//            if (!TextUtils.isEmpty(share_text)) sp.setText(share_text);
//            if (!TextUtils.isEmpty(filePath)) sp.setImageUrl(filePath);
            if (!TextUtils.isEmpty(filePath)) sp.setImagePath(filePath);
            sp.setImagePath(null);
            // 设置分享事件回调
            platform.setPlatformActionListener(new PlatformActionListener() {
                @Override
                public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                    mLoginListenter.OnLoginListenter(platform, hashMap);
                }

                @Override
                public void onError(Platform platform, int i, Throwable throwable) {
                    mLoginListenter.onError(platform, i, throwable);
                }

                @Override
                public void onCancel(Platform platform, int i) {
                    mLoginListenter.onCancel(platform, i);
                }
            });
            // 执行图文分享
            platform.share(sp);

        } else {
            ToastUtil.showShort(MyApp.getInstance(), "未安装" + AppName);
        }
    }

}

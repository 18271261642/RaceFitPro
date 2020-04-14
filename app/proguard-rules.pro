## Add project specific ProGuard rules here.
## By default, the flags in this file are appended to flags specified
## in C:/Users/alno/AppData/Local/Android/sdk/tools/proguard/proguard-android.txt
## You can edit the include path and order by changing the proguardFiles
## directive in build.gradle.
##
## For more details, see
##   http://developer.android.com/guide/developing/tools/proguard.html
#
## Add any project specific keep options here:
#
## If your project uses WebView with JS, uncomment the following
## and specify the fully qualified class name to the JavaScript interface
## class:
##-keepclassmembers class fqcn.of.javascript.interface.for.webview {
##   public *;
##}
#
#
#-------------------------------------------基本不用动区域----------------------------------------------
#
#
# -----------------------------基本 -----------------------------
#

# 指定代码的压缩级别 0 - 7(指定代码进行迭代优化的次数，在Android里面默认是5，这条指令也只有在可以优化时起作用。)
-optimizationpasses 5
# 混淆时不会产生形形色色的类名(混淆时不使用大小写混合类名)
-dontusemixedcaseclassnames
# 指定不去忽略非公共的库类(不跳过library中的非public的类)
-dontskipnonpubliclibraryclasses
# 指定不去忽略包可见的库类的成员
-dontskipnonpubliclibraryclassmembers
#不进行优化，建议使用此选项，
-dontoptimize
 # 不进行预校验,Android不需要,可加快混淆速度。
-dontpreverify
# 屏蔽警告
-ignorewarnings
# 指定混淆是采用的算法，后面的参数是一个过滤器
# 这个过滤器是谷歌推荐的算法，一般不做更改
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
# 保护代码中的Annotation不被混淆
-keepattributes *Annotation*
# 避免混淆泛型, 这在JSON实体映射时非常重要
-keepattributes Signature
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
 #优化时允许访问并修改有修饰符的类和类的成员，这可以提高优化步骤的结果。
# 比如，当内联一个公共的getter方法时，这也可能需要外地公共访问。
# 虽然java二进制规范不需要这个，要不然有的虚拟机处理这些代码会有问题。当有优化和使用-repackageclasses时才适用。
#指示语：不能用这个指令处理库中的代码，因为有的类和类成员没有设计成public ,而在api中可能变成public
-allowaccessmodification
#当有优化和使用-repackageclasses时才适用。
-repackageclasses ''
 # 混淆时记录日志(打印混淆的详细信息)
 # 这句话能够使我们的项目混淆后产生映射文件
 # 包含有类名->混淆后类名的映射关系
-verbose

#
# ----------------------------- 默认保留 -----------------------------
#
#----------------------------------------------------
# 保持哪些类不被混淆
#继承activity,application,service,broadcastReceiver,contentprovider....不进行混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.support.multidex.MultiDexApplication
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep class android.support.** {*;}## 保留support下的所有类及其内部类

-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService
#表示不混淆上面声明的类，最后这两个类我们基本也用不上，是接入Google原生的一些服务时使用的。
#----------------------------------------------------

# 保留继承的
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**


#表示不混淆任何包含native方法的类的类名以及native方法名，这个和我们刚才验证的结果是一致
-keepclasseswithmembernames class * {
native <methods>;
}


#这个主要是在layout 中写的onclick方法android:onclick="onClick"，不进行混淆
#表示不混淆Activity中参数是View的方法，因为有这样一种用法，在XML中配置android:onClick=”buttonClick”属性，
#当用户点击该按钮时就会调用Activity中的buttonClick(View view)方法，如果这个方法被混淆的话就找不到了
-keepclassmembers class * extends android.app.Activity{
public void *(android.view.View);
}

#表示不混淆枚举中的values()和valueOf()方法，枚举我用的非常少，这个就不评论了
-keepclassmembers enum * {
public static **[] values();
public static ** valueOf(java.lang.String);
}

#表示不混淆任何一个View中的setXxx()和getXxx()方法，
#因为属性动画需要有相应的setter和getter的方法实现，混淆了就无法工作了。
-keep public class * extends android.view.View{
*** get*();
void set*(***);
public <init>(android.content.Context);
public <init>(android.content.Context, android.util.AttributeSet);
public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
public <init>(android.content.Context, android.util.AttributeSet);
public <init>(android.content.Context, android.util.AttributeSet, int);
}

#表示不混淆Parcelable实现类中的CREATOR字段，
#毫无疑问，CREATOR字段是绝对不能改变的，包括大小写都不能变，不然整个Parcelable工作机制都会失败。
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
# 这指定了继承Serizalizable的类的如下成员不被移除混淆
-keepclassmembers class * implements java.io.Serializable {
static final long serialVersionUID;
private static final java.io.ObjectStreamField[] serialPersistentFields;
private void writeObject(java.io.ObjectOutputStream);
private void readObject(java.io.ObjectInputStream);
java.lang.Object writeReplace();
java.lang.Object readResolve();
}
# 保留R下面的资源
#-keep class **.R$* {
# *;
#}
#不混淆资源类下static的
-keepclassmembers class **.R$* {
public static <fields>;
}

# 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
-keepclassmembers class * {
void *(**On*Event);
void *(**On*Listener);
}

# 保留我们自定义控件（继承自View）不被混淆
-keep public class * extends android.view.View{
*** get*();
void set*(***);
public <init>(android.content.Context);
public <init>(android.content.Context, android.util.AttributeSet);
public <init>(android.content.Context, android.util.AttributeSet, int);
}

#
#----------------------------- WebView(项目中没有可以忽略) -----------------------------
#
#webView需要进行特殊处理
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
   public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
public void *(android.webkit.WebView, jav.lang.String);
}
#在app中与HTML5的JavaScript的交互进行特殊处理
#我们需要确保这些js要调用的原生方法不能够被混淆，于是我们需要做如下处理：
-keepclassmembers class com.ljd.example.JSInterface {
<methods>;
}

#
#---------------------------------实体类---------------------------------
#--------(实体Model不能混淆，否则找不到对应的属性获取不到值)-----
#


#androidx包使用混淆
-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**



-dontwarn com.example.bozhilun.android.b18.modle.**
#对含有反射类的处理
-keep class com.example.bozhilun.android.b18.modle.** { *; }



-dontwarn com.example.bozhilun.android.b31.bpoxy.uploadSpo2.**
#对含有反射类的处理
-keep class com.example.bozhilun.android.b31.bpoxy.uploadSpo2.** { *; }


-dontwarn com.example.bozhilun.android.w30s.ble.**
#对含有反射类的处理
-keep class com.example.bozhilun.android.w30s.ble.** { *; }



-dontwarn com.example.bozhilun.android.bean.**
#对含有反射类的处理
-keep class com.example.bozhilun.android.bean.** { *; }


-dontwarn com.example.bozhilun.android.bean.AreCodeBean**
#对含有反射类的处理
-keep class com.example.bozhilun.android.bean.AreCodeBean** { *; }


-dontwarn com.example.bozhilun.android.friend.bean.**
#对含有反射类的处理
-keep class com.example.bozhilun.android.friend.bean.** { *; }

-dontwarn com.example.bozhilun.android.h9.bean.**
#对含有反射类的处理
-keep class com.example.bozhilun.android.h9.bean.** { *; }

-dontwarn com.example.bozhilun.android.siswatch.bean.**
#对含有反射类的处理
-keep class com.example.bozhilun.android.siswatch.bean.** { *; }

-dontwarn com.example.bozhilun.android.w30s.bean.**
#对含有反射类的处理
-keep class com.example.bozhilun.android.w30s.bean.** { *; }

-dontwarn com.example.bozhilun.android.B18I.b18ibean.**
#对含有反射类的处理
-keep class com.example.bozhilun.android.B18I.b18ibean.** { *; }

-dontwarn com.example.bozhilun.android.b30.bean.**
#对含有反射类的处理
-keep class com.example.bozhilun.android.b30.bean.** { *; }
-keep public class com.example.bozhilun.android.b30.bean.**


-dontwarn com.example.bozhilun.android.bzlmaps.mapdb.**
#对含有反射类的处理
-keep class com.example.bozhilun.android.bzlmaps.mapdb.** { *; }

-dontwarn cn.appscomm.ota.**
#对含有反射类的处理
-keep class cn.appscomm.ota.** { *; }

#数据库
-dontwarn com.example.bozhilun.android.commdbserver.**
#对含有反射类的处理
-keep class com.example.bozhilun.android.commdbserver.** { *; }


#数据库
-dontwarn com.example.bozhilun.android.w30s.activity.W30DetailSportActivity.W30StepDetailBean.**
#对含有反射类的处理
-keep class com.example.bozhilun.android.w30s.activity.W30DetailSportActivity.W30StepDetailBean.** { *; }



#数据库
-dontwarn com.example.bozhilun.android.siswatch.mine.**
#对含有反射类的处理
-keep class com.example.bozhilun.android.siswatch.mine.** { *; }


-dontwarn com.example.bozhilun.android.friend.**
#对含有反射类的处理
-keep class com.example.bozhilun.android.friend.** { *; }

#数据库
-dontwarn com.example.bozhilun.android.w30s.bean.W30HeartBean.**
#对含有反射类的处理
-keep class com.example.bozhilun.android.w30s.bean.W30HeartBean.** { *; }



#-dontwarn com.example.bozhilun.android.friend.**
##对含有反射类的处理
#-keep class com.example.bozhilun.android.friend.** { *; }


-dontwarn com.amap.api.maps.model.**
-keep class com.amap.api.maps.model.** { *; }



-dontwarn com.example.bozhilun.android.activity.wylactivity.wyl_util.**
#对含有反射类的处理
-keep class com.example.bozhilun.android.activity.wylactivity.wyl_util.** { *; }

-dontwarn com.tencent.**
#对含有反射类的处理
-keep class com.tencent.** { *; }

-dontwarn cn.appscomm.bluetooth.**
#对含有反射类的处理
-keep class cn.appscomm.bluetooth.** { *; }


-dontwarn cn.smssdk.**
#对含有反射类的处理
-keep class cn.smssdk.** { *; }


-dontwarn com.sdk.bluetooth.**
#对含有反射类的处理
-keep class com.sdk.bluetooth.** { *; }


-dontwarn freemarker.**
#对含有反射类的处理
-keep class freemarker.** { *; }

-dontwarn com.roughike.bottombar.**
#对含有反射类的处理
-keep class com.roughike.bottombar.** { *; }

-dontwarn com.github.mikephil.charting.**
#对含有反射类的处理
-keep class com.github.mikephil.charting.** { *; }

-dontwarn cn.sharesdk.framework.**
#对含有反射类的处理
-keep class cn.sharesdk.framework.** { *; }

-dontwarn com.suchengkeji.android.w30sblelibrary.bean.**
#对含有反射类的处理
-keep class com.suchengkeji.android.w30sblelibrary.bean.** { *; }



#-dontwarn com.veepoo.protoco.model.**
##对含有反射类的处理
#-keep class com.veepoo.protocol.model.** { *; }
#
#-dontwarn com.veepoo.protocol.view.**
##对含有反射类的处理
#-keep class com.veepoo.protocol.view.** { *; }
#
#-dontwarn com.veepoo.protocol.model.datas.**
##对含有反射类的处理
#-keep class com.veepoo.protocol.model.datas.** { *; }
#
#-dontwarn com.veepoo.protocol.util.**
##对含有反射类的处理
#-keep class com.veepoo.protocol.util.** { *; }
#
#-dontwarn com.inuker.bluetooth.library.model.**
##对含有反射类的处理
#-keep class com.inuker.bluetooth.library.model.** { *; }



##自定义维亿魄的对接数据源
-dontwarn com.example.bozhilun.android.b30.model.**
##对含有反射类的处理
-keep class com.example.bozhilun.android.b30.model.** { *; }


-dontwarn com.veepoo.protocol.model.datas.**
##对含有反射类的处理
-keep class com.veepoo.protocol.model.datas.** { *; }


-dontwarn com.veepoo.protocol.model.**
##对含有反射类的处理
-keep class com.veepoo.protocol.model.** { *; }


-dontwarn com.veepoo.protocol.**
#对含有反射类的处理
-keep class com.veepoo.protocol.** { *; }

-dontwarn vpno.nordicsemi.android.**
#对含有反射类的处理
-keep class vpno.nordicsemi.android.** { *; }

-dontwarn com.inuker.bluetooth.library.**
#对含有反射类的处理
-keep class com.inuker.bluetooth.library.** { *; }

-dontwarn cn.appscomm.bluetooth.model.**
#对含有反射类的处理
-keep class cn.appscomm.bluetooth.model.** { *; }

-dontwarn org.apache.commons.lang.enum.**
#对含有反射类的处理
-keep class org.apache.commons.lang.enum.** { *; }

-dontwarn org.apache.commons.lang.enums.**
#对含有反射类的处理
-keep class org.apache.commons.lang.enums.** { *; }

-dontwarn lecho.lib.hellocharts.model.**
#对含有反射类的处理
-keep class lecho.lib.hellocharts.model.** { *; }

-dontwarn cn.appscomm.ota.mode.**
#对含有反射类的处理
-keep class cn.appscomm.ota.mode.** { *; }

-dontwarn org.mockito.cglib.**
#对含有反射类的处理
-keep class org.mockito.cglib.** { *; }

-dontwarn org.hamcrest.beans.**
#对含有反射类的处理
-keep class org.hamcrest.beans.** { *; }

-dontwarn com.nightonke.boommenu.**
#对含有反射类的处理
-keep class com.nightonke.boommenu.** { *; }

-dontwarn com.yalantis.ucrop.model.**
#对含有反射类的处理
-keep class com.yalantis.ucrop.model.** { *; }

-dontwarn com.sdk.bluetooth.bean.**
#对含有反射类的处理
-keep class com.sdk.bluetooth.bean.** { *; }

-keep class com.android.internal.telephony.ITelephony { *; }

-dontwarn com.example.bozhilun.android.b31.model.**
#对含有反射类的处理
-keep class com.example.bozhilun.android.b31.model.** { *; }

-dontwarn com.example.bozhilun.android.b15p.b15pdb.**
#对含有反射类的处理
-keep class com.example.bozhilun.android.b15p.b15pdb.** { *; }


-dontwarn vpno.nordicsemi.android.**
#对含有反射类的处理
-keep class vpno.nordicsemi.android.** { *; }

-dontwarn com.example.bozhilun.android.b15p.b15pdb.**
#对含有反射类的处理
-keep class com.example.bozhilun.android.b15p.b15pdb.** { *; }


#//不混淆某个类的内部类
-keep class com.example.bozhilun.android.activity.MyPersonalActivity$* {
        *;
}
 #//不混淆某个类的内部类
-keep class com.example.bozhilun.android.b30.b30datafragment.B30DataFragment$* {
         *;
}
#Aria
-dontwarn com.arialyy.aria.**
-keep class com.arialyy.aria.**{*;}
-keep class **$$DownloadListenerProxy{ *; }
-keep class **$$UploadListenerProxy{ *; }
-keep class **$$DownloadGroupListenerProxy{ *; }
-keepclasseswithmembernames class * {
@Download.* <methods>;
@Upload.* <methods>;
@DownloadGroup.* <methods>;
}



##
## ----------------------------- 其他的 -----------------------------
##
## 删除代码中Log相关的代码
-assumenosideeffects class android.util.Log {
public static boolean isLoggable(java.lang.String, int);
public static int v(...);
public static int i(...);
public static int w(...);
public static int d(...);
public static int e(...);
}


#
# ----------------------------- 第三方 -----------------------------
#


#Gson
# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }
-keep class com.google.gson.** { *;}

#Butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
   @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
   @butterknife.* <methods>;
}

#ShareSDK
-keep class cn.sharesdk.**{*;}
-keep class com.sina.**{*;}
-keep class **.R$* {*;}
-keep class **.R{*;}
-dontwarn cn.sharesdk.**
-dontwarn **.R$*
-dontwarn com.tencent.**
-keep class com.tencent.** {*;}

#RxJava+RXAndroid

-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
long producerIndex;
long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
-dontnote rx.internal.util.PlatformDependent
#RxJava
# rx
-dontwarn rx.**
-keepclassmembers class rx.** { *; }
# retrolambda
-dontwarn java.lang.invoke.*


#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
#下面这句不加
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule


-keep class com.mob.**{*;}
-keep class cn.smssdk.**{*;}
-dontwarn com.mob.**
-dontwarn cn.smssdk.**
# 友盟统计
-keepclassmembers class * {
public <init> (org.json.JSONObject);
}

# 友盟统计5.0.0以上SDK需要
-keepclassmembers enum * {
public static **[] values();
public static ** valueOf(java.lang.String);
}

# 友盟统计R.java删除问题
-keep public class com.gdhbgh.activity.R$*{
public static final int *;
}


#fastjson混淆
-keepattributes Signature
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.**{*;}
-keep class com.alibaba.fastjson.**{*; }
-keep public class com.ninstarscf.ld.model.entity.**{*;}

#greendao3.2.0
-dontwarn org.greenrobot.greendao.**
#对含有反射类的处理
-keep class org.greenrobot.greendao.** { *; }
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties



#eventbus 3.0
-keepclassmembers class ** {
@org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
<init>(java.lang.Throwable);
}


#EventBus
-keepclassmembers class ** {
public void onEvent*(**);
}
-keepclassmembers class ** {
public void xxxxxx(**);
}


# support-v4
#https://stackoverflow.com/questions/18978706/obfuscate-android-support-v7-widget-gridlayout-issue
-dontwarn android.support.v4.**
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v4.** { *; }


# support-v7
-dontwarn android.support.v7.**
-keep class android.support.v7.internal.** { *; }
-keep interface android.support.v7.internal.** { *; }
-keep class android.support.v7.** { *; }

# support design
#@link http://stackoverflow.com/a/31028536
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }



#高徳地图
-dontwarn  com.amap.api.**
#对含有反射类的处理
-keep class com.amap.api.** { *; }

-dontwarn  com.autonavi.**
#对含有反射类的处理
-keep class com.autonavi.** { *; }


-dontwarn pl.droidsonroids.gif.**
#对含有反射类的处理
-keep class pl.droidsonroids.gif.** { *; }

-dontwarn  com.google.android.**
#对含有反射类的处理
-keep class com.google.android.** { *; }

#litepal
-dontwarn org.litepal.*
-keep class org.litepal.** { *; }
-keep enum org.litepal.**
-keep interface org.litepal.** { *; }
-keep public class * extends org.litepal.**
-keepattributes *Annotation*
-keepclassmembers enum * {
public static **[] values();
public static ** valueOf(java.lang.String);
}

-keepclassmembers class * extends org.litepal.crud.DataSupport{
   private * ;
}

-dontwarn  okio.**
#对含有反射类的处理
-keep class okio.** { *; }
# Okio
-dontwarn com.squareup.**
-dontwarn okio.**
-keep public class org.codehaus.* { *; }
-keep public class java.nio.* { *; }
# Retrolambda
-dontwarn java.lang.invoke.*

-dontwarn okhttp3.**
#对含有反射类的处理
-keep class okhttp3.** { *; }

## okhttp
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.{*;}
#retrofit
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-dontwarn okio.**


-keepattributes EnclosingMethod



-dontwarn com.orhanobut.logger.**
-keep class com.orhanobut.logger.**{*;}
-keep interface com.orhanobut.logger.**{*;}

-dontwarn com.google.gson.**
-keep class com.google.gson.**{*;}
-keep interface com.google.gson.**{*;}


#腾讯bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

#        。。。。。。

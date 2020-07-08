package com.example.bozhilun.android.friend;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;
import com.example.bozhilun.android.util.URLs;
import com.example.bozhilun.android.w30s.utils.httputils.RequestPressent;
import com.example.bozhilun.android.w30s.utils.httputils.RequestView;
import com.google.zxing.WriterException;
import com.yzq.zxinglibrary.encode.CodeCreator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ErCodeActity extends WatchBaseActivity implements RequestView, View.OnClickListener {

    @BindView(R.id.image_er)
    ImageView imageEr;
    @BindView(R.id.bar_titles)
    TextView barTitles;
    @BindView(R.id.toolbar_normal)
    Toolbar mNormalToolbar;
    private RequestPressent requestPressent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.er_code_activity);
        ButterKnife.bind(this);


        //设置标题
        barTitles.setText(getResources().getString(R.string.string_qr_code));
        mNormalToolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.backs));//设置返回按钮
        mNormalToolbar.setNavigationOnClickListener(this);//右边返回按钮点击事件

        requestPressent = new RequestPressent();
        requestPressent.attach(this);

        getUserData();
    }


    private void getUserData() {
        String url = URLs.HTTPs + URLs.getUserInfo; //查询用户信息
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("userId", SharedPreferencesUtils.readObject(this, "userId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestPressent.getRequestJSONObject(0x01, url, MyApp.getContext(), jsonObj.toString(), 1);
    }


    //请求网络图片
    public Bitmap returnBitMap(String url) {
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private void initErCode(String phone, String userId) {
//        String contentEtString = contentEt.getText().toString().trim();
        String contentEtString = phone;
        if (WatchUtils.isEmpty(phone) || phone.equals("bozlun888@gmail.com")) {
            contentEtString = userId;
        }

        if (TextUtils.isEmpty(contentEtString)) {
            Toast.makeText(this, getResources().getString(R.string.string_network_error), Toast.LENGTH_SHORT).show();
            return;
        }
        Bitmap bitmap = null;
        try {
            /*
             * contentEtString：字符串内容
             * w：图片的宽
             * h：图片的高
             * logo：不需要logo的话直接传 0
             * */
            Bitmap logo = BitmapFactory.decodeResource(getResources(), 0);
            bitmap = CodeCreator.createQRCode(contentEtString, 400, 400, logo);
            imageEr.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }

    /**
     * @param what
     */
    @Override
    public void showLoadDialog(int what) {
        showLoadingDialog(getResources().getString(R.string.dlog));
    }

    @Override
    public void successData(int what, Object object, int daystag) {
        closeLoadingDialog();
        if (!WatchUtils.isEmpty(object.toString())) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(object.toString());
                if (jsonObject.getString("resultCode").equals("001")) {
                    JSONObject myInfoJsonObject = jsonObject.getJSONObject("userInfo");
                    if (myInfoJsonObject != null) {
                        String nickName = myInfoJsonObject.getString("nickName");
                        String imgHead = myInfoJsonObject.getString("image");
                        String phone = myInfoJsonObject.getString("phone");
                        String userId = myInfoJsonObject.getString("userId");

                        initErCode(phone, userId);
//                        if (!WatchUtils.isEmpty(imgHead)) {
//                            RequestOptions mRequestOptions = RequestOptions.circleCropTransform().diskCacheStrategy(DiskCacheStrategy.NONE)
//                                    .skipMemoryCache(true);
//                            //头像
//                            Glide.with(getActivity()).load(imgHead).apply(mRequestOptions).into(b30UserImageHead);    //头像
//                        }

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void failedData(int what, Throwable e) {
        closeLoadingDialog();
    }

    @Override
    public void closeLoadDialog(int what) {
        closeLoadingDialog();
    }

    @Override
    public void onClick(View view) {
        finish();
    }
}

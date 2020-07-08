package com.example.bozhilun.android.b36;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.bozhilun.android.R;
import com.example.bozhilun.android.imagepicker.PickerBuilder;
import com.example.bozhilun.android.siswatch.WatchBaseActivity;
import com.example.bozhilun.android.siswatch.utils.FileOperUtils;
import com.example.bozhilun.android.util.ImageTool;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.MenuSheetView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * B36自定义主题
 * Created by Admin
 * Date 2019/7/30
 */
public class CustomHomeThemeActivity extends WatchBaseActivity {

    private static final String TAG = "CustomHomeThemeActivity";


    @BindView(R.id.cusThemeBottom)
    BottomSheetLayout cusThemeBottom;
    @BindView(R.id.showCustomImgView)
    ImageView showCustomImgView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_theme);
        ButterKnife.bind(this);

        initViews();

    }

    private void initViews() {


    }


    @OnClick(R.id.cusThemeChoosePicBtn)
    public void onClick() {
        choosePick();
    }

    //选择图片或者拍照
    private void choosePick() {
        MenuSheetView menuSheetView =
                new MenuSheetView(CustomHomeThemeActivity.this, MenuSheetView.MenuType.LIST, R.string.select_photo, new MenuSheetView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (cusThemeBottom.isSheetShowing()) {
                            cusThemeBottom.dismissSheet();
                        }
                        switch (item.getItemId()) {
                            case R.id.take_camera:
                                //cameraPic();
                                break;
                            case R.id.take_Album:   //相册
                                getImage(PickerBuilder.SELECT_FROM_GALLERY);
                                break;
                            case R.id.cancle:
                                break;
                        }
                        return true;
                    }
                });
        menuSheetView.inflateMenu(R.menu.menu_takepictures);
        cusThemeBottom.showWithSheetView(menuSheetView);
    }


    private void getImage(int types) {
        new PickerBuilder(CustomHomeThemeActivity.this, types)
                .setOnImageReceivedListener(new PickerBuilder.onImageReceivedListener() {
                    @Override
                    public void onImageReceived(Uri imageUri) {
                        //设置头像
                        String imgFilePath = ImageTool.getRealFilePath(CustomHomeThemeActivity.this, imageUri);
                        Log.e(TAG,"---11----filePath="+imgFilePath);


                        Bitmap bitmap = FileOperUtils.compressBySize(imgFilePath,300,300);
                       // showCustomImgView.setImageBitmap(bitmap);


                        try {
                           File saveFile =  FileOperUtils.saveFiles(bitmap,imgFilePath);


                            Log.e(TAG,"---22----saveFile="+saveFile.length());


                            String flie2 = saveFile.getAbsolutePath();

                            Bitmap bitmap2 = BitmapFactory.decodeFile(flie2);
                            showCustomImgView.setImageBitmap(bitmap2);

//                            RequestOptions mRequestOptions = RequestOptions.circleCropTransform().diskCacheStrategy(DiskCacheStrategy.NONE)
//                                    .skipMemoryCache(true);
//                            Glide.with(CustomHomeThemeActivity.this).
//                                    load(flie2).apply(mRequestOptions).into(showCustomImgView);

                            try {
                                byte[] fileByte = readStream(flie2);

                                Log.e(TAG,"-------长度="+fileByte.length);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        //ploadPic(ImageTool.getRealFilePath(CustomHomeThemeActivity.this, imageUri), 1);
//
                    }
                })
                .setImageName("b36HeadImg")
                .setImageFolderName("CusB36ThemeFolder")
                .setCropScreenColor(Color.CYAN)
                .setOnPermissionRefusedListener(new PickerBuilder.onPermissionRefusedListener() {
                    @Override
                    public void onPermissionRefused() {
                    }
                })
                .start();
    }


    /**
     * 文件路径转二进制
     * @param imagepath
     * @return
     * @throws Exception
     */
    public static byte[] readStream(String imagepath) throws Exception {
        FileInputStream fs = new FileInputStream(imagepath);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 10];
        int len = 0;
        while (-1 != (len = fs.read(buffer))) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        fs.close();
        return outStream.toByteArray();
    }

    // 二进制转字符串
    public static String byte2hex(byte[] b)
    {
        StringBuffer sb = new StringBuffer();
        String tmp = "";
        for (int i = 0; i < b.length; i++) {
            tmp = Integer.toHexString(b[i] & 0XFF);
            if (tmp.length() == 1){
                sb.append("0" + tmp);
            }else{
                sb.append(tmp);
            }

        }
        return sb.toString();
    }


}

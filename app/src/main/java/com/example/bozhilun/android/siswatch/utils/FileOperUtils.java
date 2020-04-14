package com.example.bozhilun.android.siswatch.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2018/1/24.
 */

public class FileOperUtils {

    /**
     * 删除指定文件夹或里面的内容
     *
     * @param filePath
     */
    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        deleteFile(file);
    }

    public static void deleteFile(File file) {
        // TODO Auto-generated method stub
        if (!file.exists()) {
            return;
        } else {
            if (file.isFile()) {
                file.delete();
                return;
            }
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                DeleteFile(f);
            }
            file.delete();
        }
    }

    private static void DeleteFile(File f) {
        // TODO Auto-generated method stub
        f.delete();
    }

    //删除文件夹下的所有文件
    public static void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    }

    // 保存文件至SD卡中
    public static File saveFiles(Bitmap bitmap, String filePath2) throws IOException {
        if (!WatchUtils.isEmpty(filePath2)) {
            File testFile = new File(filePath2);
            if (testFile.exists()) {
                testFile.delete();
            }

            File myCaptureFile = new File(filePath2);
            System.out.println("------filePath2==" + filePath2);
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(myCaptureFile));
            // 100表示不进行压缩，70表示压缩率为30%
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
            System.out.println("------filePath2--length==" + myCaptureFile.length());

            bos.flush();
            bos.close();
            return myCaptureFile;
        }
        return null;

    }

    // 压缩图片尺寸
    public static Bitmap compressBySize(String pathName, int targetWidth,
                                        int targetHeight) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;// 不去真的解析图片，只是获取图片的头部信息，包含宽高等；
        Bitmap bitmap = BitmapFactory.decodeFile(pathName, opts);
        // 得到图片的宽度、高度；
        float imgWidth = opts.outWidth;
        float imgHeight = opts.outHeight;

        Log.e("File","---------imgWidth="+imgWidth+"---height="+imgHeight);

        // 分别计算图片宽度、高度与目标宽度、高度的比例；取大于等于该比例的最小整数；
        int widthRatio = (int) Math.ceil(imgWidth / (float) targetWidth);
        int heightRatio = (int) Math.ceil(imgHeight / (float) targetHeight);
        opts.inSampleSize = 1;
        if (widthRatio > 1 || widthRatio > 1) {
            if (widthRatio > heightRatio) {
                opts.inSampleSize = widthRatio;
            } else {
                opts.inSampleSize = heightRatio;
            }
        }
        // 设置好缩放比例后，加载图片进内容；
        opts.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(pathName, opts);
        return bitmap;
    }
}

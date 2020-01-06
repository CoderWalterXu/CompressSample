package com.xlh.compresslibrary.netease.utils;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author: Watler Xu
 * time:2020/1/6
 * description:
 * version:
 */
public class CachePathUtils {

    /**
     * 独立创建拍照路径
     *
     * @param fileName 图片名
     * @return 缓存文件夹路径
     */
    private static File getCameraCacheDir(String fileName) {
        File cache = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!cache.mkdirs() && (!cache.exists() || !cache.isDirectory())) {
            return null;
        } else {
            return new File(cache, fileName);
        }
    }


    /**
     * 获取图片文件名
     *
     * @return 图片文件名
     */
    private static String getBaseFileName() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
    }

    /**
     * 获取拍照缓存文件
     *
     * @return 缓存文件，例如(camera_20200105_091206.jpg)
     */
    public static File getCameraCacheFile() {
        String fileName = "camera_" + getBaseFileName() + ".jpg";
        return getCameraCacheDir(fileName);
    }

}

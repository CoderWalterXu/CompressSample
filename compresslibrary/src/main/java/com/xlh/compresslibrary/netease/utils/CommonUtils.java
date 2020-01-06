package com.xlh.compresslibrary.netease.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

/**
 * @author: Watler Xu
 * time:2020/1/6
 * description:
 * version:
 */
public class CommonUtils {

    /**
     * 许多定制的Android系统，并不带相机功能，如果强行调用，程序会崩溃
     *
     * @param activity    上下文
     * @param intent      相机意图
     * @param requestCode 回调标识码
     */
    public static void hasCamera(Activity activity, Intent intent, int requestCode) {
        if (activity == null) {
            throw new IllegalArgumentException("activity为空");
        }
        PackageManager pm = activity.getPackageManager();
        boolean hasCamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
                || pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)
                || Camera.getNumberOfCameras() > 0;
        if (hasCamera) {
            activity.startActivityForResult(intent, requestCode);
        } else {
            Toast.makeText(activity, "当前设备没有相机", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取拍照的Intent
     *
     * @param outPutUri 拍照后图片的输出Uri
     * @return 返回Intent, 方便封装跳转
     */
    public static Intent getCameraIntent(Uri outPutUri) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // 设置Action为拍照
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        // 将拍照的照片保存到指定Uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri);
        return intent;
    }

    /**
     * 跳转到图库选择
     *
     * @param activity    上下文
     * @param requestCode 回调码
     */
    public static void openAlbum(Activity activity, int requestCode) {
        // 调用图库，获取所有本地图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 显示圆形进度对话框
     *
     * @param activity      上下文
     * @param progressTitle 显示的标题
     * @return ProgressDialog
     */
    public static ProgressDialog showProgressDialog(Activity activity, String... progressTitle) {
        if (activity == null || activity.isFinishing()) {
            return null;
        }
        String title = "提示";
        if (progressTitle != null && progressTitle.length > 0) {
            title = progressTitle[0];
        }
        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle(title);
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }

}

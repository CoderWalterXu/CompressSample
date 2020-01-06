package com.xlh.study.compresssample;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xlh.compresslibrary.netease.CompressImageManager;
import com.xlh.compresslibrary.netease.Constants;
import com.xlh.compresslibrary.netease.bean.PhotoBean;
import com.xlh.compresslibrary.netease.config.CompressConfig;
import com.xlh.compresslibrary.netease.listener.CompressImage;
import com.xlh.compresslibrary.netease.utils.CachePathUtils;
import com.xlh.compresslibrary.netease.utils.CommonUtils;
import com.xlh.compresslibrary.netease.utils.ImageUtil;
import com.xlh.compresslibrary.netease.utils.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.xlh.compresslibrary.netease.utils.ImageUtil.getImageStreamFromExternal;

/**
 * @author: Watler Xu
 * time:2020/1/6
 * description:
 * version:
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener,CompressImage.CompressListener{


    Button btnCamera;
    Button btnAlbum;

    // 压缩时diglog
    ProgressDialog dialog;
    // 拍照后，源文件路径
    String cameraCachePath;
    // 压缩配置
    CompressConfig compressConfig;

    private ImageView imageOriginal;
    private ImageView imageCompress;
    private TextView tv1, tv2, tv3, tv4;
    private String mPath1, mPath2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCamera = findViewById(R.id.btn_camera);
        btnAlbum = findViewById(R.id.btn_album);

        imageOriginal = findViewById(R.id.image_original);
        imageCompress = findViewById(R.id.image_compress);

        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        tv4 = findViewById(R.id.tv4);


        btnCamera.setOnClickListener(this);
        btnAlbum.setOnClickListener(this);
        imageOriginal.setOnClickListener(this);
        imageCompress.setOnClickListener(this);

        // 运行时权限申请
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 6.0+
            String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (checkSelfPermission(perms[0]) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(perms[1]) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(perms, 100);
            }
        }

        // 默认配置
//        compressConfig = CompressConfig.getDefaultConfig();
        // 自定义配置
        compressConfig = CompressConfig.builder()
                .setUnCompressMinPixel(800) // 最小像素不压缩，默认值：1000
                .setUnCompressNormalPixel(1000) // 标准像素不压缩，默认值：2000
                .setMaxPixel(1000) // 长或宽不超过的最大像素 (单位px)，默认值：1200
                .setMaxSize(100 * 1024) // 压缩到的最大大小 (单位B)，默认值：200 * 1024 = 200KB
                .enablePixelCompress(true) // 是否启用像素压缩，默认值：true
                .enableQualityCompress(true) // 是否启用质量压缩，默认值：true
                .enableReserveRaw(true) // 是否保留源文件，默认值：true
                .setCacheDir("") // 压缩后缓存图片路径，默认值：Constants.COMPRESS_CACHE
                .setShowCompressDialog(true) // 是否显示压缩进度条，默认值：false
                .create();

        compressMore();

    }

    /**
     * 多图一起压缩
     */
    private void compressMore() {
        ArrayList<PhotoBean> photos = new ArrayList<>();
        photos.add(new PhotoBean("/storage/emulated/0/DCIM/Camera/IMG_20190417_203918.jpg"));
        photos.add(new PhotoBean("/storage/emulated/0/DCIM/P00106-222525.jpg"));
        photos.add(new PhotoBean("/storage/emulated/0/DCIM/P00106-222523.jpg"));
        compress(photos);

        // 压缩成功后，输出
//        压缩成功:/storage/emulated/0/DCIM/Camera/IMG_20190417_203918.jpg(当图片已经符合压缩大小，不会压缩)
//        压缩成功:/storage/emulated/0/DCIM/P00106-222525_compress.jpg(当图片需要压缩，且需要保留源文件时，新增“图片名_compress.jpg”)
//        压缩成功:/storage/emulated/0/DCIM/P00106-222523_compress.jpg
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_camera:
                camera();
                break;
            case R.id.btn_album:
                album();
                break;
            case R.id.image_original:
                Intent intent = new Intent(MainActivity.this, ImageActivity.class);
                intent.putExtra("path", mPath1);
                if (mPath1 != null) {
                    startActivity(intent);
                }
                break;
            case R.id.image_compress:
                Intent intent2 = new Intent(MainActivity.this, ImageActivity.class);
                intent2.putExtra("path", mPath2);
                if (mPath2 != null) {
                    startActivity(intent2);
                }
                break;
            default:
                break;
        }
    }

    public void camera() {
        // 7.0 FileProvider(指定拍完照之后的源文件路径)
        Uri outputUri;
        File file = CachePathUtils.getCameraCacheFile();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {// 6.0+
            outputUri = UriParseUtils.getCameraOutPutUri(this, file);
        } else {// 低版本兼容
            outputUri = Uri.fromFile(file);
        }
        // 拍照后，源文件路径
        cameraCachePath = file.getAbsolutePath();
        // 启动拍照
        CommonUtils.hasCamera(this, CommonUtils.getCameraIntent(outputUri), Constants.CAMERA_CODE);
    }

    private void album() {
        CommonUtils.openAlbum(this, Constants.ALBUM_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 拍照后的返回
        if (requestCode == Constants.CAMERA_CODE && resultCode == RESULT_OK) {

            Bitmap bitmap = ImageUtil.getCompressBitmap(cameraCachePath);
            mPath1 = cameraCachePath;
            imageOriginal.setImageBitmap(bitmap);

            getString(cameraCachePath, 1);

            // 开始压缩
            preCompress(cameraCachePath);
        }
        // 相册返回
        if (requestCode == Constants.ALBUM_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();

                imageOriginal.setImageURI(uri);
                mPath1 = UriParseUtils.getPath(this, uri);
                LogUtils.e("相册mPath1："+mPath1);
                getString(mPath1, 1);

                // 开始压缩
                preCompress(mPath1);
            }
        }

    }

    /**
     * 开始压缩
     *
     * @param path
     */
    private void preCompress(String path) {
        // 集合批量压缩
        ArrayList<PhotoBean> photos = new ArrayList<>();
        photos.add(new PhotoBean(path));
        if (!photos.isEmpty()) {
            compress(photos);
        }
    }

    /**
     * 交给压缩引擎，等待压缩
     *
     * @param photos
     */
    private void compress(ArrayList<PhotoBean> photos) {
        if (compressConfig.isShowCompressDialog()) {
            LogUtils.e("开始压缩");
            dialog = CommonUtils.showProgressDialog(this, "开始压缩");
        }
         // 第一种启动压缩的方式
        CompressImageManager.build(this, compressConfig, photos, this).compress();

        // 第二种启动压缩的方式
//        builderCompress(photos);

    }

    private void builderCompress(ArrayList<PhotoBean> photos) {
        CompressImageManager.builder(this)
                .setUnCompressMinPixel(1000) // 最小像素不压缩，默认值：1000
                .setUnCompressNormalPixel(2000) // 标准像素不压缩，默认值：2000
                .setMaxPixel(1000) // 长或宽不超过的最大像素 (单位px)，默认值：1200
                .setMaxSize(100 * 1024) // 压缩到的最大大小 (单位B)，默认值：200 * 1024 = 200KB
                .enablePixelCompress(true) // 是否启用像素压缩，默认值：true
                .enableQualityCompress(true) // 是否启用质量压缩，默认值：true
                .enableReserveRaw(true) // 是否保留源文件，默认值：true
                .setCacheDir("") // 压缩后缓存图片路径，默认值：Constants.COMPRESS_CACHE
                .setShowCompressDialog(true) // 是否显示压缩进度条，默认值：false
                .loadPhtos(photos)
                .setCompressListener(new CompressImage.CompressListener() {
                    @Override
                    public void onCompressSuccess(ArrayList<PhotoBean> arrayList) {

                        for (int i = 0; i < arrayList.size(); i++) {
                            LogUtils.e("压缩成功:"+arrayList.get(i).getCompressPath());
                        }

                        mPath2 = arrayList.get(0).getCompressPath();
                        imageCompress.setImageURI(getImageStreamFromExternal(mPath2));
                        getString(mPath2, 2);
                        if (dialog != null && !isFinishing()) {
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onCompressFailed(ArrayList<PhotoBean> images, String... error) {
                        LogUtils.e("压缩失败：" + error);
                        if (dialog != null && !isFinishing()) {
                            dialog.dismiss();
                        }
                    }

                }).compress();
    }

    @Override
    public void onCompressSuccess(ArrayList<PhotoBean> images) {

        for (int i = 0; i < images.size(); i++) {
            LogUtils.e("压缩成功:"+images.get(i).getCompressPath());
        }

        mPath2 = images.get(0).getCompressPath();
        imageCompress.setImageURI(getImageStreamFromExternal(mPath2));
        getString(mPath2, 2);
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void onCompressFailed(ArrayList<PhotoBean> images, String... error) {
        LogUtils.e("压缩失败：" + error);
        imageCompress.setImageBitmap(null);
        tv3.setText("");
        tv4.setText("");
        Toast.makeText(this,"压缩失败",Toast.LENGTH_LONG).show();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void getString(String photoPath, int type) {


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, options);

        //照片长度
        String photoLength = String.valueOf(options.outHeight);

        //照片宽度
        String photoWidth = String.valueOf(options.outWidth);

        if (type == 1) {
            tv1.setText("图片像素:" + photoLength + "*" + photoWidth);
        } else {
            tv3.setText("图片像素:" + photoLength + "*" + photoWidth);
        }
        File f = new File(photoPath);
        FileInputStream fis = null;
        try {

            fis = new FileInputStream(f);
            //照片大小
            float size = fis.available() / 1000;
            String photoSize = size + "KB";
            if (type == 1) {
                tv2.setText("图片大小:" + photoSize);
            } else {
                tv4.setText("图片大小:" + photoSize);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}

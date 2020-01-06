package com.xlh.compresslibrary.netease;

import android.content.Context;
import android.text.TextUtils;


import com.xlh.compresslibrary.netease.bean.PhotoBean;
import com.xlh.compresslibrary.netease.config.CompressConfig;
import com.xlh.compresslibrary.netease.core.CompressImageUtil;
import com.xlh.compresslibrary.netease.listener.CompressImage;
import com.xlh.compresslibrary.netease.listener.CompressResultListener;
import com.xlh.compresslibrary.netease.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * @author: Watler Xu
 * time:2020/1/6
 * description:压缩管理类
 * version:
 */
public class CompressImageManager implements CompressImage {

    // 压缩工具类
    private CompressImageUtil compressImageUtil;
    // 需要压缩的图片集合
    private ArrayList<PhotoBean> images;
    // 压缩监听，告知调用类(如Activity)
    private CompressImage.CompressListener listener;
    // 压缩配置类
    private CompressConfig config;

    /**
     * 私有实现
     *
     * @param context  上下文
     * @param config   配置
     * @param images   图片集合
     * @param listener 监听
     * @return
     */
    private CompressImageManager(Context context, CompressConfig config,
                                 ArrayList<PhotoBean> images, CompressListener listener) {
        compressImageUtil = new CompressImageUtil(context, config);
        this.config = config;
        this.images = images;
        this.listener = listener;
    }

    public void setImages(ArrayList<PhotoBean> images) {
        this.images = images;
    }

    public void setListener(CompressListener listener) {
        this.listener = listener;
    }

    public void setConfig(CompressConfig config) {
        this.config = config;
    }

    public void setCompressImageUtil(CompressImageUtil compressImageUtil) {
        this.compressImageUtil = compressImageUtil;
    }

    /**
     * 静态方法，new实现
     *
     * @param context  上下文
     * @param config   配置
     * @param images   图片集合
     * @param listener 监听
     * @return
     */
    public static CompressImage build(Context context,
                                      CompressConfig config,
                                      ArrayList<PhotoBean> images,
                                      CompressImage.CompressListener listener) {
        return new CompressImageManager(context, config, images, listener);
    }

    public CompressImageManager() {
    }


    public static Builder builder(Context context) {
        return new Builder(context);
    }

    public static class Builder {


        private CompressImageManager manager;
        private CompressImageUtil compressImageUtil;
        private CompressConfig config;

        public Builder(Context context) {
            manager = new CompressImageManager();
            config = CompressConfig.builder().create();
            compressImageUtil = new CompressImageUtil(context, config);
        }

        public Builder setUnCompressMinPixel(int unCompressMinPixel) {
            config.setUnCompressMinPixel(unCompressMinPixel);
            return this;
        }


        public Builder setUnCompressNormalPixel(int unCompressNormalPixel) {
            config.setUnCompressNormalPixel(unCompressNormalPixel);
            return this;
        }

        public Builder setMaxSize(int maxSize) {
            config.setMaxSize(maxSize);
            return this;
        }

        public Builder setMaxPixel(int maxPixel) {
            config.setMaxPixel(maxPixel);
            return this;
        }

        public Builder enablePixelCompress(boolean enablePixelCompress) {
            config.setEnablePixelCompress(enablePixelCompress);
            return this;
        }

        public Builder enableQualityCompress(boolean enableQualityCompress) {
            config.setEnableQualityCompress(enableQualityCompress);
            return this;
        }

        public Builder enableReserveRaw(boolean enableReserveRaw) {
            config.setEnableReserveRaw(enableReserveRaw);
            return this;
        }

        public Builder setCacheDir(String cacheDir) {
            config.setCacheDir(cacheDir);
            return this;
        }

        public Builder setShowCompressDialog(boolean showCompressDialog) {
            config.setShowCompressDialog(showCompressDialog);
            return this;
        }

//        public Builder config(CompressConfig config) {
//            compressImageUtil.setConfig(config);
//
//            return this;
//        }

        public Builder loadPhtos(ArrayList<PhotoBean> images) {
            manager.setImages(images);
            return this;
        }

        public Builder setCompressListener(CompressListener listener) {
            manager.setListener(listener);
            return this;
        }

        public Builder compress() {
            manager.setCompressImageUtil(compressImageUtil);
            manager.setConfig(config);
            manager.compress();
            return this;
        }


    }

    @Override
    public void compress() {
        if (images == null || images.isEmpty()) {
            listener.onCompressFailed(images, "图片集合为空");
            return;
        }

        for (PhotoBean image : images) {
            if (image == null) {
                listener.onCompressFailed(images, "某图片为空");
                return;
            }
        }

        // 开始递归压缩，从第一张开始
        compress(images.get(0));

    }

    /**
     * 从第一张开始压缩，index=0
     *
     * @param photoBean
     */
    private void compress(final PhotoBean photoBean) {
        // 拍照、相册的源文件路径为空
        if (TextUtils.isEmpty(photoBean.getOriginalPath())) {
            // 继续
            continueCompress(photoBean, false);
            return;
        }

        // 源文件不存在
        File file = new File(photoBean.getOriginalPath());
        if (!file.exists() || !file.isFile()) {
            continueCompress(photoBean, false);
            return;
        }

        // 需要压缩的图片少于指定的压缩参数,默认最大200KB
        if (file.length() < config.getMaxSize()) {
            // fix已经符合压缩大小，但没有compressPath值的BUG
            photoBean.setCompressPath(photoBean.getOriginalPath());
            LogUtils.e("图片："+photoBean.getOriginalPath()+"已经符合压缩大小");
            continueCompress(photoBean, true);
            return;
        }

        // 筛选后，最后真正需要压缩的，单张压缩
        startCompress(photoBean);
    }

    private void startCompress(final PhotoBean photoBean) {
        compressImageUtil.compress(photoBean.getOriginalPath(), new CompressResultListener() {
            @Override
            public void onCompressSuccess(String imgPath) {
                photoBean.setCompressPath(imgPath);
                LogUtils.e("onCompressSuccess--imgPath:" + imgPath);
                continueCompress(photoBean, true);
            }

            @Override
            public void onCompressFailed(String imgPath, String error) {
                LogUtils.e("onCompressFailed--imgPath:" + imgPath);
                continueCompress(photoBean, false, error);
            }
        });
    }

    /**
     * @param image        需要压缩的图片对象，属性包括：压缩后的图片路径
     * @param isCompressed 是否压缩过
     * @param error        压缩出现了异常
     */
    private void continueCompress(PhotoBean image, boolean isCompressed, String... error) {
        image.setCompressed(isCompressed);
        // 获取当前压缩图片对象的索引
        int index = images.indexOf(image);
        if (index == images.size() - 1) { // 如果是最后一张
            // 全部压缩完成，通知调用层
            handlerCallback(error);
        } else {
            // 继续压缩，开始下一张
            compress(images.get(index + 1));
        }
    }

    private void handlerCallback(String... error) {
        if (error.length > 0) {
            listener.onCompressFailed(images, "压缩失败-->" + error[0]);
            return;
        }

        for (PhotoBean image : images) {
            // 如果存在没有压缩的图片，或者压缩失败的
            if (!image.isCompressed()) {
                listener.onCompressFailed(images, image.getOriginalPath() + "压缩失败");
                return;
            }
        }

        LogUtils.e(images.get(0).toString());
        listener.onCompressSuccess(images);
    }
}

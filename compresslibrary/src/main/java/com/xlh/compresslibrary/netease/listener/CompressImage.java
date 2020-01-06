package com.xlh.compresslibrary.netease.listener;

import com.xlh.compresslibrary.netease.bean.PhotoBean;

import java.util.ArrayList;

/**
 * @author: Watler Xu
 * time:2020/1/6
 * description:图片集合的压缩返回监听
 * version:
 */
public interface CompressImage {

    // 开始压缩
    void compress();

    // 图片集合的压缩结果返回
    interface CompressListener {
        // 成功
        void onCompressSuccess(ArrayList<PhotoBean> images);

        // 失败(异常)
        void onCompressFailed(ArrayList<PhotoBean> images, String... error);
    }

}

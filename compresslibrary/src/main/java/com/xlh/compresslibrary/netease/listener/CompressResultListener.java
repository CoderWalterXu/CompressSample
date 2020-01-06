package com.xlh.compresslibrary.netease.listener;

/**
 * @author: Watler Xu
 * time:2020/1/6
 * description:单张（每一张）图片压缩时的监听
 * version:
 */
public interface CompressResultListener {

    // 成功
    void onCompressSuccess(String imgPath);
    // 失败（异常）
    void onCompressFailed(String imgPath,String error);

}

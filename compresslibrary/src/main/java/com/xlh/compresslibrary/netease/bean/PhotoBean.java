package com.xlh.compresslibrary.netease.bean;

import java.io.Serializable;

/**
 * @author: Watler Xu
 * time:2020/1/6
 * description:
 * version:
 */
public class PhotoBean implements Serializable {

    // 图片原始路径
    private String originalPath;
    // 是否压缩过
    private boolean compressed;
    // 压缩后路径
    private String compressPath;
    // 网络图片，比如编辑页面，会变更
    private String netImagePath;

    public PhotoBean(String originalPath) {
        this.originalPath = originalPath;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

    public String getCompressPath() {
        return compressPath;
    }

    public void setCompressPath(String compressPath) {
        this.compressPath = compressPath;
    }

    public String getNetImagePath() {
        return netImagePath;
    }

    public void setNetImagePath(String netImagePath) {
        this.netImagePath = netImagePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof PhotoBean) {
            PhotoBean photo = (PhotoBean) o;
            return originalPath.equals(photo.originalPath);
        }
        return super.equals(o);
    }

    @Override
    public String toString() {
        return "PhotoBean{" +
                "originalPath='" + originalPath + '\'' +
                ", compressed=" + compressed +
                ", compressPath='" + compressPath + '\'' +
                ", netImagePath='" + netImagePath + '\'' +
                '}';
    }
}

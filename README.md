# CompressSample
图片压缩示例，支持单图和多图

compressConfig = CompressConfig.builder()
.setUnCompressMinPixel(1000) // 最小像素不压缩，默认值：1000
.setUnCompressNormalPixel(2000) // 标准像素不压缩，默认值：2000
.setMaxPixel(1000) // 长或宽不超过的最大像素 (单位px)，默认值：1200
.setMaxSize(100 * 1024) // 压缩到的最大大小 (单位B)，默认值：200 * 1024 = 200KB
.enablePixelCompress(true) // 是否启用像素压缩，默认值：true
.enableQualityCompress(true) // 是否启用质量压缩，默认值：true
.enableReserveRaw(true) // 是否保留源文件，默认值：true
.setCacheDir("") // 压缩后缓存图片路径，默认值：Constants.COMPRESS_CACHE
.setShowCompressDialog(true) // 是否显示压缩进度条，默认值：false
.create();

fix bug:
修复传入已经符合压缩大小的图片，没有返回图片路径的问题
修复保留源图片不生效的问题


参考了：https://github.com/VincentStory/CompressImage

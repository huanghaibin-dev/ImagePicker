package com.huanghaibin_dev.imagepicker.utils;

import android.widget.ImageView;

/**
 * 暴露一个图片加载器
 * Created by huanghaibin_dev
 * on 2016/7/13.
 */
public interface ImageLoaderListener {
    void displayImage(ImageView iv, String path);
}

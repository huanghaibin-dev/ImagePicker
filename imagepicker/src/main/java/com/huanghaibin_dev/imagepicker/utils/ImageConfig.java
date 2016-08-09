package com.huanghaibin_dev.imagepicker.utils;

import android.support.annotation.ColorInt;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片选择配置器,可自由扩展选择模式
 * Created by huanghaibin_dev
 * on 2016/7/13.
 */
@SuppressWarnings("All")
public class ImageConfig {
    @ColorInt
    private int mToolBarBackgroundColor;

    private int mSelectCount;
    private SelectMode mSelectMode;
    private MediaMode mMediaMode;
    private ArrayList<String> mSelectedmage;
    private ImageLoaderListener mLoaderListener;
    private SelectedCallBack mCallBack;

    private ImageConfig() {

    }

    public int getSelectCount() {
        return mSelectCount;
    }

    public SelectMode getSelectMode() {
        return mSelectMode;
    }

    public MediaMode getMediaMode() {
        return mMediaMode;
    }

    public ArrayList<String> getSelectedImage() {
        return mSelectedmage;
    }

    public ImageLoaderListener getLoaderListener() {
        return mLoaderListener;
    }

    public SelectedCallBack getCallBack() {
        return mCallBack;
    }

    public static ImageConfig Build() {
        ImageConfig config = new ImageConfig();
        config.mMediaMode = MediaMode.HAVE_CAM_MODE;
        config.selectMode(SelectMode.MULTI_MODE);
        config.selectCount(9);
        return config;
    }

    public ImageConfig selectCount(int count) {
        this.mSelectCount = count;
        return this;
    }

    public ImageConfig selectMode(SelectMode mode) {
        this.mSelectMode = mode;
        return this;
    }

    public ImageConfig mediaMode(MediaMode mode) {
        this.mMediaMode = mode;
        return this;
    }

    public ImageConfig loaderListener(ImageLoaderListener listener) {
        this.mLoaderListener = listener;
        return this;
    }

    public ImageConfig selectedImages(List<String> images) {
        if (images != null) {
            if (mSelectedmage == null) mSelectedmage = new ArrayList<>();
            mSelectedmage.clear();
            mSelectedmage.addAll(images);
        }
        return this;
    }

    public ImageConfig toolBarBackground(@ColorInt int color) {
        mToolBarBackgroundColor = color;
        return this;
    }

    public
    @ColorInt
    int getToolBackground() {
        return mToolBarBackgroundColor;
    }

    public ImageConfig callBack(SelectedCallBack callBack) {
        this.mCallBack = callBack;
        return this;
    }

    public enum SelectMode {
        SINGLE_MODE,//单选
        MULTI_MODE//多选
    }

    public enum MediaMode {
        ONLY_IMAGE_MODE,//只有图片
        HAVE_CAM_MODE//带相机
    }
}

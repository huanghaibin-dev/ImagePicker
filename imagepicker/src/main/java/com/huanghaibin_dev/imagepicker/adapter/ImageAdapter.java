package com.huanghaibin_dev.imagepicker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.huanghaibin_dev.imagepicker.R;
import com.huanghaibin_dev.imagepicker.bean.Image;
import com.huanghaibin_dev.imagepicker.utils.ImageConfig;
import com.huanghaibin_dev.imagepicker.utils.ImageLoaderListener;

/**
 * Created by huanghaibin_dev
 * on 2016/7/13.
 */
public class ImageAdapter extends BaseRecyclerAdapter<Image> {
    private ImageLoaderListener loader;
    private ImageConfig.SelectMode selectMode;

    public ImageAdapter(Context context) {
        super(context, NEITHER);
    }

    @Override
    public int getItemViewType(int position) {
        Image image = getItem(position);
        if (image.getId() == 0)
            return 0;
        return 1;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        if (type == 0)
            return new CamViewHolder(mInflater.inflate(R.layout.image_picker_item_list_cam, parent, false));
        return new ImageViewHolder(mInflater.inflate(R.layout.image_picker_item_list_image, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, Image item, int position) {
        if (getItemViewType(position) == 1) {
            ImageViewHolder h = (ImageViewHolder) holder;
            h.cb_selected.setChecked(item.isSelect());
            h.cb_selected.setVisibility(selectMode == ImageConfig.SelectMode.SINGLE_MODE ? View.GONE : View.VISIBLE);
            if (loader != null) {
                loader.displayImage(h.iv_image, item.getPath());
            }
        }
    }

    private static class CamViewHolder extends RecyclerView.ViewHolder {
        public CamViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void setLoader(ImageLoaderListener loader) {
        this.loader = loader;
    }

    private static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image;
        CheckBox cb_selected;

        public ImageViewHolder(View itemView) {
            super(itemView);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_image);
            cb_selected = (CheckBox) itemView.findViewById(R.id.cb_selected);
        }
    }

    public ImageConfig.SelectMode getSelectMode() {
        return selectMode;
    }

    public void setSelectMode(ImageConfig.SelectMode selectMode) {
        this.selectMode = selectMode;
    }
}

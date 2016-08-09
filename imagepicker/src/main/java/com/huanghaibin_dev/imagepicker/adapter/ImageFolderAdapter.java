package com.huanghaibin_dev.imagepicker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huanghaibin_dev.imagepicker.R;
import com.huanghaibin_dev.imagepicker.bean.ImageFolder;
import com.huanghaibin_dev.imagepicker.utils.ImageLoaderListener;

/**
 * Created by huanghaibin_dev
 * on 2016/7/13.
 */

public class ImageFolderAdapter extends BaseRecyclerAdapter<ImageFolder> {
    private ImageLoaderListener loader;
    private int selectedPosition;

    public ImageFolderAdapter(Context context) {
        super(context, NEITHER);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new FolderViewHolder(mInflater.inflate(R.layout.image_picker_item_list_folder, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder holder, ImageFolder item, int position) {
        FolderViewHolder h = (FolderViewHolder) holder;
        h.tv_name.setText(item.getName());
        h.tv_size.setText(item.getImages().size() + " 张图片");
        if (loader != null) {
            loader.displayImage(h.iv_image, item.getAlbumPath());
        }
        h.ll_folder.setSelected(position == selectedPosition);
    }

    public void setLoader(ImageLoaderListener loader) {
        this.loader = loader;
    }

    public void setSelectedPosition(int selectedPosition) {
        int currentPosition = this.selectedPosition;
        this.selectedPosition = selectedPosition;
        updateItem(currentPosition);
        updateItem(this.selectedPosition);
    }

    private static class FolderViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image;
        TextView tv_name, tv_size;
        LinearLayout ll_folder;

        public FolderViewHolder(View itemView) {
            super(itemView);
            ll_folder = (LinearLayout) itemView.findViewById(R.id.ll_folder);
            iv_image = (ImageView) itemView.findViewById(R.id.iv_folder);
            tv_name = (TextView) itemView.findViewById(R.id.tv_folder_name);
            tv_size = (TextView) itemView.findViewById(R.id.tv_size);
        }
    }
}

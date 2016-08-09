package com.huanghaibin_dev.imagepicker;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.huanghaibin_dev.imagepicker.adapter.BaseRecyclerAdapter;
import com.huanghaibin_dev.imagepicker.adapter.ImageFolderAdapter;


/**
 * Created by huanghaibin_dev
 * on 2016/7/14.
 */

public class ImageFolderPopupWindow extends PopupWindow implements PopupWindow.OnDismissListener, View.OnAttachStateChangeListener {
    private ImageFolderAdapter adapter;
    private RecyclerView rv_folder;
    private IPopupWindowListener mListener;
    private RelativeLayout rl_root;

    public ImageFolderPopupWindow(Context context) {
        super(LayoutInflater.from(context).inflate(R.layout.image_picker_popup_window_folder, null),
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        View content = getContentView();
        rl_root = (RelativeLayout) content.findViewById(R.id.rl_root);
        rv_folder = (RecyclerView) content.findViewById(R.id.rv_popup_folder);
        rv_folder.setLayoutManager(new LinearLayoutManager(context));
        setOutsideTouchable(true);
        setFocusable(true);
        setOnDismissListener(this);
        rl_root.addOnAttachStateChangeListener(this);
        rl_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setAdapter(ImageFolderAdapter adapter) {
        this.adapter = adapter;
        rv_folder.setAdapter(adapter);
    }

    public void setOnItemClickListener(BaseRecyclerAdapter.OnItemClickListener listener) {
        adapter.setOnItemClickListener(listener);
    }

    public void setListener(IPopupWindowListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public void onViewAttachedToWindow(View v) {
        if (mListener != null)
            mListener.onShow();
    }

    @Override
    public void onViewDetachedFromWindow(View v) {

    }

    @Override
    public void onDismiss() {
        if (mListener != null)
            mListener.onDismiss();
    }

    public interface IPopupWindowListener {
        void onShow();

        void onDismiss();
    }
}

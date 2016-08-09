package com.huanghaibin_dev.imagepicker;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by huanghaibin_dev
 * on 16-5-9.
 */
public class SpaceGridItemDecoration extends RecyclerView.ItemDecoration {
    private int mSpace;

    public SpaceGridItemDecoration(int space) {
        this.mSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = mSpace;
        outRect.right = mSpace;
        outRect.bottom = mSpace;
        outRect.top = mSpace;
    }
}

package com.huanghaibin_dev.imagepicker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.huanghaibin_dev.imagepicker.utils.ImageConfig;

import net.thanatosx.previewer.ImagePreviewView;
import net.thanatosx.previewer.PreviewerViewPager;


/**
 * Created by huanghaibin_dev
 * on 2016/7/11.
 */

public class ImageGalleryActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private PreviewerViewPager vp_image;
    private TextView tv_index;
    private static ImageConfig mConfig;
    private ViewPagerAdapter mAdapter;
    private int currentPosition;

    public static void show(Context context, ImageConfig config, int position) {
        mConfig = config;
        Intent intent = new Intent(context, ImageGalleryActivity.class);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_picker_activity_image_gallery);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        setTitle("");
        vp_image = (PreviewerViewPager) findViewById(R.id.vp_image);
        tv_index = (TextView) findViewById(R.id.tv_index);
        mAdapter = new ViewPagerAdapter();
        vp_image.setAdapter(mAdapter);
        vp_image.addOnPageChangeListener(this);
        currentPosition = getIntent().getIntExtra("position", 0);
        tv_index.setText((currentPosition + 1) + "/" + mConfig.getSelectedImage().size());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        tv_index.setText((position + 1) + "/" + mConfig.getSelectedImage().size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onDestroy() {
        mConfig = null;
        super.onDestroy();
    }

    private class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mConfig.getSelectedImage().size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImagePreviewView view = new ImagePreviewView(ImageGalleryActivity.this);
            view.setOnReachBorderListener(new ImagePreviewView.OnReachBorderListener() {
                @Override
                public void onReachBorder(boolean isReached) {
                    vp_image.isInterceptable(isReached);
                }
            });
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            if (mConfig.getLoaderListener() != null) {
                mConfig.getLoaderListener().displayImage(view, mConfig.getSelectedImage().get(position));
            }
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImagePreviewView) object);
        }
    }
}

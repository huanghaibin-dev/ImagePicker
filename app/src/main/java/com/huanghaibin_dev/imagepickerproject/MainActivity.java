package com.huanghaibin_dev.imagepickerproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.huanghaibin_dev.imagepicker.ImagePickerActivity;
import com.huanghaibin_dev.imagepicker.utils.ImageConfig;
import com.huanghaibin_dev.imagepicker.utils.ImageLoaderListener;

public class MainActivity extends AppCompatActivity {

    RequestManager loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loader = Glide.with(getApplicationContext());
    }

    public void click(View view) {
        ImagePickerActivity.show(this, ImageConfig.Build().loaderListener(new ImageLoaderListener() {
            @Override
            public void displayImage(ImageView iv, String path) {
                loader.load(path).into(iv);
            }
        }).toolBarBackground(0xFF3F51B5).selectMode(ImageConfig.SelectMode.MULTI_MODE));
    }
}

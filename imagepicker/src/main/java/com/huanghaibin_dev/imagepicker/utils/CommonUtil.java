package com.huanghaibin_dev.imagepicker.utils;

import android.content.Context;
import android.os.Environment;
import android.view.Display;
import android.view.WindowManager;

import com.huanghaibin_dev.imagepicker.bean.Image;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by huanghaibin_dev
 * on 2016/7/13.
 */
@SuppressWarnings("All")
public class CommonUtil {

    public static boolean hasSDCard() {
        String status = Environment.getExternalStorageState();
        if (!status.equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }
        return true;
    }

    public static String getCameraPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/";// filePath:/sdcard/
    }

    public static String getSaveImageFullName() {
        return "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";// 照片命名
    }

    public static ArrayList<String> toArrayList(List<Image> images) {
        ArrayList<String> strings = new ArrayList<>();
        for (Image i : images) {
            strings.add(i.getPath());
        }
        return strings;
    }

    /**
     * 获得屏幕的宽度
     *
     * @param context context
     * @return width
     */
    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

    /**
     * 获得屏幕的高度
     *
     * @param context context
     * @return height
     */
    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }

    /**
     * dp转px
     *
     * @param context context
     * @param dpValue dp
     * @return px
     */
    public static int dipTopx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转dp
     *
     * @param context context
     * @param pxValue px
     * @return dp
     */
    public static float pxTodip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return pxValue / scale + 0.5f;
    }
}

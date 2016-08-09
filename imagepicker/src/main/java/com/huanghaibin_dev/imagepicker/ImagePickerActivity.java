package com.huanghaibin_dev.imagepicker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.huanghaibin_dev.imagepicker.utils.ImageConfig;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * Created by huanghaibin_dev
 * on 2016/7/13.
 */
@SuppressWarnings("All")
public class ImagePickerActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, ISelectImageContract.Operator {
    private static ImageConfig mConfig;
    private final int RC_CAMERA_PERM = 0x03;
    private final int RC_EXTERNAL_STORAGE = 0x04;

    private ISelectImageContract.View mView;

    public static void show(Activity activity, ImageConfig config) {
        Intent intent = new Intent(activity, ImagePickerActivity.class);
        mConfig = config;
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_picker_activity_image_picker);
        getSupportActionBar().hide();
        initData();
    }

    private void initData() {
        requestExternalStorage();
    }

    @AfterPermissionGranted(RC_CAMERA_PERM)
    @Override
    public void requestCamera() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            if (mView != null) {
                mView.onOpenCameraSuccess();
            }
        } else {
            EasyPermissions.requestPermissions(this, "", RC_CAMERA_PERM, Manifest.permission.CAMERA);
        }
    }

    @AfterPermissionGranted(RC_EXTERNAL_STORAGE)
    @Override
    public void requestExternalStorage() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            if (mView == null) {
                handleView();
            } else {
                mView.onReadExternalStorageSuccess();
            }
        } else {
            EasyPermissions.requestPermissions(this, "", RC_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public boolean shouldShowRequestPermissionRationale(String permission) {
        return false;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == RC_EXTERNAL_STORAGE) {
            if (mView != null)
                mView.onExternalStorageDenied();
            getConfirmDialog(this, "没有权限, 你需要去设置中开启读取手机存储权限.", "去设置", "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
                    finish();
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).show();
        } else {
            if (mView != null)
                mView.onCameraPermissionDenied();
            getConfirmDialog(this, "没有权限, 你需要去设置中开启相机权限.", "去设置", "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).show();
        }

    }

    private void handleView() {
        try {
            ImagePickerFragment fragment = ImagePickerFragment.getInstance(mConfig);
            mView = fragment;
            FragmentTransaction trans = getSupportFragmentManager()
                    .beginTransaction();
            trans.replace(R.id.fl_content, fragment);
            trans.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        mConfig = null;
        mView = null;
        super.onDestroy();
    }

    public static AlertDialog.Builder getConfirmDialog(Context context, String message, String
            okString, String cancelString,
                                                       DialogInterface.OnClickListener
                                                               okClickListener,
                                                       DialogInterface.OnClickListener
                                                               cancelClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton(okString, okClickListener);
        builder.setNegativeButton(cancelString, cancelClickListener);
        builder.setCancelable(false);
        return builder;
    }
}

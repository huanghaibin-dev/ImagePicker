package com.huanghaibin_dev.imagepicker;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huanghaibin_dev.imagepicker.adapter.BaseRecyclerAdapter;
import com.huanghaibin_dev.imagepicker.adapter.ImageAdapter;
import com.huanghaibin_dev.imagepicker.adapter.ImageFolderAdapter;
import com.huanghaibin_dev.imagepicker.bean.Image;
import com.huanghaibin_dev.imagepicker.bean.ImageFolder;
import com.huanghaibin_dev.imagepicker.utils.CommonUtil;
import com.huanghaibin_dev.imagepicker.utils.ImageConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huanghaibin_dev
 * on 2016/7/15.
 */
public class ImagePickerFragment extends Fragment implements ISelectImageContract.View, View.OnClickListener {

    private View mRootView;

    private RecyclerView rv_image;

    private RelativeLayout rl_tool, rl_footer;
    private LinearLayout ll_folder, ll_back;
    private ImageView iv_arrow;
    private TextView tv_folder_name;

    private TextView btn_complete, btn_preview;

    private ImageFolderPopupWindow mFolderPopupWindow;
    private ImageFolderAdapter mImageFolderAdapter;
    private ImageAdapter mImageAdapter;

    private List<Image> mSelectedImage;
    private static ImageConfig mConfig;

    private String mCamImageName;
    private LoaderListener mCursorLoader = new LoaderListener();

    private ISelectImageContract.Operator mOperator;

    public static ImagePickerFragment getInstance(ImageConfig config) {
        mConfig = config;
        return new ImagePickerFragment();
    }

    @Override
    public void onAttach(Context context) {
        this.mOperator = (ISelectImageContract.Operator) context;
        this.mOperator.setDataView(this);
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView != null) {
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null)
                parent.removeView(mRootView);
        } else {
            mRootView = inflater.inflate(R.layout.image_picker_fragment_image_picker, container, false);
            if (savedInstanceState == null) {
                initWidget();
                initData();
            } else {
                getActivity().finish();
            }
        }
        return mRootView;
    }

    private void initWidget() {
        rl_footer = (RelativeLayout) mRootView.findViewById(R.id.rl_footer);
        rl_tool = (RelativeLayout) mRootView.findViewById(R.id.rl_tool);
        ll_folder = (LinearLayout) mRootView.findViewById(R.id.ll_folder);
        ll_back = (LinearLayout) mRootView.findViewById(R.id.ll_back);
        tv_folder_name = (TextView) mRootView.findViewById(R.id.tv_folder_name);
        iv_arrow = (ImageView) mRootView.findViewById(R.id.iv_arrow);
        rv_image = (RecyclerView) mRootView.findViewById(R.id.rv_image);
        btn_complete = (TextView) mRootView.findViewById(R.id.btn_complete);
        btn_preview = (TextView) mRootView.findViewById(R.id.btn_preview);

        rl_tool.setBackgroundColor(mConfig.getToolBackground());
        rl_footer.setVisibility(mConfig.getSelectMode() == ImageConfig.SelectMode.SINGLE_MODE ? View.GONE : View.VISIBLE);

        rv_image.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        rv_image.addItemDecoration(new SpaceGridItemDecoration(4));

        mImageAdapter = new ImageAdapter(getActivity());
        mImageAdapter.setSelectMode(mConfig.getSelectMode());
        mImageFolderAdapter = new ImageFolderAdapter(getActivity());
        mImageAdapter.setLoader(mConfig.getLoaderListener());
        mImageFolderAdapter.setLoader(mConfig.getLoaderListener());

        ll_back.setOnClickListener(this);
        ll_folder.setOnClickListener(this);
        btn_complete.setOnClickListener(this);
        btn_preview.setOnClickListener(this);
        rv_image.setItemAnimator(null);

        rv_image.setAdapter(mImageAdapter);
    }

    private void initData() {
        mSelectedImage = new ArrayList<>();
        if (mConfig.getSelectMode() == ImageConfig.SelectMode.MULTI_MODE) {
            if (mConfig.getSelectedImage() != null && mConfig.getSelectedImage().size() != 0) {
                for (String s : mConfig.getSelectedImage()) {
                    Image image = new Image();
                    image.setSelect(true);
                    image.setPath(s);
                    mSelectedImage.add(image);
                }
            }
        }

        mImageAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, long itemId) {
                if (mConfig.getMediaMode() == ImageConfig.MediaMode.HAVE_CAM_MODE) {
                    if (position != 0) {
                        handleImage(position);
                    } else {
                        if (mSelectedImage.size() < mConfig.getSelectCount()) {
                            mOperator.requestCamera();
                        } else {
                            Toast.makeText(getActivity(), "最多只能选择 " + mConfig.getSelectCount() + " 张照片", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    handleImage(position);
                }
            }
        });

        getActivity().getSupportLoaderManager().initLoader(0, null, mCursorLoader);
        handButtonStates();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_complete) {
            handleResult();
        } else if (id == R.id.ll_back) {
            getActivity().finish();
        } else if (id == R.id.ll_folder) {
            showPopupFolderList();
        } else if (id == R.id.btn_preview) {
            if (mSelectedImage.size() > 0) {
                ImageGalleryActivity.show(getActivity(), mConfig.selectedImages(CommonUtil.toArrayList(mSelectedImage)), 0);
            }
        }
    }

    private void handleImage(int position) {
        Image image = mImageAdapter.getItem(position);
        //如果是多选模式
        if (mConfig.getSelectMode() == ImageConfig.SelectMode.MULTI_MODE) {
            if (image.isSelect()) {
                image.setSelect(false);
                mSelectedImage.remove(image);
                mImageAdapter.updateItem(position);
            } else {
                if (mSelectedImage.size() == mConfig.getSelectCount()) {
                    Toast.makeText(getActivity(), "最多只能选择 " + mConfig.getSelectCount() + " 张照片", Toast.LENGTH_SHORT).show();
                } else {
                    image.setSelect(true);
                    mSelectedImage.add(image);
                    mImageAdapter.updateItem(position);
                }
            }
        } else {
            mSelectedImage.add(image);
            handleResult();
        }
        btn_preview.setText("预览(" + mSelectedImage.size() + ")");
        handButtonStates();
    }

    public void handButtonStates() {
        btn_preview.setEnabled(mSelectedImage.size() != 0 ? true : false);
        btn_complete.setEnabled(mSelectedImage.size() != 0 ? true : false);
    }

    private void handleResult() {
        if (mConfig.getCallBack() != null && mSelectedImage.size() != 0) {
            mConfig.getCallBack().doBack(CommonUtil.toArrayList(mSelectedImage));
            getActivity().finish();
        }
    }

    /**
     * 申请相机权限成功
     */
    @Override
    public void onOpenCameraSuccess() {
        toOpenCamera();
    }

    /**
     * 申请读取存储成功
     */
    @Override
    public void onReadExternalStorageSuccess() {
        getActivity().getSupportLoaderManager().initLoader(0, null, mCursorLoader);
    }

    @Override
    public void onCameraPermissionDenied() {

    }

    @Override
    public void onExternalStorageDenied() {

    }

    /**
     * 创建弹出的相册
     */
    private void showPopupFolderList() {
        if (mFolderPopupWindow == null) {
            mFolderPopupWindow = new ImageFolderPopupWindow(getActivity());
            mFolderPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mFolderPopupWindow.setAdapter(mImageFolderAdapter);
            mFolderPopupWindow.setOutsideTouchable(true);
            mFolderPopupWindow.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(final int position, long itemId) {
                    ImageFolder folder = mImageFolderAdapter.getItem(position);
                    initImageData(folder.getImages());
                    rv_image.scrollToPosition(0);
                    mFolderPopupWindow.dismiss();
                    tv_folder_name.setText(folder.getName());
                    mImageFolderAdapter.setSelectedPosition(position);
                }
            });
            mFolderPopupWindow.setListener(new ImageFolderPopupWindow.IPopupWindowListener() {
                @Override
                public void onShow() {
                    iv_arrow.setImageResource(R.drawable.ic_arrow_top);
                }

                @Override
                public void onDismiss() {
                    iv_arrow.setImageResource(R.drawable.ic_arrow_bottom);
                }
            });
        }
        mFolderPopupWindow.showAsDropDown(ll_folder);
    }

    /**
     * 打开相机
     */
    private void toOpenCamera() {
        // 判断是否挂载了SD卡
        mCamImageName = null;
        String savePath = "";
        if (CommonUtil.hasSDCard()) {
            savePath = CommonUtil.getCameraPath();
            File saveDir = new File(savePath);
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
        }

        // 没有挂载SD卡，无法保存文件
        if (TextUtils.isEmpty(savePath)) {
            Toast.makeText(getActivity(), "无法保存照片，请检查SD卡是否挂载", Toast.LENGTH_LONG).show();
            return;
        }

        mCamImageName = CommonUtil.getSaveImageFullName();
        File out = new File(savePath, mCamImageName);
        Uri uri = Uri.fromFile(out);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent,
                0x03);
    }

    /**
     * 拍照完成通知系统添加照片
     *
     * @param requestCode requestCode
     * @param resultCode  resultCode
     * @param data        data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 0x03 && mCamImageName != null) {
            Uri localUri = Uri.fromFile(new File(CommonUtil.getCameraPath() + mCamImageName));
            Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
            getActivity().sendBroadcast(localIntent);
        }
    }

    private class LoaderListener implements LoaderManager.LoaderCallbacks<Cursor> {
        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.MINI_THUMB_MAGIC,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (id == 0) {
                //数据库光标加载器
                CursorLoader cursorLoader = new CursorLoader(getActivity(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        null, null, IMAGE_PROJECTION[2] + " DESC");//倒叙排列
                return cursorLoader;
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                //初始化相片
                ArrayList<Image> images = new ArrayList<>();
                //初始化文件夹
                List<ImageFolder> imageFolders = new ArrayList<>();

                ImageFolder defaultFolder = new ImageFolder();
                defaultFolder.setName("全部照片");
                defaultFolder.setPath("");
                imageFolders.add(defaultFolder);

                int count = data.getCount();
                if (count > 0) {
                    data.moveToFirst();
                    do {
                        String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                        String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                        long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                        int id = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[3]));
                        String thumbPath = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[4]));
                        String bucket = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[5]));

                        Image image = new Image();
                        image.setPath(path);
                        image.setName(name);
                        image.setDate(dateTime);
                        image.setId(id);
                        image.setThumbPath(thumbPath);
                        image.setFolderName(bucket);
                        images.add(image);

                        //如果是新拍的照片
                        if (mCamImageName != null && mCamImageName.toLowerCase().equals(image.getName().toLowerCase())) {
                            image.setSelect(true);
                            mSelectedImage.add(image);
                        }

                        //如果是被选中的图片
                        if (mSelectedImage.size() > 0) {
                            for (Image i : mSelectedImage) {
                                if (i.getPath().equals(image.getPath())) {
                                    image.setSelect(true);
                                }
                            }
                        }

                        File imageFile = new File(path);
                        File folderFile = imageFile.getParentFile();
                        ImageFolder folder = new ImageFolder();
                        folder.setName(folderFile.getName());
                        folder.setPath(folderFile.getAbsolutePath());
                        if (!imageFolders.contains(folder)) {
                            ArrayList<Image> imageList = new ArrayList<>();
                            imageList.add(image);
                            folder.setImages(imageList);
                            folder.setAlbumPath(image.getPath());//默认相册封面
                            imageFolders.add(folder);
                        } else {
                            // 更新
                            ImageFolder f = imageFolders.get(imageFolders.indexOf(folder));
                            f.getImages().add(image);
                        }


                    } while (data.moveToNext());
                }
                initImageData(images);
                defaultFolder.setImages(images);
                defaultFolder.setAlbumPath(images.size() > 0 ? images.get(0).getPath() : null);
                mImageFolderAdapter.resetItem(imageFolders);

                //删除掉不存在的，在于用户选择了相片，又去相册删除
                if (mSelectedImage.size() > 0) {
                    List<Image> rs = new ArrayList<>();
                    for (Image i : mSelectedImage) {
                        File f = new File(i.getPath());
                        if (!f.exists()) {
                            rs.add(i);
                        }
                    }
                    mSelectedImage.removeAll(rs);
                }


                btn_preview.setText("预览(" + mSelectedImage.size() + ")");
                if (mConfig != null && mConfig.getSelectMode() == ImageConfig.SelectMode.SINGLE_MODE && mCamImageName != null) {
                    handleResult();
                }else {
                    handButtonStates();
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }

    private void initImageData(ArrayList<Image> images) {
        mImageAdapter.clear();
        if (mConfig != null && mConfig.getMediaMode() == ImageConfig.MediaMode.HAVE_CAM_MODE) {
            mImageAdapter.addItem(new Image());
        }
        mImageAdapter.addAll(images);
    }


    @Override
    public void onDestroy() {
        mOperator = null;
        mConfig = null;
        super.onDestroy();
    }
}

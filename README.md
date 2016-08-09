# ImagePicker
An Image picker for Android, this picker can take photo, single or multi pick mode,you can use the image loader you like adn use configuration parameters, that is efficient.
###
这是一个图片选择器，可以在内部拍照并自动选择该图片，您可以在指定单选或多选模式，甚至是指定开启摄像头，它已经处理了权限问题，无需自己去判断APP的权限，如果您想自定义图片加载器也可以，它很高效自由。

###gradle
```
compile 'com.github.huanghaibin:imagepicker:1.0.8'
```

###AndroidManifest
```
<activity android:name="com.huanghaibin_dev.imagepicker.ImagePickerActivity" />
<activity
            android:name="com.huanghaibin_dev.imagepicker.ImageGalleryActivity"
            android:theme="@style/image_picker_Theme.Dialog.NoTitle.Translucent" />
```
###using code
```
ImagePickerActivity.show(this, ImageConfig.Build().selectMode(ImageConfig.SelectMode.MULTI_MODE)
                .toolBarBackground(0xff24936e)
                .selectCount(20)
                .mediaMode(ImageConfig.MediaMode.HAVE_CAM_MODE)
                .loaderListener(new ImageLoaderListener() {
                    @Override
                    public void displayImage(ImageView iv, String path) {
                        Glide.with(ApplicationContext).load(path).placeholder(R.drawable.ic_default).into(iv);
                    }
                })
                .selectedImages(AyyarList<String> selectedImages)
                .callBack(new SelectedCallBack() {
                    @Override
                    public void doBack(ArrayList<String> images) {
                       
                    }
                })); 
```

###Priview效果预览
<img src="https://github.com/MiracleTimes-Dev/GitHubProjectPicture/blob/master/ImagePicker/Screenshot_2016-08-09-15-09-00-965_image_picker.png" height="550"/> 

<img src="https://github.com/MiracleTimes-Dev/GitHubProjectPicture/blob/master/ImagePicker/Screenshot_2016-08-09-15-09-57-467_image_picker.png" height="550"/> 

<img src="https://github.com/MiracleTimes-Dev/GitHubProjectPicture/blob/master/ImagePicker/Screenshot_2016-08-09-15-10-11-881_image_picker.png" height="550"/> 

<img src="https://github.com/MiracleTimes-Dev/GitHubProjectPicture/blob/master/ImagePicker/Screenshot_2016-08-09-15-11-04-277_image_picker.png" height="550"/> 

##Licenses
- Copyright (C) 2013 huanghaibin_dev <huanghaibin_dev@163.com>
 
- Licensed under the Apache License, Version 2.0 (the "License");
- you may not use this file except in compliance with the License.
- You may obtain a copy of the License at
 
-         http://www.apache.org/licenses/LICENSE-2.0
 
- Unless required by applicable law or agreed to in writing, software
- distributed under the License is distributed on an "AS IS" BASIS,
- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
- See the License for the specific language governing permissions and
  limitations under the License.
 

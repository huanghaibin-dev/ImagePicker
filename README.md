# ImagePicker
An Image picker for Android, this picker can take photo, single or multi pick mode,you can use the image loader you like adn use configuration parameters, that is efficient.
###
这是一个图片选择器，可以在内部拍照并自动选择该图片，您可以在指定单选或多选模式，甚至是摄像头，如果您想自定义图片加载器也可以，它很高效自由。

###using
```
compile 'com.github.huanghaibin:imagepicker:1.0.6'
```
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
                .selectedImages(mediaAAr)
                .callBack(new SelectedCallBack() {
                    @Override
                    public void doBack(ArrayList<String> images) {
                       
                    }
                })); 
```
##licenses
- Copyright (C) 2013 huanghaibin_dev <huanghaibin_dev@163.com>
- WebSite https://github.com/MiracleTimes-Dev
- Created 8/1/2016
- Changed 8/1/2015
- Version 1.0.2
 
- Licensed under the Apache License, Version 2.0 (the "License");
- you may not use this file except in compliance with the License.
- You may obtain a copy of the License at
 
-         http://www.apache.org/licenses/LICENSE-2.0
 
- Unless required by applicable law or agreed to in writing, software
- distributed under the License is distributed on an "AS IS" BASIS,
- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
- See the License for the specific language governing permissions and
  limitations under the License.
 

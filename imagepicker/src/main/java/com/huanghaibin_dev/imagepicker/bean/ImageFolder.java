package com.huanghaibin_dev.imagepicker.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by huanghaibin_dev
 * on 2016/7/11.
 */
public class ImageFolder implements Serializable {
    private String name;
    private String path;
    private String albumPath;
    private ArrayList<Image> images;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public void setImages(ArrayList<Image> images) {
        this.images = images;
    }

    public String getAlbumPath() {
        return albumPath;
    }

    public void setAlbumPath(String albumPath) {
        this.albumPath = albumPath;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ImageFolder) {
            if (o == null || ((ImageFolder) o).getPath() == null && path != null)
                return false;
            String oPath = ((ImageFolder) o).getPath().toLowerCase();
            return oPath.equals(this.path.toLowerCase());
        }
        return false;
    }
}

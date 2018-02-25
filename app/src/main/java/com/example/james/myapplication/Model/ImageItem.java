package com.example.james.myapplication.Model;

import android.support.annotation.NonNull;

public class ImageItem implements Comparable<ImageItem> {
    private String name;
    private String rootPath;
    private String userPath;

    public String getSize() {
        return size;
    }

    private String size;

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public Boolean getMounted() {
        return isMounted;
    }

    public void setMounted(Boolean mounted) {
        isMounted = mounted;
    }

    private Boolean isSelected = false;
    private Boolean isMounted = false;

    public ImageItem(String name, String rootPath, String userPath, String size){
        this.name = name;
        this.rootPath = rootPath;
        this.userPath = userPath;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRootPath() {
        return rootPath;
    }

    public String getUserPath() {
        return userPath;
    }


    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(@NonNull ImageItem o) {
        return this.name.compareTo(o.name);
    }
}

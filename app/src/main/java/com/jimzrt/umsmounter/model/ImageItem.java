package com.jimzrt.umsmounter.model;


import android.support.annotation.NonNull;

public class ImageItem implements Comparable<ImageItem> {
    private String name;
    private String rootPath;
    private String userPath;
    private String url;
    private boolean downloading;
    private int progress;
    private int downloadId = -1;
    private String size;
    private Boolean isSelected = false;
    private Boolean isMounted = false;


    public ImageItem(String name, String rootPath, String userPath, String size) {
        this.name = name;
        this.rootPath = rootPath;
        this.userPath = userPath;
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageItem imageItem = (ImageItem) o;

        return name != null ? name.equals(imageItem.name) : imageItem.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getUserPath() {
        return userPath;
    }

    public void setUserPath(String userPath) {
        this.userPath = userPath;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(@NonNull ImageItem o) {
        return this.name.compareTo(o.name);
    }

    public boolean isDownloading() {
        return downloading;
    }

    public void setDownloading(boolean downloading) {
        this.downloading = downloading;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(int downloadId) {
        this.downloadId = downloadId;
    }
}

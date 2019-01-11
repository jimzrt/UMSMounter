package com.jimzrt.umsmounter.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.jimzrt.umsmounter.activities.MainActivity;
import com.jimzrt.umsmounter.model.ImageItem;
import com.jimzrt.umsmounter.utils.Helper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageItemViewModel extends ViewModel {
    private final MutableLiveData<ImageItem> selected = new MutableLiveData<>();
    private final MutableLiveData<ImageItem> removed = new MutableLiveData<>();
    private final MutableLiveData<ImageItem> added = new MutableLiveData<>();
    private final MutableLiveData<ImageItem> mounted = new MutableLiveData<>();
    private final MutableLiveData<ImageItem> downloading = new MutableLiveData<>();
    private MutableLiveData<List<ImageItem>> imageItems;

    public void select(ImageItem item) {
        selected.setValue(item);
    }

    public void downloading(ImageItem item) {
        downloading.postValue(item);
    }

    public LiveData<ImageItem> getDownloading() {
        return downloading;
    }


    public LiveData<ImageItem> getSelected() {
        return selected;
    }

    public void unselect() {
        selected.setValue(null);
    }

    public LiveData<List<ImageItem>> getImages(boolean force) {
        if (imageItems == null || force) {
            imageItems = new MutableLiveData<>();
            loadImages();
        }
        return imageItems;
    }

    private void loadImages() {

        List<ImageItem> items = new ArrayList<>();
        String path = MainActivity.USERPATH;
        File f = new File(path);


        File files[] = f.listFiles();
        if (files != null) {
            for (File file : files) {
                ImageItem item = new ImageItem(file.getName(), MainActivity.ROOTPATH + "/" + file.getName(), MainActivity.USERPATH + "/" + file.getName(), Helper.humanReadableByteCount(file.length()));
                items.add(item);
            }
        }

        imageItems.setValue(items);

    }

    public void remove(ImageItem item) {
        if (selected.getValue() == item) {
            unselect();
        }
        List<ImageItem> items = imageItems.getValue();
        items.remove(item);
        imageItems.postValue(items);
        removed.setValue(item);
    }

    public LiveData<ImageItem> getRemoved() {
        return removed;
    }

    public LiveData<ImageItem> getAdded() {
        return added;
    }

    public void add(ImageItem item) {
        List<ImageItem> items = imageItems.getValue();
        items.add(item);
        imageItems.postValue(items);
        added.postValue(item);
    }

    public LiveData<ImageItem> getMounted() {
        return mounted;
    }

    public void mount(ImageItem item) {
        item.setMounted(true);
        mounted.postValue(item);
    }


    public void unmount() {
        if (mounted.getValue() != null) {
            ImageItem item = mounted.getValue();
            item.setMounted(false);
            mounted.postValue(item);
        }

        //item.setMounted(false);
        //mounted.postValue(null);
        //unmounted.postValue(item);
    }

    public void unmount(ImageItem imageItem) {

        imageItem.setMounted(false);
        mounted.postValue(imageItem);
    }
}
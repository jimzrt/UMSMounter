package com.jimzrt.umsmounter.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jimzrt.umsmounter.activities.MainActivity
import com.jimzrt.umsmounter.model.ImageItem
import com.jimzrt.umsmounter.utils.Helper
import java.io.File
import java.util.*

class ImageItemViewModel : ViewModel() {
    val selected = MutableLiveData<ImageItem?>()
    val removed = MutableLiveData<ImageItem>()
    val added = MutableLiveData<ImageItem>()
    val mounted = MutableLiveData<ImageItem?>()
    val downloading = MutableLiveData<ImageItem>()
    private var imageItems: MutableLiveData<MutableList<ImageItem>>? = null
    fun select(item: ImageItem?) {
        selected.value = item
    }

    fun downloading(item: ImageItem) {
        downloading.postValue(item)
    }

    fun getDownloading(): LiveData<ImageItem> {
        return downloading
    }

    fun getSelected(): LiveData<ImageItem?> {
        return selected
    }

    fun unselect() {
        selected.value = null
    }

    fun getImages(force: Boolean): LiveData<MutableList<ImageItem>> {
        if (imageItems == null || force) {
            imageItems = MutableLiveData()
            loadImages()
        }
        return imageItems as MutableLiveData<MutableList<ImageItem>>
    }

    private fun loadImages() {
        val items: MutableList<ImageItem> = ArrayList()
        val path = MainActivity.USERPATH
        val f = File(path)
        val files = f.listFiles()
        if (files != null) {
            for (file in files) {
                if (file.name == "cache") {
                    continue
                }
                val item = ImageItem(file.name, MainActivity.ROOTPATH + "/" + file.name, MainActivity.USERPATH + "/" + file.name, Helper.humanReadableByteCount(file.length()))
                items.add(item)
            }
        }
        imageItems!!.value = items
    }

    fun remove(item: ImageItem) {
        if (selected.value === item) {
            unselect()
        }
        val items = imageItems!!.value!!
        items.remove(item)
        imageItems!!.postValue(items)
        removed.value = item
    }

    fun getRemoved(): LiveData<ImageItem> {
        return removed
    }

    fun getAdded(): LiveData<ImageItem> {
        return added
    }

    fun add(item: ImageItem) {
        val items = imageItems!!.value!!
        items.add(item)
        imageItems!!.postValue(items)
        added.postValue(item)
    }

    fun getMounted(): LiveData<ImageItem?> {
        return mounted
    }

    fun mount(item: ImageItem) {
        item.mounted = true
        mounted.postValue(item)
    }

    fun unmount() {
        if (mounted.value != null) {
            val item = mounted.value
            item!!.mounted = false
            mounted.postValue(item)
        }

        //item.setMounted(false);
        //mounted.postValue(null);
        //unmounted.postValue(item);
    }

    fun unmount(imageItem: ImageItem) {
        imageItem.mounted = false
        mounted.postValue(imageItem)
    }
}
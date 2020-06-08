package com.jimzrt.umsmounter.model

class ImageItem(var name: String, var rootPath: String, var userPath: String, var size: String) : Comparable<ImageItem> {
    var url: String? = null
    var isDownloading = false

    @JvmField
    var progress = 0
    var downloadId = -1
    var selected = false
    var mounted = false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val imageItem = other as ImageItem
        return name == imageItem.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    override fun toString(): String {
        return name
    }

    override fun compareTo(other: ImageItem): Int {
        return name.compareTo(other.name)
    }

}
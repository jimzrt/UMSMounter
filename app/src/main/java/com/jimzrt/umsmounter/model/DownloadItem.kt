package com.jimzrt.umsmounter.model

class DownloadItem : Comparable<DownloadItem> {
    var id: String? = null

    @JvmField
    var name: String? = null
    lateinit var tags: Array<String>

    @JvmField
    var url: String? = null
    lateinit var releases: Array<Release>
    override fun compareTo(o: DownloadItem): Int {
        return name!!.compareTo(o.name!!)
    }
}
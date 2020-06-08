package com.jimzrt.umsmounter.model

import com.jimzrt.umsmounter.utils.Helper.humanReadableByteCount

class Release {
    var url: String? = null
    private val version: String? = null
    private val size: String? = null
    override fun toString(): String {
        return """
            ${url!!.substring(url!!.lastIndexOf('/') + 1)}
            version: $version
            size: ${humanReadableByteCount(size!!.toLong())}
            """.trimIndent()
    }
}
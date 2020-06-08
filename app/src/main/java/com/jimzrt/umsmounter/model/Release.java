package com.jimzrt.umsmounter.model;

import com.jimzrt.umsmounter.utils.Helper;

public class Release {
    public String url;
    private String version;
    private String size;

    public String toString() {
        return url.substring(url.lastIndexOf('/') + 1) + "\nversion: " + version + "\nsize: " + Helper.humanReadableByteCount(Long.parseLong(size));
    }
}

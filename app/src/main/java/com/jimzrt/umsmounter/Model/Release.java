package com.jimzrt.umsmounter.Model;

import com.jimzrt.umsmounter.Utils.Helper;

public class Release {
    public String url;
    public String version;
    public String size;

    public String toString() {
        return url.substring(url.lastIndexOf('/') + 1, url.length()) + "\nversion: " + version + "\nsize: " + Helper.humanReadableByteCount(Long.parseLong(size));
    }
}

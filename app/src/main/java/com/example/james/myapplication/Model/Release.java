package com.example.james.myapplication.Model;

import com.example.james.myapplication.Utils.Helper;

public class Release {
    public String url;
    public String version;
    public String size;

    public String toString() {
        return url.substring(url.lastIndexOf('/') + 1, url.length()) + " - version: " + version + " - size: " + Helper.humanReadableByteCount(Long.parseLong(size));
    }
}

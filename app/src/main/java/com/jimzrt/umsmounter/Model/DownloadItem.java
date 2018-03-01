package com.jimzrt.umsmounter.Model;

import android.support.annotation.NonNull;

/**
 * Created by james on 27.02.18.
 */

public class DownloadItem implements Comparable<DownloadItem> {
    public String id;
    public String name;
    public String[] tags;
    public String url;
    public Release[] releases;


    @Override
    public int compareTo(@NonNull DownloadItem o) {
        return name.compareTo(o.name);
    }
}


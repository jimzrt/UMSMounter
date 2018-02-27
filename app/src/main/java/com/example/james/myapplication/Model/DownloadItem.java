package com.example.james.myapplication.Model;

/**
 * Created by james on 27.02.18.
 */

public class DownloadItem {
    public String id;
    public String name;
    public String[] tags;
    public String url;
    public Release[] releases;
}

class Release {
    public String url;
    public String arch;
    public String version;
    public String size;
}
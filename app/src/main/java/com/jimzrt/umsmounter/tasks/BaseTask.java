package com.jimzrt.umsmounter.tasks;


import android.app.Activity;

import java.lang.ref.WeakReference;

public abstract class BaseTask {

    //  Context ctx;
    String name;
    String description;
    String result;
    WeakReference<Activity> ctx;


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }


    public abstract boolean execute();

    public String getResult() {
        return result;
    }

    public void setContext(WeakReference<Activity> context) {
        this.ctx = context;
    }
}

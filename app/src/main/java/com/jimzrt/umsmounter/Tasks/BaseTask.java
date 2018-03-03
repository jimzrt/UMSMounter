package com.jimzrt.umsmounter.Tasks;


import android.app.Activity;

import java.lang.ref.WeakReference;

public abstract class BaseTask {

    //  Context ctx;
    protected String name;
    protected String description;
    String result;
    WeakReference<Activity> ctx;


    public BaseTask() {
    }

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

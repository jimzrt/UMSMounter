package com.jimzrt.umsmounter.Tasks;


import android.content.Context;

public abstract class BaseTask {

    protected Context ctx;
    protected String name;
    protected String description;
    public boolean successful;

    public BaseTask(Context ctx) {
        this.ctx = ctx;
    }

    public BaseTask() {
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getResult() {
        return result;
    }

    protected String result;

    public abstract void execute();

}

package com.example.james.myapplication.Model;


import android.content.Context;

public abstract class Task {

    protected Context ctx;
    protected String name;
    protected String description;
    public boolean successful;

    public Task(Context ctx){
        this.ctx = ctx;
    }

    public Task() {
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

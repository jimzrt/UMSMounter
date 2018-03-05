package com.jimzrt.umsmounter.tasks;


import android.os.Environment;

import java.io.File;


public class CheckFolderTask extends BaseTask {

    public CheckFolderTask(){
        this.name = "Check Folder";
        this.description = "Checking Folders...";
    }
    @Override
    public boolean execute() {

        String path = Environment.getExternalStorageDirectory().toString()+"/UMSMounter";
        File f = new File(path);
        boolean success = true;

        if(!f.exists()){
            success = f.mkdir();
                    this.result = "Path " + path + " not found, creating.\n";
                }

                File files[] = f.listFiles();
                if(files != null) {

                    this.result = "Found path " + path + " with " + files.length + " files.\n";
                }

        return success;


    }
}

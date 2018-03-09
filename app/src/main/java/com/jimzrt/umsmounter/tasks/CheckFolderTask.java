package com.jimzrt.umsmounter.tasks;


import com.jimzrt.umsmounter.activities.MainActivity;

import java.io.File;


public class CheckFolderTask extends BaseTask {

    public CheckFolderTask(){
        this.name = "Check Folder";
        this.description = "Checking Folders...";
    }
    @Override
    public boolean execute() {

        String path = MainActivity.USERPATH;
        File f = new File(path);
        boolean success = true;

        if(!f.exists()){
            success = f.mkdir();
                    this.result = "Path " + path + " not found, creating.\n";
                }
        String cachePath = MainActivity.USERPATH + MainActivity.CACHEDIR;
        f = new File(cachePath);
        if (!f.exists()) {
            success = f.mkdir();
            this.result = "Cache path " + cachePath + " not found, creating.\n";
        }



        return success;


    }
}

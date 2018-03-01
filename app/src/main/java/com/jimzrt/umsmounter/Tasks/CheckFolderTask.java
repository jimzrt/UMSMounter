package com.jimzrt.umsmounter.Tasks;


import android.os.Environment;

import java.io.File;


public class CheckFolderTask extends BaseTask {

    public CheckFolderTask(){
        this.name = "Check Folder";
        this.description = "Checking Folders...";
    }
    @Override
    public void execute() {

        String path = Environment.getExternalStorageDirectory().toString()+"/UMSMounter";
        File f = new File(path);

        if(!f.exists()){
                    f.mkdir();
                    this.result = "Path " + path + " not found, creating.\n";
                }

                File files[] = f.listFiles();
                if(files != null) {

                    this.result = "Found path " + path + " with " + files.length + " files.\n";
                }

        this.successful = true;


    }
}

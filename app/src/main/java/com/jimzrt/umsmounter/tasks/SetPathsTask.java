package com.jimzrt.umsmounter.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import com.jimzrt.umsmounter.activities.MainActivity;
import com.jimzrt.umsmounter.utils.Helper;

import java.io.File;

public class SetPathsTask extends BaseTask {

    public SetPathsTask() {
        this.name = "Set paths";
        this.description = "Setting paths...";
    }

    @Override
    public boolean execute() {

        //      File file = ctx.get().getExternalFilesDir(null);
        //      String applicationSpecificAbsolutePath = file.getAbsolutePath();


        String rootPath = System.getenv("EXTERNAL_STORAGE");
        //   String rootPath = System.getenv("EMULATED_STORAGE_TARGET");

        //  String rootPath = applicationSpecificAbsolutePath.substring(0, applicationSpecificAbsolutePath.indexOf("Android/data"));


        File dir = Environment.getExternalStorageDirectory();
        String userPath = dir.getAbsolutePath();


        MainActivity.ROOTPATH = Helper.combinePaths(rootPath, MainActivity.ROOTDIR);
        MainActivity.USERPATH = Helper.combinePaths(userPath, MainActivity.ROOTDIR);

        SharedPreferences sharedPref = this.ctx.get().getSharedPreferences(null, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("rootpath", MainActivity.ROOTPATH);
        editor.putString("userpath", MainActivity.USERPATH);
        editor.apply();
        return true;

    }


}

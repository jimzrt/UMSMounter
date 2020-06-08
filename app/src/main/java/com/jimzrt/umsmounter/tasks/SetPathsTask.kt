package com.jimzrt.umsmounter.tasks

import android.content.Context
import android.os.Environment
import com.jimzrt.umsmounter.activities.MainActivity
import com.jimzrt.umsmounter.utils.Helper

class SetPathsTask : BaseTask() {
    override fun execute(): Boolean {

        //      File file = ctx.get().getExternalFilesDir(null);
        //      String applicationSpecificAbsolutePath = file.getAbsolutePath();
        val rootPath = System.getenv("EXTERNAL_STORAGE")
        //   String rootPath = System.getenv("EMULATED_STORAGE_TARGET");

        //  String rootPath = applicationSpecificAbsolutePath.substring(0, applicationSpecificAbsolutePath.indexOf("Android/data"));
        val dir = Environment.getExternalStorageDirectory()
        val userPath = dir.absolutePath //ctx?.get()?.getExternalFilesDir(null)?.absolutePath
        //val userPath = dir.absolutePath
        MainActivity.ROOTPATH = Helper.combinePaths(rootPath, MainActivity.ROOTDIR)
        MainActivity.USERPATH = Helper.combinePaths(userPath, MainActivity.ROOTDIR)
        val sharedPref = ctx!!.get()!!.getSharedPreferences(null, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("rootpath", MainActivity.ROOTPATH)
        editor.putString("userpath", MainActivity.USERPATH)
        editor.apply()
        return true
    }

    init {
        name = "Set paths"
        description = "Setting paths..."
    }
}
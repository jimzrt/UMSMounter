package com.jimzrt.umsmounter.tasks

import com.jimzrt.umsmounter.activities.MainActivity
import com.jimzrt.umsmounter.utils.Helper
import java.io.File

class CheckFolderTask : BaseTask() {
    override fun execute(): Boolean {
        val path = MainActivity.USERPATH
        var f = File(path)
        var success = true
        if (!f.exists()) {
            success = f.mkdir()
            result = "Path $path not found, creating.\n"
        }
        val cachePath = Helper.combinePaths(MainActivity.USERPATH, MainActivity.CACHEDIR)
        f = File(cachePath)
        if (!f.exists()) {
            success = f.mkdir()
            result = "Cache path $cachePath not found, creating.\n"
        }
        return success
    }

    init {
        name = "Check Folder"
        description = "Checking Folders..."
    }
}
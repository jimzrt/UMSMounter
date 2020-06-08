package com.jimzrt.umsmounter.tasks

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.jimzrt.umsmounter.activities.MainActivity

class CheckPermissionTask : BaseTask() {
    override fun execute(): Boolean {
        if (ContextCompat.checkSelfPermission(ctx!!.get()!!,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {


            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(ctx!!.get()!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MainActivity.WRITE_EXTERNAL_STORAGE_PERM)
            val sharedPref = ctx!!.get()!!.getSharedPreferences(null, Context.MODE_PRIVATE)
            var hasPerm = sharedPref.getBoolean("hasPermission", false)
            while (!hasPerm) {
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                hasPerm = sharedPref.getBoolean("hasPermission", false)
            }

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
        return true
    }

    init {
        name = "Checking permissions"
        description = "Checking permissions support..."
    }
}
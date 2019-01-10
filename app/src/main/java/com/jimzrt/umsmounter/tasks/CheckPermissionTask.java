package com.jimzrt.umsmounter.tasks;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.jimzrt.umsmounter.activities.MainActivity;

public class CheckPermissionTask extends BaseTask {

    public CheckPermissionTask() {
        this.name = "Checking permissions";
        this.description = "Checking permissions support...";
    }


    @Override
    public boolean execute() {

        if (ContextCompat.checkSelfPermission(this.ctx.get(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {


            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this.ctx.get(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MainActivity.WRITE_EXTERNAL_STORAGE_PERM);


            SharedPreferences sharedPref = this.ctx.get().getSharedPreferences(null, Context.MODE_PRIVATE);
            boolean hasPerm = sharedPref.getBoolean("hasPermission", false);

            while (!hasPerm) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                hasPerm = sharedPref.getBoolean("hasPermission", false);
            }

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }


        return true;
    }
}

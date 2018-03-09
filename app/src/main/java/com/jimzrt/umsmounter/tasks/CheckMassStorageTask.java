package com.jimzrt.umsmounter.tasks;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

public class CheckMassStorageTask extends BaseTask {

    public CheckMassStorageTask() {
        this.name = "Checking mass_storage";
        this.description = "Checking mass_storage support...";
    }

    @Override
    public boolean execute() {
        SharedPreferences sharedPref = this.ctx.get().getSharedPreferences(null, Context.MODE_PRIVATE);



                 SharedPreferences.Editor editor = sharedPref.edit();

                File configfsDir = new File("/config/usb_gadget/g1/functions/mass_storage.0/lun.0");
                File usbDir = new File( "/sys/class/android_usb/android0/f_mass_storage/lun");
        File usbDir2 = new File("/sys/class/android_usb/android0/f_mass_storage/lun0");


        if (configfsDir.exists() && configfsDir.isDirectory()) {
                    this.result = "mass_storage via configfs supported!\n";
                    editor.putString("usbMode", "configfs");
            editor.putString("usbPath", configfsDir.getAbsolutePath());

            File cdrom = new File("/config/usb_gadget/g1/functions/mass_storage.0/lun.0/cdrom");
                    if (cdrom.exists()) {
                        editor.putBoolean("cdrom", true);
                    } else {
                        editor.putBoolean("cdrom", false);
                    }
                    editor.apply();
            return true;
                } else if(usbDir.exists() && usbDir.isDirectory()) {
                    this.result = "mass_storage via android_usb supported!\n";
                    editor.putString("usbMode", "android_usb");
            editor.putString("usbPath", usbDir.getAbsolutePath());
                    File cdrom = new File( "/sys/class/android_usb/android0/f_mass_storage/lun/cdrom");
                    if(cdrom.exists()){
                        editor.putBoolean("cdrom", true);
                    } else {
                        editor.putBoolean("cdrom", false);
                    }
                    editor.apply();
            return true;

        } else if (usbDir2.exists() && usbDir2.isDirectory()) {
            this.result = "mass_storage via android_usb supported!\n";
            editor.putString("usbMode", "android_usb");
            editor.putString("usbPath", usbDir2.getAbsolutePath());

            File cdrom = new File("/sys/class/android_usb/android0/f_mass_storage/lun/cdrom");
            if (cdrom.exists()) {
                editor.putBoolean("cdrom", true);
            } else {
                editor.putBoolean("cdrom", false);
            }
            editor.apply();
            return true;
        } else {
                    this.result = "mass_storage not supported!\n";
            return false;
                }


    }
}
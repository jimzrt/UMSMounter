package com.example.james.myapplication.Model;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

public class CheckMassStorageTask extends Task{

    public CheckMassStorageTask(Context ctx){
        super(ctx);
        this.name = "Checking mass_storage";
        this.description = "Checking mass_storage support...";
    }

    @Override
    public void execute() {
        SharedPreferences sharedPref = ctx.getSharedPreferences(null,Context.MODE_PRIVATE);



                 SharedPreferences.Editor editor = sharedPref.edit();

                File configfsDir = new File("/config/usb_gadget/g1/functions/mass_storage.0/lun.0");
                File usbDir = new File( "/sys/class/android_usb/android0/f_mass_storage/lun");



                if(configfsDir.exists() && configfsDir.isDirectory()) {
                    this.result = "mass_storage via configfs supported!\n";
                    this.successful = true;
                    editor.putString("usbMode", "configfs");
                    File cdrom = new File("/config/usb_gadget/g1/functions/mass_storage.0/lun.0/cdrom");
                    if (cdrom.exists()) {
                        editor.putBoolean("cdrom", true);
                    } else {
                        editor.putBoolean("cdrom", false);
                    }
                    editor.apply();
                } else if(usbDir.exists() && usbDir.isDirectory()) {
                    this.result = "mass_storage via android_usb supported!\n";
                    editor.putString("usbMode", "android_usb");
                    File cdrom = new File( "/sys/class/android_usb/android0/f_mass_storage/lun/cdrom");
                    if(cdrom.exists()){
                        editor.putBoolean("cdrom", true);
                    } else {
                        editor.putBoolean("cdrom", false);
                    }
                    this.successful = true;
                    editor.apply();

                }else {
                    this.successful = false;
                    this.result = "mass_storage not supported!\n";

                }


    }
}

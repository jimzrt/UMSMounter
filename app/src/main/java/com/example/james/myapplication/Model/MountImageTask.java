package com.example.james.myapplication.Model;

import android.util.Log;

import com.topjohnwu.superuser.Shell;

import java.io.File;
import java.util.List;



public class MountImageTask extends Task {

    private ImageItem imageItem;
    private String mode;

    public MountImageTask(ImageItem imageItem, String mode) {
        this.name = "Mounting";
        this.description = "Mounting " + imageItem.getName() + " in " + mode.toLowerCase() + " mode...";
        this.imageItem = imageItem;
        this.mode = mode;
    }


    @Override
    public void execute() {
        String removable = "";
        String ro = "";
        String cdrom = "";

        switch (mode){
            case "Writable USB":
                removable = "1";
                ro = "0";
                cdrom = "0";
                break;
            case "Read-only USB":
                removable = "0";
                ro = "1";
                cdrom = "0";
                break;
            case "CD-ROM":
                removable = "0";
                ro = "1";
                cdrom = "1";
                break;
            default:
                this.result = "Unknown Mode!";
                this.successful = false;
                return;

        }


        File configsPath = new File("/config/usb_gadget/g1/functions/mass_storage.0/lun.0");
        if(configsPath.exists() && false){

            Shell.Sync.sh(new String[]{"setprop sys.usb.config none",
                    "echo \"\" > /config/usb_gadget/g1/UDC",
                    "echo " + removable + " > /config/usb_gadget/g1/functions/mass_storage.0/lun.0/removable",
                    "echo " + ro + " > /config/usb_gadget/g1/functions/mass_storage.0/lun.0/ro",
                    "echo " + cdrom + " > /config/usb_gadget/g1/functions/mass_storage.0/lun.0/cdrom",
                    "echo \"" + imageItem.getRootPath() + "\" > /config/usb_gadget/g1/functions/mass_storage.0/lun.0/file",
                    "setprop sys.usb.config mass_storage"});
            this.result = imageItem.getName() + " mounted!\n";
            this.successful = true;

        } else {
            String usb = "/sys/class/android_usb/android0";
            List<String> test = Shell.Sync.sh(new String[]{
                    "setprop sys.usb.config none",
                    "echo > " + usb + "/f_mass_storage/lun/file",
                    // "echo 0 > " + usb + "/enable",
                    //  "echo mass_storage > " + usb + "/functions",
                    //  "echo disk > " + usb + "/f_mass_storage/luns",
                    //"echo \"\" > /config/usb_gadget/g1/UDC",
                    // "echo " + removable + " > " + usb + "/f_mass_storage/lun/removable",
                    "echo " + ro + " > " + usb + "/f_mass_storage/lun/ro",
                    ///  "echo " + cdrom + " > " + usb + "/f_mass_storage/lun/cdrom",
                    "echo " + imageItem.getRootPath() + " > " + usb + "/f_mass_storage/lun/file",
                    // "echo 1 > " + usb + "/enable"
                    "setprop sys.usb.config mass_storage"
            });

            for(String lala : test){
                Log.i("lala", lala);
            }
            this.result = imageItem.getName() + " mounted!\n";
            this.successful = true;
        }






    }
}

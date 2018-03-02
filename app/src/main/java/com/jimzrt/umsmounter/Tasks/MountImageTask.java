package com.jimzrt.umsmounter.Tasks;

import android.content.Context;
import android.content.SharedPreferences;

import com.jimzrt.umsmounter.Model.ImageItem;
import com.topjohnwu.superuser.Shell;


public class MountImageTask extends BaseTask {

    private ImageItem imageItem;
    private String mode;
    private Context ctx;

    public MountImageTask(ImageItem imageItem, String mode, Context ctx) {
        this.name = "Mounting";
        this.description = "Mounting " + imageItem.getName() + " in " + mode.toLowerCase() + " mode...";
        this.imageItem = imageItem;
        this.mode = mode;
        this.ctx = ctx;
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


        SharedPreferences sharedPref = ctx.getSharedPreferences(null, Context.MODE_PRIVATE);
        String usbMode = sharedPref.getString("usbMode", "Not supported");
        String usbPath = sharedPref.getString("usbPath", "Not supported");
        boolean cdRomSupport = sharedPref.getBoolean("cdrom", false);
        if (usbPath.equals("Not supported") || usbMode.equals("Not supported")) {
            this.successful = false;
            this.result = "not mounted!\n";
            return;
        }


        switch (usbMode) {
            case "configfs":
                Shell.Sync.sh("setprop sys.usb.config none",
                        "echo \"\" > /config/usb_gadget/g1/UDC",
                        "echo " + removable + " > " + usbPath + "/removable",
                        "echo " + ro + " > " + usbPath + "/ro",
                        "echo " + cdrom + " > " + usbPath + "/cdrom",
                        "echo \"" + imageItem.getRootPath() + "\" > " + usbPath + "/file",
                        "setprop sys.usb.config mass_storage");
                this.result = imageItem.getName() + " mounted!\n";
                this.successful = true;

                break;
            case "android_usb":
                Shell.Sync.sh("setprop sys.usb.config none",
                        "echo > " + usbPath + "/file",
                        "echo " + ro + " > " + usbPath + "/ro",
                        "echo " + cdrom + " >  " + usbPath + "/cdrom",
                        "echo " + imageItem.getRootPath() + " > " + usbPath + "/file",
                        "setprop sys.usb.config mass_storage");

                this.result = imageItem.getName() + " mounted!\n";
                this.successful = true;
                break;
            default:
                this.result = "not mounted!\n";
                this.successful = false;
                break;
        }






    }
}

package com.jimzrt.umsmounter.Tasks;


import android.content.Context;
import android.content.SharedPreferences;

import com.topjohnwu.superuser.Shell;


public class UnmountingTask extends BaseTask {

    private String oldFunctions;

    public UnmountingTask(String oldFunctions, Context ctx) {
        this.name = "Reverting";
        this.description = "Revert to " + oldFunctions + "...";
        this.oldFunctions = oldFunctions;
        this.ctx = ctx;
    }


    @Override
    public void execute() {

        SharedPreferences sharedPref = ctx.getSharedPreferences(null, Context.MODE_PRIVATE);
        String usbMode = sharedPref.getString("usbMode", "Not supported");
        String usbPath = sharedPref.getString("usbPath", "Not supported");
        if (usbPath.equals("Not supported") || usbMode.equals("Not supported")) {
            this.successful = false;
            this.result = "not mounted!\n";
            return;
        }

        switch (usbMode) {
            case "configfs":
                Shell.Sync.sh("setprop sys.usb.config none",
                        "echo \"\" > /config/usb_gadget/g1/UDC",
                        "echo \"\" > " + usbPath + "/file",
                        "setprop sys.usb.config " + oldFunctions);

                break;
            case "android_usb":
                String usb = "/sys/class/android_usb/android0";
                Shell.Sync.sh("setprop sys.usb.config none",
                        "echo \"\" > " + usbPath + "/file",
                        "setprop sys.usb.config " + oldFunctions);
                this.result = "Reverted to " + oldFunctions + "!\n";
                this.successful = true;

                break;
            default:
                this.result = "could not unmounted!\n";
                this.successful = false;
                break;
        }

    }
}
package com.jimzrt.umsmounter.Tasks;


import com.topjohnwu.superuser.Shell;

/**
 * Created by james on 13.02.18.
 */

public class UnmountingTask extends BaseTask {

    private String oldFunctions;
    public UnmountingTask(String oldFunctions){
        this.name = "Reverting";
        this.description = "Revert to " + oldFunctions + "...";
        this.oldFunctions = oldFunctions;
    }


    @Override
    public void execute() {
        Shell.Sync.sh("setprop sys.usb.config none", "echo \"\" > /config/usb_gadget/g1/UDC", "echo \"\" > /config/usb_gadget/g1/functions/mass_storage.0/lun.0/file", "setprop sys.usb.config " + oldFunctions);
        this.result = "Reverted to "+oldFunctions+"!\n";
        this.successful = true;

    }
}
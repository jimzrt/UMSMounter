package com.jimzrt.umsmounter.tasks

import android.content.Context
import com.topjohnwu.superuser.Shell

class UnmountingTask(oldFunctions: String) : BaseTask() {
    private val oldFunctions: String
    override fun execute(): Boolean {
        val sharedPref = ctx!!.get()!!.getSharedPreferences(null, Context.MODE_PRIVATE)
        val usbMode = sharedPref.getString("usbMode", "Not supported")
        val usbPath = sharedPref.getString("usbPath", "Not supported")
        if (usbPath == "Not supported" || usbMode == "Not supported") {
            result = "not mounted!\n"
            return false
        }
        return when (usbMode) {
            "configfs" -> {
                Shell.su("setprop sys.usb.config none",
                        "echo \"\" > /config/usb_gadget/g1/UDC",
                        "echo \"\" > $usbPath/file",
                        "setprop sys.usb.config $oldFunctions").exec()
                true
            }
            "android_usb" -> {
                Shell.su("setprop sys.usb.config none",
                        "echo \"\" > $usbPath/file",
                        "setprop sys.usb.config $oldFunctions").exec()
                result = "Reverted to $oldFunctions!\n"
                true
            }
            else -> {
                result = "could not unmounted!\n"
                false
            }
        }
    }

    init {
        name = "Reverting"
        description = "Revert to $oldFunctions..."
        this.oldFunctions = oldFunctions
    }
}
package com.jimzrt.umsmounter.tasks

import android.content.Context
import com.jimzrt.umsmounter.model.ImageItem
import com.topjohnwu.superuser.Shell
import java.util.*

class MountImageTask(imageItem: ImageItem, mode: String) : BaseTask() {
    private val imageItem: ImageItem
    private val mode: String
    override fun execute(): Boolean {
        val removable: String
        val ro: String
        val cdrom: String
        when (mode) {
            "Writable USB" -> {
                removable = "1"
                ro = "0"
                cdrom = "0"
            }
            "Read-only USB" -> {
                removable = "0"
                ro = "1"
                cdrom = "0"
            }
            "CD-ROM" -> {
                removable = "0"
                ro = "1"
                cdrom = "1"
            }
            else -> {
                result = "Unknown Mode!"
                return false
            }
        }
        val sharedPref = ctx!!.get()!!.getSharedPreferences(null, Context.MODE_PRIVATE)
        val usbMode = sharedPref.getString("usbMode", "Not supported")
        val usbPath = sharedPref.getString("usbPath", "Not supported")
        val cdRomSupport = sharedPref.getBoolean("cdrom", false)
        if (usbPath == "Not supported" || usbMode == "Not supported") {
            result = "Mounting not supported!\n"
            return false
        }
        return when (usbMode) {
            "configfs" -> {
                Shell.su("setprop sys.usb.config none",
                        "echo \"\" > /config/usb_gadget/g1/UDC",
                        "echo $removable > $usbPath/removable",
                        "echo $ro > $usbPath/ro",
                        "echo $cdrom > $usbPath/cdrom",
                        "echo \"" + imageItem.rootPath + "\" > " + usbPath + "/file",
                        "setprop sys.usb.config mass_storage").exec()
                result = """${imageItem.name} mounted!
"""
                true
            }
            "android_usb" -> {
                Shell.su("setprop sys.usb.config none",
                        "echo > $usbPath/file",
                        "echo $ro > $usbPath/ro",
                        "echo $cdrom >  $usbPath/cdrom",
                        "echo " + imageItem.rootPath + " > " + usbPath + "/file",
                        "setprop sys.usb.config mass_storage").exec()
                result = """${imageItem.name} mounted!
"""
                true
            }
            else -> {
                result = "not mounted!\n"
                false
            }
        }
    }

    init {
        name = "Mounting"
        description = "Mounting " + imageItem.name + " in " + mode.toLowerCase(Locale.ROOT) + " mode..."
        this.imageItem = imageItem
        this.mode = mode
    }
}
package com.jimzrt.umsmounter.tasks

import android.content.Context
import java.io.File

class CheckMassStorageTask : BaseTask() {
    override fun execute(): Boolean {
        val sharedPref = ctx!!.get()!!.getSharedPreferences(null, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val configfsDir = File("/config/usb_gadget/g1/functions/mass_storage.0/lun.0")
        val usbDir = File("/sys/class/android_usb/android0/f_mass_storage/lun")
        val usbDir2 = File("/sys/class/android_usb/android0/f_mass_storage/lun0")
        return if (configfsDir.exists() && configfsDir.isDirectory) {
            result = "mass_storage via configfs supported!\n"
            editor.putString("usbMode", "configfs")
            editor.putString("usbPath", configfsDir.absolutePath)
            val cdrom = File("/config/usb_gadget/g1/functions/mass_storage.0/lun.0/cdrom")
            if (cdrom.exists()) {
                editor.putBoolean("cdrom", true)
            } else {
                editor.putBoolean("cdrom", false)
            }
            editor.apply()
            true
        } else if (usbDir.exists() && usbDir.isDirectory) {
            result = "mass_storage via android_usb supported!\n"
            editor.putString("usbMode", "android_usb")
            editor.putString("usbPath", usbDir.absolutePath)
            val cdrom = File("/sys/class/android_usb/android0/f_mass_storage/lun/cdrom")
            if (cdrom.exists()) {
                editor.putBoolean("cdrom", true)
            } else {
                editor.putBoolean("cdrom", false)
            }
            editor.apply()
            true
        } else if (usbDir2.exists() && usbDir2.isDirectory) {
            result = "mass_storage via android_usb supported!\n"
            editor.putString("usbMode", "android_usb")
            editor.putString("usbPath", usbDir2.absolutePath)
            val cdrom = File("/sys/class/android_usb/android0/f_mass_storage/lun/cdrom")
            if (cdrom.exists()) {
                editor.putBoolean("cdrom", true)
            } else {
                editor.putBoolean("cdrom", false)
            }
            editor.apply()
            true
        } else {
            result = "mass_storage not supported!\n"
            false
        }
    }

    init {
        name = "Checking mass_storage"
        description = "Checking mass_storage support..."
    }
}
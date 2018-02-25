package com.example.james.myapplication.Model;

import com.topjohnwu.superuser.Shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;


/**
 * Created by james on 23.02.18.
 */

public class CreateImageTask extends Task {
    @Override
    public void execute() {


        RandomAccessFile f = null;
        try {
            f = new RandomAccessFile("/sdcard/UMSMounter/tmp.img", "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            f.setLength(1024 * 1024 * 1024 * 1024);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Shell.Sync.sh(new String[]{"busybox truncate -s1024M /sdcard/UMSMounter/tmp.img","echo \"o\\nn\\np\\n1\\n2\\n\\nt\\nb\\na\\n1\\nw\\n\" | busybox fdisk -S 32 -H 64 /sdcard/UMSMounter/tmp.img", "busybox dd if=/sdcard/UMSMounter/tmp.img of=/sdcard/UMSMounter/disk.img bs=512 count=2048", "rm /sdcard/UMSMounter/tmp.img", "busybox truncate -s1023M /sdcard/UMSMounter/fat.img", "busybox mkfs.vfat -n DRIVE /sdcard/UMSMounter/fat.img", "cat /sdcard/UMSMounter/fat.img >> /sdcard/UMSMounter/disk.img", "rm /sdcard/UMSMounter/fat.img"});

        String sourceFile = "/sdcard/UMSMounter/fat.img";
        String destFile = "/sdcard/UMSMounter/disk.img";
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream(sourceFile);
            fos = new FileOutputStream(destFile,true);

            byte[] buffer = new byte[1024*512];
            int noOfBytes = 0;

            System.out.println("Copying file using streams");

            // read bytes from source file and write to destination file
            while ((noOfBytes = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, noOfBytes);
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found" + e);
        }
        catch (IOException ioe) {
            System.out.println("Exception while copying file " + ioe);
        }
        finally {
            // close the streams using close method
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            }
            catch (IOException ioe) {
                System.out.println("Error while closing stream: " + ioe);
            }
        }

        File src = new File(sourceFile);
        src.delete();

    }

}

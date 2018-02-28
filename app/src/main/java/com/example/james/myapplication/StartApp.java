package com.example.james.myapplication;

import com.downloader.PRDownloader;
import com.topjohnwu.superuser.BusyBox;
import com.topjohnwu.superuser.Shell;

/**
 * Created by james on 24.02.18.
 */

public class StartApp extends Shell.ContainerApp {

    @Override
    public void onCreate() {
        super.onCreate();
        // Set flags
        Shell.setFlags(Shell.FLAG_REDIRECT_STDERR);
        Shell.verboseLogging(BuildConfig.DEBUG);
        // Use internal busybox
        BusyBox.setup(this);
        PRDownloader.initialize(getApplicationContext());

    }

}

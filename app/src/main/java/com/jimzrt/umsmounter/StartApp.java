package com.jimzrt.umsmounter;

import com.topjohnwu.superuser.BusyBox;
import com.topjohnwu.superuser.ContainerApp;
import com.topjohnwu.superuser.Shell;

public class StartApp extends ContainerApp {

    @Override
    public void onCreate() {
        super.onCreate();
        // Set flags
        Shell.Config.setFlags(Shell.FLAG_REDIRECT_STDERR);
        Shell.Config.verboseLogging(BuildConfig.DEBUG);

        // Use libsu's internal BusyBox
        BusyBox.setup(this);


    }

}

package com.jimzrt.umsmounter.Tasks;

import com.jimzrt.umsmounter.Activities.MainActivity;
import com.topjohnwu.superuser.Shell;

import java.util.List;

public class SetPathsTask extends BaseTask {

    public SetPathsTask() {
        this.name = "Set paths";
        this.description = "Setting paths...";
    }

    @Override
    public boolean execute() {
        List<String> out = Shell.Sync.sh("echo $EMULATED_STORAGE_SOURCE", "echo $EMULATED_STORAGE_TARGET");
        MainActivity.ROOTPATH = out.get(0) + "/0" + MainActivity.ROOTDIR;
        MainActivity.USERPATH = out.get(1) + "/0" + MainActivity.ROOTDIR;
        return true;

    }


}
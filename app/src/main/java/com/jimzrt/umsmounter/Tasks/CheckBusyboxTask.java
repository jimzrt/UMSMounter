package com.jimzrt.umsmounter.Tasks;

import com.topjohnwu.superuser.Shell;

import java.util.List;


public class CheckBusyboxTask extends BaseTask {

    public CheckBusyboxTask(){
        this.name = "Check Busybox";
        this.description = "Checking busybox...";
    }
    @Override
    public void execute() {
        List<String> output = Shell.Sync.sh("busybox");

        if (output.size() == 0) {
            this.successful=false;
            this.result = "Busybox not found!\n";
        } else {
            this.successful = true;
            this.result ="Busybox found!\n";

        }




    }


}

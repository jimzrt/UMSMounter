package com.jimzrt.umsmounter.Tasks;


import com.topjohnwu.superuser.Shell;

public class CheckRootTask extends BaseTask {

    public CheckRootTask(){
        this.name = "Check Root";
        this.description = "Checking root...";
    }
    @Override
    public boolean execute() {
        if(Shell.rootAccess()){
           this.result ="Root working!\n";
            return true;
        } else {
            this.result ="Root not working!\n";
            return false;
        }



    }


}

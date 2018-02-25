package com.example.james.myapplication.Model;


import com.topjohnwu.superuser.Shell;

public class CheckRootTask extends Task {

    public CheckRootTask(){
        this.name = "Check Root";
        this.description = "Checking root...";
    }
    @Override
    public void execute() {
        if(Shell.rootAccess()){
           this.successful = true;
           this.result ="Root working!\n";
        } else {
            this.successful = false;
            this.result ="Root not working!\n";
        }



    }


}

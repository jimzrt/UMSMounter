package com.example.james.myapplication.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.example.james.myapplication.Model.Task;

import java.lang.ref.WeakReference;

public class BackgroundTask extends AsyncTask<Void, Void, Void> {
    private ProgressDialog dialog = null;
    private Task[] tasks = null;
    WeakReference<Activity> mWeakActivity;

    // you may separate this or combined to caller class.
    public interface AsyncResponse {
        void processFinish(Boolean successful, String output);
    }

    AsyncResponse delegate = null;


    public BackgroundTask(Activity activity) {
        mWeakActivity = new WeakReference<>(activity);
    }

    public BackgroundTask setDelegate(AsyncResponse delegate){
        this.delegate = delegate;
        return this;
    }



    public BackgroundTask setTasks(Task[] tasks) {
        this.tasks = tasks;
        return this;
    }


    @Override
    protected void onPreExecute() {

        dialog = new ProgressDialog(mWeakActivity.get());
        dialog.setTitle("Starting Tasks");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {

        StringBuilder description = new StringBuilder();
        String newline = "";

        if(tasks != null){
            for(Task task : tasks){
                description.append(newline).append(task.getDescription());
                String finalDescription = description.toString();
                mWeakActivity.get().runOnUiThread(() -> {
                    dialog.setTitle(task.getName());
                    dialog.setMessage(finalDescription);

                });
                task.execute();
                String success;
                if(task.successful){
                    success = "✓";
                    // break;
                } else {
                    success = "✗";
                }

                description.append(success);
                String finalDescription2 = description.toString();
                mWeakActivity.get().runOnUiThread(() -> {
                    dialog.setMessage(finalDescription2);

                });
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                newline = "\n";

                if(!task.successful) {
                    break;
                }

            }

        }


        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        dialog.dismiss();


        if(delegate != null){
            StringBuilder results = new StringBuilder();
            Boolean success = true;
            StringBuilder errorString = new StringBuilder();
            for(Task task : tasks){
                if(!task.successful){
                    success = false;
                    errorString.append(task.getResult()).append("\n");
                    break;
                }
                results.append(task.getResult());
            }
            if(success){
                delegate.processFinish(true, results.toString());

            } else {
                delegate.processFinish(false, errorString.toString());
            }

        }

    }


}
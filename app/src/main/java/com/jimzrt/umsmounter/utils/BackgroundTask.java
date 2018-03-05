package com.jimzrt.umsmounter.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.jimzrt.umsmounter.tasks.BaseTask;

import java.lang.ref.WeakReference;

public class BackgroundTask extends AsyncTask<Void, Void, Void> {
    WeakReference<Activity> mWeakActivity;
    AsyncResponse delegate = null;
    String errorMessage;
    boolean successful = true;
    private ProgressDialog dialog = null;
    private BaseTask[] tasks = null;
    public BackgroundTask(Activity activity) {
        mWeakActivity = new WeakReference<>(activity);
    }

    public BackgroundTask setDelegate(AsyncResponse delegate) {
        this.delegate = delegate;
        return this;
    }

    public BackgroundTask setTasks(BaseTask[] tasks) {
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

        if (tasks != null) {
            for (BaseTask task : tasks) {
                description.append(newline).append(task.getDescription());
                String finalDescription = description.toString();
                mWeakActivity.get().runOnUiThread(() -> {
                    dialog.setTitle(task.getName());
                    dialog.setMessage(finalDescription);

                });

                String success;
                task.setContext(mWeakActivity);
                if (task.execute()) {
                    success = "✓";
                    // break;
                } else {
                    success = "✗";
                    description.append(success);
                    String finalDescription2 = description.toString();
                    mWeakActivity.get().runOnUiThread(() -> {
                        dialog.setMessage(finalDescription2);

                    });
                    successful = false;
                    errorMessage = task.getResult();
                    break;
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


            }

        }


        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        dialog.dismiss();


        if (delegate != null) {
            StringBuilder results = new StringBuilder();
            StringBuilder errorString = new StringBuilder();

            for (BaseTask task : tasks) {
                results.append(task.getResult());
            }


            if (successful) {
                delegate.processFinish(true, results.toString());

            } else {
                delegate.processFinish(false, errorMessage);
            }

        }

    }

    // you may separate this or combined to caller class.
    public interface AsyncResponse {
        void processFinish(Boolean successful, String output);
    }


}
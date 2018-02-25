package com.example.james.myapplication;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.james.myapplication.Utils.Helper;

/**
 * Created by james on 14.02.18.
 */

public class DownloadFragment extends Fragment {

    private String statusMessage(Cursor c) {
        String msg = "???";

        switch (c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
            case DownloadManager.STATUS_FAILED:
                msg = "Download failed!";
                break;

            case DownloadManager.STATUS_PAUSED:
                msg = "Download paused!";
                break;

            case DownloadManager.STATUS_PENDING:
                msg = "Download pending!";
                break;

            case DownloadManager.STATUS_RUNNING:
                msg = "Download in progress!";
                break;

            case DownloadManager.STATUS_SUCCESSFUL:
                msg = "Download complete!";
                break;

            default:
                msg = "Download is nowhere in sight";
                break;
        }

        return (msg);
    }

    private String DownloadStatus(Cursor cursor, long DownloadId){

        //column for download  status
        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int status = cursor.getInt(columnIndex);
        //column for reason code if the download failed or paused
        int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
        int reason = cursor.getInt(columnReason);
        //get the download filename


        String statusText = "";
        String reasonText = "";

        switch(status){
            case DownloadManager.STATUS_FAILED:
                statusText = "STATUS_FAILED";
                switch(reason){
                    case DownloadManager.ERROR_CANNOT_RESUME:
                        reasonText = "ERROR_CANNOT_RESUME";
                        break;
                    case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                        reasonText = "ERROR_DEVICE_NOT_FOUND";
                        break;
                    case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                        reasonText = "ERROR_FILE_ALREADY_EXISTS";
                        break;
                    case DownloadManager.ERROR_FILE_ERROR:
                        reasonText = "ERROR_FILE_ERROR";
                        break;
                    case DownloadManager.ERROR_HTTP_DATA_ERROR:
                        reasonText = "ERROR_HTTP_DATA_ERROR";
                        break;
                    case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                        reasonText = "ERROR_INSUFFICIENT_SPACE";
                        break;
                    case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                        reasonText = "ERROR_TOO_MANY_REDIRECTS";
                        break;
                    case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                        reasonText = "ERROR_UNHANDLED_HTTP_CODE";
                        break;
                    case DownloadManager.ERROR_UNKNOWN:
                        reasonText = "ERROR_UNKNOWN";
                        break;
                }
                break;
            case DownloadManager.STATUS_PAUSED:
                statusText = "STATUS_PAUSED";
                switch(reason){
                    case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                        reasonText = "PAUSED_QUEUED_FOR_WIFI";
                        break;
                    case DownloadManager.PAUSED_UNKNOWN:
                        reasonText = "PAUSED_UNKNOWN";
                        break;
                    case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                        reasonText = "PAUSED_WAITING_FOR_NETWORK";
                        break;
                    case DownloadManager.PAUSED_WAITING_TO_RETRY:
                        reasonText = "PAUSED_WAITING_TO_RETRY";
                        break;
                }
                break;
            case DownloadManager.STATUS_PENDING:
                statusText = "STATUS_PENDING";
                break;
            case DownloadManager.STATUS_RUNNING:
                statusText = "STATUS_RUNNING";
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                statusText = "STATUS_SUCCESSFUL";
                break;
        }




            return statusText + "\n " + reasonText;



    }

    long downloadId;
    DownloadManager manager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);

        Button button = view.findViewById(R.id.createImageButton);
        TextView progressText = view.findViewById(R.id.progessText);
        progressText.setText("Not started");
        button.setOnClickListener(view1 -> {
            progressText.setText("Starting...");

            String urlDownload = "https://mirror.rackspace.com/archlinux/iso/2017.12.01/archlinux-2017.12.01-x86_64.iso";
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(urlDownload));

            request.setDescription("Testando");
            request.setTitle("Download");
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "archlinux-2017.12.01-x86_64.iso");

            manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);

            downloadId = manager.enqueue(request);

            final ProgressBar mProgressBar = view.findViewById(R.id.imageProgress);

            new Thread(() -> {

                boolean downloading = true;
                long time = System.currentTimeMillis();
                long bytesDownloaded = 0;
                long downloaded = 0;
                while (downloading) {

//                    DownloadManager.Query q = new DownloadManager.Query();
//                    q.setFilterById(downloadId);
//
//                    Cursor cursor = manager.query(q);
//                    cursor.moveToFirst();
//                    int bytes_downloaded = cursor.getInt(cursor
//                            .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
//                    int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
//
//                    if ((cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) || (cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.) {
//                        downloading = false;
//                    }
//
//                    final int dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);
//
//                    getActivity().runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//
//                            mProgressBar.setProgress((int) dl_progress);
//
//                        }
//                    });
//
//                    Log.d("lala", statusMessage(cursor));
//                    cursor.close();




                    DownloadManager.Query ImageDownloadQuery = new DownloadManager.Query();
                    //set the query filter to our previously Enqueued download
                    ImageDownloadQuery.setFilterById(downloadId);

                    //Query the download manager about downloads that have been requested.
                    Cursor cursor = manager.query(ImageDownloadQuery);
                    if(cursor.moveToFirst()){


                        long bytes_downloaded = cursor.getLong(cursor
                            .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    long bytes_total = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                        if((System.currentTimeMillis() - time) > 5000){
                            time = System.currentTimeMillis();
                            downloaded = (bytes_downloaded - bytesDownloaded) / 5;
                            bytesDownloaded = bytes_downloaded;
                        }

                    final int dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);

                        long finalDownloaded = downloaded;
                        getActivity().runOnUiThread(() -> {

                            mProgressBar.setProgress((int) dl_progress);
                            progressText.setText("Downloading: " + Helper.humanReadableByteCount(bytes_downloaded)+ "/" + Helper.humanReadableByteCount(bytes_total) +"\n");
                            if(finalDownloaded == 0){
                                progressText.append("Speed: N/A\n");
                                progressText.append("Time left: N/A\n");
                            }else {
                                progressText.append("Speed: " + Helper.humanReadableByteCount(finalDownloaded) + "/s\n");
                                progressText.append("Time left: " + Helper.parseTime((int)((bytes_total-bytes_downloaded)/finalDownloaded)*1000) + "\n");
                            }

                            progressText.append("Status: " + DownloadStatus(cursor, downloadId));
                        });
                    } else {
                        downloading = false;
                        Log.i("lala", "stopped downloading");

                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                mProgressBar.setProgress(0);
                                progressText.setText("Download Stopped");

                            }
                        });
                    }


                }

            }).start();


        });

        Button stopButton = view.findViewById(R.id.button3);
        stopButton.setOnClickListener(v -> {

            manager.remove(downloadId);

        });


        return view;
    }
}

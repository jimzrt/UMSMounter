package com.example.james.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.james.myapplication.Model.DownloadItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by james on 27.02.18.
 */

public class LinuxImageFragment extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //((MainActivity)getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
        // ((MainActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(null);


        super.onCreate(savedInstanceState);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();

        String downloadItemString = intent.getStringExtra("downloadItem");
        Gson gson = new GsonBuilder().create();

        DownloadItem downloadItem = gson.fromJson(downloadItemString, DownloadItem.class);
        setContentView(R.layout.linux_image);
        TextView nameView = findViewById(R.id.linuxName);
        nameView.setText(downloadItem.name);


    }
}

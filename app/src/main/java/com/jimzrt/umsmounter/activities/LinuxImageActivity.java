package com.jimzrt.umsmounter.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jimzrt.umsmounter.R;
import com.jimzrt.umsmounter.model.DownloadItem;
import com.jimzrt.umsmounter.model.Release;

/**
 * Created by james on 27.02.18.
 */

public class LinuxImageActivity extends AppCompatActivity {


    private ListView releaseList;


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
        TextView urlView = findViewById(R.id.linuxUrl);
        Button downloadButton = findViewById(R.id.liunxDownload);
        nameView.setText(downloadItem.name);
        urlView.setText(downloadItem.url);
        releaseList = findViewById(R.id.releaseList);
        releaseList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        ArrayAdapter<Release> itemsAdapter =
                new ArrayAdapter<>(this, R.layout.linux_image_list_row, downloadItem.releases);

        releaseList.setAdapter(itemsAdapter);


        releaseList.setItemChecked(0, true);


        //EventBus.getDefault().post(new MessageEvent(downloadItem.name));
        //finish();


        downloadButton.setOnClickListener(v -> {
            Release rel = (Release) releaseList.getItemAtPosition(releaseList.getCheckedItemPosition());
            String url = rel.url;
            String fileName = url.substring(url.lastIndexOf('/') + 1, url.length());


            Intent returnIntent = new Intent();
            returnIntent.putExtra("name", fileName);
            returnIntent.putExtra("url", url);


            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        });


    }

}

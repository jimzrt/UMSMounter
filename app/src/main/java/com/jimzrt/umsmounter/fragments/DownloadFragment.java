package com.jimzrt.umsmounter.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jimzrt.umsmounter.R;
import com.jimzrt.umsmounter.listadapters.ImageDownloadListAdapter;
import com.jimzrt.umsmounter.model.DownloadItem;
import com.jimzrt.umsmounter.utils.Helper;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class DownloadFragment extends Fragment {

    OnImageDownloadListener mCallback;
    RecyclerView recyclerView;
    ImageDownloadListAdapter listViewAdapter;
    private TextView updatedTextView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnImageDownloadListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Download Image");
        }


        View view = inflater.inflate(R.layout.fragment_download, container, false);
        recyclerView = view.findViewById(R.id.downloadImageList);
        updatedTextView = view.findViewById(R.id.updatedTextView);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        listViewAdapter = new ImageDownloadListAdapter(null, mCallback);
        recyclerView.setAdapter(listViewAdapter);
        recyclerView.setHasFixedSize(true);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                mLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        new DownloadFilesTask().execute(false);

        ImageButton refreshButton = view.findViewById(R.id.buttonRefreshDownload);
        refreshButton.setOnClickListener(v -> {
            listViewAdapter.clear();
            new DownloadFilesTask().execute(true);
        });


        return view;
    }

    public interface OnImageDownloadListener {
        void OnImageListClick(DownloadItem downloadItem);
    }

    private class DownloadFilesTask extends AsyncTask<Boolean, Void, List<DownloadItem>> {


        protected List<DownloadItem> doInBackground(Boolean... forceReload) {


            boolean force = forceReload[0];


            Type imageListType = new TypeToken<ArrayList<DownloadItem>>() {
            }.getType();
            File file = new File(getContext().getFilesDir(), "data.json");
            List<DownloadItem> list = null;
            if (file.exists() && !force) {
                try (Reader reader = new FileReader(getContext().getFilesDir() + "/data.json")) {
                    Gson gson = new GsonBuilder().create();

                    list = gson.fromJson(reader, imageListType);


                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {

                //  try (Reader reader = new InputStreamReader(new URL("http://pelzekaufen.de/main.json").openStream())) {
                try (Reader reader = new InputStreamReader(new URL("http://softwarebakery.com/apps/drivedroid/repositories/distrowatch.json").openStream())) {

                    // Reader reader = new InputStreamReader(new URL("http://pelzekaufen.de/main.json").openStream());

                    Gson gson = new GsonBuilder().create();

                    list = gson.fromJson(reader, imageListType);
                    Iterator<DownloadItem> iterator = list.iterator();

                    while (iterator.hasNext()) {
                        DownloadItem item = iterator.next();
                        if (item.releases.length == 0) {
                            iterator.remove();
                        }
                    }

                    Collections.sort(list);

                    try (Writer writer = new FileWriter(getContext().getFilesDir() + "/data.json")) {
                        gson = new GsonBuilder().create();
                        gson.toJson(list, writer);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            return list;
        }


        protected void onPostExecute(List<DownloadItem> list) {


            File file = new File(getContext().getFilesDir() + "/data.json");
            updatedTextView.setText("Last updated: " + Helper.convertDate("" + file.lastModified()));

            listViewAdapter.setItems(list);
            listViewAdapter.notifyDataSetChanged();
        }
    }
}

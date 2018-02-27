package com.example.james.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.james.myapplication.Model.DownloadItem;
import com.example.james.myapplication.Model.ImageDownloadListAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

/**
 * Created by james on 14.02.18.
 */

public class DownloadFragment extends Fragment {

    public interface OnImageDownloadListener {
        void OnImageListClick(DownloadItem downloadItem);
    }

    OnImageDownloadListener mCallback;

    RecyclerView recyclerView;
    ImageDownloadListAdapter listViewAdapter;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Thread thread = new Thread(() -> {
            try {
                Reader reader = new InputStreamReader(new URL("http://softwarebakery.com/apps/drivedroid/repositories/main.json").openStream());
                Gson gson = new GsonBuilder().create();

                DownloadItem[] obj = gson.fromJson(reader, DownloadItem[].class);
                getActivity().runOnUiThread(() -> {
                    listViewAdapter.setItems(obj);
                    listViewAdapter.notifyDataSetChanged();
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        thread.start();

        View view = inflater.inflate(R.layout.fragment_test, container, false);
        recyclerView = view.findViewById(R.id.downloadImageList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        listViewAdapter = new ImageDownloadListAdapter(null, mCallback);
        recyclerView.setAdapter(listViewAdapter);
        recyclerView.setHasFixedSize(true);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                mLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);



        return view;
    }
}

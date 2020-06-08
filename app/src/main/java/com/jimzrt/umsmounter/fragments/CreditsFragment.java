package com.jimzrt.umsmounter.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.jimzrt.umsmounter.BuildConfig;
import com.jimzrt.umsmounter.R;

import java.util.Objects;


public class CreditsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (getActivity() != null) {
            Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setTitle("Credits");
        }


        View view = inflater.inflate(R.layout.fragment_credits, container, false);
        TextView versionView = view.findViewById(R.id.versionView);
        TextView librariesView = view.findViewById(R.id.librariesView);
        TextView ackView = view.findViewById(R.id.ackView);
        TextView sourceView = view.findViewById(R.id.sourceView);

        versionView.setText("Version: " + BuildConfig.VERSION_NAME + "-" + BuildConfig.BUILD_TYPE);
        librariesView.setText("" +
                "libsu - https://github.com/topjohnwu/libsu\n" +
                "google-gson - https://github.com/google/gson\n" +
                "Fetch - https://github.com/tonyofrancis/Fetch\n");
        ackView.setText("DriveDroid\n" +
                "https://softwarebakery.com/projects/drivedroid\nMain inspiration");
        sourceView.setText("https://github.com/jimzrt/UMSMounter");


        return view;
    }
}

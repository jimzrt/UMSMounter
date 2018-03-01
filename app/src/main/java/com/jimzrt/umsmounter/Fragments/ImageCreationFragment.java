package com.jimzrt.umsmounter.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.jimzrt.umsmounter.R;
import com.jimzrt.umsmounter.Utils.Helper;
import com.topjohnwu.superuser.Shell;


public class ImageCreationFragment extends Fragment {

    public interface OnImageCreationListener {
        void OnImageCreation(String imageItem);
    }

    OnImageCreationListener mCallback;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnImageCreationListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {




        View view = inflater.inflate(R.layout.fragment_image_creation, container, false);

        Button button = view.findViewById(R.id.createImageButton);
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Create Image");
        }

        // ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);




        button.setOnClickListener(view1 -> {
            InputMethodManager inputManager = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow((null == getActivity().getCurrentFocus()) ? null : getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

            String imageName = ((TextView) view.findViewById(R.id.imageName)).getText().toString() + ".img";
            int imageSize = Integer.parseInt(((TextView) view.findViewById(R.id.imageSize)).getText().toString());



            ProgressDialog barProgressDialog = new ProgressDialog(getContext());

            barProgressDialog.setTitle("Creating " + imageName + "...");
            barProgressDialog.setMessage("Starting ...");
            barProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            barProgressDialog.setProgress(0);
            barProgressDialog.setMax(100);
            barProgressDialog.setCancelable(false);
            barProgressDialog.show();


            new Thread(() -> {
                try {


                    getActivity().runOnUiThread(() -> {
                        barProgressDialog.setMessage("Preparing...");
                        Helper.verifyStoragePermissions(getActivity());
                    });

                    String cacheDir = getContext().getCacheDir().getAbsolutePath();

                    Shell.Sync.sh("busybox truncate -s" + imageSize + "M " + cacheDir + "/tmp.img", "echo \"o\\nn\\np\\n1\\n2\\n\\nt\\nc\\na\\n1\\nw\\n\" | busybox fdisk -S 32 -H 64 " + cacheDir + "/tmp.img", "busybox dd if=" + cacheDir + "/tmp.img of=" + MainFragment.ROOTPATH + "/" + imageName + " bs=512 count=2048", "rm " + cacheDir + "/tmp.img", "busybox truncate -s" + (imageSize - 1) + "M " + cacheDir + "/fat.img", "busybox mkfs.vfat -n DRIVE " + cacheDir + "/fat.img", "chmod 0755 " + cacheDir + "/fat.img");
                    getActivity().runOnUiThread(() -> {
                        barProgressDialog.setMessage("Creating Image...");
                    });


                    barProgressDialog.dismiss();

                    getActivity().runOnUiThread(() -> {
                        //    Toast.makeText(getActivity(), "Image successfully created",Toast.LENGTH_LONG).show();
                        mCallback.OnImageCreation(imageName);
                      //  getFragmentManager().popBackStackImmediate();
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();



        });


        return view;
    }
}

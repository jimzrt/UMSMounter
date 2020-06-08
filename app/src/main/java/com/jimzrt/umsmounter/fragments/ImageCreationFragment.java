package com.jimzrt.umsmounter.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.StatFs;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.jimzrt.umsmounter.R;
import com.jimzrt.umsmounter.activities.MainActivity;
import com.jimzrt.umsmounter.utils.Helper;
import com.topjohnwu.superuser.Shell;


public class ImageCreationFragment extends Fragment {

    private OnImageCreationListener mCallback;

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

        TextView nameTextView = view.findViewById(R.id.imageName);
        TextView sizeTextView = view.findViewById(R.id.imageSize);

        StatFs stat = new StatFs(MainActivity.USERPATH);
        long bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();

        TextView freeSpaceView = view.findViewById(R.id.freeSpaceView);
        freeSpaceView.setText("Free Space: " + Helper.humanReadableByteCount(bytesAvailable));


        button.setOnClickListener(view1 -> {
            InputMethodManager inputManager = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);


            String nameText = nameTextView.getText().toString();
            if (nameText.isEmpty()) {
                nameTextView.requestFocus();
                nameTextView.setError("Cannot be empty");
                return;
            }

            String sizeText = sizeTextView.getText().toString();
            if (sizeText.isEmpty()) {
                sizeTextView.requestFocus();
                sizeTextView.setError("Cannot be empty");
                return;
            }
            if ((Long.parseLong(sizeText) * 1024L * 1024L) > bytesAvailable) {
                sizeTextView.requestFocus();
                sizeTextView.setError("Not enough space");
                return;
            }
            if (Integer.parseInt(sizeText) < 2) {
                sizeTextView.requestFocus();
                sizeTextView.setError("At least 2 MB");
                return;
            }


            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow((null == getActivity().getCurrentFocus()) ? null : getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

            String imageName = nameTextView.getText().toString() + ".img";
            int imageSize = Integer.parseInt(sizeTextView.getText().toString());



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


                    Shell.su("busybox truncate -s" + imageSize + "M " + MainActivity.ROOTPATH + "/tmp.img", "echo \"x\\nc\\n" + imageSize + "\\nr\\no\\nn\\np\\n1\\n2\\n\\nt\\nc\\na\\n1\\nw\\n\" | busybox fdisk -S 32 -H 64 " + MainActivity.ROOTPATH + "/tmp.img", "fdisk -l " + MainActivity.ROOTPATH + "/tmp.img", "busybox dd if=" + MainActivity.ROOTPATH + "/tmp.img of=" + MainActivity.ROOTPATH + "/" + imageName + " bs=512 count=2048", "rm " + MainActivity.ROOTPATH + "/tmp.img", "busybox truncate -s" + (imageSize - 1) + "M " + MainActivity.ROOTPATH + "/fat.img", "busybox mkfs.vfat -n DRIVE " + MainActivity.ROOTPATH + "/fat.img", "chmod 777 " + MainActivity.ROOTPATH + "/fat.img", "chmod 777 " + MainActivity.ROOTPATH + "/" + imageName).exec();
                    getActivity().runOnUiThread(() -> {
                        barProgressDialog.setProgress(100);
                        barProgressDialog.dismiss();
                    });




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

    public interface OnImageCreationListener {
        void OnImageCreation(String imageItem);
    }
}

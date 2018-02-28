package com.example.james.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.james.myapplication.Utils.Helper;
import com.topjohnwu.superuser.Shell;

/**
 * Created by james on 23.02.18.
 */

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {




        View view = inflater.inflate(R.layout.fragment_image_creation, container, false);

        Button button = view.findViewById(R.id.createImageButton);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Create Image");

       // ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);




        button.setOnClickListener(view1 -> {
            InputMethodManager inputManager = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow((null == getActivity().getCurrentFocus()) ? null : getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

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

                    Shell.Sync.sh("busybox truncate -s" + imageSize + "M " + cacheDir + "/tmp.img", "echo \"o\\nn\\np\\n1\\n2\\n\\nt\\nc\\na\\n1\\nw\\n\" | busybox fdisk -S 32 -H 64 " + cacheDir + "/tmp.img", "busybox dd if=" + cacheDir + "/tmp.img of=" + MainActivityFragment.ROOTPATH + "/" + imageName + " bs=512 count=2048", "rm " + cacheDir + "/tmp.img", "busybox truncate -s" + (imageSize - 1) + "M " + cacheDir + "/fat.img", "busybox mkfs.vfat -n DRIVE " + cacheDir + "/fat.img", "chmod 0755 " + cacheDir + "/fat.img");
                    getActivity().runOnUiThread(() -> {
                        barProgressDialog.setMessage("Creating Image...");
                    });
                    //    String sourceFile = cacheDir + "/fat.img";
                    //    String destFile = MainActivityFragment.USERPATH + "/" + imageName;
//                    FileInputStream fis = null;
//                    FileOutputStream fos = null;
//
//                    try {
//                        fis = new FileInputStream(sourceFile);
//                        fos = new FileOutputStream(destFile,true);
//
//                        long size = fis.getChannel().size() + fos.getChannel().size();
//                        byte[] buffer = new byte[1024*512];
//                        long pos =  (int) Math.ceil(fos.getChannel().size() / buffer.length);
//
//                        int noOfBytes = 0;
//
//                        System.out.println("Copying file using streams");
//
//                        // read bytes from source file and write to destination file
//                        while ((noOfBytes = fis.read(buffer)) != -1) {
//                            // if(pos % 4 == 0){
//
//                            pos += 1;
//
//                            long finalPos = pos;
//                            getActivity().runOnUiThread(() -> {
//
//                                barProgressDialog.setMessage(Helper.humanReadableByteCount(finalPos*buffer.length) + " / " + Helper.humanReadableByteCount(size));
//                                barProgressDialog.setProgress((int) ((finalPos*buffer.length * 100l) / size));
//                            });
//                            //}
//                            fos.write(buffer, 0, noOfBytes);
//                        }
//                    }
//                    catch (FileNotFoundException e) {
//                        System.out.println("File not found" + e);
//                    }
//                    catch (IOException ioe) {
//                        System.out.println("Exception while copying file " + ioe);
//                    }
//                    finally {
//                        // close the streams using close method
//                        try {
//                            if (fis != null) {
//                                fis.close();
//                            }
//                            if (fos != null) {
//                                fos.close();
//                            }
//                        }
//                        catch (IOException ioe) {
//                            System.out.println("Error while closing stream: " + ioe);
//                        }
//                    }
//
//                    File src = new File(sourceFile);
//                    src.delete();


                //    Thread.sleep(500);

                    barProgressDialog.dismiss();
                  //
                    getActivity().runOnUiThread(() -> {
                        //    Toast.makeText(getActivity(), "Image successfully created",Toast.LENGTH_LONG).show();
                        mCallback.OnImageCreation(imageName);
                      //  getFragmentManager().popBackStackImmediate();
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();



//            progressText.setText("Starting...");
//
//
//
//            final ProgressBar mProgressBar = view.findViewById(R.id.imageProgress);
//
//            new Thread(() -> {
//                getActivity().runOnUiThread(() -> {
//                            progressText.setText("Preparing...");
//                        });
//
//                Shell.SH.run(new String[]{"busybox truncate -s1024M " + MainActivityFragment.ROOTPATH + "/tmp.img","echo \"o\\nn\\np\\n1\\n2\\n\\nt\\nb\\na\\n1\\nw\\n\" | busybox fdisk -S 32 -H 64 " + MainActivityFragment.ROOTPATH + "/tmp.img", "busybox dd if=" + MainActivityFragment.ROOTPATH + "/tmp.img of=" + MainActivityFragment.ROOTPATH + "/disk.img bs=512 count=2048", "rm " + MainActivityFragment.ROOTPATH + "/tmp.img", "busybox truncate -s1023M " + MainActivityFragment.ROOTPATH + "/fat.img", "busybox mkfs.vfat -n DRIVE " + MainActivityFragment.ROOTPATH + "/fat.img"});
//                getActivity().runOnUiThread(() -> {
//                progressText.setText("Creating Image...");
//                    mProgressBar.setProgress(0);
//                });
//                String sourceFile = "" + MainActivityFragment.ROOTPATH + "/fat.img";
//                String destFile = "" + MainActivityFragment.ROOTPATH + "/disk.img";
//                FileInputStream fis = null;
//                FileOutputStream fos = null;
//
//                try {
//                    fis = new FileInputStream(sourceFile);
//                    fos = new FileOutputStream(destFile,true);
//
//                    long size = fis.getChannel().size() + fos.getChannel().size();
//                    byte[] buffer = new byte[1024*512];
//                    long pos = fos.getChannel().size() / buffer.length;
//
//                    int noOfBytes = 0;
//
//                    System.out.println("Copying file using streams");
//
//                    // read bytes from source file and write to destination file
//                    while ((noOfBytes = fis.read(buffer)) != -1) {
//                       // if(pos % 4 == 0){
//
//
//                        long finalPos = pos;
//                        getActivity().runOnUiThread(() -> {
//                        progressText.setText(Helper.humanReadableByteCount(finalPos*buffer.length) + " / " + Helper.humanReadableByteCount(size));
//                            mProgressBar.setProgress((int) ((finalPos*buffer.length * 100l) / size));
//                        });
//                        //}
//                        pos += 1;
//                        fos.write(buffer, 0, noOfBytes);
//                    }
//                }
//                catch (FileNotFoundException e) {
//                    System.out.println("File not found" + e);
//                }
//                catch (IOException ioe) {
//                    System.out.println("Exception while copying file " + ioe);
//                }
//                finally {
//                    // close the streams using close method
//                    try {
//                        if (fis != null) {
//                            fis.close();
//                        }
//                        if (fos != null) {
//                            fos.close();
//                        }
//                    }
//                    catch (IOException ioe) {
//                        System.out.println("Error while closing stream: " + ioe);
//                    }
//                }
//
//                File src = new File(sourceFile);
//                src.delete();
//                getActivity().runOnUiThread(() -> {
//                            progressText.setText("Done!");
//                        });
//
////                boolean downloading = true;
////                long time = System.currentTimeMillis();
////                long bytesDownloaded = 0;
////                long downloaded = 0;
////                while (downloading) {
////
////
////
////
////
////                   //     final int dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);
////
////                        long finalDownloaded = downloaded;
////                        getActivity().runOnUiThread(() -> {
////
////                         //   mProgressBar.setProgress((int) dl_progress);
////                       //     progressText.setText("Downloading: " + Helper.humanReadableByteCount(bytes_downloaded)+ "/" + Helper.humanReadableByteCount(bytes_total) +"\n");
////                            if(finalDownloaded == 0){
////                                progressText.append("Speed: N/A\n");
////                                progressText.append("Time left: N/A\n");
////                            }else {
////                                progressText.append("Speed: " + Helper.humanReadableByteCount(finalDownloaded) + "/s\n");
////                         //       progressText.append("Time left: " + Helper.parseTime((int)((bytes_total-bytes_downloaded)/finalDownloaded)*1000) + "\n");
////                            }
////
////                      //      progressText.append("Status: " + DownloadStatus(cursor, downloadId));
////                        });
////                    }
//
//
//            }).start();


        });


        return view;
    }
}

package com.jimzrt.umsmounter.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.jimzrt.umsmounter.ListAdapters.ImageListAdapter;
import com.jimzrt.umsmounter.Model.ImageItem;
import com.jimzrt.umsmounter.R;
import com.jimzrt.umsmounter.Tasks.BaseTask;
import com.jimzrt.umsmounter.Tasks.MountImageTask;
import com.jimzrt.umsmounter.Tasks.UnmountingTask;
import com.jimzrt.umsmounter.Utils.BackgroundTask;
import com.jimzrt.umsmounter.Utils.Helper;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.topjohnwu.superuser.Shell;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements ImageListAdapter.OnImageButtonListener {

    public static final String ROOTPATH = "/data/media/0/UMSMounter";
    public static final String USERPATH = "/sdcard/UMSMounter";
    public static final String ROOTDIR = "/UMSMounter";

    // Animation animation;
    public ToggleButton toggleButton;
    // TextView logView = null;
    RecyclerView listView = null;
    ImageListAdapter listViewAdapter = null;
    Spinner usbMode = null;
    ArrayAdapter<String> usbModeAdapter = null;
    private View view;
    private boolean populate;
    private String functionMode = "mtp,adb";
    private Fetch mainFetch;

    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (populate) {
            populateList();
            populate = false;
        }
    }

    public void populateList() {
        String path = Environment.getExternalStorageDirectory().toString() + ROOTDIR;
        File f = new File(path);


        File files[] = f.listFiles();
        if (files != null) {
            for (File file : files) {
                ImageItem item = new ImageItem(file.getName(), ROOTPATH + "/" + file.getName(), USERPATH + "/" + file.getName(), Helper.humanReadableByteCount(file.length()));
                if (!listViewAdapter.contains(item)) {
                    listViewAdapter.addItem(item, false);
                }
            }
        }

        SharedPreferences sharedPref = getContext().getSharedPreferences(null, Context.MODE_PRIVATE);
        TextView textMode = view.findViewById(R.id.textMode);
        textMode.setText(sharedPref.getString("usbMode", "Not Supported"));


        // use a linear layout manager
        //   listViewAdapter.addItems(list);

        //listView.setOnItemClickListener(adapter);
        //adapter.addAll(list);
        List<String> modes = new ArrayList<>(Arrays.asList("Writable USB", "Read-only USB"));
        if (sharedPref.getBoolean("cdrom", false)) {
            modes.add("CD-ROM");
        }

        usbModeAdapter.clear();
        usbModeAdapter.addAll(modes);
        usbMode.setSelection(0);

    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (getActivity() != null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("UMS Mounter");
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onStop() {
        //    mainFetch.close();
        super.onStop();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);


        if (getActivity() != null)
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("UMS Mounter");
        listView = view.findViewById(R.id.listview);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        listView.setLayoutManager(mLayoutManager);

        listViewAdapter = new ImageListAdapter(new ArrayList<>(), getContext(), this);
//        DefaultItemAnimator animator = new DefaultItemAnimator() {
//            @Override
//            public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder) {
//                return true;
//            }
//        };
//        listView.setItemAnimator(animator);
        ((SimpleItemAnimator) listView.getItemAnimator()).setSupportsChangeAnimations(false);
        listView.setAdapter(listViewAdapter);
        listView.setHasFixedSize(true);
        listView.getItemAnimator().setChangeDuration(0);
        listView.getItemAnimator().setAddDuration(500);
        listView.getItemAnimator().setMoveDuration(500);
        listView.getItemAnimator().setRemoveDuration(500);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(listView.getContext(),
                mLayoutManager.getOrientation());
        listView.addItemDecoration(dividerItemDecoration);


        usbMode = view.findViewById(R.id.spinner);

        usbModeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,
                new ArrayList<>());
        //usbModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //  spinnerAdapter.notifyDataSetChanged();
        usbMode.setAdapter(usbModeAdapter);


        // ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
//        Button button = view.findViewById(R.id.button);
//        button.setOnClickListener(v -> {
//            mCallback.OnAboutButtonClick();
//        });

        //   logView = findViewById(R.id.editText);
        //logView.setKeyListener(null);
        // logView.append("==== UMS Mounter====\n");


        toggleButton = view.findViewById(R.id.toggleButton);
        toggleButton.setOnClickListener((View v) -> {
            if (toggleButton.isChecked()) {

                int checkedItemPosition = ((ImageListAdapter) listView.getAdapter()).getSelectedItemPosition();
                if (checkedItemPosition == -1) {
                    Toast.makeText(getContext(), "Please select Image", Toast.LENGTH_SHORT).show();
                    toggleButton.setChecked(false);
                    return;
                }
                ImageItem imageItem = ((ImageListAdapter) listView.getAdapter()).getItemAtPosition(checkedItemPosition);

                mount(imageItem);

            } else {

                unmount(functionMode);


            }
        });

        //  animation  = AnimationUtils.loadAnimation(getContext(),R.anim.rotate);

        // animation.setRepeatCount(-1);
        // animation.setDuration(2000);

        ImageButton refreshButton = view.findViewById(R.id.buttonRefresh);

        refreshButton.setOnClickListener((View v) -> {
            populateList();
            Toast.makeText(getContext(), "List updated!", Toast.LENGTH_LONG).show();
        });


        SwipeRefreshLayout mySwipeRefreshLayout = view.findViewById(R.id.swiperefresh);

        mySwipeRefreshLayout.setOnRefreshListener(
                () -> {

                    populateList();
                    mySwipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), "List updated!", Toast.LENGTH_LONG).show();
                }
        );


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View v, Bundle b) {
        populateList();

    }

    public void unmount(String functionMode) {
        (new BackgroundTask(getActivity())).setDelegate((successful, result) -> {
            if (successful) {
                ((ImageListAdapter) listView.getAdapter()).setMountedItemPosition(-1);


            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setMessage(result)
                        .setTitle("Error!");
                builder.setPositiveButton("Ok", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            //((TextView)findViewById(R.id.editText)).append(result);
        }).setTasks(new BaseTask[]{new UnmountingTask(functionMode)}).execute();
    }

    public void mount(ImageItem imageItem) {


        List<String> output = Shell.Sync.sh("getprop sys.usb.config");
        functionMode = output.get(0);
        if (functionMode == null || functionMode.contains("mass_storage")) {
            functionMode = "mtp,adb";
        }
        (new BackgroundTask(getActivity())).setDelegate((successful, result) -> {
            if (successful) {
                ((ImageListAdapter) listView.getAdapter()).setMountedItemPosition(((ImageListAdapter) listView.getAdapter()).getSelectedItemPosition());
                //     ((ImageListAdapter)listView.getAdapter()).setSelectedItemPosition(-1);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setMessage(result)
                        .setTitle("Error!");
                builder.setPositiveButton("Ok", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            // ((TextView)findViewById(R.id.editText)).append(result);
        }).setTasks(new BaseTask[]{new MountImageTask(imageItem, usbMode.getSelectedItem().toString(), getContext())}).execute();

    }


    @Override
    public void onMountImageButtonClicked() {

        int mountedItemPosition = ((ImageListAdapter) listView.getAdapter()).getMountedItemPosition();
        int checkedItemPosition = ((ImageListAdapter) listView.getAdapter()).getSelectedItemPosition();

        if (mountedItemPosition == checkedItemPosition) {
            unmount(functionMode);
            toggleButton.setChecked(false);
        } else if (mountedItemPosition != -1) {
            unmount(functionMode);
            mount(((ImageListAdapter) listView.getAdapter()).getItemAtPosition(checkedItemPosition));
            toggleButton.setChecked(true);
        } else {
            mount(((ImageListAdapter) listView.getAdapter()).getItemAtPosition(checkedItemPosition));
            toggleButton.setChecked(true);
        }


    }

    @Override
    public void onDeleteImageButtonClicked() {

        int checkedItemPosition = ((ImageListAdapter) listView.getAdapter()).getSelectedItemPosition();
        ImageItem checkedItem = ((ImageListAdapter) listView.getAdapter()).getItemAtPosition(checkedItemPosition);

        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());


        adb.setTitle("Delete");


        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setMessage("Do you really want to delete " + checkedItem.getName() + "?");


        adb.setPositiveButton("Delete", (dialog, which) -> {
            File file = new File(checkedItem.getUserPath());
            file.delete();
            Toast.makeText(getContext(), "Deleted!", Toast.LENGTH_SHORT).show();
            ((ImageListAdapter) listView.getAdapter()).setSelectedItemPosition(-1);
            ((ImageListAdapter) listView.getAdapter()).remove(checkedItem);
            //    ((ImageListAdapter)listView.getAdapter()).notifyItemRemoved(checkedItemPosition);
        });


        adb.setNegativeButton("Cancel", null);
        adb.show();


    }

    public void createImage(ImageItem destFile) {


        int index = listViewAdapter.addItem(destFile, false);
        listView.smoothScrollToPosition(index);


        destFile.setDownloading(true);


        final int[] oldProgress = {0};
        Thread createImageThread = new Thread(() -> {
            try {

                String sourceFile = getContext().getCacheDir().getAbsolutePath() + "/fat.img";
                FileInputStream fis = null;
                FileOutputStream fos = null;
                long size = 0;

                try {
                    fis = new FileInputStream(sourceFile);
                    fos = new FileOutputStream(destFile.getUserPath(), true);

                    size = fis.getChannel().size() + fos.getChannel().size();
                    byte[] buffer = new byte[1024 * 512];
                    long pos = (int) Math.ceil(fos.getChannel().size() / buffer.length);

                    int noOfBytes = 0;


                    // read bytes from source file and write to destination file
                    while ((noOfBytes = fis.read(buffer)) != -1) {
                        // if(pos % 4 == 0){

                        pos += 1;

                        long finalPos = pos;
                        int progress = (int) ((finalPos * buffer.length * 100L) / size);
                        destFile.setProgress(progress);
                        destFile.setSize(Helper.humanReadableByteCount((finalPos * buffer.length)));
                        if ((progress - oldProgress[0]) > 1) {
                            oldProgress[0] = progress;

                            getActivity().runOnUiThread(() -> {
                                listView.getAdapter().notifyItemChanged(listViewAdapter.getPositionOfItem(destFile), "download");
                            });
                        }


                        fos.write(buffer, 0, noOfBytes);
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("File not found" + e);
                } catch (IOException ioe) {
                    System.out.println("Exception while copying file " + ioe);
                } finally {
                    // close the streams using close method
                    try {
                        if (fis != null) {
                            fis.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException ioe) {
                        System.out.println("Error while closing stream: " + ioe);
                    }

                    File src = new File(sourceFile);
                    src.delete();


                }


                //
                long finalSize = size;
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getActivity(), "Image successfully created", Toast.LENGTH_LONG).show();
                    destFile.setDownloading(false);
                    destFile.setSize(Helper.humanReadableByteCount(finalSize));
                    //  ((ImageListAdapter) listView.getAdapter()).setSelectedItemPosition(-1);
                    listView.smoothScrollToPosition(listViewAdapter.getPositionOfItem(destFile));
                    listViewAdapter.notifyItemChanged(listViewAdapter.getPositionOfItem(destFile), "download");

                    listView.postDelayed(() -> ((ImageListAdapter) listView.getAdapter()).setSelectedItemPosition(listViewAdapter.getPositionOfItem(destFile)), 500);

                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        createImageThread.setDaemon(true);
        createImageThread.start();


        // ((ImageListAdapter)listView.getAdapter()).notifyDataSetChanged();
    }

    public void setPopulate(boolean populate) {
        this.populate = populate;
    }

    public void addImage(ImageItem imageItem) {

        if (mainFetch == null) {
            mainFetch = new Fetch.Builder(getContext(), "Main")
                    .setDownloadConcurrentLimit(4) // Allows Fetch to download 4 downloads in Parallel.
                    .enableLogging(true)
                    .build();
            mainFetch.removeAll();
            mainFetch.cancelAll();

        }

        // Helper.trustAllHosts();
        imageItem.setDownloading(true);
        int position = listViewAdapter.addItem(imageItem, false);
        listView.smoothScrollToPosition(position);

        final Request request = new Request(imageItem.getRootPath(), USERPATH + "/" + imageItem.getName());
        request.setPriority(Priority.HIGH);
        request.setNetworkType(NetworkType.WIFI_ONLY);


        final FetchListener fetchListener = new FetchListener() {
            int oldProgress = 0;

            @Override
            public void onQueued(@NotNull Download download) {
                if (request.getId() == download.getId()) {
                    imageItem.setUserPath(USERPATH + "/" + imageItem.getName());
                    imageItem.setRootPath(ROOTPATH + "/" + imageItem.getName());
                }
            }

            @Override
            public void onCompleted(@NotNull Download download) {
                imageItem.setDownloading(false);
                imageItem.setSize(Helper.humanReadableByteCount(download.getTotal()));
                getActivity().runOnUiThread(() -> {
                    listViewAdapter.notifyItemChanged(listViewAdapter.getPositionOfItem(imageItem));
                    Toast.makeText(getContext(), "Downloaded!", Toast.LENGTH_SHORT).show();
                });
                mainFetch.removeListener(this);
            }

            @Override
            public void onError(@NotNull Download download) {

                getActivity().runOnUiThread(() -> {
                    File file = new File(imageItem.getUserPath());
                    file.delete();
                    Toast.makeText(getContext(), "Deleted!", Toast.LENGTH_SHORT).show();
                    ((ImageListAdapter) listView.getAdapter()).setSelectedItemPosition(-1);
                    ((ImageListAdapter) listView.getAdapter()).remove(imageItem);
                    mainFetch.removeListener(this);

                    //listViewAdapter.notifyItemChanged(position, "download");
                });

            }

            @Override
            public void onProgress(@NotNull Download download, long etaInMilliSeconds, long downloadedBytesPerSecond) {
                if (request.getId() == download.getId()) {


                    oldProgress = download.getProgress();

                    imageItem.setProgress(download.getProgress());
                    imageItem.setSize(Helper.humanReadableByteCount(download.getDownloaded()) + " / " + Helper.humanReadableByteCount(download.getTotal()) + " - " + Helper.humanReadableByteCount(downloadedBytesPerSecond) + "/s");


                    getActivity().runOnUiThread(() -> {
                        listViewAdapter.notifyItemChanged(listViewAdapter.getPositionOfItem(imageItem), "download");
                    });


                    final int progress = download.getProgress();
                    Log.d("Fetch", "Progress Completed :" + progress);
                }

            }

            @Override
            public void onPaused(@NotNull Download download) {

            }

            @Override
            public void onResumed(@NotNull Download download) {

            }

            @Override
            public void onCancelled(@NotNull Download download) {

            }

            @Override
            public void onRemoved(@NotNull Download download) {

            }

            @Override
            public void onDeleted(@NotNull Download download) {

            }
        };

        mainFetch.addListener(fetchListener);
        mainFetch.enqueue(request, download -> Log.i("lala", "added " + download.getId()), error -> Log.i("lala", "error  " + error.name()));

    }
}

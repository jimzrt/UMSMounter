package com.jimzrt.umsmounter.Fragments;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
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

import com.jimzrt.umsmounter.Activities.MainActivity;
import com.jimzrt.umsmounter.ListAdapters.ImageListAdapter;
import com.jimzrt.umsmounter.Model.ImageItem;
import com.jimzrt.umsmounter.R;
import com.jimzrt.umsmounter.Tasks.BaseTask;
import com.jimzrt.umsmounter.Tasks.MountImageTask;
import com.jimzrt.umsmounter.Tasks.UnmountingTask;
import com.jimzrt.umsmounter.Utils.BackgroundTask;
import com.jimzrt.umsmounter.Utils.Helper;
import com.jimzrt.umsmounter.ViewModels.ImageItemViewModel;
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
public class MainFragment extends Fragment implements ImageListAdapter.OnImageListListener {



    // Animation animation;
    RecyclerView listView = null;
    ImageListAdapter listViewAdapter = null;
    Spinner usbMode = null;
    ArrayAdapter<String> usbModeAdapter = null;
    private boolean populate;
    private String functionMode = "mtp,adb";
    private Fetch mainFetch;
    private ImageItemViewModel model;


    @Override
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (populate) {
            populate = false;
        }
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
    public void onDestroy() {
        super.onDestroy();
        mainFetch.close();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (mainFetch == null) {
            mainFetch = new Fetch.Builder(getContext(), "Main")
                    .setDownloadConcurrentLimit(4) // Allows Fetch to download 4 downloads in Parallel.
                    .enableLogging(true)
                    .build();
            mainFetch.cancelAll();
            mainFetch.removeAll();

        }

        View view = inflater.inflate(R.layout.fragment_main, container, false);


        if (getActivity() != null)
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("UMS Mounter");
        listView = view.findViewById(R.id.listview);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        listView.setLayoutManager(mLayoutManager);
        model = ViewModelProviders.of(this).get(ImageItemViewModel.class);

        listViewAdapter = new ImageListAdapter(model.getImages(false).getValue(), getContext(), this);


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


        usbMode.setAdapter(usbModeAdapter);


        ImageButton refreshButton = view.findViewById(R.id.buttonRefresh);

        refreshButton.setOnClickListener((View v) -> {
            // populateList();
            listViewAdapter.addItems(model.getImages(true).getValue());
            Toast.makeText(getContext(), "List updated!", Toast.LENGTH_LONG).show();
        });


        SwipeRefreshLayout mySwipeRefreshLayout = view.findViewById(R.id.swiperefresh);

        mySwipeRefreshLayout.setOnRefreshListener(
                () -> {

                    listViewAdapter.addItems(model.getImages(true).getValue());
                    mySwipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), "List updated!", Toast.LENGTH_LONG).show();
                }
        );


        SharedPreferences sharedPref = getContext().getSharedPreferences(null, Context.MODE_PRIVATE);
        TextView textMode = view.findViewById(R.id.textMode);
        textMode.setText(sharedPref.getString("usbMode", "Not Supported"));


        List<String> modes = new ArrayList<>(Arrays.asList("Writable USB", "Read-only USB"));
        if (sharedPref.getBoolean("cdrom", false)) {
            modes.add("CD-ROM");
        }

        usbModeAdapter.clear();
        usbModeAdapter.addAll(modes);
        usbMode.setSelection(0);

        TextView statusText = view.findViewById(R.id.statusText);
        statusText.setText("Nothing mounted");
        statusText.setTextColor(Color.LTGRAY);


        model.getSelected().observe(this, image -> {
            listViewAdapter.setSelectedItem(image);
        });
        model.getDownloading().observe(this, image -> {
            Log.i("lala", "position: " + listViewAdapter.getPositionOfItem(image));
            listViewAdapter.notifyItemChanged(listViewAdapter.getPositionOfItem(image), "download");
        });
        model.getRemoved().observe(this, image -> {
            Log.i("lala", "REMOVEDDD");
            File file = new File(image.getUserPath());
            file.delete();
            listViewAdapter.remove(image);
        });
        model.getAdded().observe(this, image -> {
            Log.i("lala", "ADDEDDD");
            listView.smoothScrollToPosition(listViewAdapter.addItem(image));
        });
        model.getMounted().observe(this, image -> {
            if (image.getMounted()) {
                Log.i("lala", "Mounted");
                statusText.setText(image.getName() + " mounted");
                statusText.setTextColor(Color.DKGRAY);
                listViewAdapter.notifyItemChanged(listViewAdapter.getPositionOfItem(image), "mount");
            } else {
                Log.i("lala", "Unounted");
                statusText.setText("Nothing mounted");
                statusText.setTextColor(Color.LTGRAY);

                listViewAdapter.notifyItemChanged(listViewAdapter.getPositionOfItem(image), "unmount");
            }

        });


        return view;
    }

    public void unmount(String functionMode) {
        (new BackgroundTask(getActivity())).setDelegate((successful, result) -> {
            if (successful) {
                model.unmount();

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setMessage(result)
                        .setTitle("Error!");
                builder.setPositiveButton("Ok", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
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
                model.mount(imageItem);

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setMessage(result)
                        .setTitle("Error!");
                builder.setPositiveButton("Ok", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }).setTasks(new BaseTask[]{new MountImageTask(imageItem, usbMode.getSelectedItem().toString())}).execute();

    }


    @Override
    public void onMountImageButtonClicked() {

        ImageItem mounted = model.getMounted().getValue();
        ImageItem selected = model.getSelected().getValue();
        if (mounted == null || !mounted.getMounted()) {
            mount(selected);
        } else if (mounted != selected) {
            unmount(functionMode);
            mount(selected);
        } else {
            unmount(functionMode);
        }


    }

    @Override
    public void onDeleteImageButtonClicked() {

        ImageItem checkedItem = model.getSelected().getValue();

        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());


        adb.setTitle("Delete");


        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setMessage("Do you really want to delete " + checkedItem.getName() + "?");


        adb.setPositiveButton("Delete", (dialog, which) -> {
            if (checkedItem.getDownloadId() != -1) {
                mainFetch.cancel(checkedItem.getDownloadId());
                mainFetch.remove(checkedItem.getDownloadId());
            }

            Toast.makeText(getContext(), "Deleted!", Toast.LENGTH_SHORT).show();
            model.unselect();
            model.remove(checkedItem);
        });


        adb.setNegativeButton("Cancel", null);
        adb.show();


    }

    @Override
    public void onImageSelected(ImageItem item) {
        model.select(item);
    }

    public void createImage(ImageItem destFile) {

        model.unselect();
        model.add(destFile);


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

                            model.downloading(destFile);


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
                    model.downloading(destFile);
                    listView.smoothScrollToPosition(listViewAdapter.getPositionOfItem(destFile));

                    model.select(destFile);

                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        createImageThread.setDaemon(true);
        createImageThread.start();


    }


    public void addImage(ImageItem imageItem) {


        model.unselect();
        model.remove(imageItem);
        model.add(imageItem);

        imageItem.setDownloading(true);


        final Request request = new Request(imageItem.getUrl(), MainActivity.USERPATH + "/" + imageItem.getName());
        request.setPriority(Priority.HIGH);
        request.setNetworkType(NetworkType.WIFI_ONLY);

        imageItem.setDownloadId(request.getId());


        final FetchListener fetchListener = new FetchListener() {
            int oldProgress = 0;

            @Override
            public void onQueued(@NotNull Download download) {
                if (request.getId() == download.getId()) {


                }
            }

            @Override
            public void onCompleted(@NotNull Download download) {
                imageItem.setDownloading(false);
                imageItem.setSize(Helper.humanReadableByteCount(download.getTotal()));
                model.downloading(imageItem);
                listView.smoothScrollToPosition(listViewAdapter.getPositionOfItem(imageItem));
                model.select(imageItem);

                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Downloaded!", Toast.LENGTH_SHORT).show();
                });
                mainFetch.removeListener(this);
                mainFetch.remove(download.getId());
            }

            @Override
            public void onError(@NotNull Download download) {
                model.remove(imageItem);

                getActivity().runOnUiThread(() -> {
                    File file = new File(imageItem.getUserPath());
                    file.delete();
                    Toast.makeText(getContext(), "Deleted!", Toast.LENGTH_SHORT).show();
                    mainFetch.removeListener(this);
                    mainFetch.remove(download.getId());
                });

            }

            @Override
            public void onProgress(@NotNull Download download, long etaInMilliSeconds, long downloadedBytesPerSecond) {
                if (request.getId() == download.getId()) {


                    oldProgress = download.getProgress();

                    imageItem.setProgress(download.getProgress());
                    imageItem.setSize(Helper.humanReadableByteCount(download.getDownloaded()) + " / " + Helper.humanReadableByteCount(download.getTotal()) + " - " + Helper.humanReadableByteCount(downloadedBytesPerSecond) + "/s");


                    model.downloading(imageItem);


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

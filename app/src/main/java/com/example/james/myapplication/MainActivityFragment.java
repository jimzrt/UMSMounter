package com.example.james.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.james.myapplication.Model.ImageItem;
import com.example.james.myapplication.Model.ImageListAdapter;
import com.example.james.myapplication.Model.MountImageTask;
import com.example.james.myapplication.Model.Task;
import com.example.james.myapplication.Model.UnmountingTask;
import com.example.james.myapplication.Utils.BackgroundTask;
import com.example.james.myapplication.Utils.Helper;
import com.topjohnwu.superuser.Shell;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements ImageListAdapter.OnImageButtonListener {

    static final String ROOTPATH = "/data/media/0/UMSMounter";
    static final String USERPATH = "/sdcard/UMSMounter";
    static final String ROOTDIR = "/UMSMounter";

    Animation animation;



    private View view;
    private ImageButton refreshButton;

    public ImageListAdapter getListViewAdapter() {
        return (ImageListAdapter)listView.getAdapter();
    }

    public ToggleButton toggleButton;




    private String functionMode="mtp,adb";
    // TextView logView = null;
    ListView listView = null;
    Spinner usbMode = null;

    public MainActivityFragment() {
    }

    public void populateList() {
        String path = Environment.getExternalStorageDirectory().toString()+ROOTDIR;
        File f = new File(path);

        File files[] = f.listFiles();
        final ArrayList<ImageItem> list = new ArrayList<>();
        if(files != null) {
            for (File file : files) {
                list.add(new ImageItem(file.getName(), ROOTPATH + "/" + file.getName(),USERPATH + "/" + file.getName(), Helper.humanReadableByteCount(file.length())));
            }
        }
        Collections.sort(list);
        SharedPreferences sharedPref = getContext().getSharedPreferences(null,Context.MODE_PRIVATE);
        TextView textMode = view.findViewById(R.id.textMode);
        textMode.setText(sharedPref.getString("usbMode", "Not Supported"));


        ImageListAdapter adapter = new ImageListAdapter(new ArrayList<>(), getContext(), this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(adapter);
        adapter.addAll(list);
        List<String> modes = new ArrayList<>(Arrays.asList("Writable USB", "Read-only USB"));
        if(sharedPref.getBoolean("cdrom", false)){
            modes.add("CD-ROM");
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item,
                modes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //  spinnerAdapter.notifyDataSetChanged();
        usbMode.setAdapter(spinnerAdapter);
        usbMode.setSelection(0);

    }



    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("UMS Mounter");
        }

    }

    @Override
    public void onResume()
    {
        super.onResume();


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("UMS Mounter");
        listView = view.findViewById(R.id.listview);
        usbMode = view.findViewById(R.id.spinner);


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
            if(toggleButton.isChecked()){

                int checkedItemPosition = ((ImageListAdapter)listView.getAdapter()).getSelectedItemPosition();
                if(checkedItemPosition == -1){
                    Toast.makeText(getContext(),"Please select Image",Toast.LENGTH_SHORT).show();
                    toggleButton.setChecked(false);
                    return;
                }
                ImageItem imageItem = ((ImageItem)listView.getItemAtPosition(checkedItemPosition));

               mount(imageItem);

            }else{

                unmount(functionMode);


            }
        });

        animation  = AnimationUtils.loadAnimation(getContext(),R.anim.rotate);

        // animation.setRepeatCount(-1);
       // animation.setDuration(2000);

        refreshButton = view.findViewById(R.id.buttonRefresh);

        refreshButton.setOnClickListener((View v) -> {
            populateList();
            Toast.makeText(getContext(),"List updated!", Toast.LENGTH_LONG).show();
        });


        SwipeRefreshLayout mySwipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        populateList();
                        mySwipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getContext(),"List updated!", Toast.LENGTH_LONG).show();
                    }
                }
        );



        return view;
    }

    public void unmount(String functionMode) {
        (new BackgroundTask(getActivity())).setDelegate((successful, result) -> {
            if(successful){
                ((ImageListAdapter)listView.getAdapter()).setMountedItemPosition(-1);


            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setMessage(result)
                        .setTitle("Error!");
                builder.setPositiveButton("Ok", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            //((TextView)findViewById(R.id.editText)).append(result);
        }).setTasks(new Task[]{new UnmountingTask(functionMode)}).execute();
    }

    public void mount(ImageItem imageItem){


        List<String> output = Shell.Sync.sh("getprop sys.usb.config");
        functionMode = output.get(0);
        if(functionMode == null || functionMode.contains("mass_storage")){
            functionMode = "mtp,adb";
        }
        (new BackgroundTask(getActivity())).setDelegate((successful, result) -> {
            if(successful){
                ((ImageListAdapter)listView.getAdapter()).setMountedItemPosition(((ImageListAdapter)listView.getAdapter()).getSelectedItemPosition());
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
        }).setTasks(new Task[]{new MountImageTask(imageItem, usbMode.getSelectedItem().toString(), getContext())}).execute();

    }


    @Override
    public void onMountImageButtonClicked() {

        int mountedItemPosition = ((ImageListAdapter)listView.getAdapter()).getMountedItemPosition();
        int checkedItemPosition = ((ImageListAdapter)listView.getAdapter()).getSelectedItemPosition();

        if(mountedItemPosition == checkedItemPosition){
            unmount(functionMode);
            toggleButton.setChecked(false);
        } else if(mountedItemPosition != -1){
            unmount(functionMode);
            mount(((ImageItem)listView.getItemAtPosition(checkedItemPosition)));
            toggleButton.setChecked(true);
        }else {
            mount(((ImageItem)listView.getItemAtPosition(checkedItemPosition)));
            toggleButton.setChecked(true);
        }


    }

    @Override
    public void onDeleteImageButtonClicked() {

        int checkedItemPosition = ((ImageListAdapter)listView.getAdapter()).getSelectedItemPosition();
        ImageItem checkedItem = ((ImageItem)listView.getItemAtPosition(checkedItemPosition));

        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());


        adb.setTitle("Delete");


        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setMessage("Do you really want to delete " + checkedItem.getName() + "?");


        adb.setPositiveButton("Delete", (dialog, which) -> {
            File file = new File(checkedItem.getUserPath());
            file.delete();
            Toast.makeText(getContext(), "Deleted!", Toast.LENGTH_SHORT).show();
            ((ImageListAdapter)listView.getAdapter()).setSelectedItemPosition(-1);
            ((ImageListAdapter)listView.getAdapter()).remove(checkedItem);
            ((ImageListAdapter)listView.getAdapter()).notifyDataSetChanged();
        });


        adb.setNegativeButton("Cancel", null);
        adb.show();


    }
}

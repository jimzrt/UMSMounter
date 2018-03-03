package com.jimzrt.umsmounter.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jimzrt.umsmounter.Fragments.DownloadFragment;
import com.jimzrt.umsmounter.Fragments.ImageCreationFragment;
import com.jimzrt.umsmounter.Fragments.MainFragment;
import com.jimzrt.umsmounter.Model.DownloadItem;
import com.jimzrt.umsmounter.Model.ImageItem;
import com.jimzrt.umsmounter.R;
import com.jimzrt.umsmounter.Tasks.BaseTask;
import com.jimzrt.umsmounter.Tasks.CheckFolderTask;
import com.jimzrt.umsmounter.Tasks.CheckMassStorageTask;
import com.jimzrt.umsmounter.Tasks.CheckRootTask;
import com.jimzrt.umsmounter.Tasks.SetPathsTask;
import com.jimzrt.umsmounter.Utils.BackgroundTask;
import com.jimzrt.umsmounter.Utils.Helper;

import java.util.UUID;

public class MainActivity extends AppCompatActivity implements ImageCreationFragment.OnImageCreationListener, DownloadFragment.OnImageDownloadListener {


    public static final String ROOTDIR = "/UMSMounter";

    public static String ROOTPATH = "/data/media/0/UMSMounter";
    public static String USERPATH = "/sdcard/UMSMounter";


    // private static final int READ_REQUEST_CODE = 42;
    // TextView logView = null;
    // Spinner usbMode = null;

    public static String android_id = UUID.randomUUID().toString().substring(31);


    MainFragment mainFragment;
    ImageCreationFragment createImageFragment;
    DownloadFragment downloadFragment;

    Fragment currentFragment;

    Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;

    //private ActionBarDrawerToggle mDrawerToggle;


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        //  Helper.verifyStoragePermissions(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setLogo(R.drawable.ic_logo);
        //  getSupportActionBar().setDisplayUseLogoEnabled(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        }


        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            mainFragment = new MainFragment();
            createImageFragment = new ImageCreationFragment();
            downloadFragment = new DownloadFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            // firstFragment.setArguments(getIntent().getExtras());

            SharedPreferences sharedPref = getSharedPreferences(null, Context.MODE_PRIVATE);
            boolean firstRun = sharedPref.getBoolean("firstRun", true);
            if (firstRun) {
                checkAll();
            } else {
                // Add the fragment to the 'fragment_container' FrameLayout
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, mainFragment).commit();
            }



        }

        currentFragment = mainFragment;

        navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    // set item as selected to persist highlight
                    menuItem.setChecked(true);
                    // Log.i("lala", ""+currentFragmentId);
                    // close drawer when item is tapped
                    mDrawerLayout.closeDrawers();

                    switch (menuItem.getItemId()) {
                        case R.id.nav_home:
                            if (currentFragment != mainFragment) {
                                showMain();

                            } else {
                                Toast.makeText(this, "Already on this mothafucka", Toast.LENGTH_LONG).show();
                            }
                            break;
                        case R.id.nav_create_image:
                            if (currentFragment != createImageFragment) {
                                showCreateImage();
                            } else {
                                Toast.makeText(this, "Already on this mothafucka", Toast.LENGTH_LONG).show();
                            }
                            break;
                        case R.id.nav_download_image:
                            if (currentFragment != downloadFragment) {
                                showDownloadImage();
                            } else {
                                Toast.makeText(this, "Already on this mothafucka", Toast.LENGTH_LONG).show();
                            }
                            break;

                    }
                    // Add code here to update the UI based on the item selected
                    // For example, swap UI fragments here

                    return true;
                });


        //  }
        getSupportFragmentManager().addOnBackStackChangedListener(
                () -> {
                    if (mainFragment.isVisible()) {
                        navigationView.setCheckedItem(R.id.nav_home);
                        currentFragment = mainFragment;
                    } else if (createImageFragment.isAdded()) {
                        navigationView.setCheckedItem(R.id.nav_create_image);
                        currentFragment = createImageFragment;
                    } else if (downloadFragment.isAdded()) {
                        navigationView.setCheckedItem(R.id.nav_download_image);
                        currentFragment = downloadFragment;
                    } else {
                        navigationView.setCheckedItem(-1);
                        currentFragment = downloadFragment;
                    }

                });


        Helper.trustAllHosts();
    }

    private void showDownloadImage() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                R.anim.card_flip_right_in,
                R.anim.card_flip_right_out,
                R.anim.card_flip_left_in,
                R.anim.card_flip_left_out);

        if (currentFragment == mainFragment) {
            transaction.hide(mainFragment);
        } else {
            transaction.remove(currentFragment);
        }
        transaction.add(R.id.fragment_container, downloadFragment);


        transaction.addToBackStack(null);
        transaction.commit();
        currentFragment = downloadFragment;
        navigationView.setCheckedItem(R.id.nav_download_image);
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    private void showCreateImage() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                R.anim.card_flip_right_in,
                R.anim.card_flip_right_out,
                R.anim.card_flip_left_in,
                R.anim.card_flip_left_out);
        if (currentFragment == mainFragment) {
            transaction.hide(mainFragment);
        } else {
            transaction.remove(currentFragment);
        }
        transaction.add(R.id.fragment_container, createImageFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        currentFragment = createImageFragment;
        navigationView.setCheckedItem(R.id.nav_create_image);
    }

    private void showMain() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                R.anim.card_flip_left_in,
                R.anim.card_flip_left_out,
                R.anim.card_flip_right_in,
                R.anim.card_flip_right_out);
        transaction.remove(currentFragment);
        transaction.show(mainFragment);
        //transaction.addToBackStack(null);
        transaction.commit();
        currentFragment = mainFragment;
        navigationView.setCheckedItem(R.id.nav_home);
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_revert:
                mainFragment.unmount("mtp,adb");
                return true;
            case R.id.action_check_dependencies:
                checkAll();
                return true;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

        }


        return super.onOptionsItemSelected(item);
    }


    void checkAll() {


        SharedPreferences sharedPref = getSharedPreferences(null, Context.MODE_PRIVATE);
        (new BackgroundTask(this).setDelegate((successful, output) -> {
            if (successful) {


                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("firstRun", false);
                editor.apply();

                if (!mainFragment.isAdded()) {
                    // Add the fragment to the 'fragment_container' FrameLayout
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.fragment_container, mainFragment).commit();
                }


            } else {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear().apply();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage(output + "\n\nAfter clicking Ok the app will crash to send a report so the issue can be fixed!\nYour id: " + android_id)
                        .setTitle("Error!");
                builder.setPositiveButton("Ok", (dialog, which) -> {
                    Crashlytics.setUserIdentifier(android_id);
                    Crashlytics.log(1, "Error", output);
                    Crashlytics.getInstance().crash();
                });

                builder.setNegativeButton("Close App", (dialog, which) -> {
                    finish();

                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        })).setTasks(new BaseTask[]{new CheckRootTask(), new CheckFolderTask(), new SetPathsTask(), new CheckMassStorageTask()}).execute();
    }

    @Override
    public void OnImageCreation(String imageItemName) {
        showMain();
        ImageItem imageItem = new ImageItem(imageItemName, ROOTPATH + "/" + imageItemName, USERPATH + "/" + imageItemName, Helper.humanReadableByteCount(0));
        mainFragment.createImage(imageItem);

    }

    @Override
    public void OnImageListClick(DownloadItem downloadItem) {

        Gson gson = new GsonBuilder().create();
        String downloadItemString = gson.toJson(downloadItem);

        Intent intent = new Intent(this, LinuxImageActivity.class);


        intent.putExtra("downloadItem", downloadItemString);
        startActivityForResult(intent, 0);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == 0) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
//
                String name = data.getStringExtra("name");
                String url = data.getStringExtra("url");
                ImageItem imageItem = new ImageItem(name, ROOTPATH + "/" + name, USERPATH + "/" + name, Helper.humanReadableByteCount(0));
                imageItem.setUrl(url);
                showMain();
                mainFragment.addImage(imageItem);

            }
        }
    }
}

package com.example.james.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.james.myapplication.Model.CheckFolderTask;
import com.example.james.myapplication.Model.CheckMassStorageTask;
import com.example.james.myapplication.Model.CheckRootTask;
import com.example.james.myapplication.Model.CreateImageTask;
import com.example.james.myapplication.Model.ImageItem;
import com.example.james.myapplication.Model.ImageListAdapter;
import com.example.james.myapplication.Model.Task;
import com.example.james.myapplication.Model.UnmountingTask;
import com.example.james.myapplication.Utils.BackgroundTask;
import com.example.james.myapplication.Utils.Helper;
import com.topjohnwu.superuser.Shell;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ImageCreationFragment.OnImageCreationListener {





    private static final int READ_REQUEST_CODE = 42;
   // TextView logView = null;
    Spinner usbMode = null;

    MainActivityFragment main;
    ImageCreationFragment test;

    Fragment currentFragment;

    Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    //private ActionBarDrawerToggle mDrawerToggle;


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        Helper.verifyStoragePermissions(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setLogo(R.drawable.ic_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);


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
            main = new MainActivityFragment();
            test = new ImageCreationFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
           // firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, main).commit();
        }

        currentFragment = main;

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    // set item as selected to persist highlight
                    menuItem.setChecked(true);
                   // Log.i("lala", ""+currentFragmentId);
                    // close drawer when item is tapped
                    mDrawerLayout.closeDrawers();

                    switch (menuItem.getItemId()){
                        case R.id.nav_home:
                            if(currentFragment != main){
                                showMain();

                            } else {
                                Toast.makeText(this,"Already on this mothafucka", Toast.LENGTH_LONG).show();
                            }
                            break;
                            //KEEP CURRENT FRAGMENT ID
                        case R.id.nav_create_image:
                            if(currentFragment != test){
                                showCreateImage();
                            } else {
                                Toast.makeText(this,"Already on this mothafucka", Toast.LENGTH_LONG).show();
                            }
                            break;

                    }
                    // Add code here to update the UI based on the item selected
                    // For example, swap UI fragments here

                    return true;
                });


        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        if(main.isVisible()){
                            navigationView.setCheckedItem(R.id.nav_home);
                            currentFragment = main;
                        } else if(test.isVisible()){
                            navigationView.setCheckedItem(R.id.nav_create_image);
                            currentFragment = test;
                        }
                       // String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount()-1).getName();
                      //  currentFragment =  getSupportFragmentManager().findFragmentByTag(tag);
                    //    if (tag == main.getTag()) {
                  //          navigationView.setCheckedItem(R.id.nav_home);
                   //     } else if(tag == test.getTag()){
                //            navigationView.setCheckedItem(R.id.nav_create_image);
                        }
                  //  }
                });
//        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
//                R.string.drawer_open, R.string.drawer_close) {
//
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//                getSupportActionBar().setTitle("Navigation!");
//                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
//            }
//
//            /** Called when a drawer has settled in a completely closed state. */
//            public void onDrawerClosed(View view) {
//                super.onDrawerClosed(view);
//                getSupportActionBar().setTitle("Yo");
//                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
//            }
//
//        };

//        mDrawerToggle.setDrawerIndicatorEnabled(true);
      //  mDrawerLayout.addDrawerListener(mDrawerToggle);



    }

    @Override
    public void onStart() {

        super.onStart();
        SharedPreferences sharedPref = getSharedPreferences(null, Context.MODE_PRIVATE);
        boolean firstRun = sharedPref.getBoolean("firstRun", true);
        if(firstRun) {
            checkAll();
            main.populateList();

        } else {

            main.populateList();

        }
    }

    private void showCreateImage() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                R.anim.card_flip_right_in,
                R.anim.card_flip_right_out,
                R.anim.card_flip_left_in,
                R.anim.card_flip_left_out);
        transaction.hide(main);
        transaction.add(R.id.fragment_container, test);
        transaction.addToBackStack(null);
        transaction.commit();
        currentFragment = test;
    }

    private void showMain() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                R.anim.card_flip_left_in,
                R.anim.card_flip_left_out,
                R.anim.card_flip_right_in,
                R.anim.card_flip_right_out);
        transaction.remove(currentFragment);
        transaction.show(main);
        transaction.addToBackStack(null);
        transaction.commit();
        currentFragment = main;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
     //   mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
   //     mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

     //   if (mDrawerToggle.onOptionsItemSelected(item)) {
     //       return true;


    //    }
        switch (id){
            case R.id.action_revert:
                main.unmount("mtp,adb");
                if(main.isAdded()){
                    main.toggleButton.setChecked(false);
                }
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
        //refreshButton.setAnimation(animation);
        //  refreshButton.startAnimation(animation);

        SharedPreferences sharedPref = getSharedPreferences(null, Context.MODE_PRIVATE);
        (new BackgroundTask(this).setDelegate((successful, output) -> {
            //  refreshButton.clearAnimation();
            if (successful) {

                //  populateList();


                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("firstRun", false);
                editor.apply();

            } else {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear().apply();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage(output)
                        .setTitle("Error!");
                builder.setPositiveButton("Ok", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            // ((TextView)findViewById(R.id.editText)).append(output);
        })).setTasks(new Task[]{new CheckRootTask(), new CheckFolderTask(), new CheckMassStorageTask(this)}).execute();
    }

    @Override
    public void OnImageCreation() {
        main.populateList();
        showMain();
    }
}

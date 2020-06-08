package com.jimzrt.umsmounter.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.navigation.NavigationView
import com.google.gson.GsonBuilder
import com.jimzrt.umsmounter.BuildConfig
import com.jimzrt.umsmounter.R
import com.jimzrt.umsmounter.fragments.CreditsFragment
import com.jimzrt.umsmounter.fragments.DownloadFragment
import com.jimzrt.umsmounter.fragments.DownloadFragment.OnImageDownloadListener
import com.jimzrt.umsmounter.fragments.ImageCreationFragment
import com.jimzrt.umsmounter.fragments.ImageCreationFragment.OnImageCreationListener
import com.jimzrt.umsmounter.fragments.MainFragment
import com.jimzrt.umsmounter.model.DownloadItem
import com.jimzrt.umsmounter.model.ImageItem
import com.jimzrt.umsmounter.tasks.*
import com.jimzrt.umsmounter.utils.BackgroundTask
import com.jimzrt.umsmounter.utils.Helper

class MainActivity : AppCompatActivity(), OnImageCreationListener, OnImageDownloadListener {
    private var mainFragment: MainFragment? = null
    private var createImageFragment: ImageCreationFragment? = null
    private var downloadFragment: DownloadFragment? = null
    private var creditsFragment: CreditsFragment? = null
    private var currentFragment: Fragment? = null
    private var mDrawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mDrawerLayout = findViewById(R.id.drawer_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeButtonEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_menu)
        }
        if (findViewById<View?>(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return
            }

            // Create a new Fragment to be placed in the activity layout
            mainFragment = MainFragment()
            createImageFragment = ImageCreationFragment()
            downloadFragment = DownloadFragment()
            creditsFragment = CreditsFragment()
            val sharedPref = getSharedPreferences(null, Context.MODE_PRIVATE)
            val firstRun = sharedPref.getBoolean("firstRun", true)
            val version = sharedPref.getString("version", "")
            USERPATH = sharedPref.getString("userpath", "")
            ROOTPATH = sharedPref.getString("rootpath", "")
            if (firstRun || BuildConfig.VERSION_NAME != version) {
                checkAll()
            } else {
                // Add the fragment to the 'fragment_container' FrameLayout
                supportFragmentManager.beginTransaction()
                        .add(R.id.fragment_container, mainFragment!!).commit()
            }
        }
        currentFragment = mainFragment
        navigationView = findViewById(R.id.nav_view)
        navigationView?.menu?.getItem(0)?.isChecked = true
        navigationView?.setNavigationItemSelectedListener { menuItem: MenuItem ->
            // set item as selected to persist highlight
            // menuItem.setChecked(true);

            //   mDrawerLayout.closeDrawers();
            val delay = 150
            when (menuItem.itemId) {
                R.id.nav_home -> if (currentFragment !== mainFragment) {
                    Handler().postDelayed({ showMain() }, delay.toLong())
                }
                R.id.nav_create_image -> if (currentFragment !== createImageFragment) {
                    Handler().postDelayed({ showCreateImage() }, delay.toLong())
                }
                R.id.nav_download_image -> if (currentFragment !== downloadFragment) {
                    Handler().postDelayed({ showDownloadImage() }, delay.toLong())
                }
                R.id.nav_credits -> if (currentFragment !== createImageFragment) {
                    Handler().postDelayed({ showCredits() }, delay.toLong())
                }
            }
            // Add code here to update the UI based on the item selected
            // For example, swap UI fragments here
            mDrawerLayout?.closeDrawer(GravityCompat.START)
            true
        }


        //  }
        supportFragmentManager.addOnBackStackChangedListener {
            when {
                mainFragment!!.isVisible -> {
                    navigationView?.setCheckedItem(R.id.nav_home)
                    currentFragment = mainFragment
                }
                createImageFragment!!.isAdded -> {
                    navigationView?.setCheckedItem(R.id.nav_create_image)
                    currentFragment = createImageFragment
                }
                downloadFragment!!.isAdded -> {
                    navigationView?.setCheckedItem(R.id.nav_download_image)
                    currentFragment = downloadFragment
                }
                creditsFragment!!.isAdded -> {
                    navigationView?.setCheckedItem(R.id.nav_credits)
                    currentFragment = creditsFragment
                }
            }
        }


        //Helper.trustAllHosts();
    }

    private fun showDownloadImage() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out)
        if (currentFragment === mainFragment) {
            transaction.hide(mainFragment!!)
        } else {
            transaction.remove(currentFragment!!)
        }
        transaction.add(R.id.fragment_container, downloadFragment!!)
        transaction.addToBackStack(null)
        transaction.commit()
        currentFragment = downloadFragment
        navigationView!!.setCheckedItem(R.id.nav_download_image)
    }

    private fun showCredits() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out)
        if (currentFragment === mainFragment) {
            transaction.hide(mainFragment!!)
        } else {
            transaction.remove(currentFragment!!)
        }
        transaction.add(R.id.fragment_container, creditsFragment!!)
        transaction.addToBackStack(null)
        transaction.commit()
        currentFragment = creditsFragment
        navigationView!!.setCheckedItem(R.id.nav_credits)
    }

    public override fun onStart() {
        super.onStart()
    }

    private fun showCreateImage() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out)
        if (currentFragment === mainFragment) {
            transaction.hide(mainFragment!!)
        } else {
            transaction.remove(currentFragment!!)
        }
        transaction.add(R.id.fragment_container, createImageFragment!!)
        transaction.addToBackStack(null)
        transaction.commit()
        currentFragment = createImageFragment
        navigationView!!.setCheckedItem(R.id.nav_create_image)
    }

    private fun showMain() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out)
        transaction.remove(currentFragment!!)
        transaction.show(mainFragment!!)
        //transaction.addToBackStack(null);
        transaction.commit()
        currentFragment = mainFragment
        navigationView!!.setCheckedItem(R.id.nav_home)
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_revert -> {
                mainFragment!!.unmount("mtp,adb")
                return true
            }
            R.id.action_check_dependencies -> {
                checkAll()
                return true
            }
            android.R.id.home -> {
                mDrawerLayout!!.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkAll() {
        val sharedPref = getSharedPreferences(null, Context.MODE_PRIVATE)
        val context = this
        BackgroundTask(this).setDelegate(object : BackgroundTask.AsyncResponse {
            override fun processFinish(successful: Boolean?, output: String?) {
                if (successful!!) {
                    val editor = sharedPref.edit()
                    editor.putBoolean("firstRun", false)
                    editor.putString("version", BuildConfig.VERSION_NAME)
                    editor.apply()
                } else {
                    val editor = sharedPref.edit()
                    editor.clear().apply()
                    val builder = AlertDialog.Builder(context)
                    builder.setMessage(output)
                            .setTitle("Error!")
                    builder.setPositiveButton("Ok", null)
                    val dialog = builder.create()
                    dialog.show()
                }
                if (!mainFragment!!.isAdded) {
                    // Add the fragment to the 'fragment_container' FrameLayout
                    supportFragmentManager.beginTransaction()
                            .add(R.id.fragment_container, mainFragment!!).commit()
                }
            }
        }
        ).setTasks(arrayOf(CheckRootTask(), CheckPermissionTask(), SetPathsTask(), CheckFolderTask(), CheckMassStorageTask())).execute()
    }

    override fun OnImageCreation(imageItem: String?) {
        showMain()
        val imageItemObj = ImageItem(imageItem!!, "$ROOTPATH/$imageItem", "$USERPATH/$imageItem", Helper.humanReadableByteCount(0))
        mainFragment!!.createImage(imageItemObj)
    }

    override fun OnImageListClick(downloadItem: DownloadItem?) {
        val gson = GsonBuilder().create()
        val downloadItemString = gson.toJson(downloadItem)
        val intent = Intent(this, LinuxImageActivity::class.java)
        intent.putExtra("downloadItem", downloadItemString)
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Check which request we're responding to
        if (requestCode == 0) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
//
                val name = data!!.getStringExtra("name")
                val url = data.getStringExtra("url")
                val imageItem = ImageItem(name!!, "$ROOTPATH/$name", "$USERPATH/$name", Helper.humanReadableByteCount(0))
                imageItem.url = url
                showMain()
                mainFragment!!.addImage(imageItem)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == WRITE_EXTERNAL_STORAGE_PERM) {
            val sharedPref = getSharedPreferences(null, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putBoolean("hasPermission", true)
            editor.apply()
            Toast.makeText(this, "granteddd!!!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "dont know this shit", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        const val ROOTDIR = "/UMSMounter"
        const val CACHEDIR = "/cache"

        @JvmField
        var ROOTPATH: String? = null

        @JvmField
        var USERPATH: String? = null
        const val WRITE_EXTERNAL_STORAGE_PERM = 1337
    }
}
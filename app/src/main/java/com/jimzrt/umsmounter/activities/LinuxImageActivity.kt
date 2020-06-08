package com.jimzrt.umsmounter.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import com.jimzrt.umsmounter.R
import com.jimzrt.umsmounter.model.DownloadItem
import com.jimzrt.umsmounter.model.Release

class LinuxImageActivity : AppCompatActivity() {
    private var releaseList: ListView? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        //((MainActivity)getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(false);
        // ((MainActivity)getActivity()).getSupportActionBar().setHomeAsUpIndicator(null);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        super.onCreate(savedInstanceState)

        // Get the Intent that started this activity and extract the string
        val intent = intent
        val downloadItemString = intent.getStringExtra("downloadItem")
        val gson = GsonBuilder().create()
        val downloadItem = gson.fromJson(downloadItemString, DownloadItem::class.java)
        setContentView(R.layout.linux_image)
        val nameView = findViewById<TextView>(R.id.linuxName)
        val urlView = findViewById<TextView>(R.id.linuxUrl)
        val downloadButton = findViewById<Button>(R.id.liunxDownload)
        nameView.text = downloadItem.name
        supportActionBar!!.title = "Download " + downloadItem.name
        urlView.text = downloadItem.url
        releaseList = findViewById(R.id.releaseList)
        releaseList?.choiceMode = ListView.CHOICE_MODE_SINGLE
        val itemsAdapter = ArrayAdapter(this, R.layout.linux_image_list_row, downloadItem.releases)
        releaseList?.adapter = itemsAdapter
        releaseList?.setItemChecked(0, true)


        //EventBus.getDefault().post(new MessageEvent(downloadItem.name));
        //finish();
        downloadButton.setOnClickListener { v: View? ->
            val rel = releaseList?.checkedItemPosition?.let { releaseList?.getItemAtPosition(it) } as Release
            val url = rel.url
            val fileName = url?.substring(url.lastIndexOf('/') + 1)
            val returnIntent = Intent()
            returnIntent.putExtra("name", fileName)
            returnIntent.putExtra("url", url)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
package com.jimzrt.umsmounter.fragments

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.jimzrt.umsmounter.R
import com.jimzrt.umsmounter.listadapters.ImageDownloadListAdapter
import com.jimzrt.umsmounter.model.DownloadItem
import com.jimzrt.umsmounter.utils.Helper
import java.io.*
import java.lang.ref.WeakReference
import java.net.URL
import java.util.*

class DownloadFragment : Fragment() {
    private var mCallback: OnImageDownloadListener? = null
    private var listViewAdapter: ImageDownloadListAdapter? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        mCallback = try {
            context as OnImageDownloadListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //if (activity != null) {
        (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Download Image"
        // }
        val view = inflater.inflate(R.layout.fragment_download, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.downloadImageList)
        val mLayoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = mLayoutManager
        listViewAdapter = mCallback?.let { ImageDownloadListAdapter(ArrayList(), it) }
        recyclerView.adapter = listViewAdapter
        recyclerView.setHasFixedSize(true)
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context,
                mLayoutManager.orientation)
        recyclerView.addItemDecoration(dividerItemDecoration)
        DownloadFilesTask(activity, listViewAdapter!!).execute(false)
        val refreshButton = view.findViewById<ImageButton>(R.id.buttonRefreshDownload)
        refreshButton.setOnClickListener {
            listViewAdapter!!.clear()
            DownloadFilesTask(activity, listViewAdapter!!).execute(true)
        }
        return view
    }

    interface OnImageDownloadListener {
        fun OnImageListClick(downloadItem: DownloadItem?)
    }

    private class DownloadFilesTask internal constructor(activity: Activity?, listAdapter: ImageDownloadListAdapter) : AsyncTask<Boolean?, Void?, List<DownloadItem>?>() {
        val mWeakActivity: WeakReference<Activity?> = WeakReference(activity)
        val listAdapter: ImageDownloadListAdapter = listAdapter
        protected override fun doInBackground(vararg forceReload: Boolean?): List<DownloadItem>? {
            val force = forceReload[0]
            val imageListType = object : TypeToken<ArrayList<DownloadItem?>?>() {}.type
            val file = File(mWeakActivity.get()!!.filesDir, "data.json")
            var list: MutableList<DownloadItem>? = null
            if (file.exists() && !force!!) {
                try {
                    FileReader(mWeakActivity.get()!!.filesDir.toString() + "/data.json").use { reader ->
                        val gson = GsonBuilder().create()
                        list = gson.fromJson(reader, imageListType)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                try {
                    InputStreamReader(URL("https://softwarebakery.com/apps/drivedroid/repositories/main.json").openStream()).use { reader ->
                        var gson = GsonBuilder().create()
                        list = gson.fromJson(reader, imageListType)
                        val iterator = list?.iterator()
                        if (iterator != null) {
                            while (iterator.hasNext()) {
                                val item = iterator.next()
                                if (item.releases.isEmpty()) {
                                    iterator.remove()
                                }
                            }
                        }
                        list?.sort()
                        FileWriter(mWeakActivity.get()!!.filesDir.toString() + "/data.json").use { writer ->
                            gson = GsonBuilder().create()
                            gson.toJson(list, writer)
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return list
        }

        override fun onPostExecute(list: List<DownloadItem>?) {
            val file = File(mWeakActivity.get()!!.filesDir.toString() + "/data.json")
            val updatedTextView = mWeakActivity.get()!!.findViewById<TextView>(R.id.updatedTextView)
            updatedTextView.text = "Last updated: " + Helper.convertDate("" + file.lastModified())
            if (list != null) listAdapter.setItems(list as MutableList<DownloadItem>?)
            listAdapter.notifyDataSetChanged()
        }

    }
}
package com.jimzrt.umsmounter.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.jimzrt.umsmounter.R
import com.jimzrt.umsmounter.activities.MainActivity
import com.jimzrt.umsmounter.listadapters.ImageListAdapter
import com.jimzrt.umsmounter.listadapters.ImageListAdapter.OnImageListListener
import com.jimzrt.umsmounter.model.ImageItem
import com.jimzrt.umsmounter.tasks.MountImageTask
import com.jimzrt.umsmounter.tasks.UnmountingTask
import com.jimzrt.umsmounter.utils.BackgroundTask
import com.jimzrt.umsmounter.utils.Helper
import com.jimzrt.umsmounter.viewmodels.ImageItemViewModel
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2.Fetch.Impl.getInstance
import com.tonyodev.fetch2core.DownloadBlock
import com.tonyodev.fetch2core.Func
import com.topjohnwu.superuser.Shell
import java.io.*
import java.util.*
import kotlin.math.ceil

/**
 * A placeholder fragment containing a simple view.
 */
class MainFragment : Fragment(), OnImageListListener {
    // Animation animation;
    private var listView: RecyclerView? = null
    private var listViewAdapter: ImageListAdapter? = null
    private var usbMode: Spinner? = null
    private var populate = false
    private var functionMode: String? = "mtp,adb"
    private var mainFetch: Fetch? = null
    private var model: ImageItemViewModel? = null
    override fun onActivityCreated(bundle: Bundle?) {
        super.onActivityCreated(bundle)
        if (populate) {
            populate = false
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            if (activity != null) {
                (activity as AppCompatActivity?)!!.supportActionBar!!.title = "UMS Mounter"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainFetch!!.close()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (mainFetch == null) {
            val fetchConfiguration = FetchConfiguration.Builder(context!!)
                    .setDownloadConcurrentLimit(4) // Allows Fetch to download 4 downloads in Parallel.
                    .enableLogging(true)
                    .build()
            mainFetch = getInstance(fetchConfiguration)
            mainFetch!!.cancelAll()
            mainFetch!!.removeAll()
        }
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        if (activity != null) (activity as AppCompatActivity?)!!.supportActionBar!!.title = "UMS Mounter"
        listView = view.findViewById(R.id.listview)
        val mLayoutManager = LinearLayoutManager(activity)
        listView?.layoutManager = mLayoutManager
        model = ViewModelProvider(this).get(ImageItemViewModel::class.java)
        listViewAdapter = ImageListAdapter(model!!.getImages(false).value, context!!, this)
        (listView?.itemAnimator as SimpleItemAnimator?)!!.supportsChangeAnimations = false
        listView?.adapter = listViewAdapter
        listView?.setHasFixedSize(true)
        listView?.itemAnimator!!.changeDuration = 0
        listView?.itemAnimator!!.addDuration = 500
        listView?.itemAnimator!!.moveDuration = 500
        listView?.itemAnimator!!.removeDuration = 500
        val dividerItemDecoration = DividerItemDecoration(listView!!.context,
                mLayoutManager.orientation)
        listView?.addItemDecoration(dividerItemDecoration)
        usbMode = view.findViewById(R.id.spinner)
        val usbModeAdapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item,
                ArrayList<String>())
        usbMode?.adapter = usbModeAdapter
        val refreshButton = view.findViewById<ImageButton>(R.id.buttonRefresh)
        refreshButton.setOnClickListener {
            // populateList();
            listViewAdapter!!.addItems(model!!.getImages(true).value!!)
            Toast.makeText(context, "List updated!", Toast.LENGTH_LONG).show()
        }
        val mySwipeRefreshLayout: SwipeRefreshLayout = view.findViewById(R.id.swiperefresh)
        mySwipeRefreshLayout.setOnRefreshListener {
            listViewAdapter!!.addItems(model!!.getImages(true).value!!)
            mySwipeRefreshLayout.isRefreshing = false
            Toast.makeText(context, "List updated!", Toast.LENGTH_LONG).show()
        }
        val sharedPref = context!!.getSharedPreferences(null, Context.MODE_PRIVATE)
        val textMode = view.findViewById<TextView>(R.id.textMode)
        textMode.text = sharedPref.getString("usbMode", "Not Supported")
        val modes: MutableList<String> = ArrayList(Arrays.asList("Writable USB", "Read-only USB"))
        if (sharedPref.getBoolean("cdrom", false)) {
            modes.add("CD-ROM")
        }
        usbModeAdapter.clear()
        usbModeAdapter.addAll(modes)
        usbMode?.setSelection(0)
        val statusText = view.findViewById<TextView>(R.id.statusText)
        statusText.text = "Nothing mounted"
        statusText.setTextColor(Color.LTGRAY)
        model!!.selected.observe(viewLifecycleOwner, Observer { image: ImageItem? -> listViewAdapter!!.setSelectedItem(image) })
        model!!.downloading.observe(viewLifecycleOwner, Observer { image: ImageItem? ->
            Log.i("lala", "position: " + listViewAdapter!!.getPositionOfItem(image!!))
            listViewAdapter!!.notifyItemChanged(listViewAdapter!!.getPositionOfItem(image), "download")
        })
        model!!.removed.observe(viewLifecycleOwner, Observer { image: ImageItem ->
            Log.i("lala", "REMOVEDDD")
            val file = File(image.userPath)
            file.delete()
            listViewAdapter!!.remove(image)
        })
        model!!.added.observe(viewLifecycleOwner, Observer { image: ImageItem? ->
            Log.i("lala", "ADDEDDD")
            listView?.smoothScrollToPosition(listViewAdapter!!.addItem(image!!))
        })
        model!!.mounted.observe(viewLifecycleOwner, Observer { image: ImageItem? ->
            if (image?.mounted!!) {
                Log.i("lala", "Mounted")
                statusText.text = image.name + " mounted"
                statusText.setTextColor(Color.DKGRAY)
                listViewAdapter!!.notifyItemChanged(listViewAdapter!!.getPositionOfItem(image), "mount")
            } else {
                Log.i("lala", "Unounted")
                statusText.text = "Nothing mounted"
                statusText.setTextColor(Color.LTGRAY)
                listViewAdapter!!.notifyItemChanged(listViewAdapter!!.getPositionOfItem(image), "unmount")
            }
        })
        return view
    }

    fun unmount(functionMode: String?) {

        activity?.let {
            BackgroundTask(it).setDelegate(object : BackgroundTask.AsyncResponse {
                override fun processFinish(successful: Boolean?, output: String?) {
                    if (successful!!) {
                        model!!.unmount()
                    } else {
                        val builder = AlertDialog.Builder(context)
                        builder.setMessage(output)
                                .setTitle("Error!")
                        builder.setPositiveButton("Ok", null)
                        val dialog = builder.create()
                        dialog.show()
                    }
                }

            }).setTasks(arrayOf(UnmountingTask(functionMode!!))).execute()
        }
    }

    private fun mount(imageItem: ImageItem?) {
        val output = Shell.su("getprop sys.usb.config").exec().out
        functionMode = output[0]
        if (functionMode == null || functionMode!!.contains("mass_storage")) {
            functionMode = "mtp,adb"
        }
        activity?.let {
            BackgroundTask(it).setDelegate(object : BackgroundTask.AsyncResponse {
                override fun processFinish(successful: Boolean?, output: String?) {
                    if (successful!!) {
                        if (imageItem != null) {
                            model!!.mount(imageItem)
                        }
                    } else {
                        val builder = AlertDialog.Builder(context)
                        builder.setMessage(output)
                                .setTitle("Error!")
                        builder.setPositiveButton("Ok", null)
                        val dialog = builder.create()
                        dialog.show()
                        if (imageItem != null) {
                            model!!.unmount(imageItem)
                        }
                    }
                }
            }).setTasks(arrayOf(MountImageTask(imageItem!!, usbMode!!.selectedItem.toString()))).execute()
        }
    }

    override fun onMountImageButtonClicked() {
        val mounted = model!!.mounted.value
        val selected = model!!.selected.value
        if (mounted == null || !mounted.mounted) {
            mount(selected)
        } else if (mounted !== selected) {
            unmount(functionMode)
            mount(selected)
        } else {
            unmount(functionMode)
        }
    }

    override fun onDeleteImageButtonClicked() {
        val checkedItem = model!!.selected.value
        val adb = AlertDialog.Builder(activity)
        adb.setTitle("Delete")
        adb.setIcon(android.R.drawable.ic_dialog_alert)
        adb.setMessage("Do you really want to delete " + checkedItem!!.name + "?")
        adb.setPositiveButton("Delete") { _: DialogInterface?, _: Int ->
            if (checkedItem.downloadId != -1) {
                mainFetch!!.cancel(checkedItem.downloadId)
                mainFetch!!.remove(checkedItem.downloadId)
            }
            Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show()
            model!!.unselect()
            model!!.remove(checkedItem)
        }
        adb.setNegativeButton("Cancel", null)
        adb.show()
    }

    override fun onImageSelected(item: ImageItem?) {
        model!!.select(item)
    }

    fun createImage(destFile: ImageItem) {
        model!!.unselect()
        model!!.add(destFile)
        destFile.isDownloading = true
        val oldProgress = intArrayOf(0)
        val createImageThread = Thread(Runnable {
            try {
                val sourceFile = MainActivity.USERPATH + "/fat.img"
                var fis: FileInputStream? = null
                var fos: FileOutputStream? = null
                var size: Long = 0
                try {
                    fis = FileInputStream(sourceFile)
                    fos = FileOutputStream(destFile.userPath, true)
                    size = fis.channel.size() + fos.channel.size()
                    val buffer = ByteArray(1024 * 512)
                    var pos: Long = ceil(fos.channel.size() / buffer.size.toDouble()).toLong()
                    var noOfBytes: Int


                    // read bytes from source file and write to destination file
                    while (fis.read(buffer).also { noOfBytes = it } != -1) {
                        // if(pos % 4 == 0){
                        pos += 1
                        val finalPos = pos
                        val progress = (finalPos * buffer.size * 100L / size).toInt()
                        destFile.progress = progress
                        destFile.size = Helper.humanReadableByteCount(finalPos * buffer.size)
                        if (progress - oldProgress[0] > 1) {
                            oldProgress[0] = progress
                            model!!.downloading(destFile)
                        }
                        fos.write(buffer, 0, noOfBytes)
                    }
                } catch (e: FileNotFoundException) {
                    println("File not found$e")
                } catch (ioe: IOException) {
                    println("Exception while copying file $ioe")
                } finally {
                    // close the streams using close method
                    try {
                        fis?.close()
                        fos?.close()
                    } catch (ioe: IOException) {
                        println("Error while closing stream: $ioe")
                    }
                    val src = File(sourceFile)
                    src.delete()
                }


                //
                val finalSize = size
                activity!!.runOnUiThread {
                    Toast.makeText(activity, "Image successfully created", Toast.LENGTH_LONG).show()
                    destFile.isDownloading = false
                    destFile.size = Helper.humanReadableByteCount(finalSize)
                    model!!.downloading(destFile)
                    listView!!.smoothScrollToPosition(listViewAdapter!!.getPositionOfItem(destFile))
                    model!!.select(destFile)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
        createImageThread.isDaemon = true
        createImageThread.start()
    }

    fun addImage(imageItem: ImageItem) {
        model!!.unselect()
        model!!.remove(imageItem)
        model!!.add(imageItem)
        imageItem.isDownloading = true
        val request = Request(imageItem.url!!, MainActivity.USERPATH + "/" + imageItem.name)
        request.priority = Priority.HIGH
        request.networkType = NetworkType.WIFI_ONLY
        imageItem.downloadId = request.id
        val fetchListener: FetchListener = object : FetchListener {
            override fun onWaitingNetwork(download: Download) {
                activity!!.runOnUiThread { Toast.makeText(context, "Waiting for Network!", Toast.LENGTH_SHORT).show() }
            }

            override fun onStarted(download: Download, downloadBlocks: List<DownloadBlock>, totalBlocks: Int) {
                activity!!.runOnUiThread { Toast.makeText(context, "Download started!", Toast.LENGTH_SHORT).show() }
            }

            override fun onError(download: Download, error: Error, throwable: Throwable?) {
                model!!.remove(imageItem)
                activity!!.runOnUiThread {
                    val file = File(imageItem.userPath)
                    file.delete()
                    Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show()
                    mainFetch!!.removeListener(this)
                    mainFetch!!.remove(download.id)
                }
            }

            override fun onDownloadBlockUpdated(download: Download, downloadBlock: DownloadBlock, totalBlocks: Int) {}
            override fun onQueued(download: Download, waitingOnNetwork: Boolean) {}
            override fun onAdded(download: Download) {}
            override fun onCompleted(download: Download) {
                imageItem.isDownloading = false
                imageItem.size = Helper.humanReadableByteCount(download.total)
                model!!.downloading(imageItem)
                listView!!.smoothScrollToPosition(listViewAdapter!!.getPositionOfItem(imageItem))
                model!!.select(imageItem)
                activity!!.runOnUiThread { Toast.makeText(context, "Downloaded!", Toast.LENGTH_SHORT).show() }
                mainFetch!!.removeListener(this)
                mainFetch!!.remove(download.id)
            }

            override fun onProgress(download: Download, etaInMilliSeconds: Long, downloadedBytesPerSecond: Long) {
                if (request.id == download.id) {
                    imageItem.progress = download.progress
                    imageItem.size = Helper.humanReadableByteCount(download.downloaded) + " / " + Helper.humanReadableByteCount(download.total) + " - " + Helper.humanReadableByteCount(downloadedBytesPerSecond) + "/s"
                    model!!.downloading(imageItem)
                    val progress = download.progress
                    Log.d("Fetch", "Progress Completed :$progress")
                }
            }

            override fun onPaused(download: Download) {}
            override fun onResumed(download: Download) {}
            override fun onCancelled(download: Download) {
                model!!.remove(imageItem)
                activity!!.runOnUiThread {
                    val file = File(imageItem.userPath)
                    file.delete()
                    Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show()
                    mainFetch!!.removeListener(this)
                    mainFetch!!.remove(download.id)
                }
            }

            override fun onRemoved(download: Download) {}
            override fun onDeleted(download: Download) {}
        }
        mainFetch!!.addListener(fetchListener)
        mainFetch!!.enqueue(request, Func { download: Request -> Log.i("lala", "added " + download.id) }, Func { error: Error -> Log.i("lala", "error  " + error.name) })
    }
}
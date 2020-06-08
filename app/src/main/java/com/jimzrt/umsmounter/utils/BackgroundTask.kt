package com.jimzrt.umsmounter.utils

import android.app.Activity
import android.app.ProgressDialog
import android.os.AsyncTask
import com.jimzrt.umsmounter.tasks.BaseTask
import java.lang.ref.WeakReference

class BackgroundTask(activity: Activity) : AsyncTask<Void?, Void?, Void?>() {
    private val mWeakActivity: WeakReference<Activity> = WeakReference(activity)
    private var delegate: AsyncResponse? = null
    private var errorMessage: String? = null
    private var successful = true
    private var dialog: ProgressDialog? = null
    private var tasks: Array<BaseTask>? = null
    fun setDelegate(delegate: AsyncResponse?): BackgroundTask {
        this.delegate = delegate
        return this
    }

    fun setTasks(tasks: Array<BaseTask>?): BackgroundTask {
        this.tasks = tasks
        return this
    }

    override fun onPreExecute() {
        dialog = ProgressDialog(mWeakActivity.get())
        dialog!!.setTitle("Starting Tasks")
        dialog!!.isIndeterminate = true
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    override fun doInBackground(vararg params: Void?): Void? {
        val description = StringBuilder()
        var newline = ""
        if (tasks != null) {
            for (task in tasks!!) {
                description.append(newline).append(task.description)
                val finalDescription = description.toString()
                mWeakActivity.get()!!.runOnUiThread {
                    dialog!!.setTitle(task.name)
                    dialog!!.setMessage(finalDescription)
                }
                var success: String
                task.setContext(mWeakActivity)
                if (task.execute()) {
                    success = "✓"
                    // break;
                } else {
                    success = "✗"
                    description.append(success)
                    val finalDescription2 = description.toString()
                    mWeakActivity.get()!!.runOnUiThread { dialog!!.setMessage(finalDescription2) }
                    successful = false
                    errorMessage = task.result
                    break
                }
                description.append(success)
                val finalDescription2 = description.toString()
                mWeakActivity.get()!!.runOnUiThread { dialog!!.setMessage(finalDescription2) }
                try {
                    Thread.sleep(500)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                newline = "\n"
            }
        }
        return null
    }

    override fun onPostExecute(result: Void?) {
        dialog!!.dismiss()
        if (delegate != null) {
            val results = StringBuilder()
            for (task in tasks!!) {
                results.append(task.result)
            }
            if (successful) {
                delegate!!.processFinish(true, results.toString())
            } else {
                delegate!!.processFinish(false, errorMessage)
            }
        }
    }

    // you may separate this or combined to caller class.
    interface AsyncResponse {
        fun processFinish(successful: Boolean?, output: String?)
    }

}
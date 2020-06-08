package com.jimzrt.umsmounter.fragments

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.os.StatFs
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.jimzrt.umsmounter.R
import com.jimzrt.umsmounter.activities.MainActivity
import com.jimzrt.umsmounter.utils.Helper
import com.topjohnwu.superuser.Shell

class ImageCreationFragment : Fragment() {
    private var mCallback: OnImageCreationListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        mCallback = try {
            context as OnImageCreationListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_image_creation, container, false)
        val button = view.findViewById<Button>(R.id.createImageButton)
        if (activity != null) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = "Create Image"
        }

        // ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        val nameTextView = view.findViewById<TextView>(R.id.imageName)
        val sizeTextView = view.findViewById<TextView>(R.id.imageSize)
        val stat = StatFs(MainActivity.USERPATH)
        val bytesAvailable = stat.blockSizeLong * stat.availableBlocksLong
        val freeSpaceView = view.findViewById<TextView>(R.id.freeSpaceView)
        freeSpaceView.text = "Free Space: " + Helper.humanReadableByteCount(bytesAvailable)
        button.setOnClickListener {
            val inputManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val nameText = nameTextView.text.toString()
            if (nameText.isEmpty()) {
                nameTextView.requestFocus()
                nameTextView.error = "Cannot be empty"
                return@setOnClickListener
            }
            val sizeText = sizeTextView.text.toString()
            if (sizeText.isEmpty()) {
                sizeTextView.requestFocus()
                sizeTextView.error = "Cannot be empty"
                return@setOnClickListener
            }
            if (sizeText.toLong() * 1024L * 1024L > bytesAvailable) {
                sizeTextView.requestFocus()
                sizeTextView.error = "Not enough space"
                return@setOnClickListener
            }
            if (sizeText.toInt() < 2) {
                sizeTextView.requestFocus()
                sizeTextView.error = "At least 2 MB"
                return@setOnClickListener
            }
            inputManager.hideSoftInputFromWindow(if (null == activity!!.currentFocus) null else activity!!.currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            val imageName = nameTextView.text.toString() + ".img"
            val imageSize = sizeTextView.text.toString().toInt()
            val barProgressDialog = ProgressDialog(context)
            barProgressDialog.setTitle("Creating $imageName...")
            barProgressDialog.setMessage("Starting ...")
            barProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            barProgressDialog.progress = 0
            barProgressDialog.max = 100
            barProgressDialog.setCancelable(false)
            barProgressDialog.show()
            Thread(Runnable {
                try {
                    activity!!.runOnUiThread {
                        barProgressDialog.setMessage("Preparing...")
                        Helper.verifyStoragePermissions(activity)
                    }
                    Shell.su("busybox truncate -s" + imageSize + "M " + MainActivity.ROOTPATH + "/tmp.img", "echo \"x\\nc\\n" + imageSize + "\\nr\\no\\nn\\np\\n1\\n2\\n\\nt\\nc\\na\\n1\\nw\\n\" | busybox fdisk -S 32 -H 64 " + MainActivity.ROOTPATH + "/tmp.img", "fdisk -l " + MainActivity.ROOTPATH + "/tmp.img", "busybox dd if=" + MainActivity.ROOTPATH + "/tmp.img of=" + MainActivity.ROOTPATH + "/" + imageName + " bs=512 count=2048", "rm " + MainActivity.ROOTPATH + "/tmp.img", "busybox truncate -s" + (imageSize - 1) + "M " + MainActivity.ROOTPATH + "/fat.img", "busybox mkfs.vfat -n DRIVE " + MainActivity.ROOTPATH + "/fat.img", "chmod 777 " + MainActivity.ROOTPATH + "/fat.img", "chmod 777 " + MainActivity.ROOTPATH + "/" + imageName).exec()
                    activity!!.runOnUiThread {
                        barProgressDialog.progress = 100
                        barProgressDialog.dismiss()
                    }
                    activity!!.runOnUiThread {
                        //    Toast.makeText(getActivity(), "Image successfully created",Toast.LENGTH_LONG).show();
                        mCallback!!.OnImageCreation(imageName)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }).start()
        }
        return view
    }

    interface OnImageCreationListener {
        fun OnImageCreation(imageItem: String?)
    }
}
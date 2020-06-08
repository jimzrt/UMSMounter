package com.jimzrt.umsmounter.listadapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.jimzrt.umsmounter.R
import com.jimzrt.umsmounter.fragments.DownloadFragment.OnImageDownloadListener
import com.jimzrt.umsmounter.model.DownloadItem

class ImageDownloadListAdapter // Provide a suitable constructor (depends on the kind of dataset)
(private var mDataset: MutableList<DownloadItem>?, private val mCallback: OnImageDownloadListener) : RecyclerView.Adapter<ImageDownloadListAdapter.ViewHolder>(), View.OnClickListener {
    private var mRecyclerView: RecyclerView? = null
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    fun setItems(items: MutableList<DownloadItem>?) {
        mDataset = items
    }

    override fun onClick(v: View) {
        val itemPosition = mRecyclerView!!.getChildLayoutPosition(v)
        mCallback.OnImageListClick(mDataset!![itemPosition])
    }

    fun clear() {
        if (mDataset != null) mDataset!!.clear()
        notifyDataSetChanged()
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        // create a new view
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_download, parent, false) as ConstraintLayout
        return ViewHolder(v, this)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.downloadNameView.text = mDataset!![position].name
        holder.downloadUrlView.text = mDataset!![position].url
        holder.downloadReleasesView.text = mDataset!![position].releases.size.toString() + " Releases"
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return if (mDataset == null) 0 else mDataset!!.size
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class ViewHolder(// each data item is just a string in this case
            private val root: ConstraintLayout, listener: View.OnClickListener?) : RecyclerView.ViewHolder(root) {
        val downloadNameView: TextView
        val downloadUrlView: TextView
        val downloadReleasesView: TextView

        init {
            root.setOnClickListener(listener)
            downloadNameView = root.findViewById(R.id.downloadName)
            downloadUrlView = root.findViewById(R.id.download_url)
            downloadReleasesView = root.findViewById(R.id.download_releases)
        }
    }

}
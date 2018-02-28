package com.example.james.myapplication.Model;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.james.myapplication.DownloadFragment;
import com.example.james.myapplication.R;

import java.util.List;

/**
 * Created by james on 27.02.18.
 */

public class ImageDownloadListAdapter extends RecyclerView.Adapter<ImageDownloadListAdapter.ViewHolder> implements View.OnClickListener {
    private DownloadFragment.OnImageDownloadListener mCallback;
    private List<DownloadItem> mDataset;


    RecyclerView mRecyclerView;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
    }

    public void setItems(List<DownloadItem> items) {
        this.mDataset = items;
    }

    @Override
    public void onClick(View v) {

        int itemPosition = mRecyclerView.getChildLayoutPosition(v);

        mCallback.OnImageListClick(mDataset.get(itemPosition));

    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ConstraintLayout root;
        TextView downloadNameView;
        TextView downloadUrlView;
        TextView downloadReleasesView;

        ViewHolder(ConstraintLayout v, View.OnClickListener listener) {
            super(v);
            root = v;
            root.setOnClickListener(listener);
            downloadNameView = v.findViewById(R.id.downloadName);
            downloadUrlView = v.findViewById(R.id.download_url);
            downloadReleasesView = v.findViewById(R.id.download_releases);

        }

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ImageDownloadListAdapter(List<DownloadItem> myDataset, DownloadFragment.OnImageDownloadListener mCallback) {
        mDataset = myDataset;
        this.mCallback = mCallback;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ImageDownloadListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                  int viewType) {
        // create a new view
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_download, parent, false);


        ViewHolder vh = new ViewHolder(v, this);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.downloadNameView.setText(mDataset.get(position).name);
        holder.downloadUrlView.setText(mDataset.get(position).url);
        holder.downloadReleasesView.setText(mDataset.get(position).releases.length + " Releases");

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (mDataset == null)
            return 0;
        return mDataset.size();
    }
}
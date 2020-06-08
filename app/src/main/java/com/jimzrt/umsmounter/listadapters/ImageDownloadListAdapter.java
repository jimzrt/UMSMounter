package com.jimzrt.umsmounter.listadapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.jimzrt.umsmounter.R;
import com.jimzrt.umsmounter.fragments.DownloadFragment;
import com.jimzrt.umsmounter.model.DownloadItem;

import java.util.List;

public class ImageDownloadListAdapter extends RecyclerView.Adapter<ImageDownloadListAdapter.ViewHolder> implements View.OnClickListener {
    private final DownloadFragment.OnImageDownloadListener mCallback;
    private RecyclerView mRecyclerView;
    private List<DownloadItem> mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public ImageDownloadListAdapter(List<DownloadItem> mDataset, DownloadFragment.OnImageDownloadListener mCallback) {
        this.mDataset = mDataset;
        this.mCallback = mCallback;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
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

    public void clear() {
        if (mDataset != null)
            mDataset.clear();
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ImageDownloadListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                  int viewType) {
        // create a new view
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_download, parent, false);


        return new ViewHolder(v, this);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        final ConstraintLayout root;
        final TextView downloadNameView;
        final TextView downloadUrlView;
        final TextView downloadReleasesView;

        ViewHolder(ConstraintLayout v, View.OnClickListener listener) {
            super(v);
            root = v;
            root.setOnClickListener(listener);
            downloadNameView = v.findViewById(R.id.downloadName);
            downloadUrlView = v.findViewById(R.id.download_url);
            downloadReleasesView = v.findViewById(R.id.download_releases);

        }

    }
}
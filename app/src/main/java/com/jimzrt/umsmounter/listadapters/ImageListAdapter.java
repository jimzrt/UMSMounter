package com.jimzrt.umsmounter.listadapters;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jimzrt.umsmounter.R;
import com.jimzrt.umsmounter.model.ImageItem;
import com.jimzrt.umsmounter.utils.Helper;

import java.util.List;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> implements View.OnClickListener {

    private final Context mContext;
    private final OnImageListListener mCallback;
    private final SortedList<ImageItem> dataSet;
    private RecyclerView mRecyclerView;
    private int lastSelected = -1;

    public ImageListAdapter(List<ImageItem> data, Context context, OnImageListListener listener) {
        this.dataSet = new SortedList<>(ImageItem.class, new SortedList.Callback<ImageItem>() {
            @Override
            public int compare(ImageItem o1, ImageItem o2) {
                return o1.compareTo(o2);
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);

            }

            @Override
            public boolean areContentsTheSame(ImageItem oldItem, ImageItem newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(ImageItem item1, ImageItem item2) {
                return item1.equals(item2);
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);

            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);

            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);

            }
        });

        if (data != null) {
            dataSet.beginBatchedUpdates();
            for (int i = 0; i < data.size(); i++) {
                dataSet.add(data.get(i));
            }
            dataSet.endBatchedUpdates();
        }


        this.mContext = context;
        this.mCallback = listener;

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // create a new view
        ConstraintLayout rootView = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item, parent, false);

        ViewHolder holder = new ViewHolder(rootView, this);

        holder.imageView = rootView.findViewById(R.id.item_info);
        holder.name = rootView.findViewById(R.id.name);
        holder.size = rootView.findViewById(R.id.type);
        holder.mountButton = rootView.findViewById(R.id.mountButton);
        holder.mountButton.setOnClickListener(v -> mCallback.onMountImageButtonClicked());
        holder.deleteButton = rootView.findViewById(R.id.deleteButton);
        holder.deleteButton.setOnClickListener(v -> mCallback.onDeleteImageButtonClicked());
        holder.progressBar = rootView.findViewById(R.id.progressBar);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            // Perform a full update

            onBindViewHolder(holder, holder.getAdapterPosition());
        } else {
            // Perform a partial update
            for (Object payload : payloads) {
                View convertView = holder.rootView;

                if (payload.equals("aufklappen")) {

                    // boolean visible = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).isViewPartiallyVisible(convertView, false, true);


                    // int colorFrom = Color.argb(0, 250, 250, 250);
                    // int colorTo = Color.argb(255, 220, 220, 220);
                    ValueAnimator colorAnimation = ValueAnimator.ofInt(0, 255).setDuration(1000);

                    colorAnimation.addUpdateListener(animator -> convertView.setBackgroundColor(Color.argb((Integer) colorAnimation.getAnimatedValue(), 220, 220, 220)));
                    colorAnimation.start();

                    // convertView.animate().rotationX(20f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);

                    ValueAnimator anim0 = ValueAnimator.ofFloat(0, 20);
                    anim0.addUpdateListener(valueAnimator -> {
                        float val = (Float) valueAnimator.getAnimatedValue();
                        convertView.setRotationX(val);
                    });
                    anim0.setDuration(500);
                    anim0.start();

                    ValueAnimator anim = ValueAnimator.ofInt(Helper.px2dp(70, mContext), Helper.px2dp(70 + 30, mContext));
                    anim.addUpdateListener(valueAnimator -> {
                        int val = (Integer) valueAnimator.getAnimatedValue();
                        ViewGroup.LayoutParams layoutParams = convertView.getLayoutParams();
                        layoutParams.height = val;
                        convertView.setLayoutParams(layoutParams);
                    });
                    anim.setDuration(300);
                    anim.start();


                    convertView.postDelayed(() -> {
                        if (dataSet.get(holder.getAdapterPosition()).isDownloading()) {
                            holder.mountButton.setVisibility(View.INVISIBLE);
                        } else {
                            holder.mountButton.setVisibility(View.VISIBLE);

                        }
                        holder.deleteButton.setVisibility(View.VISIBLE);

                        mRecyclerView.smoothScrollToPosition(holder.getAdapterPosition());

                    }, 200);



                    // holder.deleteButton.setVisibility(View.VISIBLE);

                } else if (payload.equals("zuklappen")) {

                    ValueAnimator anim0 = ValueAnimator.ofFloat(convertView.getRotationX(), 0);
                    anim0.addUpdateListener(valueAnimator -> {
                        float val = (Float) valueAnimator.getAnimatedValue();
                        convertView.setRotationX(val);
                    });
                    anim0.setDuration(500);
                    anim0.start();

                    //  convertView.animate().rotationX(0).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500).withEndAction(() -> {

                    //    });;

                    ValueAnimator colorAnimation = ValueAnimator.ofInt(convertView.getBackground().getAlpha(), 0).setDuration(1000);

                    colorAnimation.addUpdateListener(animator -> convertView.setBackgroundColor(Color.argb((Integer) colorAnimation.getAnimatedValue(), 220, 220, 220)));
                    colorAnimation.start();


                    ValueAnimator anim = ValueAnimator.ofInt(convertView.getLayoutParams().height, Helper.px2dp(70, mContext));
                    anim.addUpdateListener(valueAnimator -> {
                        int val = (Integer) valueAnimator.getAnimatedValue();
                        ViewGroup.LayoutParams layoutParams = convertView.getLayoutParams();
                        layoutParams.height = val;
                        convertView.setLayoutParams(layoutParams);
                    });

                    anim.setDuration(300);
                    anim.start();

                    convertView.postDelayed(() -> {
                        holder.mountButton.setVisibility(View.INVISIBLE);
                        holder.deleteButton.setVisibility(View.INVISIBLE);
                    }, 200);


                } else if (payload.equals("mount")) {

                    holder.imageView.setColorFilter(mContext.getResources().getColor(R.color.green));
                    holder.mountButton.setChecked(true);


                } else if (payload.equals("unmount")) {

                    holder.imageView.setColorFilter(null);
                    holder.mountButton.setChecked(false);
                } else if (payload.equals("download")) {
                    ImageItem imageItem = dataSet.get(holder.getAdapterPosition());
                    if (imageItem.isDownloading()) {
                        holder.progressBar.setVisibility(View.VISIBLE);
                        holder.progressBar.setProgress(imageItem.getProgress());
                        holder.size.setText(imageItem.getSize());
                    } else {
                        holder.progressBar.setVisibility(View.INVISIBLE);
                        holder.size.setText(imageItem.getSize());
                    }

                }
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ImageItem imageItem = dataSet.get(position);


        holder.name.setText(imageItem.getName());
        holder.size.setText(imageItem.getSize());

        View convertView = holder.rootView;

        ViewGroup.LayoutParams layoutParams = convertView.getLayoutParams();
        if (imageItem.getSelected()) {


            convertView.setBackgroundColor(Color.argb(255, 220, 220, 220));
            convertView.setRotationX(20f);
            layoutParams.height = Helper.px2dp(70 + 30, mContext);

            if (!imageItem.isDownloading()) {
                holder.mountButton.setVisibility(View.INVISIBLE);

            } else {
                holder.mountButton.setVisibility(View.VISIBLE);

            }
            holder.deleteButton.setVisibility(View.VISIBLE);
        } else {
            convertView.setBackgroundColor(Color.argb(0, 220, 220, 220));
            convertView.setRotationX(0);
            layoutParams.height = Helper.px2dp(70, mContext);
            holder.mountButton.setVisibility(View.INVISIBLE);
            holder.deleteButton.setVisibility(View.INVISIBLE);
        }


        convertView.setLayoutParams(layoutParams);
        //holder.mountButton.setAlpha(0);
        //holder.deleteButton.setAlpha(0);


        //  }
        //  } else {
        //      bgColor = Color.WHITE;
        //  }

        //   if(imageItem.getMounted()){
        //       holder.imageView.setColorFilter(mContext.getResources().getColor(R.color.green));
        //       holder.mountButton.setChecked(true);

        //   } else {
        //  holder.imageView.setColorFilter(null);
        //    holder.mountButton.setChecked(false);

        //    }

        if (imageItem.isDownloading()) {
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.progressBar.setProgress(imageItem.getProgress());
        } else {
            holder.progressBar.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    public void remove(ImageItem checkedItem) {
        //animationMap.remove(dataSet.indexOf(checkedItem));
        //   setSelectedItemPosition(-1);
        dataSet.remove(checkedItem);
    }

    @Override
    public void onClick(View v) {
        int itemPosition = mRecyclerView.getChildLayoutPosition(v);

        //setSelectedItemPosition(itemPosition);
        ImageItem item = dataSet.get(itemPosition);
        mCallback.onImageSelected(item);

    }

    public void addItems(List<ImageItem> list) {

        // dataSet.beginBatchedUpdates();
        //   dataSet.clear();

        //    dataSet.beginBatchedUpdates();
        for (int j = 0; j < dataSet.size(); j++) {
            if (!list.contains(dataSet.get(j))) {
                dataSet.remove(dataSet.get(j));
            }
        }
        //     if(dataSet.size() == 0){

        for (int i = 0; i < list.size(); i++) {
            //  addItem(list.get(i), false);
            if (!contains(list.get(i))) {
                dataSet.add(list.get(i));
            }
        }
//        }


        // dataSet.endBatchedUpdates();

    }

    public int addItem(ImageItem imageItem) {

        return dataSet.add(imageItem);

    }

    private boolean contains(ImageItem item) {
        return dataSet.indexOf(item) != -1;
    }

    public int getPositionOfItem(ImageItem imageItem) {
        return dataSet.indexOf(imageItem);
    }

    public void setSelectedItem(ImageItem selectedItem) {
        if (selectedItem == null) {
            setSelectedItemPosition(-1);
        } else {
            int position = dataSet.indexOf(selectedItem);
            setSelectedItemPosition(position);
        }

    }


    private void setSelectedItemPosition(int position) {
        //notifyDataSetChanged();
        if (position == -1) {
            if (lastSelected == -1) {
                return;
            }
            dataSet.get(lastSelected).setSelected(false);
            notifyItemChanged(lastSelected, "zuklappen");
            lastSelected = -1;
            return;
        }

        if (lastSelected == position) {
            dataSet.get(position).setSelected(false);
            notifyItemChanged(position, "zuklappen");
            lastSelected = -1;
            return;
        } else if (lastSelected != -1) {
            dataSet.get(lastSelected).setSelected(false);
            notifyItemChanged(lastSelected, "zuklappen");

        }
        dataSet.get(position).setSelected(true);
        notifyItemChanged(position, "aufklappen");
        lastSelected = position;
    }


    public interface OnImageListListener {

        void onMountImageButtonClicked();

        void onDeleteImageButtonClicked();

        void onImageSelected(ImageItem item);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ConstraintLayout rootView;
        private ImageView imageView;
        private TextView name;
        private TextView size;
        private ToggleButton mountButton;
        private Button deleteButton;
        private ProgressBar progressBar;

        ViewHolder(View rootView, View.OnClickListener listener) {
            super(rootView);
            this.rootView = (ConstraintLayout) rootView;
            this.rootView.setOnClickListener(listener);
        }
    }
}
package com.example.james.myapplication.Model;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.james.myapplication.R;
import com.example.james.myapplication.Utils.Helper;

import java.util.ArrayList;
import java.util.List;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> implements View.OnClickListener {

    RecyclerView mRecyclerView;
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        RelativeLayout rootView = (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_item, parent, false);

        ViewHolder holder = new ViewHolder(rootView, this);

        holder.imageView = rootView.findViewById(R.id.item_info);
        holder.name = rootView.findViewById(R.id.name);
        holder.size = rootView.findViewById(R.id.type);
        holder.mountButton = rootView.findViewById(R.id.mountButton);
        holder.mountButton.setOnClickListener(v -> {
            mCallback.onMountImageButtonClicked();
        });
        holder.deleteButton = rootView.findViewById(R.id.deleteButton);
        holder.deleteButton.setOnClickListener(v -> {
            mCallback.onDeleteImageButtonClicked();
        });
        holder.progressBar = rootView.findViewById(R.id.progressBar);
        return holder;
    }

    //private HashSet<Integer> animationMap = new HashSet<>();

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            // Perform a full update
            onBindViewHolder(holder, position);
        } else {
            Log.i("lala", "ZUKLAPPEN!");
            // Perform a partial update
            for (Object payload : payloads) {
                View convertView = holder.rootView;

                if (payload.equals("aufklappen")) {

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


                    //holder.mountButton.setAlpha(0);
                    ValueAnimator anim2 = ValueAnimator.ofFloat(0f, 1f);
                    anim2.addUpdateListener(valueAnimator -> {
                        float val = (Float) valueAnimator.getAnimatedValue();
                        holder.mountButton.setAlpha(val);
                        holder.deleteButton.setAlpha(val);
                    });
                    anim2.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            holder.mountButton.setVisibility(View.VISIBLE);
                            holder.deleteButton.setVisibility(View.VISIBLE);

                            mRecyclerView.smoothScrollToPosition(position);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    anim2.setDuration(200);
                    anim2.start();
                    holder.mountButton.setVisibility(View.VISIBLE);
                    holder.deleteButton.setVisibility(View.VISIBLE);

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

                    ValueAnimator anim2 = ValueAnimator.ofInt(255, 0);
                    anim2.addUpdateListener(valueAnimator -> {
                        int val = (Integer) valueAnimator.getAnimatedValue();
                        holder.mountButton.setAlpha(val);
                        holder.deleteButton.setAlpha(val);

                    });
                    anim2.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            holder.mountButton.setVisibility(View.INVISIBLE);
                            holder.deleteButton.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    anim2.setDuration(200);
                    anim2.start();

                } else if (payload.equals("mount")) {

                    holder.imageView.setColorFilter(mContext.getResources().getColor(R.color.green));
                    holder.mountButton.setChecked(true);


                } else if (payload.equals("unmount")) {

                    holder.imageView.setColorFilter(null);
                    holder.mountButton.setChecked(false);
                } else if (payload.equals("download")) {
                    ImageItem imageItem = dataSet.get(position);
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
    public void onBindViewHolder(ViewHolder holder, int position) {

        ImageItem imageItem = dataSet.get(position);


        holder.name.setText(imageItem.getName());
        holder.size.setText(imageItem.getSize());

        View convertView = holder.rootView;

        convertView.setBackgroundColor(Color.argb(0, 220, 220, 220));

        //    if(imageItem.getSelected()) {


        // } else {
        //   convertView.setBackgroundColor(Color.argb(0, 250, 250, 250));
        //  convertView.setRotationX(0f);
        // ViewGroup.LayoutParams layoutParams = convertView.getLayoutParams();
        // layoutParams.height = Helper.px2dp(70, mContext);
        // convertView.setLayoutParams(layoutParams);
        //holder.mountButton.setAlpha(0);
        //holder.deleteButton.setAlpha(0);
        holder.mountButton.setVisibility(View.INVISIBLE);
        holder.deleteButton.setVisibility(View.INVISIBLE);


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

        //    if(imageItem.isDownloading()){
        //      holder.progressBar.setVisibility(View.VISIBLE);
        //       holder.progressBar.setProgress(imageItem.getProgress());
        //    } else {
        holder.progressBar.setVisibility(View.INVISIBLE);
        //    }


    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public ImageItem getItemAtPosition(int checkedItemPosition) {
        return dataSet.get(checkedItemPosition);
    }

    public void remove(ImageItem checkedItem) {
        //animationMap.remove(dataSet.indexOf(checkedItem));
        dataSet.remove(checkedItem);
    }

    @Override
    public void onClick(View v) {
        int itemPosition = mRecyclerView.getChildLayoutPosition(v);

        setSelectedItemPosition(itemPosition);

    }


    public void addItems(ArrayList<ImageItem> list) {

        dataSet.beginBatchedUpdates();
        for (int i = 0; i < list.size(); i++) {
            dataSet.add(list.get(i));
        }
        dataSet.endBatchedUpdates();

    }

    public void addItem(ImageItem imageItem) {
        dataSet.add(imageItem);
    }

    public boolean contains(ImageItem item) {
        return dataSet.indexOf(item) != -1;
    }

    public interface OnImageButtonListener {
        void onMountImageButtonClicked();

        void onDeleteImageButtonClicked();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rootView;
        private ImageView imageView;
        private TextView name;
        private TextView size;
        private ToggleButton mountButton;
        private Button deleteButton;
        private ProgressBar progressBar;
        private boolean animated;

        public ViewHolder(View rootView, View.OnClickListener listener) {
            super(rootView);
            this.rootView = (RelativeLayout) rootView;
            this.rootView.setOnClickListener(listener);
        }
    }


    private SortedList<ImageItem> dataSet;
    Context mContext;
    OnImageButtonListener mCallback;


    private int lastSelected = -1;


    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        setSelectedItemPosition(position);

    }

    public void setSelectedItemPosition(int position) {
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

    public int getSelectedItemPosition() {
        return lastSelected;
    }


    public ImageListAdapter(ArrayList<ImageItem> data, Context context, OnImageButtonListener listener) {
        // super(context, R.layout.row_item, data);
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

        dataSet.beginBatchedUpdates();
        for (int i = 0; i < data.size(); i++) {
            dataSet.add(data.get(i));
        }
        dataSet.endBatchedUpdates();


        this.mContext = context;
        this.mCallback = listener;

    }


    private int lastMounted = -1;

    public void setMountedItemPosition(int mountedItemPosition) {
        if(lastMounted != -1) {
            dataSet.get(lastMounted).setMounted(false);
            notifyItemChanged(lastMounted, "unmount");
        }
        lastMounted = mountedItemPosition;
        if(lastMounted != -1){
            dataSet.get(lastMounted).setMounted(true);
            notifyItemChanged(lastMounted, "mount");
        }

    }

    public int getMountedItemPosition(){
        return lastMounted;
    }
}
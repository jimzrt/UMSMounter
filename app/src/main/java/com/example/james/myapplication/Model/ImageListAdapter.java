package com.example.james.myapplication.Model;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.design.widget.Snackbar;
import android.support.graphics.drawable.ArgbEvaluator;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.james.myapplication.R;
import com.example.james.myapplication.Utils.Helper;

import java.util.ArrayList;

public class ImageListAdapter extends ArrayAdapter<ImageItem> implements AdapterView.OnItemClickListener {

    public interface OnImageButtonListener {
        void onMountImageButtonClicked();
        void onDeleteImageButtonClicked();
    }



    static class ViewHolder {
        private ImageView imageView;
        private TextView name;
        private TextView size;
        private ToggleButton mountButton;
        private Button deleteButton;
    }


    private ArrayList<ImageItem> dataSet;
    Context mContext;
    OnImageButtonListener mCallback;


    private int lastSelected = -1;
    private int lastlastSelected = -1;

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        setSelectedItemPosition(position);

    }

    public void setSelectedItemPosition(int position){
        lastlastSelected = lastSelected;
        if(position == -1){
            if(lastSelected == -1){
                return;
            }
            getItem(lastSelected).setSelected(false);
            lastSelected = -1;
            notifyDataSetChanged();
            return;
        }

        if(lastSelected == position){
            getItem(position).setSelected(false);
            lastSelected = -1;
            notifyDataSetChanged();
            return;
        }else if(lastSelected != -1){
            getItem(lastSelected).setSelected(false);
        }
        getItem(position).setSelected(true);
        lastSelected = position;
        notifyDataSetChanged();
    }

    public int getSelectedItemPosition(){
        return lastSelected;
    }


    public ImageListAdapter(ArrayList<ImageItem> data, Context context, OnImageButtonListener listener) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext=context;
        this.mCallback = listener;

    }

  //  @Override
   // public int getViewTypeCount() {
   //     return 2;
   // }

    //@Override
   // public int getItemViewType(int position) {
    //    return getItem(position).getSelected() ? 1 : 0;
   // }


    private int viewHeight;
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder holder;
        ImageItem imageItem = getItem(position);

    //    int type = getItemViewType(position);


      //  if (type == 0){

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_item, parent, false);
                holder = new ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.item_info);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.size = (TextView) convertView.findViewById(R.id.type);
                holder.mountButton = convertView.findViewById(R.id.mountButton);
                holder.mountButton.setOnClickListener(v -> {
                    mCallback.onMountImageButtonClicked();
                });
                holder.deleteButton = convertView.findViewById(R.id.deleteButton);
                holder.deleteButton.setOnClickListener(v -> {
                    mCallback.onDeleteImageButtonClicked();
                });
                convertView.setTag(holder);


            } else {
                holder = (ViewHolder) convertView.getTag();
            }
//    } else{
//        if (convertView == null) {
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_item_expanded, parent, false);
//            holder = new ViewHolder();
//            holder.imageView = (ImageView) convertView.findViewById(R.id.item_info);
//            holder.name = (TextView) convertView.findViewById(R.id.name);
//            holder.size = (TextView) convertView.findViewById(R.id.type);
//            convertView.setTag(holder);
//
//
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//    }



        holder.name.setText(imageItem.getName());
        holder.size.setText(imageItem.getSize());





        //convertView.setOnClickListener(this);
      //  int bgColor;
        if(imageItem.getSelected()) {
            convertView.setRotationX(0);

            int colorFrom = Color.argb(0,250,250,250);
            int colorTo = Color.argb(255,220,220,220);
            ValueAnimator colorAnimation = ValueAnimator.ofArgb(colorFrom, colorTo).setDuration(1000);

            View finalConvertView = convertView;
            colorAnimation.addUpdateListener(animator -> finalConvertView.setBackgroundColor((int) animator.getAnimatedValue()));
            colorAnimation.start();

            convertView.animate().rotationX(20f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);

            ValueAnimator anim = ValueAnimator.ofInt(Helper.px2dp(70,getContext()), Helper.px2dp(70+30,getContext()));
            anim.addUpdateListener(valueAnimator -> {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = finalConvertView.getLayoutParams();
                layoutParams.height = val;
                finalConvertView.setLayoutParams(layoutParams);
            });
            anim.setDuration(300);
            anim.start();


            //holder.mountButton.setAlpha(0);
            ValueAnimator anim2 = ValueAnimator.ofInt(0, 255);
            anim2.addUpdateListener(valueAnimator -> {
                int val = (Integer) valueAnimator.getAnimatedValue();
                holder.mountButton.setAlpha(val);
                holder.deleteButton.setAlpha(val);
            });
            anim2.setDuration(200);
            anim2.start();

            holder.mountButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);

            convertView.postDelayed(() -> ((ListView)parent).smoothScrollToPosition(position), 500);




        } else if(lastlastSelected == position){
            convertView.animate().rotationX(0).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500).withEndAction(() -> {
                holder.mountButton.setVisibility(View.INVISIBLE);
                holder.deleteButton.setVisibility(View.INVISIBLE);
            });;

            int colorFrom = Color.argb(255,220,220,220);
            int colorTo = Color.argb(0,250,250,250);
            ValueAnimator colorAnimation = ValueAnimator.ofArgb(colorFrom, colorTo).setDuration(1000);

            View finalConvertView = convertView;
            colorAnimation.addUpdateListener(animator -> finalConvertView.setBackgroundColor((int) animator.getAnimatedValue()));
            colorAnimation.start();

            ValueAnimator anim = ValueAnimator.ofInt(Helper.px2dp(70+30,getContext()), Helper.px2dp(70,getContext()));
            anim.addUpdateListener(valueAnimator -> {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = finalConvertView.getLayoutParams();
                layoutParams.height = val;
                finalConvertView.setLayoutParams(layoutParams);
            });
            anim.setDuration(300);
            anim.start();

            ValueAnimator anim2 = ValueAnimator.ofInt(255, 0);
            anim2.addUpdateListener(valueAnimator -> {
                int val = (Integer) valueAnimator.getAnimatedValue();
                holder.mountButton.setAlpha(val);
                holder.deleteButton.setAlpha(val);

            });
            anim2.setDuration(200);
            anim2.start();



        } else {
            holder.mountButton.setVisibility(View.INVISIBLE);
            holder.deleteButton.setVisibility(View.INVISIBLE);
        }
      //  } else {
      //      bgColor = Color.WHITE;
      //  }

            if(imageItem.getMounted()){
                holder.imageView.setColorFilter(getContext().getResources().getColor(R.color.green));
                holder.mountButton.setChecked(true);

            } else {
                holder.imageView.setColorFilter(null);
                holder.mountButton.setChecked(false);

            }



            //convertView.setBackgroundColor(bgColor);


        return convertView;


    }
    private int lastMounted = -1;

    public void setMountedItemPosition(int mountedItemPosition) {
        if(lastMounted != -1) {
            getItem(lastMounted).setMounted(false);
            notifyDataSetChanged();
        }
        lastMounted = mountedItemPosition;
        if(lastMounted != -1){
            getItem(lastMounted).setMounted(true);
            notifyDataSetChanged();
        }

    }

    public int getMountedItemPosition(){
        return lastMounted;
    }
}
package com.jimzrt.umsmounter.listadapters

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.jimzrt.umsmounter.R
import com.jimzrt.umsmounter.model.ImageItem
import com.jimzrt.umsmounter.utils.Helper.px2dp

class ImageListAdapter(data: List<ImageItem>?, context: Context, listener: OnImageListListener) : RecyclerView.Adapter<ImageListAdapter.ViewHolder>(), View.OnClickListener {
    private val mContext: Context
    private val mCallback: OnImageListListener
    private val dataSet: SortedList<ImageItem>
    private var mRecyclerView: RecyclerView? = null
    private var lastSelected = -1
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        // create a new view
        val rootView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_item, parent, false) as ConstraintLayout
        val holder = ViewHolder(rootView, this)
        holder.imageView = rootView.findViewById(R.id.item_info)
        holder.name = rootView.findViewById(R.id.name)
        holder.size = rootView.findViewById(R.id.type)
        holder.mountButton = rootView.findViewById(R.id.mountButton)
        holder.mountButton?.setOnClickListener { mCallback.onMountImageButtonClicked() }
        holder.deleteButton = rootView.findViewById(R.id.deleteButton)
        holder.deleteButton?.setOnClickListener { mCallback.onDeleteImageButtonClicked() }
        holder.progressBar = rootView.findViewById(R.id.progressBar)
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            // Perform a full update
            onBindViewHolder(holder, holder.adapterPosition)
        } else {
            // Perform a partial update
            for (payload in payloads) {
                val convertView: View = holder.rootView
                when (payload) {
                    "aufklappen" -> {

                        // boolean visible = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).isViewPartiallyVisible(convertView, false, true);


                        // int colorFrom = Color.argb(0, 250, 250, 250);
                        // int colorTo = Color.argb(255, 220, 220, 220);
                        val colorAnimation = ValueAnimator.ofInt(0, 255).setDuration(1000)
                        colorAnimation.addUpdateListener { convertView.setBackgroundColor(Color.argb((colorAnimation.animatedValue as Int), 235, 235, 235)) }
                        colorAnimation.start()

                        // convertView.animate().rotationX(20f).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500);
                        val anim0 = ValueAnimator.ofFloat(0f, 15f)
                        anim0.addUpdateListener { valueAnimator: ValueAnimator ->
                            val `val` = valueAnimator.animatedValue as Float
                            convertView.rotationX = `val`
                        }
                        anim0.duration = 500
                        anim0.start()
                        val anim = ValueAnimator.ofInt(px2dp(70, mContext), px2dp(70 + 70, mContext))
                        anim.addUpdateListener { valueAnimator: ValueAnimator ->
                            val `val` = valueAnimator.animatedValue as Int
                            val layoutParams = convertView.layoutParams
                            layoutParams.height = `val`
                            convertView.layoutParams = layoutParams
                        }
                        anim.duration = 300
                        anim.start()
                        convertView.postDelayed({
                            if (dataSet[holder.adapterPosition].isDownloading) {
                                holder.mountButton!!.visibility = View.INVISIBLE
                            } else {
                                holder.mountButton!!.visibility = View.VISIBLE
                            }
                            holder.deleteButton!!.visibility = View.VISIBLE
                            mRecyclerView!!.smoothScrollToPosition(holder.adapterPosition)
                        }, 230)


                        // holder.deleteButton.setVisibility(View.VISIBLE);
                    }
                    "zuklappen" -> {
                        val anim0 = ValueAnimator.ofFloat(convertView.rotationX, 0f)
                        anim0.addUpdateListener { valueAnimator: ValueAnimator ->
                            val `val` = valueAnimator.animatedValue as Float
                            convertView.rotationX = `val`
                        }
                        anim0.duration = 500
                        anim0.start()

                        //  convertView.animate().rotationX(0).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(500).withEndAction(() -> {

                        //    });;
                        val colorAnimation = ValueAnimator.ofInt(convertView.background.alpha, 0).setDuration(1000)
                        colorAnimation.addUpdateListener { convertView.setBackgroundColor(Color.argb((colorAnimation.animatedValue as Int), 235, 235, 235)) }
                        colorAnimation.start()
                        val anim = ValueAnimator.ofInt(convertView.layoutParams.height, px2dp(70, mContext))
                        anim.addUpdateListener { valueAnimator: ValueAnimator ->
                            val `val` = valueAnimator.animatedValue as Int
                            val layoutParams = convertView.layoutParams
                            layoutParams.height = `val`
                            convertView.layoutParams = layoutParams
                        }
                        anim.duration = 300
                        anim.start()
                        convertView.postDelayed({
                            holder.mountButton!!.visibility = View.INVISIBLE
                            holder.deleteButton!!.visibility = View.INVISIBLE
                        }, 130)
                    }
                    "mount" -> {
                        holder.imageView!!.setColorFilter(mContext.resources.getColor(R.color.green))
                        holder.mountButton!!.isChecked = true
                    }
                    "unmount" -> {
                        holder.imageView?.colorFilter = null
                        holder.mountButton!!.isChecked = false
                    }
                    "download" -> {
                        val imageItem = dataSet[holder.adapterPosition]
                        if (imageItem.isDownloading) {
                            holder.progressBar!!.visibility = View.VISIBLE
                            holder.progressBar?.progress = imageItem.progress
                        } else {
                            holder.progressBar!!.visibility = View.INVISIBLE
                        }
                        holder.size!!.text = imageItem.size
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageItem = dataSet[position]
        holder.name!!.text = imageItem.name
        holder.size!!.text = imageItem.size
        val convertView: View = holder.rootView
        val layoutParams = convertView.layoutParams
        if (imageItem.selected) {
            convertView.setBackgroundColor(Color.argb(255, 220, 220, 220))
            convertView.rotationX = 20f
            layoutParams.height = px2dp(70 + 30, mContext)
            if (!imageItem.isDownloading) {
                holder.mountButton!!.visibility = View.INVISIBLE
            } else {
                holder.mountButton!!.visibility = View.VISIBLE
            }
            holder.deleteButton!!.visibility = View.VISIBLE
        } else {
            convertView.setBackgroundColor(Color.argb(0, 220, 220, 220))
            convertView.rotationX = 0f
            layoutParams.height = px2dp(70, mContext)
            holder.mountButton!!.visibility = View.INVISIBLE
            holder.deleteButton!!.visibility = View.INVISIBLE
        }
        convertView.layoutParams = layoutParams
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
        if (imageItem.isDownloading) {
            holder.progressBar!!.visibility = View.VISIBLE
            holder.progressBar?.progress = imageItem.progress
        } else {
            holder.progressBar!!.visibility = View.INVISIBLE
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size()
    }

    fun remove(checkedItem: ImageItem) {
        //animationMap.remove(dataSet.indexOf(checkedItem));
        //   setSelectedItemPosition(-1);
        dataSet.remove(checkedItem)
    }

    override fun onClick(v: View) {
        val itemPosition = mRecyclerView!!.getChildLayoutPosition(v)

        //setSelectedItemPosition(itemPosition);
        val item = dataSet[itemPosition]
        mCallback.onImageSelected(item)
    }

    fun addItems(list: List<ImageItem>) {

        // dataSet.beginBatchedUpdates();
        //   dataSet.clear();

        //    dataSet.beginBatchedUpdates();
        for (j in 0 until dataSet.size()) {
            if (!list.contains(dataSet[j])) {
                dataSet.remove(dataSet[j])
            }
        }
        //     if(dataSet.size() == 0){
        for (i in list.indices) {
            //  addItem(list.get(i), false);
            if (!contains(list[i])) {
                dataSet.add(list[i])
            }
        }
        //        }


        // dataSet.endBatchedUpdates();
    }

    fun addItem(imageItem: ImageItem): Int {
        return dataSet.add(imageItem)
    }

    private operator fun contains(item: ImageItem): Boolean {
        return dataSet.indexOf(item) != -1
    }

    fun getPositionOfItem(imageItem: ImageItem): Int {
        return dataSet.indexOf(imageItem)
    }

    fun setSelectedItem(selectedItem: ImageItem?) {
        if (selectedItem == null) {
            setSelectedItemPosition(-1)
        } else {
            val position = dataSet.indexOf(selectedItem)
            setSelectedItemPosition(position)
        }
    }

    private fun setSelectedItemPosition(position: Int) {
        //notifyDataSetChanged();
        if (position == -1) {
            if (lastSelected == -1) {
                return
            }
            dataSet[lastSelected].selected = false
            notifyItemChanged(lastSelected, "zuklappen")
            lastSelected = -1
            return
        }
        if (lastSelected == position) {
            dataSet[position].selected = false
            notifyItemChanged(position, "zuklappen")
            lastSelected = -1
            return
        } else if (lastSelected != -1) {
            dataSet[lastSelected].selected = false
            notifyItemChanged(lastSelected, "zuklappen")
        }
        dataSet[position].selected = true
        notifyItemChanged(position, "aufklappen")
        lastSelected = position
    }

    interface OnImageListListener {
        fun onMountImageButtonClicked()
        fun onDeleteImageButtonClicked()
        fun onImageSelected(item: ImageItem?)
    }

    class ViewHolder(rootView: View, listener: View.OnClickListener?) : RecyclerView.ViewHolder(rootView) {
        val rootView: ConstraintLayout = rootView as ConstraintLayout
        var imageView: ImageView? = null
        var name: TextView? = null
        var size: TextView? = null
        var mountButton: ToggleButton? = null
        var deleteButton: Button? = null
        var progressBar: ProgressBar? = null

        init {
            this.rootView.setOnClickListener(listener)
        }
    }

    init {
        dataSet = SortedList(ImageItem::class.java, object : SortedList.Callback<ImageItem>() {
            override fun compare(o1: ImageItem, o2: ImageItem): Int {
                return o1.compareTo(o2)
            }

            override fun onChanged(position: Int, count: Int) {
                notifyItemRangeChanged(position, count)
            }

            override fun areContentsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(item1: ImageItem, item2: ImageItem): Boolean {
                return item1 == item2
            }

            override fun onInserted(position: Int, count: Int) {
                notifyItemRangeInserted(position, count)
            }

            override fun onRemoved(position: Int, count: Int) {
                notifyItemRangeRemoved(position, count)
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                notifyItemMoved(fromPosition, toPosition)
            }
        })
        if (data != null) {
            dataSet.beginBatchedUpdates()
            for (i in data.indices) {
                dataSet.add(data[i])
            }
            dataSet.endBatchedUpdates()
        }
        mContext = context
        mCallback = listener
    }
}
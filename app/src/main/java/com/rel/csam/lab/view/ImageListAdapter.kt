package com.rel.csam.lab.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.bumptech.glide.Glide
import com.rel.csam.lab.R
import com.rel.csam.lab.model.LinkImage

import java.util.ArrayList

/**
 * Created by leechansaem on 2016. 11. 2..
 */
class ImageListAdapter(private val mContext: MainActivity, private var mImageList: ArrayList<LinkImage>) : RecyclerView.Adapter<ImageListAdapter.ViewHolder>() {

    fun setImageList(imageList: ArrayList<LinkImage>) {
        this.mImageList = imageList
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.row, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val data = mImageList[i]
        //        viewHolder.title.setText(mImageList.get(i));
        viewHolder.img.setOnClickListener {
            if (data.url != null) {
                mContext.getImageToLink(data.url!!, data.image!!)
            }
        }
        Glide.with(mContext).load(data.image).thumbnail(0.8f).into(viewHolder.img)
    }

    override fun getItemCount(): Int {
        return mImageList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val title: TextView? = null
        val img: CustomImageView = view.findViewById<View>(R.id.img) as CustomImageView

        init {
            //            title = (TextView)view.findViewById(R.id.title);
        }
    }
}

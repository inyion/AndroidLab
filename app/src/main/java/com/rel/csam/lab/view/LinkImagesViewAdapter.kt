package com.rel.csam.lab.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.BindingAdapter
import android.databinding.DataBindingComponent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.rel.csam.lab.R
import com.rel.csam.lab.model.LinkImage
import com.rel.csam.lab.viewmodel.LinkImageModel
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by leechansaem on 2016. 11. 2..
 */
class LinkImagesViewAdapter(private val viewModel: LinkImageModel) : RecyclerView.Adapter<LinkImagesViewAdapter.ViewHolder>(), DataBindingComponent {

    private var mImageList: ArrayList<LinkImage> = ArrayList()

    override fun getLinkImagesViewAdapter(): LinkImagesViewAdapter {
        return this
    }

    fun setImageList(imageList: ArrayList<LinkImage>) {
        this.mImageList = imageList
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.row, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val data = mImageList[i]

        viewHolder.imageView.setOnClickListener {
            if (data.url != null) {
                viewModel.getImageToLink(data.url!!, data.image!!)
            }
        }
        Glide.with(viewHolder.imageView).load(data.image).thumbnail(0.8f).into(viewHolder.imageView)
    }

    override fun getItemCount(): Int {
        return mImageList.size
    }

    @BindingAdapter("setItems")
    fun setItems(view: RecyclerView, items: ArrayList<LinkImage>) {
        if (view.adapter == null) {
            view.adapter = LinkImagesViewAdapter(viewModel)
        }

        if (view.adapter is LinkImagesViewAdapter) {
            (view.adapter as LinkImagesViewAdapter).setImageList(items)
        }
        view.adapter!!.notifyDataSetChanged()
    }

    @BindingAdapter("setMainImage")
    fun setMainImage(view: ImageView, imgUrl: String?) {
        if (!TextUtils.isEmpty(imgUrl)) {
            view.visibility = View.VISIBLE
            Glide.with(view.context).load(imgUrl).into(view)
        } else {
            view.visibility = View.GONE
            (view.context as Activity).refresh_layout.isRefreshing = false
        }
    }

    @BindingAdapter("goZoomInImage")
    fun goZoomInImage(view: RecyclerView, imageUrl: String?) {
        if (!TextUtils.isEmpty(imageUrl)) {
            val intent = Intent(view.context, ZoomInImageActivity::class.java)
            intent.putExtra("image", imageUrl)
            ContextCompat.startActivity(view.context, intent, null)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: SquareImageView = view.findViewById<View>(R.id.img) as SquareImageView
    }
}

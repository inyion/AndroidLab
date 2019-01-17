package com.rel.csam.lab.view

import android.content.Intent
import android.databinding.BindingAdapter
import android.databinding.DataBindingComponent
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.android.databinding.library.baseAdapters.BR
import com.bumptech.glide.Glide
import com.rel.csam.lab.R
import com.rel.csam.lab.model.LinkImage
import com.rel.csam.lab.viewmodel.LinkImageModel


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

        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, R.layout.row, viewGroup, false, this)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(viewModel, position)
        val data = getItem(position)
        Glide.with(viewHolder.itemView.context).load(data.image).thumbnail(0.8f).into(viewHolder.imageView)
    }

    fun getItem(position: Int): LinkImage {
        return mImageList[position]
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

    @BindingAdapter("setLoadingImage")
    fun setLoadingImage(view: ImageView, imgUrl: String?) {
        if (!TextUtils.isEmpty(imgUrl)) {
            view.visibility = View.VISIBLE
            Glide.with(view.context).load(imgUrl).into(view)
        } else {
            view.visibility = View.GONE
        }
    }

    @BindingAdapter("setLoadingImage")
    fun setLoadingImage(view: SwipeRefreshLayout, imgUrl: String?) {
        if (TextUtils.isEmpty(imgUrl)) {
            view.isRefreshing = false
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

    inner class ViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        val imageView: SquareImageView = itemView.findViewById<View>(R.id.img) as SquareImageView

        fun bind(viewModel: LinkImageModel, position: Int) {
            binding.setVariable(BR.viewModel, viewModel)
            binding.setVariable(BR.position, position)
            binding.executePendingBindings()
        }
    }
}

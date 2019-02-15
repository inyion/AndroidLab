package com.rel.csam.lab.view

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
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

    private var mImageList: LiveData<ArrayList<LinkImage>> = MutableLiveData()

    override fun getLinkImagesViewAdapter(): LinkImagesViewAdapter {
        return this
    }

    fun setImageList(imageList: LiveData<ArrayList<LinkImage>>) {
        this.mImageList = imageList
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {

        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, R.layout.row, viewGroup, false, this)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(viewModel, position)
    }

    fun getItem(position: Int): LinkImage {
        return mImageList.value!![position]
    }

    override fun getItemCount(): Int {
        return if (mImageList.value != null) {
            mImageList.value!!.size
        } else {
            0
        }
    }

    @BindingAdapter("setItems")
    fun setItems(view: RecyclerView, items: LiveData<ArrayList<LinkImage>>) {
        if (view.adapter == null) {
            view.adapter = LinkImagesViewAdapter(viewModel)
        }

        if (view.adapter is LinkImagesViewAdapter) {
            (view.adapter as LinkImagesViewAdapter).setImageList(items)
        }
        view.adapter!!.notifyDataSetChanged()
    }

    @BindingAdapter("setLoadingImage")
    fun setLoadingImage(view: ImageView, imgUrl: LiveData<String>) {
        if (!TextUtils.isEmpty(imgUrl.value)) {
            view.visibility = View.VISIBLE
            Glide.with(view.context).load(imgUrl.value).into(view)
        } else {
            view.visibility = View.GONE
        }
    }

    @BindingAdapter("setLoadingImage")
    fun setLoadingImage(view: SwipeRefreshLayout, imgUrl: LiveData<String>) {
        if (TextUtils.isEmpty(imgUrl.value)) {
            view.isRefreshing = false
        }
    }

    @BindingAdapter("goZoomInImage")
    fun goZoomInImage(view: RecyclerView, imageUrl: LiveData<String>) {
        if (!TextUtils.isEmpty(imageUrl.value)) {
            val intent = Intent(view.context, ZoomInImageActivity::class.java)
            intent.putExtra("image", imageUrl.value)
            ContextCompat.startActivity(view.context, intent, null)
        }
    }

    @BindingAdapter("imageSrc")
    fun imageSrc(view: SquareImageView, image: String) {
        Glide.with(view).load(image).thumbnail(0.8f).into(view)
    }

    inner class ViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(viewModel: LinkImageModel, position: Int) {
            binding.setVariable(BR.viewModel, viewModel)
            binding.setVariable(BR.position, position)
            binding.setVariable(BR.image, getItem(position).image)
            binding.executePendingBindings()
        }
    }
}

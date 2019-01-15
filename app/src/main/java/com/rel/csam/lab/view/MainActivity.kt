package com.rel.csam.lab.view

import android.content.Intent
import android.databinding.BindingAdapter
import android.databinding.DataBindingComponent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.rel.csam.lab.R
import com.rel.csam.lab.databinding.ActivityMainBinding
import com.rel.csam.lab.model.LinkImage
import com.rel.csam.lab.viewmodel.LinkImageModel
import kotlinx.android.synthetic.main.activity_main.*

/**
 * creator : sam
 * date : 2019. 1. 12.
 */
class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener, DataBindingComponent {
    override fun getMainActivity(): MainActivity {
        return this
    }

    var viewModel: LinkImageModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main, this)
        viewModel = LinkImageModel()
        binding.linkImageModel = viewModel
        binding.recyclerView.layoutManager = GridLayoutManager(applicationContext, 3)
        binding.executePendingBindings()

        refresh_layout.setOnRefreshListener(this)
        recycler_view.setHasFixedSize(true)
        //        DisplayMetrics displayMetrics = new DisplayMetrics();
        //        WindowManager windowmanager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        //        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        //        mScreenSize = displayMetrics.widthPixels;
        main_image.visibility = View.VISIBLE
        Glide.with(this).load(R.drawable.intro).thumbnail(0.8f).into(main_image)
        onRefresh()
    }

    override fun onDestroy() {
        if (viewModel != null) viewModel!!.onCleared()
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (!viewModel!!.backHistory()) {
            super.onBackPressed()
        }
    }

    override fun onRefresh() {
        viewModel!!.init()
    }

    @BindingAdapter("setItems")
    fun setItems(view: RecyclerView, items: ArrayList<LinkImage>) {
        if (view.adapter == null) {
            view.adapter = ImageListAdapter(view.context, items)
        } else {
            if (view.adapter is ImageListAdapter) {
                (view.adapter as ImageListAdapter).setImageList(items)
            }
            view.adapter!!.notifyDataSetChanged()
        }
    }

    @BindingAdapter("setMainImage")
    fun setMainImage(view: ImageView, imgUrl: String?) {
        if (!TextUtils.isEmpty(imgUrl)) {
            view.visibility = View.VISIBLE
            Glide.with(this).load(imgUrl).into(view)
        } else {
            view.visibility = View.GONE
            refresh_layout.isRefreshing = false
        }
    }

    @BindingAdapter("goZoomInImage")
    fun goZoomInImage(view: RecyclerView, imageUrl: String?) {
        if (!TextUtils.isEmpty(imageUrl)) {
            val intent = Intent(view.context, FullImageActivity::class.java)
            intent.putExtra("image", imageUrl)
            startActivity(view.context, intent, null)
        }
    }
}

package com.rel.csam.lab.view

import android.databinding.DataBindingComponent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rel.csam.lab.R
import com.rel.csam.lab.databinding.ActivityMainBinding
import com.rel.csam.lab.viewmodel.LinkImageModel
import kotlinx.android.synthetic.main.activity_main.*

/**
 * creator : sam
 * date : 2019. 1. 12.
 */
class MainActivity : ViewModelActivity(), SwipeRefreshLayout.OnRefreshListener {

    override fun onCreate() {
        viewModel = LinkImageModel()
        val adapter = LinkImagesViewAdapter(viewModel as LinkImageModel)
        var binding = super.setContentView<ActivityMainBinding>(R.layout.activity_main, adapter, viewModel as LinkImageModel)
        if (binding != null) {
            binding.linkImageModel = viewModel as LinkImageModel?
            binding.recyclerView.layoutManager = GridLayoutManager(applicationContext, 3)
        }

        refresh_layout.setOnRefreshListener(this)
        recycler_view.setHasFixedSize(true)
        Glide.with(this).load(R.drawable.intro).thumbnail(0.8f).into(main_image)
        onRefresh()
    }

    override fun onRefresh() {
        initViewModel()
    }
}

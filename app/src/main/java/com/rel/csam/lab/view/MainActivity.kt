package com.rel.csam.lab.view

import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.rel.csam.lab.R
import com.rel.csam.lab.databinding.ActivityMainBinding
import com.rel.csam.lab.viewmodel.LinkImageModel

/**
 * creator : sam
 * date : 2019. 1. 12.
 */
class MainActivity : ViewModelActivity(), SwipeRefreshLayout.OnRefreshListener {

    override fun createViewModel() {
        createViewModel(LinkImageModel::class.java)
    }

    override fun createDataBindingComponent() {
        createDataBindingComponent(LinkImagesViewAdapter(viewModel as LinkImageModel))
    }

    override fun onCreate() {
        val binding= setContentLayout<ActivityMainBinding>(R.layout.activity_main)
        binding.viewModel = viewModel as LinkImageModel?
        binding.recyclerView.layoutManager = GridLayoutManager(applicationContext, 3)
        binding.recyclerView.setHasFixedSize(true)
        binding.refreshLayout.setOnRefreshListener(this)
        Glide.with(this).load(R.drawable.intro).thumbnail(0.8f).into(binding.mainImage)

        onRefresh()
    }

    override fun onRefresh() {
        initViewModel()
    }
}

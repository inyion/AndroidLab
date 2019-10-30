package com.rel.csam.lab.view

import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.rel.csam.lab.R
import com.rel.csam.lab.databinding.WebImageListBinding
import com.rel.csam.lab.viewmodel.LinkImageModel

/**
 * creator : sam
 * date : 2019. 1. 12.
 */
class WebImageListActivity : ViewModelActivity<LinkImageModel>(), SwipeRefreshLayout.OnRefreshListener {

    override fun createViewModel() {
        createViewModel(LinkImageModel::class.java)
    }

    override fun createDataBindingComponent() {
        createDataBindingComponent(LinkImagesViewAdapter(viewModel))
    }

    override fun onCreate() {
        val binding= setContentLayout<WebImageListBinding>(R.layout.web_image_list)
        viewModel.keyword = intent.getStringExtra("keyword")
        binding.viewModel = viewModel
        binding.recyclerView.layoutManager = GridLayoutManager(applicationContext, 3)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = bindingComponent as LinkImagesViewAdapter
        binding.refreshLayout.setOnRefreshListener(this)
        Glide.with(this).load(R.drawable.intro).thumbnail(0.8f).into(binding.mainImage)

        onRefresh()
    }

    override fun onRefresh() {
        initViewModel()
    }
}

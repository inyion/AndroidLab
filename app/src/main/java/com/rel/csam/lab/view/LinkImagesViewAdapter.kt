package com.rel.csam.lab.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.rel.csam.lab.BR
import com.rel.csam.lab.R
import com.rel.csam.lab.model.LinkImage
import com.rel.csam.lab.viewmodel.BaseBindAdapter
import com.rel.csam.lab.viewmodel.LinkImageModel
import com.rel.csam.lab.viewmodel.ListModel


/**
 * Created by leechansaem on 2016. 11. 2..
 */
class LinkImagesViewAdapter(private val viewModel: LinkImageModel) : BaseBindAdapter<LinkImagesViewAdapter.ViewHolder, LinkImage>(), DataBindingComponent {
    override fun getListModel(): ListModel<LinkImage> {
        return viewModel
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {

        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, R.layout.row, viewGroup, false, this)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(viewModel, position)
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

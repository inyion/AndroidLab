package com.rel.csam.lab.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.rel.csam.lab.BR
import com.rel.csam.lab.R
import com.rel.csam.lab.database.Tag
import com.rel.csam.lab.viewmodel.BaseBindAdapter
import com.rel.csam.lab.viewmodel.ListModel
import com.rel.csam.lab.viewmodel.TagModel
import kotlinx.android.synthetic.main.tag_row.view.*

class TagViewAdapter(private val viewModel: TagModel) : BaseBindAdapter<TagViewAdapter.ViewHolder, Tag>(), DataBindingComponent {
    override fun getListModel(): ListModel<Tag> {
        return viewModel
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {

        val layoutInflater = LayoutInflater.from(viewGroup.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, R.layout.tag_row, viewGroup, false, this)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(viewModel, position)
    }

    inner class ViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

        val chip = itemView.chip

        fun bind(viewModel: TagModel, position: Int) {

            chip.setOnSelectClickListener { v, selected ->
                if (selected) {

                }
            }

            chip.chipText = viewModel.getTagName(position)

            binding.setVariable(BR.viewModel, viewModel)
            binding.setVariable(BR.position, position)
            binding.executePendingBindings()
        }
    }
}

package com.rel.csam.lab.view

import android.databinding.DataBindingComponent
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.rel.csam.lab.viewmodel.BaseViewModel

abstract class ViewModelActivity : AppCompatActivity() {

    var viewModel: BaseViewModel? = null
    var binding: ViewDataBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreate()
        binding!!.executePendingBindings()
    }

    abstract fun onCreate()

    override fun onBackPressed() {
        if (viewModel!!.onBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        if (viewModel != null) viewModel!!.onDispose()
        super.onDestroy()
    }

    override fun onStop() {
        if (viewModel != null) viewModel!!.onStop()
        super.onStop()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: ViewDataBinding> setContentView(layoutId: Int, bindingComponent: DataBindingComponent, viewModel: BaseViewModel):T ? {
        binding = DataBindingUtil.setContentView(this, layoutId, bindingComponent)
        this.viewModel = viewModel
        return binding as T?
    }

    fun initViewModel() {
        viewModel!!.init()
    }

}

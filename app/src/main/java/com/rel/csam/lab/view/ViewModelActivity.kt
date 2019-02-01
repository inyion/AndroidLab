package com.rel.csam.lab.view

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingComponent
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.rel.csam.lab.viewmodel.BaseViewModel

abstract class ViewModelActivity : AppCompatActivity() {

    lateinit var viewModel: BaseViewModel
    private lateinit var binding: ViewDataBinding
    private lateinit var bindingComponent: DataBindingComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createViewModel()
        createDataBindingComponent()
        onCreate()
        binding.executePendingBindings()
    }

    abstract fun createViewModel()

    abstract fun createDataBindingComponent()

    abstract fun onCreate()

    override fun onBackPressed() {
        if (viewModel.onBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        viewModel.onDispose()
        super.onDestroy()
    }

    override fun onStop() {
        viewModel.onStop()
        super.onStop()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: ViewDataBinding> setContentLayout(layoutId: Int):T  {
        binding = DataBindingUtil.setContentView(this, layoutId, bindingComponent)
        binding.setLifecycleOwner(this)
        return binding as T
    }

    fun <T : ViewModel> createViewModel(modelClass: Class<T>) {
        val vm = ViewModelProviders.of(this).get(modelClass)
        this.viewModel = vm as BaseViewModel
    }

    fun createDataBindingComponent(component: DataBindingComponent) {
        this.bindingComponent = component
    }

    fun initViewModel() {
        viewModel.init()
    }

}

package com.rel.csam.lab.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rel.csam.lab.viewmodel.BaseViewModel

abstract class ViewModelActivity<VM: BaseViewModel> : AppCompatActivity() {

    lateinit var viewModel: VM
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
        binding.lifecycleOwner = this
        return binding as T
    }

    fun <T : ViewModel> createViewModel(modelClass: Class<T>) {
        val viewModel = ViewModelProviders.of(this).get(modelClass)
        this.viewModel = viewModel as VM
    }

    fun createDataBindingComponent(component: DataBindingComponent) {
        this.bindingComponent = component
    }

    fun initViewModel() {
        viewModel.init()
    }

}

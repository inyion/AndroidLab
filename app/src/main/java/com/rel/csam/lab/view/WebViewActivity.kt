package com.rel.csam.lab.view

import android.text.TextUtils
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.rel.csam.lab.R
import com.rel.csam.lab.databinding.WebViewBinding
import com.rel.csam.lab.viewmodel.CommonBindingComponent
import com.rel.csam.lab.viewmodel.TempViewModel


class WebViewActivity: ViewModelActivity<TempViewModel>(){
    override fun createViewModel() {
        createViewModel(TempViewModel::class.java)
    }

    override fun createDataBindingComponent() {
        createDataBindingComponent(CommonBindingComponent())
    }

    lateinit var binding: WebViewBinding
    override fun onCreate() {
        binding = setContentLayout(R.layout.web_view)

        var url = intent.getStringExtra("url")
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webChromeClient = WebChromeClient()                 //웹뷰에 크롬 사용 허용//이 부분이 없으면 크롬에서 alert가 뜨지 않음
        binding.webView.webViewClient = WebViewClientClass()
        binding.webView.loadUrl(url)
    }

    private inner class WebViewClientClass : WebViewClient() {
        //페이지 이동
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            Log.d("check URL", url)
            return true
        }
    }

}

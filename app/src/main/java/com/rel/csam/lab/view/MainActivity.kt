package com.rel.csam.lab.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.databinding.BindingAdapter
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.rel.csam.lab.R
import com.rel.csam.lab.R.id.*
import com.rel.csam.lab.databinding.LinkImageGridBinding
import com.rel.csam.lab.model.LinkImage
import com.rel.csam.lab.viewmodel.LinkImageModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.link_image_grid.*
import org.jsoup.Connection
import org.jsoup.Jsoup

/**
 * creator : sam
 * date : 2019. 1. 12.
 */
class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
//    private val TAG: String = "Main"
    private var mZoomImage: String? = null
    private var mAdapter: ImageListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<LinkImageGridBinding>(this, R.layout.activity_main)
        binding.linkImageModel = LinkImageModel()
        binding.executePendingBindings()


        refresh_layout.setOnRefreshListener(this)
        //        DisplayMetrics displayMetrics = new DisplayMetrics();
        //        WindowManager windowmanager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        //        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        //        mScreenSize = displayMetrics.widthPixels;

        recycler_view.setHasFixedSize(true)
        val layoutManager = GridLayoutManager(applicationContext, 3)
        recycler_view.layoutManager = layoutManager
        mAdapter = ImageListAdapter(this@MainActivity, mDataList)
        recycler_view.adapter = mAdapter
        mUrlList.add(mSite)
        onRefresh()
    }

    override fun onBackPressed() {
        if (mUrlList.size > 1) { // 메인은 제외
            mUrlList.removeAt(mUrlList.lastIndex)
            val url = mUrlList[mUrlList.lastIndex]
            getImageToLink(url, getMainImage(url))
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    override fun onRefresh() {
        getImageToLink(mSite, getMainImage(mSite))
    }

    fun putMainImage(url: String, image: String) {
        val edit = sharedPreferences!!.edit()
        edit.putString(url, image)
        edit.commit()
    }

    fun getMainImage(url: String): String {
        return sharedPreferences!!.getString(url, "")
    }

    @BindingAdapter("setItems")
    fun setItems(view: RecyclerView, items: ArrayList<LinkImage>) {

    }

    @BindingAdapter("setMainImage")
    fun setMainImage(view: ImageView, imgUrl: String) {

    }
}

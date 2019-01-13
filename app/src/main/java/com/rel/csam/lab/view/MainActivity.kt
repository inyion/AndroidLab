package com.rel.csam.lab.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.rel.csam.lab.R
import com.rel.csam.lab.model.LinkImage
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.jsoup.Connection
import org.jsoup.Jsoup

/**
 * creator : sam
 * date : 2019. 1. 12.
 */
class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    private val TAG: String = "Main"
    private val mSite: String = "https://www.gettyimagesgallery.com/collection/celebrities/"

    private val compositeDisposable = CompositeDisposable()

    private var sharedPreferences: SharedPreferences? = null

    private var mDataList: ArrayList<LinkImage> = ArrayList()
    private var mUrlList: ArrayList<String> = ArrayList()
    private var mZoomImage: String? = null

    private var mAdapter: ImageListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("image", Context.MODE_PRIVATE)
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

    fun getImageToLink(url: String, thumbnailsUrl: String) {
        Log.i(TAG, "getImageToLink")
        // 데이터 초기화
        mZoomImage = null

        // 히스토리 등록
        if (!url.equals(mSite) && !url.equals(mUrlList[mUrlList.lastIndex])) {
            mUrlList.add(url)
        }

        // 페이지 로딩중 보여줄 저장해둔 메인이미지를 찾아보고
        var mainImage = getMainImage(url)
        // 없으면 썸네일로 대체
        if (TextUtils.isEmpty(mainImage)) mainImage = thumbnailsUrl

        if (!TextUtils.isEmpty(mainImage)) {
            main_image.visibility = View.VISIBLE
            Glide.with(this).load(mainImage).into(main_image)
        } else {
            if (url.equals(mSite)) {
                Glide.with(this).load(R.drawable.intro).thumbnail(0.8f).into(main_image)
            } else {
                main_image.visibility = View.GONE
            }
        }

        val disposable = Observable.fromCallable {
            Log.i(TAG, "fromCallable")
            // 로그인 이후 이용가능한 페이지는
            // 로그인 페이지 띄우고 CookieManager 에서 쿠키를 가져와서 Jsoup header에 넣으면 가능하다고함
            // 응답이 느려서 라이브러리 문제라고 생각했는데 라이브러리 안쓰고 해도 페이지 자체가 연결이 느림
            val response = Jsoup.connect(url)
                    .method(Connection.Method.GET)
                    .execute()
            Log.i(TAG, "execute")
            val document = response.parse()
            Log.i(TAG, "parse")
//            val imgRegex = "(?i)<img[^>]+?src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>"
//            val p = Pattern.compile(imgRegex)
//            val imgRegex = "img[src~=(?i)\\\\.(png|jpe?g|gif)]"
            val images = document.select("img")
            Log.i(TAG, "select img")
            var replaceImages: ArrayList<LinkImage> = ArrayList()
            for (image in images) {
                if (image.attr("class").equals("img-fluid")) {
                    putMainImage(url, image.attr("src"))
                } else {
                    if (image.parentNode() != null) {
                        val parentNode = image.parentNode().parentNode()
                        if (image.hasAttr("data-zoomable")) {
                            mZoomImage = image.attr("src")
                        }
                        else if (parentNode != null && parentNode.nodeName() == "a") {
                            val data = LinkImage()
                            data.url = parentNode.attr("href")
                            data.image = image.attr("src")
                            replaceImages.add(data)
                        }
                    }
                }
            }

            if (replaceImages.size > 0) {
                mDataList = replaceImages
            }
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            Log.i(TAG, "subscribe")
            main_image.visibility = View.GONE
            if (!TextUtils.isEmpty(mZoomImage)) {
                mUrlList.removeAt(mUrlList.lastIndex)
                val intent = Intent(this, FullImageActivity::class.java)
                intent.putExtra("image", mZoomImage!!)
                startActivity(intent)
            }
            else if (mDataList.size > 0) {
                mAdapter!!.setImageList(mDataList)
                mAdapter!!.notifyDataSetChanged()
            }

            refresh_layout.isRefreshing = false
        }
        compositeDisposable.add(disposable)
    }

    fun putMainImage(url: String, image: String) {
        val edit = sharedPreferences!!.edit()
        edit.putString(url, image)
        edit.commit()
    }

    fun getMainImage(url: String): String {
        return sharedPreferences!!.getString(url, "")
    }
}

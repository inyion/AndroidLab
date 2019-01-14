package com.rel.csam.lab.viewmodel

import android.content.Intent
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableArrayList
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.rel.csam.lab.R
import com.rel.csam.lab.model.LinkImage
import com.rel.csam.lab.view.FullImageActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jsoup.Connection
import org.jsoup.Jsoup

class LinkImageModel: DisposableModel() {
    private val TAG: String = "LinkImage"
    private val mainWeb: String = "https://www.gettyimagesgallery.com/collection/celebrities/"
    var urlList = ArrayList<String>()               // 히스토리

    @Bindable
    var items = ObservableArrayList<LinkImage>()    // 이미지리스트
    @Bindable
    var mainImage: String? = null                   // 메인이미지
    var zoomImage: String? = null                  // 이미지상세

    init {

    }

    fun getImageToLink(url: String, thumbnailsUrl: String) {
        Log.i(TAG, "getImageToLink")
        // 데이터 초기화
        zoomImage = null

        // 히스토리 등록
        if (!url.equals(mainWeb) && !url.equals(urlList[urlList.lastIndex])) {
            urlList.add(url)
        }

        // 페이지 로딩중 보여줄 저장해둔 메인이미지를 찾아보고
        var mainImage = getMainImage(url)
        // 없으면 썸네일로 대체
        if (TextUtils.isEmpty(mainImage)) mainImage = thumbnailsUrl

        if (!TextUtils.isEmpty(mainImage)) {
            main_image.visibility = View.VISIBLE
            Glide.with(this).load(mainImage).into(main_image)
        } else {
            if (url.equals(mainWeb)) {
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
                            zoomImage = image.attr("src")
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
                items = replaceImages
            }
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            Log.i(TAG, "subscribe")
            main_image.visibility = View.GONE
            if (!TextUtils.isEmpty(zoomImage)) {
                urlList.removeAt(urlList.lastIndex)
                val intent = Intent(this, FullImageActivity::class.java)
                intent.putExtra("image", zoomImage!!)
                startActivity(intent)
            }
            else if (items.size > 0) {
                mAdapter!!.setImageList(items)
                mAdapter!!.notifyDataSetChanged()
            }

            refresh_layout.isRefreshing = false
        }
        add(disposable)
    }
}
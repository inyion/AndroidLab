package com.rel.csam.lab.viewmodel

import android.databinding.Bindable
import android.databinding.ObservableArrayList
import android.text.TextUtils
import android.util.Log
import com.android.databinding.library.baseAdapters.BR
import com.rel.csam.lab.App
import com.rel.csam.lab.model.LinkImage
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jsoup.Connection
import org.jsoup.Jsoup

class LinkImageModel: BaseViewModel() {

    companion object {
        const val mainWeb: String = "https://www.gettyimagesgallery.com/collection/celebrities/"
    }
    private val tag: String = "LinkImage"
    private var urlList = ArrayList<String>()       // 히스토리

    @Bindable
    var items = ObservableArrayList<LinkImage>()    // 이미지리스트
    @Bindable
    var mainImage: String? = null                   // 로딩중 보여줄 대표이미지
    @Bindable
    var zoomImage: String? = null                   // 이미지상세

    override fun init() {
        urlList.clear()
        urlList.add(mainWeb)
        getImageToLink(mainWeb, getMainImage(mainWeb))
    }

    override fun onBackPressed(): Boolean {
        return if (urlList.size > 1) { // 메인은 제외
            urlList.removeAt(urlList.lastIndex)
            val url = urlList[urlList.lastIndex]
            getImageToLink(url, getMainImage(url))
            false
        } else {
            true
        }
    }

    override fun onStop() {
        this.mainImage = null
        notifyPropertyChanged(BR.mainImage)
    }

    fun getImageToLink(url: String, thumbnailsUrl: String) {
        Log.d(tag, "getImageToLink")
        // 데이터 초기화
        zoomImage = null

        // 히스토리 등록
        if (url != mainWeb && url != urlList[urlList.lastIndex]) {
            urlList.add(url)
        }

        // 페이지 로딩중 보여줄 저장해둔 메인이미지를 찾아보고
        mainImage = getMainImage(url)
        // 없으면 썸네일로 대체
        if (TextUtils.isEmpty(mainImage)) mainImage = thumbnailsUrl
        notifyPropertyChanged(BR.mainImage)

        val replaceImages: ObservableArrayList<LinkImage> = ObservableArrayList()
        val disposable = Observable.fromCallable {
            Log.d(tag, "fromCallable")

            // 로그인 이후 이용가능한 페이지는
            // 로그인 페이지 띄우고 CookieManager 에서 쿠키를 가져와서 Jsoup header에 넣으면 가능하다고함
            // 응답이 느려서 라이브러리 문제라고 생각했는데 라이브러리 안쓰고 해도 페이지 자체가 연결이 느림
            val response = Jsoup.connect(url)
                    .method(Connection.Method.GET)
                    .execute()
            val document = response.parse()
            val images = document.select("img")

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
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {
            Log.d(tag, "subscribe")

            if (!TextUtils.isEmpty(zoomImage)) {
                urlList.removeAt(urlList.lastIndex)
                notifyPropertyChanged(BR.zoomImage)
            } else {

                this.mainImage = null
                notifyPropertyChanged(BR.mainImage)

                if (replaceImages.size > 0) {
                    items = replaceImages
                }
                notifyPropertyChanged(BR.items)
            }
        }
        addDisposable(disposable)
    }

    private fun putMainImage(url: String, image: String) {
        val edit = App.prefs.edit()
        edit.putString(url, image)
        edit.apply()
    }

    private fun getMainImage(url: String): String {
        return App.prefs.getString(url, "")
    }
}
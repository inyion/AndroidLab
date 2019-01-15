package com.rel.csam.lab.viewmodel

import android.databinding.Bindable
import android.databinding.ObservableArrayList
import android.text.TextUtils
import android.util.Log
import com.rel.csam.lab.App
import com.rel.csam.lab.model.LinkImage
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jsoup.Connection
import org.jsoup.Jsoup

class LinkImageModel: DisposableModel() {
    private val tag: String = "LinkImage"
    companion object {
        const val mainWeb: String = "https://www.gettyimagesgallery.com/collection/celebrities/"
    }


    var urlList = ArrayList<String>()               // 히스토리

    @Bindable
    var items = ObservableArrayList<LinkImage>()    // 이미지리스트
    @Bindable
    var mainImage: String? = null                   // 로딩중 보여줄 대표이미지
    @Bindable
    var zoomImage: String? = null                   // 이미지상세

    init {
        urlList.add(mainWeb)
    }

    fun init() {
        getImageToLink(mainWeb, getMainImage(mainWeb))
    }

    fun getImageToLink(url: String, thumbnailsUrl: String) {
        Log.i(tag, "getImageToLink")
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
        this.mainImage = mainImage

        val replaceImages: ObservableArrayList<LinkImage> = ObservableArrayList()
        var zoomImage: String? = null
        val disposable = Observable.fromCallable {
            Log.i(tag, "fromCallable")
            // 로그인 이후 이용가능한 페이지는
            // 로그인 페이지 띄우고 CookieManager 에서 쿠키를 가져와서 Jsoup header에 넣으면 가능하다고함
            // 응답이 느려서 라이브러리 문제라고 생각했는데 라이브러리 안쓰고 해도 페이지 자체가 연결이 느림
            val response = Jsoup.connect(url)
                    .method(Connection.Method.GET)
                    .execute()
            Log.i(tag, "execute")
            val document = response.parse()
            Log.i(tag, "parse")
//            val imgRegex = "(?i)<img[^>]+?src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>"
//            val p = Pattern.compile(imgRegex)
//            val imgRegex = "img[src~=(?i)\\\\.(png|jpe?g|gif)]"
            val images = document.select("img")
            Log.i(tag, "select img")
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
            Log.i(tag, "subscribe")
            // 로딩이미지 제거
            this.mainImage = null

            // 줌인이미지가 있는지..
            if (!TextUtils.isEmpty(zoomImage)) {
                this.zoomImage = zoomImage
                urlList.removeAt(urlList.lastIndex)
            } else {
                // 없으면 그리드뷰
                if (replaceImages.size > 0) {
                    items = replaceImages
                }
            }
        }
        addDisposable(disposable)
    }

    fun backHistory(): Boolean {
        if (urlList.size > 1) { // 메인은 제외
            urlList.removeAt(urlList.lastIndex)
            val url = urlList[urlList.lastIndex]
            getImageToLink(url, getMainImage(url))
            return true
        } else {
            return false
        }
    }

    fun putMainImage(url: String, image: String) {
        val edit = App.prefs.edit()
        edit.putString(url, image)
        edit.apply()
    }

    fun getMainImage(url: String): String {
        return App.prefs.getString(url, "")
    }


}
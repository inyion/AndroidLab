package com.rel.csam.lab.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.databinding.ObservableArrayList
import android.text.TextUtils
import android.util.Log
import com.rel.csam.lab.App
import com.rel.csam.lab.model.LinkImage
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jsoup.Connection
import org.jsoup.Jsoup

class LinkImageModel: ListModel<LinkImage>() {

    companion object {
        const val mainWeb: String = "https://www.gettyimagesgallery.com/collection/celebrities/"
    }
    private val tag: String = "LinkImage"
    private var urlList = ArrayList<String>()       // 히스토리
    private var itemsMap = HashMap<String, ArrayList<LinkImage>>()
    private var zoomInImageMap = HashMap<String, String>()

    private val _loadingImage =  MutableLiveData<String>()       // 로딩중 보여줄 대표이미지
    private val _zoomImage = MutableLiveData<String>()            // 이미지상세

    val loadingImage: LiveData<String> = _loadingImage                   // 로딩중 보여줄 대표이미지
    val zoomImage: LiveData<String> = _zoomImage                   // 이미지상세val

    override fun init() {
        if (isLoading()) return

        urlList.clear()
        urlList.add(mainWeb)

        getImageToLink(mainWeb, getLoadingImage(mainWeb))
    }

    override fun onBackPressed(): Boolean {

        if (isLoading()) return false

        return if (urlList.size > 1) {

            urlList.removeAt(urlList.lastIndex)

            val url = urlList[urlList.lastIndex]
            getImageToLink(url, getLoadingImage(url))

            false

        } else {

            true

        }
    }

    override fun onStop() {
        _loadingImage.value = null
    }

    fun isLoading():Boolean {
        return loadingImage.value != null
    }

    fun getImageToLink(url: String, thumbnailsUrl: String) {

        if (isLoading()) return

        // 데이터 초기화
        var zoomImage = zoomInImageMap[url] // 상세보기 데이터
        val replaceImages = if (itemsMap.containsKey(url)) { // 리스트데이터
            itemsMap[url]!!
        } else {
            ObservableArrayList()
        }

        // 로딩이미지 처리
        if (zoomImage == null && replaceImages.size == 0) {

            var loadingImage = getLoadingImage(url)
            if (TextUtils.isEmpty(loadingImage)) loadingImage = thumbnailsUrl
            _loadingImage.value = loadingImage
        }

        // 히스토리 등록
        if (url != mainWeb && url != urlList[urlList.lastIndex]) {
            urlList.add(url)
        }

        val disposable = Observable.fromCallable {

            // 새로 불러와야 할 때만
            if (zoomImage == null && replaceImages.size == 0) {

                // 로그인 이후 이용가능한 페이지는 로그인 페이지 띄우고 CookieManager 에서 쿠키를 가져와서 Jsoup header에 넣으면 가능하다고함
                // 응답이 느려서 라이브러리 문제라고 생각했는데 라이브러리 안쓰고 해도 페이지 자체가 연결이 느림

                // 페이지 읽어오고 파싱 및 검색
                val response = Jsoup.connect(url)
                        .method(Connection.Method.GET)
                        .execute()
                val document = response.parse()
                val images = document.select("img")

                for (image in images) {
                    if (image.attr("class") == "img-fluid") {
                        putLoadingImage(url, image.attr("src"))
                    } else {
                        if (image.parentNode() != null) {
                            val parentNode = image.parentNode().parentNode()
                            if (image.hasAttr("data-zoomable")) {
                                zoomImage = image.attr("src")
                            }
                            else if (parentNode != null && parentNode.nodeName() == "a") {
                                val data = LinkImage()
                                data.href = parentNode.attr("href")
                                data.image = image.attr("src")
                                if (TextUtils.isEmpty(data.image)) {
                                    data.image = image.attr("data-src")
                                }
                                replaceImages.add(data)
                            }
                        }
                    }
                }

                when {
                    zoomImage != null -> zoomInImageMap[url] = zoomImage!!
                    replaceImages.size > 0 -> itemsMap[url] = replaceImages

                    else -> {

                    }
                }
            }
        }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe {

            if (!TextUtils.isEmpty(zoomImage)) {
                urlList.removeAt(urlList.lastIndex)
                _zoomImage.value = zoomImage
            } else {

                _loadingImage.value = null
                setItemList(replaceImages)
            }
        }
        addDisposable(disposable)
    }

    fun onItemClick(index: Int) {
        val data = items.value!![index]
        if (data.href != null) {
            getImageToLink(data.href!!, data.image!!)
        }
    }

    private fun putLoadingImage(url: String, image: String) {
        val edit = App.prefs.edit()
        edit.putString(url, image)
        edit.apply()
    }

    private fun getLoadingImage(url: String): String {
        return App.prefs.getString(url, "")
    }
}
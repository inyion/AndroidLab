package com.rel.csam.lab.viewmodel

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableArrayList
import com.rel.csam.lab.model.LinkImage

class LinkImageModel: BaseObservable() {

    val urlList = ArrayList<String>()               // 히스토리
    @Bindable
    val items = ObservableArrayList<LinkImage>()    // 이미지리스트
    @Bindable
    var mainImage: String? = null                   // 메인이미지
    var zoomImage: String? = null                  // 이미지상세

    init {

    }
}
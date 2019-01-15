package com.rel.csam.lab.viewmodel

import com.rel.csam.lab.model.LinkImage

class LinkImageItemModel: ItemViewModel<LinkImage>() {
    private var linkImage: LinkImage? = null
    override fun setItem(item: LinkImage) {
        this.linkImage = item
        notifyChange()
    }

}
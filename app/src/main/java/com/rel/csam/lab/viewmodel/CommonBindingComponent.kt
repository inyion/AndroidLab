package com.rel.csam.lab.viewmodel

import android.app.Dialog
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.util.Base64
import android.util.TypedValue
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingComponent
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.rel.csam.lab.getSafeContext
import com.rel.csam.lab.model.LinkImage
import com.rel.csam.lab.util.MemoryImageCache
import com.rel.csam.lab.util.Util
import com.rel.csam.lab.view.CommonActivity
import com.rel.csam.lab.view.SquareImageView
import com.rel.csam.lab.view.ZoomInImageActivity

class CommonBindingComponent : DataBindingComponent {

    override fun getBaseBindAdapter(): BaseBindAdapter<*, *>? {
        return null
    }

    override fun getCommonBindingComponent(): CommonBindingComponent {
        return this
    }

    @BindingAdapter("progress")
    fun progress(view: View, msg: LiveData<String>?) {
        if (view.getSafeContext() is CommonActivity) {
//            if (msg?.value != null) {
//                (view.getSafeContext() as CommonActivity).progressDialog.show(msg.value)
//            } else {
//                (view.getSafeContext() as CommonActivity).progressDialog.hideProgressDialog()
//            }
        }
    }

    @BindingAdapter("progressStrChange")
    fun progressStrChange(view: View, msg: LiveData<String>?) {
        if (view.getSafeContext() is CommonActivity) {
//            if (msg?.value != null && (view.getSafeContext() as CommonActivity).progressDialog.isShowing) {
//                (view.getSafeContext() as CommonActivity).progressDialog.progressStrChange(msg.value)
//            }
        }
    }

    @BindingAdapter("setProgress")
    fun setProgress(bar: ProgressBar, progress: Int) {
        bar.progress = progress
    }

//    @BindingAdapter("startActivity", "activityFinish", requireAll = false)
//    fun startActivity(view: View, data: LiveData<StartActivityModel>?, isFinish: Boolean?) {
//        val activityData: StartActivityModel? = data?.value
//
//        if (view.getSafeContext() is ViewModelActivity && activityData != null) {
//            if (activityData.bindListener != null) {
//                val startActivityModel = StartActivityModel(view.getSafeContext())
//                activityData.bindListener!!.bind(startActivityModel)
//                startActivityModel.start()
//            }
//
//            if ((isFinish != null && isFinish) || activityData.isAfterFinish) {
//                (view.getSafeContext() as ViewModelActivity).finish()
//            }
//        }
//
//    }

//    @BindingAdapter("checkPermission")
//    fun checkPermission(view: View, data: LiveData<PermissionData>?) {
//        val permissionData: PermissionData? = data?.value
//        if (view.getSafeContext() is ViewModelActivity && permissionData != null) {
//            if (permissionData.permission != null) {
//                permissionData.permission!!.bind(view.getSafeContext() as Activity)
//
//            }
//        }
//    }


    @BindingAdapter("topIcon")
    fun topIcon(view: Button, icon: Int) {
        view.setCompoundDrawablesWithIntrinsicBounds(0, icon, 0, 0)
    }

    @BindingAdapter("startIcon", "replaceIcon", requireAll = false)
    fun startIcon(view: TextView, iconStr: String?, replaceIcon: Int) {

        if (!TextUtils.isEmpty(iconStr)) {
//            val checkedUrl = MediaManager.imageUrlCheck(iconStr)
            Glide.with(view).load(iconStr)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                            return false
                        }

                        override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                            view.post {
                                view.setCompoundDrawablesWithIntrinsicBounds(resource, null, null, null)
                            }
                            return false
                        }
                    }).submit()
        } else {
            view.setCompoundDrawablesWithIntrinsicBounds(replaceIcon, 0, 0, 0)
        }
    }

    @BindingAdapter("buttonName")
    fun buttonName(view: Button, name: Int) {
        view.setText(name)
    }

    @BindingAdapter("setResourceImage")
    fun setResourceImage(view: ImageButton, resourceId: Int) {
        view.setImageResource(resourceId)
    }

    @BindingAdapter("setDrawableStart")
    fun setDrawableStart(view: TextView, resourceId: Int) {
        view.setCompoundDrawablesWithIntrinsicBounds(resourceId, 0, 0, 0)
    }

    @BindingAdapter("setImageEnd")
    fun setImageEnd(view: TextView, iconStr: String?) {

        if (!TextUtils.isEmpty(iconStr)) {
//            val checkedUrl = MediaManager.imageUrlCheck(iconStr)
            Glide.with(view).load(iconStr)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                            return false
                        }

                        override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                            view.post {
                                view.setCompoundDrawablesWithIntrinsicBounds(null, null, resource, null)
                            }
                            return false
                        }
                    }).submit()
        } else {
//            view.setCompoundDrawablesWithIntrinsicBounds(0, 0, replaceIcon, 0)
        }
    }

    private var chatImage: MemoryImageCache = MemoryImageCache()
//    @BindingAdapter("setImage", "setImageScaleType", "setResourceId", requireAll = false)
//    fun setImage(view: ImageView, url: LiveData<String>, scaleType: ImageView.ScaleType?, resourceId: Int) {
//        setImage(view, url.value, scaleType, resourceId, false, view.width, view.height, null, null, false)
//    }

    var maxMessageWidth: Int = 0
    var minImageWidthHeigh: Int = 0
    //    var maxMessageWidthForAd: Int = 0 // 요건 내부에선 쓸필요가 없음 케이스에따라 밖에서 들고올 데이터
    var maxCellSize: Int = 0
    var screenWidth: Int = 0
//    @BindingAdapter("setImage", "setImageScaleType", "setResourceId", // 기본기능
//            "topCrop", "imageWidth", "imageHeight", // 탑 크롭 기능
//            "createThumbnail",  //비디오 썸네일케이스
//            "messageForSizeUpdate",
//            "emptyVisibleGone", //이미지 비었을 시 gone처리
//            requireAll = false)
//    fun setImage(view: ImageView, url: String?, scaleType: ImageView.ScaleType?, resourceId: Int,
//                 topCrop: Boolean, imageWidth: Int, imageHeight: Int,
//                 thumbnailPath: String?, message: MessageInfo?, emptyVisibleGone: Boolean) {
//
////        if (message != null && message.isSend &&
////                !TextUtils.isEmpty(message.local_media_path) && message.media_msg_width > 0 && message.media_msg_height > 0) {
////            // 미디어 전송시는 초기화 안하기로
////        } else {
//        view.setImageResource(0)
////        }
//
//        if (TextUtils.isEmpty(url)) {
//            if (resourceId > 0) {
//                view.setImageResource(resourceId)
//            }
//
//            if (!TextUtils.isEmpty(thumbnailPath)) {
//
//                var bmThumbnail = chatImage.getBitmap(thumbnailPath)
//                if (bmThumbnail == null) {
//                    bmThumbnail = ThumbnailUtils.createVideoThumbnail(thumbnailPath, MediaStore.Video.Thumbnails.MINI_KIND)
//
//                    if (bmThumbnail != null) {
//                        // 동영상의 경우 서버의 섬네일로 바꿔지기 때문에 해당 경우에는 msgNo 로 저장하도록 ....
//                        chatImage.addBitmap(thumbnailPath, bmThumbnail)
//                    }
//                }
//
//                if (bmThumbnail != null) {
//                    if (scaleType != null) {
//                        view.scaleType = scaleType
//                    }
//
//                    // 리사이즈 및 셀사이즈 저장
//                    checkSellSizeUpdate(message, bmThumbnail, view)
//                    view.setImageBitmap(bmThumbnail)
//                }
//            }
//
//            if (emptyVisibleGone) {
//                view.visibility = View.GONE
//            }
//
//        } else {
//            view.visibility = View.VISIBLE
//            var checkedUrl = ""
//            if(QUtil.safeContains(url, "/storage/emulated/0/")){
//                checkedUrl = "file://$url"
//            } else {
//                checkedUrl = MediaManager.imageUrlCheck(url)
//            }
//            var bitmap = chatImage.getBitmap(checkedUrl)
//
//            if (bitmap != null) {
//
//                if (topCrop) {
//                    bitmap = MediaPicker.getTopCropImage(bitmap, imageWidth, imageHeight)
//                    if (bitmap != null) {
//                        val drawable = BitmapDrawable(view.resources, bitmap)
//                        view.background = drawable
//                    }
//                } else {
//                    if (scaleType != null) {
//                        view.scaleType = scaleType
//                    }
//                    // 리사이즈 및 셀사이즈 저장
//                    checkSellSizeUpdate(message, bitmap, view)
//                    view.setImageBitmap(bitmap)
//                }
//            } else {
//
//                Glide.with(view).load(checkedUrl)
//                        .listener(object : RequestListener<Drawable> {
//
//                            override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
//                                return false
//                            }
//
//                            override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
//
//                                view.post {
//                                    if (topCrop) {
//
//                                        if (resource is BitmapDrawable) {
//                                            var downLoadBitmap: Bitmap? = resource.bitmap
//                                            downLoadBitmap = MediaPicker.getTopCropImage(downLoadBitmap!!, imageWidth, imageHeight)
//                                            if (downLoadBitmap != null) {
//                                                val drawable = BitmapDrawable(view.resources, downLoadBitmap)
//                                                view.background = drawable
//                                            }
//                                            chatImage.addBitmap(checkedUrl, downLoadBitmap)
//                                        } else {
//                                            view.setImageDrawable(resource)
//                                        }
//
//                                    } else {
//
//                                        if (scaleType != null) {
//                                            view.scaleType = scaleType
//                                        }
//
//                                        if (resource is BitmapDrawable) {
//                                            val downLoadBitmap: Bitmap? = resource.bitmap
//                                            if (downLoadBitmap != null) {
//                                                // 리사이즈 및 셀사이즈 저장
//                                                checkSellSizeUpdate(message, downLoadBitmap, view)
//                                                if (message != null
//                                                        && (downLoadBitmap.width > maxCellSize || downLoadBitmap.height > maxCellSize)) {
//
//                                                    var viewWidth: Int = view.layoutParams.width
//                                                    var viewHeight: Int = view.layoutParams.height
//
//                                                    if (viewWidth == 0 || viewHeight == 0) {
//                                                        if (maxMessageWidth > 0) {
//                                                            viewWidth = maxMessageWidth
//                                                        }
//
//                                                        val rate: Float
//                                                        val width = viewWidth.toFloat()
//                                                        var height = viewHeight
//
//                                                        val viewWidth = viewWidth.toFloat()
//
//                                                        if (viewWidth > 0) {
//                                                            rate = viewWidth / width
//                                                            height = (height * rate).toInt()
//                                                        }
//
//                                                        viewHeight = if (height <= maxCellSize) {
//                                                            height
//                                                        } else {
//                                                            maxCellSize
//                                                        }
//                                                    }
//
//                                                    try {
//                                                        if (minImageWidthHeigh == 0) {
//                                                            val dm = Qtalk.instance.resources.displayMetrics
//                                                            minImageWidthHeigh = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 200F, dm).toInt()
//                                                        }
//
//                                                        if (viewWidth == 0) viewWidth = minImageWidthHeigh
//                                                        if (viewHeight == 0) viewHeight = minImageWidthHeigh
//
//                                                        val addBitmap = MediaManager.createScaledBitmap(downLoadBitmap, viewWidth, viewHeight)
//                                                        chatImage.addBitmap(checkedUrl, addBitmap)
//                                                        view.setImageBitmap(addBitmap)
//                                                    } catch (iae: IllegalArgumentException) {
//
//                                                    }
//                                                } else {
//                                                    chatImage.addBitmap(checkedUrl, downLoadBitmap)
//                                                    view.setImageDrawable(resource)
//                                                }
//                                            } else {
//                                                view.setImageDrawable(resource)
//                                            }
//
//                                        } else if (resource is GifDrawable) {
//                                            if (resource.firstFrame != null) {
//                                                chatImage.addBitmap(checkedUrl, resource.firstFrame)
//                                                // 리사이즈 및 셀사이즈 저장
//                                                checkSellSizeUpdate(message, resource.firstFrame, view)
//                                            }
//                                            view.setImageDrawable(resource)
//                                        }
//                                    }
//
//                                    if ((view.getSafeContext() as ViewModelActivity).viewModel is ChatModel) {
//                                        val chatModel = ((view.getSafeContext() as ViewModelActivity).viewModel as ChatModel)
//                                        if (chatModel.isLastPosition || chatModel.isFirstLoading) {
//                                            ((view.getSafeContext() as ViewModelActivity).viewModel as ChatModel).lastPostionAction()
//                                        }
//                                    }
//                                }
//                                return false
//                            }
//                        }).submit()
//            }
//        }
//    }

//    fun checkSellSizeUpdate(message: MessageInfo?, bm: Bitmap, view: View) {
//
//        if (maxMessageWidth == 0) {
//            val dm = Qtalk.instance.resources.displayMetrics
//            maxMessageWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, ((dm.widthPixels.toFloat()) * 0.61).toFloat(), dm).toInt()
////            maxMessageWidthForAd = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, ((dm.widthPixels.toFloat()) * 0.70).toFloat(), dm).toInt()
//            maxCellSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 342F, Qtalk.instance.resources.displayMetrics).toInt()
//        }
//
//        // 리사이즈 및 셀사이즈 저장
//        if (message != null && message.media_msg_width == 0 && message.media_msg_height == 0) {
//
//            setLayoutSizeForChat(view, bm.width, bm.height, maxMessageWidth)
//            val layoutParams = view.layoutParams
//            message.media_msg_width = layoutParams.width
//            message.media_msg_height = layoutParams.height
//            if (message.msg_seq > 0) {
//                MessageTable.updateMediaMessageParams(message)
//            }
//        }
//    }

//    @BindingAdapter("setImage", "setImageScaleType", "setResourceId", requireAll = false)
//    fun setImage(view: CircleImageView, url: String?, scaleType: ImageView.ScaleType?, resourceId: Int) {
//        view.setImageResource(0)
//        if (TextUtils.isEmpty(url)) {
//
//            if (resourceId > 0) {
//                view.setImageResource(resourceId)
//            }
//
//        } else {
//            view.visibility = View.VISIBLE
//            Glide.with(view).load(MediaManager.imageUrlCheck(url))
//                    .listener(object : RequestListener<Drawable> {
//
//                        override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
//                            return false
//                        }
//
//                        override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
//                            view.post {
//                                if (scaleType != null) {
//                                    view.scaleType = scaleType
//                                }
//                                view.setImageDrawable(resource)
//                            }
//                            return false
//                        }
//                    }).submit()
//        }
//    }
    @BindingAdapter("setString")
    fun setString(view: TextView, str: LiveData<String>) {
        setString(view, str.value)
    }

    @BindingAdapter("setString")
    fun setString(view: TextView, str: String?) {
        if (TextUtils.isEmpty(str)) {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
            view.text = str
        }
    }

    @BindingAdapter("setSpannableString")
    fun setSpannableString(view: TextView, str: LiveData<SpannableString>) {
        if (TextUtils.isEmpty(str.value)) {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
            view.text = str.value
        }
    }

    @BindingAdapter("setSpannable")
    fun setSpannable(view: TextView, str: Spannable?) {
        if (str != null) {
            view.text = str
        }
    }

//    @BindingAdapter("chatMessage")
//    fun chatMessage(view: TextView, message: MessageInfo?) {
//        if (message != null) {
//            message.addMsg = " timezone"
//            view.movementMethod = LinkMovementMethod.getInstance()
//            if (message.isLongLengthMsg()) view.setOnClickListener { ((view.getSafeContext() as ViewModelActivity).viewModel as ChatModel).showMoreMessage(message) }
//            else view.setOnClickListener(null)
//            view.setOnLongClickListener { ((view.getSafeContext() as ViewModelActivity).viewModel as ChatModel).onMessageLongClick(message) }
//            view.tag = message
//            if (!TextUtils.isEmpty(message.sub_msg)) {
//                StickerLoader.requestContentsManager(view.getSafeContext(), message, view, false, false)
//            } else {
//                val messageSpan = StringUtil.linkifyText((view.getSafeContext() as ViewModelActivity).viewModel, QUtil.convertUniToStr(message.getSplitMsg()), null, message)
//                view.text = messageSpan
//            }
//
//            val isTagMsg = !TextUtils.isEmpty(message.tagMsg)
//            if (isTagMsg && !TextUtils.isEmpty(message.cellBackGround)) {
//                view.setBackgroundColor(Color.parseColor(message.cellBackGround))
//            }
//        }
//    }
//
//    @BindingAdapter("chatMessage")
//    fun chatMessage(view: ImageView, message: MessageInfo?) {
//        if (message != null) {
//            view.tag = message
//            StickerLoader.requestContentsManager(view.getSafeContext(), message.stickerInfo, view, null, StickerLoader.VIEW_GROUP_TYPE_FRAME, null)
//        }
//    }

    @BindingAdapter("setTextColorTheme")
    fun setTextColorTheme(view: TextView, attrId: Int?) {
        val outValue = TypedValue()
        if (attrId != null) {
            view.getSafeContext().theme.resolveAttribute(attrId, outValue, true)
            view.setTextColor(ContextCompat.getColor(view.getSafeContext(), outValue.resourceId))
        }
    }

//    @BindingAdapter("backgroundColor")
//    fun setBackGroundColor(view: HeaderView, backgroundColor: LiveData<String>?) {
//        if (backgroundColor != null && backgroundColor.value != null) {
//            view.oldStyleSetting(backgroundColor.value)
//        }
//    }

//    @BindingAdapter("badge")
//    fun setBadge(view: HeaderView, badge: LiveData<Int>?) {
//        if (badge != null && badge.value != null) {
//            BroadcastHelper.sendBroadcastBadgeCount(view.getSafeContext(), badge.value.toString())
//            if (badge.value!! <= 0) {
//                view.setNotReadCount("")
//            } else {
//                if (badge.value!! > 99) {
//                    view.setNotReadCount("99+")
//                } else {
//                    view.setNotReadCount(badge.value.toString())
//                }
//            }
//        }
//    }

//    @BindingAdapter("title", "memberCnt", requireAll = false)
//    fun setTitle(view: HeaderView, title: LiveData<String>, memberCnt: LiveData<String>?) {
//        if (title.value != null) {
//            if (memberCnt != null && TextUtils.isEmpty(memberCnt.value)) {
//                view.setTitleText(title.value, memberCnt.value)
//            } else {
//                view.setTitleText(title.value)
//            }
//        }
//    }

    @BindingAdapter("customBackground")
    fun setBackground(layout: ImageView, wallPaper: LiveData<String>) {
        if (wallPaper.value != null) {
            val arrWallpaper = wallPaper.value!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (arrWallpaper.size == 2) {

                val wallPaperType = arrWallpaper[0]
                val wallPaperName = arrWallpaper[1]
                layout.setImageResource(0)
                layout.setImageBitmap(null)
                try {
//                    when {
//                        Util.safeEqual(wallPaperType, WallpaperData.WALLPAPER_TYPE_CODE) -> layout.setBackgroundColor(Color.parseColor(wallPaperName))
//                        Util.safeEqual(wallPaperType, WallpaperData.WALLPAPER_TYPE_DRAWABLE) -> try {
//                            val idField = R.drawable::class.java.getDeclaredField(wallPaperName)
//                            layout.setImageResource(idField.getInt(idField))
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
//                        Util.safeEqual(wallPaperType, WallpaperData.WALLPAPER_TYPE_PHOTO) -> Glide.with(layout)
//                                .asBitmap()
//                                .load(wallPaperName)
//                                .into(object : SimpleTarget<Bitmap>() {
//                                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                                        layout.setImageBitmap(resource)
//
//
//                                    }
//                                })
//                    }
                } catch (ome: OutOfMemoryError) {
                    ome.printStackTrace()
                }

            }
        }
    }

//    @BindingAdapter("hint")
//    fun setHint(view: MessageEditText, hint: LiveData<String>?) {
//        if (hint != null && hint.value != null) {
//            view.hint = hint.value
//        }
//    }

    @BindingAdapter("setBackgroundTheme")
    fun setBackgroundTheme(view: View, attrId: Int?) {
        val outValue = TypedValue()
        if (attrId != null) {
            view.getSafeContext().theme.resolveAttribute(attrId, outValue, true)
            view.setBackgroundResource(outValue.resourceId)
        }
    }

    @BindingAdapter("setBackgroundResource")
    fun setBackgroundResource(view: View, resourceId: Int?) {
        if (resourceId != null) {
            view.setBackgroundResource(resourceId)
        }
    }

//    @BindingAdapter("refresh")
//    fun refresh(view: View, mainData: LiveData<MainDataModel>?) {
//        if (view.getSafeContext() is ViewModelActivity && mainData != null && mainData.value != null) {
//            (view.getSafeContext() as ViewModelActivity).initLayout()
//        }
//    }
//
//    @BindingAdapter("finish")
//    fun finish(view: View, isFinish: LiveData<Boolean>?) {
//        if (view.getSafeContext() is ViewModelActivity && isFinish != null && isFinish.value != null && isFinish.value as Boolean) {
//            (view.getSafeContext() as ViewModelActivity).finish()
//        }
//    }

    @BindingAdapter("setTag")
    fun setTag(view: View, tag: Any?) {
        if (tag != null) {
            view.tag = tag
        }
    }

    @BindingAdapter("setEditText")
    fun setEditText(view: EditText, string: LiveData<String>?) {
        var str = string?.value
        if (str == null
                || (TextUtils.isEmpty(string?.value) && Util.safeEqual(view.text.toString(), string?.value))) {
            return
        }

        view.setText(string?.value)
        view.setSelection(string?.value!!.length)
    }

//    @BindingAdapter("showCustomDialog")
//    fun showCustomDialog(view: View, builder: LiveData<CustomDialog.Builder>?) {
//        if (view.getSafeContext() is CommonActivity && builder != null) {
//            if (builder.value != null) {
////                customDialog가 열려있을 때 닫고 새로운 builder로 열리도록 -> 한화면에 하나만 뜨는 다이얼로그
//                (view.getSafeContext() as CommonActivity).customDialog.dismissDialog()
//
//                if ((view.getSafeContext() as CommonActivity).customDialog != null) {
//                    (view.getSafeContext() as CommonActivity).customDialog.init(builder.value!!)
//                    (view.getSafeContext() as CommonActivity).customDialog.initForType(builder.value!!)
//                }
//                if (builder.value != null && builder.value!!.bindDialog != null) {
//                    builder.value!!.bindDialog!!.bindDialogListener((view.getSafeContext() as CommonActivity).customDialog.dialog)
//                }
//
//                (view.getSafeContext() as CommonActivity).customDialog.show(builder.value)
//            } else {
//                (view.getSafeContext() as CommonActivity).customDialog.dismissDialog()
//            }
//        }
//    }

    var dialog: Dialog? = null
    private lateinit var messageGridView: RecyclerView

    @BindingAdapter("copyToClipboard")
    fun copyToClipboard(view: View, str: LiveData<String>?) {
        if (view.getSafeContext() is CommonActivity && str != null && str.value != null) {
            val clip = view.getSafeContext().getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            clip.primaryClip = ClipData.newPlainText("text", str.value)
//            ToastUtil.showToastCenter(view.getSafeContext().getString(R.string.copy_clipboard))
        }
    }

    // 언젠가 제거해야함 일단 리펙토링하면서 일괄처리함
//    @BindingAdapter("ban")
//    fun ban(view: View, banStr: LiveData<Int>?) {
//        if (banStr == null) return
//
//        val chatModel = (view.getSafeContext() as ChattingActivity).viewModel as ChatModel
//        when (banStr.value) {
//            R.string.context_text_block,
//            R.string.text_unblock -> {
//                if (view.getSafeContext() is ChattingActivity && chatModel.roomInfo.isStoreRoom) {
//                    (view.getSafeContext() as ChattingActivity).showReportPopup(chatModel.dialogMenuModel.messageInfo, LiveForumReportPopup.POPUP_TYPE.BLOCK)
//                } else if (view.getSafeContext() is ChatOptionMenuListener) {
//                    (view.getSafeContext() as ChatOptionMenuListener).optionBlock(null)
//                }
//            }
//
//            R.string.report -> {
//                (view.getSafeContext() as ChattingActivity).showReportPopup(chatModel.dialogMenuModel.messageInfo, LiveForumReportPopup.POPUP_TYPE.REPORT)
//            }
//        }
//    }

    @BindingAdapter("enable")
    fun enable(view: View, enable: LiveData<Boolean>?) {
        if (enable != null) {
            view.isEnabled = enable.value!!
        }
    }

    @BindingAdapter("handleAction")
    fun handleAction(view: View, action: LiveData<String>?) {

        if (action != null && action.value != null && view.getSafeContext() is CommonActivity) {
            view.postDelayed({
                (view.getSafeContext() as CommonActivity).handleAction(action.value)
            }, 300)
        }
    }

    @BindingAdapter("directAction")
    fun directAction(view: View, action: LiveData<String>?) {

        if (action != null && action.value != null && view.getSafeContext() is CommonActivity) {
            view.postDelayed({
                (view.getSafeContext() as CommonActivity).directAction(action.value)
            }, 300)
        }
    }


//    @BindingAdapter("cookieBar")
//    fun cookieBar(view: View, data: LiveData<CookieBarData>?) {
//
//        if (data != null && data.value != null) {
//            CookieBar.build(view.getSafeContext() as Activity)
//                    .setCustomView(data.value!!.layoutId)
//                    .setEnableAutoDismiss(data.value!!.isAutoDismiss)
//                    .setCookiePosition(data.value!!.cookiePosition).show()
//        } else {
//            CookieBar.dismiss(view.getSafeContext() as Activity)
//        }
//    }

    @BindingAdapter("layout_width", "layout_height", requireAll = false)
    fun setLayoutSize(view: View, width: Int, height: Int) {
        val layoutParams = view.layoutParams
        if (width > 0) {
            layoutParams.width = width
        }

        if (height > 0) {
            layoutParams.height = height
        }

        view.layoutParams = layoutParams
    }

    @BindingAdapter("contents_width", "contents_height", "max_width", requireAll = false)
    fun setLayoutSizeForChat(view: View, width: Int, height: Int, maxWidth: Int) {
        val layoutParams = view.layoutParams

//        if (view.layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
//            if (maxWidth > 0) {
//                if (width <= maxWidth) {
//                    layoutParams.width = width
//                } else {
//                    layoutParams.width = maxWidth
//                }
//            } else {
//                if (width > 0) {
//                    layoutParams.width = width
//                }
//            }
//        } else
        if (maxWidth > 0) {// 채팅이 기본값이 화면에 일정비율로 꽉채워 보여줘서
            layoutParams.width = maxWidth
        }

        if (height > 0 && width > 0) {

            if (maxCellSize == 0) {
                val dm = view.getSafeContext().resources.displayMetrics
                maxMessageWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, ((dm.widthPixels.toFloat()) * 0.61).toFloat(), dm).toInt()
//                maxMessageWidthForAd = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, ((dm.widthPixels.toFloat()) * 0.70).toFloat(), dm).toInt()
                maxCellSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 342F, view.getSafeContext().resources.displayMetrics).toInt()
            }

            val rate: Float
            val width = width.toFloat()
            var height = height

            val viewWidth = maxWidth.toFloat()
            // 세로값은 넘겨준값 그대로 쓰기로... html뷰때문에..나중에 케이스 필요하면 케이스 추가
//            val viewWidth = if (view.layoutParams.width == ViewGroup.LayoutParams.MATCH_PARENT) {
//                if (screenWidth == 0) {
//                    val dm = Qtalk.instance.resources.displayMetrics
//                    screenWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, dm.widthPixels.toFloat(), dm).toInt()
//                }
//                screenWidth.toFloat()
//            } else {
//                maxWidth.toFloat()
//            }

            if (viewWidth > 0) {
                rate = viewWidth / width
                height = (height * rate).toInt()
            }

            //max제한이 없거나
            if (maxWidth == 0 || height <= maxCellSize) {
                layoutParams.height = height
            } else {
                layoutParams.height = maxCellSize
            }

        }

        view.layoutParams = layoutParams
    }

    @BindingAdapter("setLoadingImage")
    fun setLoadingImage(view: ImageView, imgUrl: LiveData<String>) {
        if (!TextUtils.isEmpty(imgUrl.value)) {
            view.visibility = View.VISIBLE

            val uri = Uri.parse(imgUrl.value)
            if (Util.safeEqual(uri.scheme, "data")) {
                val decodedString = Base64.decode(uri.toString(), Base64.DEFAULT)
                val bitMap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                view.setImageBitmap(bitMap)
            } else {

                val url = if(imgUrl.value!!.startsWith("/")) {
                    "https://www.google.com$imgUrl.value"
                } else {
                    imgUrl.value
                }

                Glide.with(view.context).load(url).into(view)
            }

        } else {
            view.visibility = View.GONE
        }
    }

    @BindingAdapter("setLoadingImage")
    fun setLoadingImage(view: SwipeRefreshLayout, imgUrl: LiveData<String>) {
        if (TextUtils.isEmpty(imgUrl.value)) {
            view.isRefreshing = false
        }
    }

    @BindingAdapter("goZoomInImage")
    fun goZoomInImage(view: RecyclerView, imageUrl: LiveData<String>) {
        if (!TextUtils.isEmpty(imageUrl.value)) {
            val intent = Intent(view.context, ZoomInImageActivity::class.java)
            intent.putExtra("image", imageUrl.value)
            ContextCompat.startActivity(view.context, intent, null)
        }
    }

    @BindingAdapter("imageSrc")
    fun imageSrc(view: SquareImageView, image: String) {
        val uri = Uri.parse(image)
        if (Util.safeEqual(uri.scheme, "data")) {
            val decodedString = Base64.decode(uri.toString(), Base64.DEFAULT)
            val bitMap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            view.setImageBitmap(bitMap)
        } else {

            val url = if(image.startsWith("/")) {
                "https://www.google.com$image"
            } else {
                image
            }

            Glide.with(view).load(url).thumbnail(0.8f).into(view)
        }
    }

}
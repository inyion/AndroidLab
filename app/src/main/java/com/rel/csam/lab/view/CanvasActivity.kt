package com.rel.csam.lab.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Color.*
import android.graphics.Matrix
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.util.Log
import android.util.SparseIntArray
import android.view.Gravity
import android.view.Surface
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.colorChooser
import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager
import com.beloo.widget.chipslayoutmanager.SpacingItemDecoration
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.text.FirebaseVisionCloudTextRecognizerOptions
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import com.rel.csam.lab.R
import com.rel.csam.lab.databinding.DrawNoteBinding
import com.rel.csam.lab.util.PermissionUtils
import com.rel.csam.lab.viewmodel.TagModel


class CanvasActivity : ViewModelActivity<TagModel>() {
    override fun createViewModel() {
        createViewModel(TagModel::class.java)
    }

    override fun createDataBindingComponent() {
        createDataBindingComponent(TagViewAdapter(viewModel))
    }

//    private lateinit var tessBaseAPI: TessBaseAPI
    private lateinit var detector: FirebaseVisionTextRecognizer

    private var canvasView: CanvasView? = null
//    private var lang = "eng"
    private var selectColor = 0

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_pencil -> {

                val colors = intArrayOf(
                        parseColor("#F44336"), parseColor("#E91E63"), parseColor("#9C27B0"),
                        parseColor("#673AB7"), parseColor("#3F51B5"), parseColor("#2196F3"),
                        parseColor("#03A9F4"), parseColor("#00BCD4"), parseColor("#009688"),
                        parseColor("#4CAF50"), parseColor("#8BC34A"), parseColor("#CDDC39"),
                        parseColor("#FFEB3B"), parseColor("#FFC107"), parseColor("#FF9800"),
                        parseColor("#FF5722"), parseColor("#795548"), parseColor("#9E9E9E"),
                        parseColor("#607D8B")
                )

                MaterialDialog(this).show {
                    title(R.string.colors)
                    colorChooser(colors) { dialog, color ->
                        selectColor = color
                        binding.navigation.itemTextColor = ColorStateList.valueOf(color)
                        binding.navigation.itemIconTintList = ColorStateList.valueOf(color)
                        canvasView?.setTextColor(color)
                    }
                    positiveButton(R.string.select)
                }

            }
            R.id.navigation_save -> {
                canvasView!!.isDrawingCacheEnabled = true
                if (!PermissionUtils().storagePermission(this@CanvasActivity)) {
                    Toast.makeText(this@CanvasActivity, "Enable Storage Permission", Toast.LENGTH_SHORT).show()
                } else {
                    saveCanvas()
                }
            }
            R.id.navigation_delete -> canvasView!!.clearCanvas()
        }
        false
    }

    lateinit var binding: DrawNoteBinding
    override fun onCreate() {
        binding = setContentLayout(R.layout.draw_note)

        canvasView = findViewById(R.id.canvasView)
        viewModel.initDatabase(this@CanvasActivity)
        binding.viewModel = viewModel
        binding.navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

//        tessBaseAPI = TessBaseAPI()
//        val dir = "$filesDir/tesseract"
//        lang = "eng"
//        checkLanguageFile("$dir/tessdata")
//        lang = "kor"
//        checkLanguageFile("$dir/tessdata")
//        tessBaseAPI.init(dir, "eng+kor")


        val chipsLayoutManager = ChipsLayoutManager.newBuilder(this)
                .setChildGravity(Gravity.TOP)
                .setScrollingEnabled(true)
                .setGravityResolver { Gravity.CENTER; }
                .setOrientation(ChipsLayoutManager.HORIZONTAL)
                .setRowStrategy(ChipsLayoutManager.STRATEGY_DEFAULT)
                .withLastRow(true)
                .build()
        binding.tagLayout.layoutManager = chipsLayoutManager
        binding.tagLayout.adapter = bindingComponent as TagViewAdapter

        binding.tagLayout.addItemDecoration(
                SpacingItemDecoration(
                        resources.getDimensionPixelOffset(R.dimen.faboptions_separator_margin),
                        resources.getDimensionPixelOffset(R.dimen.faboptions_separator_margin)
                ))

        val options = FirebaseVisionCloudTextRecognizerOptions.Builder()
                .setLanguageHints(listOf("en", "ko"))
                .build()
        detector = FirebaseVision.getInstance().getCloudTextRecognizer(options)
        initViewModel()
    }

    private fun saveCanvas() {

        //        final File Path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Canvas");
        //        Path.mkdirs();
        //        String fileName = "Canvas-" + System.currentTimeMillis() + ".jpg";
        //        File saveFile = new File(Path, fileName);
        //        FileOutputStream FOS = null;
        //        try {
        //            FOS = new FileOutputStream(saveFile);
        //            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, FOS);
        //            FOS.close();
        //        } catch (FileNotFoundException e) {
        //            e.printStackTrace();
        //        } catch (IOException e) {
        //            e.printStackTrace();
        //        }


//        val options = BitmapFactory.Options()
//        options.inSampleSize = 8

        var bitmap: Bitmap? = canvasView!!.drawingCache
        bitmap = GetRotatedBitmap(bitmap, getRotationCompensation("0", this@CanvasActivity, this@CanvasActivity))

        Toast.makeText(this, "노트 인식중...", Toast.LENGTH_SHORT).show()

        val image = FirebaseVisionImage.fromBitmap(bitmap!!)

        val result = detector.processImage(image)
                .addOnSuccessListener { result ->
                    var text = ""

                    var serverCheck = false
                    for (block in result.textBlocks) {
                        val blockText = block.text
//                        val blockConfidence = block.confidence
//                        val blockLanguages = block.recognizedLanguages
//                        val blockCornerPoints = block.cornerPoints
//                        val blockFrame = block.boundingBox
                        for (line in block.lines) {
                            val lineText = line.text
//                            val lineConfidence = line.confidence
//                            val lineLanguages = line.recognizedLanguages
//                            val lineCornerPoints = line.cornerPoints
//                            val lineFrame = line.boundingBox

                            if (!TextUtils.isEmpty(text)) {
                                text += " "
                            }

                            text += lineText

//                            for (element in line.elements) {
//                                val elementText = element.text
//                                val elementConfidence = element.confidence
//                                val elementLanguages = element.recognizedLanguages
//                                val elementCornerPoints = element.cornerPoints
//                                val elementFrame = element.boundingBox
//                            }
                        }
                        serverCheck = true
                    }

                    if (TextUtils.isEmpty(text)) {
                        if(serverCheck) {
                            Toast.makeText(this, "악필. 인식불가.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "구글 ML서버 이상 인식불량", Toast.LENGTH_SHORT).show()
                            val options = FirebaseVisionCloudTextRecognizerOptions.Builder()
                                    .setLanguageHints(listOf("en", "ko"))
                                    .build()
                            detector = FirebaseVision.getInstance().getCloudTextRecognizer(options)
                        }

                    } else {
                        val data = Intent()
                        data.putExtra("tag_color", if (selectColor != 0) selectColor else Color.WHITE)
                        if (viewModel.selectTagList.size > 0) {
                            Toast.makeText(this, "메모 추가: $text", Toast.LENGTH_SHORT).show()
                            data.putExtra("memo", text)

                            val tagSpan = SpannableStringBuilder()
                            for (tag in viewModel.selectTagList) {
                                if (!tagSpan.isEmpty()) {
                                    tagSpan.append(",")
                                }
                                tagSpan.append(tag.tagName)
                            }

                            data.putExtra("tag_list", tagSpan.toString())
                        } else {
                            Toast.makeText(this, "태그 추가 : $text", Toast.LENGTH_SHORT).show()
                            data.putExtra("tag", text)
                        }

                        setResult(Activity.RESULT_OK, data)
                        finish()
                    }
                }
                .addOnFailureListener { ex->

                    Toast.makeText(this, "실패 : " + ex.message, Toast.LENGTH_SHORT).show()
                }

//        viewModel.addDisposable(Observable.fromCallable {
//
//                    tessBaseAPI.setImage(parentView!!.drawingCache)
//                    tessBaseAPI.utF8Text
//                }
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe{ text ->
//
//                    Toast.makeText(this, "태그 : $text", Toast.LENGTH_SHORT).show()
//                    val data = Intent()
//                    data.putExtra("tag", text)
//                    setResult(Activity.RESULT_OK, data)
//                    finish()
//
//                })
    }


//    internal fun checkLanguageFile(dir: String): Boolean {
//        val file = File(dir)
//        if (!file.exists() && file.mkdirs())
//            createFiles(dir)
//        else if (file.exists()) {
//            val filePath = "$dir/$lang.traineddata"
//            val langDataFile = File(filePath)
//            if (!langDataFile.exists())
//                createFiles(dir)
//        }
//        return true
//    }

//    private fun createFiles(dir: String) {
//        val assetMgr = this.assets
//
//        var inputStream: InputStream? = null
//        var outputStream: OutputStream? = null
//
//        try {
//            inputStream = assetMgr.open("$lang.traineddata")
//
//            val destFile = "$dir/$lang.traineddata"
//
//            outputStream = FileOutputStream(destFile)
//
//            val buffer = ByteArray(1024)
//            var read: Int
//            while (true) {
//                read = inputStream!!.read(buffer)
//                if (read == -1) break
//                outputStream.write(buffer, 0, read)
//            }
//
//            inputStream!!.close()
//            outputStream.flush()
//            outputStream.close()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//    }

    private val ORIENTATIONS = SparseIntArray()

    init {
        ORIENTATIONS.append(Surface.ROTATION_0, 90)
        ORIENTATIONS.append(Surface.ROTATION_90, 0)
        ORIENTATIONS.append(Surface.ROTATION_180, 270)
        ORIENTATIONS.append(Surface.ROTATION_270, 180)
    }/**
     * Get the angle by which an image must be rotated given the device's current
     * orientation.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Throws(CameraAccessException::class)
    private fun getRotationCompensation(cameraId: String, activity: Activity, context: Context): Int {
        // Get the device's current rotation relative to its "native" orientation.
        // Then, from the ORIENTATIONS table, look up the angle the image must be
        // rotated to compensate for the device's rotation.
        val deviceRotation = activity.windowManager.defaultDisplay.rotation
        var rotationCompensation = ORIENTATIONS.get(deviceRotation)

        // On most devices, the sensor orientation is 90 degrees, but for some
        // devices it is 270 degrees. For devices with a sensor orientation of
        // 270, rotate the image an additional 180 ((270 + 270) % 360) degrees.
        val cameraManager = context.getSystemService(CAMERA_SERVICE) as CameraManager
        val sensorOrientation = cameraManager
                .getCameraCharacteristics(cameraId)
                .get(CameraCharacteristics.SENSOR_ORIENTATION)!!
        rotationCompensation = (rotationCompensation + sensorOrientation + 270) % 360

        // Return the corresponding FirebaseVisionImageMetadata rotation value.
        val result: Int
        when (rotationCompensation) {
            0 -> result = FirebaseVisionImageMetadata.ROTATION_0
            90 -> result = FirebaseVisionImageMetadata.ROTATION_90
            180 -> result = FirebaseVisionImageMetadata.ROTATION_180
            270 -> result = FirebaseVisionImageMetadata.ROTATION_270
            else -> {
                result = FirebaseVisionImageMetadata.ROTATION_0
                Log.e("Canvas", "Bad rotation value: $rotationCompensation")
            }
        }
        return result
    }

    companion object {

        @Synchronized
        fun GetRotatedBitmap(bitmap: Bitmap?, degrees: Int): Bitmap? {
            var bitmap = bitmap
            if (degrees != 0 && bitmap != null) {
                val m = Matrix()
                m.setRotate(degrees.toFloat(), bitmap.width.toFloat() / 2, bitmap.height.toFloat() / 2)
                try {
                    val b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, m, true)
                    if (bitmap != b2) {
                        bitmap = b2
                    }
                } catch (ex: OutOfMemoryError) {
                    ex.printStackTrace()
                }

            }
            return bitmap
        }
    }
}

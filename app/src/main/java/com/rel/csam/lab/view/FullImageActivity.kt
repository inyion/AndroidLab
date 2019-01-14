package com.rel.csam.lab.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.rel.csam.lab.R
import kotlinx.android.synthetic.main.activity_full_image.*

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_image)
        Glide.with(this).load(intent.getStringExtra("image")).into(fullscreen_content!!)
    }
}

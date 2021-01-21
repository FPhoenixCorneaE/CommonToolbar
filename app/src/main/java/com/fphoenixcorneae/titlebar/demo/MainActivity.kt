package com.fphoenixcorneae.titlebar.demo

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fphoenixcorneae.titlebar.CommonTitleBar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val toolbar = CommonTitleBar(this).apply {
            centerType = CommonTitleBar.TYPE_CENTER_TEXT_VIEW
            centerTextView?.apply {
                setTextColor(Color.WHITE)
                textSize = 18f
                paint.isFakeBoldText = true
            }
            showBottomLine = true
            bottomLineColor = Color.RED
            bottomShadowHeight = 30f
            titleBarColor = Color.BLACK
            titleBarHeight = 200
            statusBarColor = Color.BLACK
            statusBarMode = 1
        }
        toolbar.centerTextView?.text = "首页"
        mLlContent.addView(toolbar)
    }
}
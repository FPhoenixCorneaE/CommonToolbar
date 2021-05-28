package com.fphoenixcorneae.toolbar.demo

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fphoenixcorneae.ext.dp2Px
import com.fphoenixcorneae.ext.dpToPx
import com.fphoenixcorneae.ext.toast
import com.fphoenixcorneae.ext.toastQQStyle
import com.fphoenixcorneae.ext.view.setTintColor
import com.fphoenixcorneae.toolbar.CommonToolbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val toolbar = CommonToolbar(this).apply {
            leftType = CommonToolbar.TYPE_LEFT_IMAGE_BUTTON
            leftImageButton?.setTintColor(Color.WHITE)
            centerType = CommonToolbar.TYPE_CENTER_TEXT_VIEW
            centerTextView?.apply {
                setTextColor(Color.WHITE)
                textSize = 18f
                paint.isFakeBoldText = true
            }
            rightType = CommonToolbar.TYPE_RIGHT_TEXT_VIEW
            rightTextView?.apply {
                setTextColor(Color.WHITE)
                textSize = 16f
                paint.isFakeBoldText = true
                text = "确定"
            }
            showBottomLine = true
            bottomLineColor = Color.RED
            bottomShadowHeight = 10f.dpToPx()
            toolbarColor = Color.BLACK
            toolbarHeight = 44.dp2Px()
            statusBarColor = Color.BLACK
            statusBarMode = 1
            onToolbarClickListener = { v, action, extra ->
                when (action) {
                    CommonToolbar.MotionAction.ACTION_LEFT_BUTTON -> {
                        toastQQStyle("点击了返回按钮")
                    }
                    CommonToolbar.MotionAction.ACTION_RIGHT_TEXT -> {
                        toastQQStyle("点击了确定")
                    }
                }
            }
            onToolbarCenterDoubleClickListener = {
                toast("双击了中间")
            }
        }
        toolbar.centerTextView?.text = "首页"
        mLlContent.addView(toolbar)
        toolbar.showCenterProgress()
        toolbar.postDelayed({
            toolbar.dismissCenterProgress()
        },2000)
    }
}
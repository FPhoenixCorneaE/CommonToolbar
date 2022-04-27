package com.fphoenixcorneae.toolbar.demo

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fphoenixcorneae.common.ext.*
import com.fphoenixcorneae.toolbar.CommonToolbar
import com.fphoenixcorneae.toolbar.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var mViewBinding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mViewBinding!!.root)
        mViewBinding?.run {
            rlToolbarProgress.showCenterProgress()
        }
        CommonToolbar(this).apply {
            leftType = CommonToolbar.TYPE_LEFT_TEXT_VIEW
            leftTextDrawableRes = R.drawable.common_toolbar_selector_ic_back
            leftText = "返回"
            leftTextColor = Color.WHITE
            centerType = CommonToolbar.TYPE_CENTER_TEXT_VIEW
            centerTextColor = Color.WHITE
            centerTextSize = 18f.sp.toFloat()
            centerTextBold = true
            rightType = CommonToolbar.TYPE_RIGHT_TEXT_VIEW
            rightTextColor = Color.WHITE
            rightTextSize = 16f.sp.toFloat()
            rightTextBold = true
            rightText = "确定"
            showBottomLine = true
            bottomLineColor = Color.RED
            bottomShadowHeight = 10f.dp.toFloat()
            toolbarColor = Color.BLACK
            toolbarHeight = 60.dp
            statusBarColor = Color.BLACK
            statusBarMode = 1
            onToolbarClickListener = { v, action, extra ->
                when (action) {
                    CommonToolbar.MotionAction.ACTION_LEFT_TEXT -> {
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
            centerText = "动态构造 Toolbar"
            showCenterProgress()
        }.also {
            mViewBinding!!.llContent.addView(it)
            it.postDelayed({
                it.dismissCenterProgress()
            }, 2000)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewBinding = null
    }
}
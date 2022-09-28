package com.fphoenixcorneae.toolbar

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.getColorOrThrow
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.fphoenixcorneae.common.ext.*
import com.fphoenixcorneae.common.ext.view.setFocus
import com.fphoenixcorneae.common.ext.view.setTintColor
import com.fphoenixcorneae.common.ext.view.textString
import com.fphoenixcorneae.common.util.statusbar.StatusBarUtil
import kotlin.math.max

/**
 * @desc：通用标题栏
 * @date：2021/08/04 16:47
 */
class CommonToolbar @JvmOverloads constructor(
    context: Context,
    private val attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : RelativeLayout(context, attrs, defStyleAttr, defStyleRes), View.OnClickListener {

    /** 布局加载器 */
    private val mLayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    /** 状态栏 */
    private val mStatusBar by lazy {
        View(context).apply {
            id = View.generateViewId()
            val statusBarHeight = StatusBarUtil.getStatusBarHeight(context)
            layoutParams = LayoutParams(MATCH_PARENT, statusBarHeight).apply {
                addRule(ALIGN_PARENT_TOP)
            }
        }
    }

    /** 标题栏底部分隔线 */
    private val mBottomLine by lazy {
        View(context).apply {
            id = View.generateViewId()
            layoutParams = LayoutParams(MATCH_PARENT, max(1, 0.4f.dp)).apply {
                addRule(BELOW, mRlMain.id)
            }
        }
    }

    /** 标题栏底部阴影 */
    private val mBottomShadow by lazy {
        View(context).apply {
            id = View.generateViewId()
            setBackgroundResource(R.drawable.common_toolbar_shape_bottom_shadow)
            layoutParams = LayoutParams(MATCH_PARENT, bottomShadowHeight.dp).apply {
                addRule(BELOW, mRlMain.id)
            }
        }
    }

    /** 主视图 */
    private val mRlMain by lazy {
        RelativeLayout(context).apply {
            id = View.generateViewId()
            layoutParams = LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                val transparentStatusBar = StatusBarUtil.supportTransparentStatusBar()
                if (fillStatusBar && transparentStatusBar) {
                    addRule(BELOW, mStatusBar.id)
                } else {
                    addRule(ALIGN_PARENT_TOP)
                }
            }
        }
    }

    /** 标题栏左边布局参数 */
    private val mLeftLayoutParams by lazy {
        LayoutParams(WRAP_CONTENT, MATCH_PARENT).apply {
            addRule(ALIGN_PARENT_START)
            addRule(CENTER_VERTICAL)
        }
    }

    /** 标题栏中间自定义布局参数 */
    private val mCenterCustomLayoutParams by lazy {
        LayoutParams(WRAP_CONTENT, MATCH_PARENT).apply {
            addRule(CENTER_IN_PARENT)
            marginStart = PADDING_16
            marginEnd = PADDING_16
        }
    }

    /** 标题栏右边布局参数 */
    private val mRightLayoutParams by lazy {
        LayoutParams(WRAP_CONTENT, MATCH_PARENT).apply {
            addRule(ALIGN_PARENT_END)
            addRule(CENTER_VERTICAL)
        }
    }

    /** 是否撑起状态栏, true 时,标题栏浸入状态栏 */
    var fillStatusBar = true
        set(value) {
            field = value
            mStatusBar.isVisible = value
        }

    /** 标题栏左边 TextView，对应 leftType = textView */
    val leftTextView by lazy {
        TextView(context).apply {
            id = View.generateViewId()
            gravity = Gravity.START or Gravity.CENTER_VERTICAL
            isSingleLine = true
            setPadding(PADDING_16, 0, PADDING_16, 0)
            setOnClickListener(this@CommonToolbar)
        }
    }

    /** 标题栏左边 ImageButton，对应 leftType = imageButton */
    val leftImageButton by lazy {
        ImageButton(context).apply {
            id = View.generateViewId()
            setBackgroundColor(Color.TRANSPARENT)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            setPadding(PADDING_16, 0, PADDING_16, 0)
            setOnClickListener(this@CommonToolbar)
        }
    }

    /** 左边自定义布局 */
    var leftCustomView: View? = null
        private set

    /** 标题栏右边 TextView，对应 rightType = textView */
    val rightTextView by lazy {
        TextView(context).apply {
            id = View.generateViewId()
            gravity = Gravity.END or Gravity.CENTER_VERTICAL
            isSingleLine = true
            setPadding(PADDING_16, 0, PADDING_16, 0)
            setOnClickListener(this@CommonToolbar)
        }
    }

    /** 标题栏右边 ImageButton，对应 rightType = imageButton */
    val rightImageButton by lazy {
        ImageButton(context).apply {
            id = View.generateViewId()
            setBackgroundColor(Color.TRANSPARENT)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            setPadding(PADDING_16, 0, PADDING_16, 0)
            setOnClickListener(this@CommonToolbar)
        }
    }

    /** 标题栏右边自定义布局 */
    var rightCustomView: View? = null
        private set

    /** 标题栏中间布局 */
    val centerLayout by lazy {
        LinearLayout(context).apply {
            id = View.generateViewId()
            gravity = Gravity.CENTER
            orientation = LinearLayout.VERTICAL
            setOnClickListener(this@CommonToolbar)
            layoutParams = LayoutParams(WRAP_CONTENT, MATCH_PARENT).apply {
                marginStart = PADDING_16
                marginEnd = PADDING_16
                addRule(CENTER_IN_PARENT)
            }
        }
    }

    /** 标题栏中间 TextView，对应 centerType = textView */
    val centerTextView by lazy {
        TextView(context).apply {
            gravity = Gravity.CENTER
            isSingleLine = true
            layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            if (isInEditMode.not()) {
                maxWidth = (screenWidth * 3 / 5.0).toInt()
            }
        }
    }

    /** 副标题栏文字 */
    val centerSubTextView by lazy {
        TextView(context).apply {
            isGone = true
            gravity = Gravity.CENTER
            isSingleLine = true
            layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        }
    }

    /** 中间进度条,默认隐藏 */
    val progressCenter by lazy {
        ProgressBar(context).apply {
            isGone = true
            indeterminateDrawable =
                ContextCompat.getDrawable(context, R.drawable.common_toolbar_progress_draw)
            val progressWidth = 18f.dp
            layoutParams = LayoutParams(progressWidth, progressWidth).apply {
                addRule(CENTER_VERTICAL)
                addRule(START_OF, centerLayout.id)
            }
        }
    }

    /** 中间搜索框布局，对应 centerType = searchView */
    val centerSearchView by lazy {
        RelativeLayout(context).apply {
            layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT).apply {
                // 设置边距
                topMargin = 7f.dp
                bottomMargin = 7f.dp
                marginStart = PADDING_5
                marginEnd = PADDING_5
                // 根据左边的布局类型来设置边距,布局依赖规则
                when (leftType) {
                    TYPE_LEFT_TEXT_VIEW -> addRule(END_OF, leftTextView.id)
                    TYPE_LEFT_IMAGE_BUTTON -> addRule(END_OF, leftImageButton.id)
                    TYPE_LEFT_CUSTOM_VIEW -> leftCustomView?.let { addRule(END_OF, it.id) }
                    else -> marginStart = PADDING_16
                }
                // 根据右边的布局类型来设置边距,布局依赖规则
                when (rightType) {
                    TYPE_RIGHT_TEXT_VIEW -> addRule(START_OF, rightTextView.id)
                    TYPE_RIGHT_IMAGE_BUTTON -> addRule(START_OF, rightImageButton.id)
                    TYPE_RIGHT_CUSTOM_VIEW -> rightCustomView?.let { addRule(START_OF, it.id) }
                    else -> marginEnd = PADDING_16
                }
            }
        }
    }

    /** 搜索框内部输入框，对应 centerType = searchView */
    val centerSearchEditText by lazy {
        EditText(context).apply {
            setBackgroundColor(Color.TRANSPARENT)
            gravity = Gravity.START or Gravity.CENTER_VERTICAL
            setPadding(PADDING_5, 0, PADDING_5, 0)
            isCursorVisible = false
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
            imeOptions = EditorInfo.IME_ACTION_SEARCH
            addTextChangedListener(centerSearchWatcher)
            setOnFocusChangeListener { _, hasFocus ->
                if (centerSearchRightType == TYPE_CENTER_SEARCH_RIGHT_DELETE) {
                    val input: CharSequence = textString
                    centerSearchRightImageView.isVisible = hasFocus && input.isEmpty().not()
                }
            }
            setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val input: CharSequence = textString
                    onToolbarClickListener?.invoke(v, MotionAction.ACTION_SEARCH_SUBMIT, input)
                }
                false
            }
            setOnClickListener {
                isCursorVisible = true
            }
            layoutParams = LayoutParams(MATCH_PARENT, MATCH_PARENT).apply {
                addRule(END_OF, centerSearchLeftImageView.id)
                addRule(START_OF, centerSearchRightImageView.id)
                addRule(CENTER_VERTICAL)
                marginStart = PADDING_5
                marginEnd = PADDING_5
            }
        }
    }

    /** 搜索框左边图标 ImageView，对应 centerType = searchView */
    val centerSearchLeftImageView by lazy {
        ImageView(context).apply {
            id = View.generateViewId()
            setImageResource(R.drawable.common_toolbar_ic_search)
            val searchIconWidth = 15f.dp
            layoutParams = LayoutParams(searchIconWidth, searchIconWidth).apply {
                addRule(CENTER_VERTICAL)
                addRule(ALIGN_PARENT_START)
                marginStart = PADDING_16
            }
            setOnClickListener(this@CommonToolbar)
        }
    }

    /**
     * 获取搜索框右边图标 ImageView，对应centerType = searchView
     */
    val centerSearchRightImageView by lazy {
        ImageView(context).apply {
            id = View.generateViewId()
            layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
                addRule(CENTER_VERTICAL)
                addRule(ALIGN_PARENT_END)
                marginEnd = PADDING_16
            }
            setOnClickListener(this@CommonToolbar)
        }
    }

    /** 中间自定义布局视图 */
    var centerCustomView: View? = null
        private set

    /** 标题栏背景颜色 */
    var toolbarColor: Int = Color.TRANSPARENT
        set(value) {
            field = value
            mRlMain.setBackgroundColor(value)
        }

    /** 标题栏高度 */
    var toolbarHeight: Int = 44.dp
        set(value) {
            field = value
            mRlMain.layoutParams?.height = if (showBottomLine) {
                value - max(1, 0.4f.dp)
            } else {
                value
            }
        }

    /** 状态栏颜色 */
    var statusBarColor: Int = Color.TRANSPARENT
        set(value) {
            field = value
            mStatusBar.setBackgroundColor(value)
        }

    /** 状态栏图片模式：0-暗色 非0-亮色 */
    var statusBarMode: Int = 0
        set(value) {
            field = value
            if (isInEditMode.not()) {
                val window = window ?: return
                if (value == 0) {
                    StatusBarUtil.setDarkMode(window)
                } else {
                    StatusBarUtil.setLightMode(window)
                }
            }
        }

    /** 是否显示底部分割线 */
    var showBottomLine: Boolean = true
        set(value) {
            field = value
            mBottomLine.isVisible = value
        }

    /** 分割线颜色 */
    var bottomLineColor: Int = Color.parseColor("#EEEEEE")
        set(value) {
            field = value
            mBottomLine.setBackgroundColor(value)
        }

    /** 底部阴影高度 */
    var bottomShadowHeight: Float = 0f
        set(value) {
            field = value
            if (value > 0f) {
                mBottomShadow.layoutParams?.height = value.toInt()
            }
        }

    /** 左边视图类型 */
    var leftType = TYPE_LEFT_NONE
        set(value) {
            field = value
            initLeftViews()
        }

    /** 左边 TextView 文字 */
    var leftText: CharSequence? = null
        set(value) {
            field = value
            leftTextView.text = value
        }

    /** 左边 TextView 颜色 */
    var leftTextColor = 0
        set(value) {
            field = value
            if (value != 0) {
                leftTextView.setTextColor(value)
            } else if (isInEditMode.not()) {
                leftTextView.setTextColor(getColorStateList(R.color.common_toolbar_selector_text_color))
            }
        }

    /** 左边 TextView 文字大小 */
    var leftTextSize = 16f.sp.toFloat()
        set(value) {
            field = value
            leftTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
        }

    /** 左边 TextView 文字字体 */
    var leftTextFontFamily = NO_ID
        set(value) {
            field = value
            if (isInEditMode.not() && value != NO_ID) {
                leftTextView.typeface = getFont(value)
            }
        }

    /** 左边 TextView 文字是否加粗 */
    var leftTextBold = false
        set(value) {
            field = value
            leftTextView.paint.isFakeBoldText = value
        }

    /** 左边 TextView drawableLeft 资源 */
    var leftTextDrawableRes = NO_ID
        @SuppressLint("ObsoleteSdkInt")
        set(value) {
            field = value
            if (isInEditMode.not() && leftTextDrawableRes != NO_ID) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    leftTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(value, 0, 0, 0)
                } else {
                    leftTextView.setCompoundDrawablesWithIntrinsicBounds(value, 0, 0, 0)
                }
            }
        }

    /** 左边 TextView drawablePadding */
    var leftTextDrawablePadding = 5f.dp
        set(value) {
            field = value
            if (leftTextDrawableRes != NO_ID) {
                leftTextView.compoundDrawablePadding = value
            }
        }

    /** 左边图片资源 */
    var leftImageRes = R.drawable.common_toolbar_selector_ic_back
        set(value) {
            field = value
            leftImageButton.setImageResource(value)
        }

    /** 左边图片着色 */
    var leftImageTint: Int? = null
        set(value) {
            field = value
            value?.let { leftImageButton.setTintColor(it) }
        }

    /** 左边自定义视图布局资源 */
    var leftCustomViewRes = NO_ID
        set(value) {
            field = value
            if (value != NO_ID) {
                leftCustomView = mLayoutInflater.inflate(value, mRlMain, false)
                leftCustomView?.apply {
                    if (id == View.NO_ID) {
                        id = View.generateViewId()
                    }
                }
            }
        }

    /** 右边视图类型 */
    var rightType = TYPE_RIGHT_NONE
        set(value) {
            field = value
            initRightViews()
        }

    /** 右边 TextView 文字 */
    var rightText: CharSequence? = null
        set(value) {
            field = value
            rightTextView.text = value
        }

    /** 右边 TextView 颜色 */
    var rightTextColor = 0
        set(value) {
            field = value
            if (value != 0) {
                rightTextView.setTextColor(value)
            } else if (isInEditMode.not()) {
                rightTextView.setTextColor(getColorStateList(R.color.common_toolbar_selector_text_color))
            }
        }

    /** 右边 TextView 文字大小 */
    var rightTextSize = 16f.sp.toFloat()
        set(value) {
            field = value
            rightTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
        }

    /** 右边 TextView 文字字体 */
    var rightTextFontFamily = NO_ID
        set(value) {
            field = value
            if (isInEditMode.not() && value != NO_ID) {
                rightTextView.typeface = ResourcesCompat.getFont(context, value)
            }
        }

    /** 右边 TextView 文字是否加粗 */
    var rightTextBold = false
        set(value) {
            field = value
            rightTextView.paint.isFakeBoldText = rightTextBold
        }

    /** 右边图片资源 */
    var rightImageRes = NO_ID
        set(value) {
            field = value
            if (value != NO_ID) {
                rightImageButton.setImageResource(value)
            }
        }

    /** 右边图片着色 */
    var rightImageTint: Int? = null
        set(value) {
            field = value
            value?.let { rightImageButton.setTintColor(it) }
        }

    /** 右边自定义视图布局资源 */
    var rightCustomViewRes = NO_ID
        set(value) {
            field = value
            if (value != NO_ID) {
                rightCustomView = mLayoutInflater.inflate(value, mRlMain, false)
                rightCustomView?.apply {
                    if (id == View.NO_ID) {
                        id = View.generateViewId()
                    }
                }
            }
        }

    /** 中间视图类型 */
    var centerType = TYPE_CENTER_NONE
        set(value) {
            field = value
            initCenterViews()
        }

    /** 中间 TextView 文字 */
    var centerText: CharSequence? = null
        set(value) {
            field = value
            centerTextView.text = value
        }

    /** 中间 TextView 字体颜色 */
    var centerTextColor = Color.parseColor("#333333")
        set(value) {
            field = value
            centerTextView.setTextColor(value)
        }

    /** 中间 TextView 字体大小 */
    var centerTextSize = 18f.sp.toFloat()
        set(value) {
            field = value
            centerTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
        }

    /** 中间 TextView 文字字体 */
    var centerTextFontFamily = NO_ID
        set(value) {
            field = value
            if (isInEditMode.not() && value != NO_ID) {
                centerTextView.typeface = getFont(value)
            }
        }

    /** 中间 TextView 文字是否加粗 */
    var centerTextBold = true
        set(value) {
            field = value
            centerTextView.paint.isFakeBoldText = value
        }

    /** 中间 TextView 字体是否显示跑马灯效果 */
    var centerTextMarquee = true
        set(value) {
            field = value
            if (value) {
                centerTextView.ellipsize = TextUtils.TruncateAt.MARQUEE
                centerTextView.marqueeRepeatLimit = -1
                centerTextView.requestFocus()
                centerTextView.isSelected = true
            } else {
                centerTextView.ellipsize = TextUtils.TruncateAt.END
            }
        }

    /** 中间 subTextView 文字 */
    var centerSubText: CharSequence? = null
        set(value) {
            field = value
            centerSubTextView.text = value
            if (value.isNullOrEmpty().not()) {
                centerSubTextView.isVisible = true
            }
        }

    /** 中间 subTextView 字体颜色 */
    var centerSubTextColor = Color.parseColor("#666666")
        set(value) {
            field = value
            centerSubTextView.setTextColor(value)
        }

    /** 中间 subTextView 字体大小 */
    var centerSubTextSize = 11f.sp.toFloat()
        set(value) {
            field = value
            centerSubTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
        }

    /** 中间 subTextView 文字字体 */
    var centerSubTextFontFamily = NO_ID
        set(value) {
            field = value
            if (isInEditMode.not() && value != NO_ID) {
                centerSubTextView.typeface = getFont(value)
            }
        }

    /** 中间 subTextView 文字是否加粗 */
    var centerSubTextBold = false
        set(value) {
            field = value
            centerSubTextView.paint.isFakeBoldText = value
        }

    /** 搜索输入框是否可输入 */
    var centerSearchEditable = true
        set(value) {
            field = value
            if (value.not()) {
                centerSearchEditText.apply {
                    isCursorVisible = false
                    clearFocus()
                    isFocusable = false
                    setOnClickListener(this@CommonToolbar)
                }
            }
        }

    /** 搜索输入框提示文字 */
    var centerSearchHintText: CharSequence? = null
        set(value) {
            field = value
            if (field.isNullOrBlank()) {
                field = context.getString(R.string.common_toolbar_search_hint)
            }
            centerSearchEditText.hint = field
        }

    /** 搜索输入框提示文字颜色 */
    var centerSearchHintTextColor = Color.parseColor("#999999")
        set(value) {
            field = value
            centerSearchEditText.setHintTextColor(value)
        }

    /** 搜索输入框文字颜色 */
    var centerSearchTextColor = Color.parseColor("#666666")
        set(value) {
            field = value
            centerSearchEditText.setTextColor(value)
        }

    /** 搜索输入框文字大小 */
    var centerSearchTextSize = 14f.sp.toFloat()
        set(value) {
            field = value
            centerSearchEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
        }

    /** 搜索输入框左边图标着色 */
    var centerSearchLeftIconTint: Int? = null
        set(value) {
            field = value
            value?.let { centerSearchLeftImageView.setTintColor(it) }
        }

    /** 搜索输入框背景图片 */
    var centerSearchBgRes = R.drawable.common_toolbar_shape_search_bg_default
        set(value) {
            field = value
            centerSearchView.setBackgroundResource(value)
        }

    /** 搜索框背景颜色 */
    var centerSearchBgColor = 0
        set(value) {
            field = value
            if (value != 0) {
                centerSearchView.background = GradientDrawable().apply {
                    setColor(value)
                }
            }
        }

    /** 搜索框背景圆角半径 */
    var centerSearchBgCornerRadius = 0f
        set(value) {
            field = value
            if (centerSearchView.background is GradientDrawable) {
                centerSearchView.background = (centerSearchView.background as GradientDrawable).apply {
                    cornerRadius = value
                }
            }
        }

    /** 搜索输入框右边按钮类型 (0: voice; 1: delete) */
    var centerSearchRightType = TYPE_CENTER_SEARCH_RIGHT_VOICE

    /** 搜索输入框右边声音图标图片资源 */
    var centerSearchRightVoiceRes = R.drawable.common_toolbar_ic_voice
        set(value) {
            field = value
            if (centerSearchRightType == TYPE_CENTER_SEARCH_RIGHT_VOICE) {
                centerSearchRightImageView.setImageResource(value)
            }
        }

    /** 搜索输入框右边删除图标图片资源 */
    var centerSearchRightDeleteRes = R.drawable.common_toolbar_ic_delete
        set(value) {
            field = value
            if (centerSearchRightType == TYPE_CENTER_SEARCH_RIGHT_DELETE) {
                centerSearchRightImageView.setImageResource(value)
                centerSearchRightImageView.isGone = true
            }
        }

    /** 搜索输入框右边声音图标着色 */
    var centerSearchRightVoiceTint: Int? = null
        set(value) {
            field = value
            if (centerSearchRightType == TYPE_CENTER_SEARCH_RIGHT_VOICE) {
                value?.let { centerSearchRightImageView.setTintColor(it) }
            }
        }

    /** 搜索输入框右边删除图标着色 */
    var centerSearchRightDeleteTint: Int? = null
        set(value) {
            field = value
            if (centerSearchRightType == TYPE_CENTER_SEARCH_RIGHT_DELETE) {
                value?.let { centerSearchRightImageView.setTintColor(it) }
            }
        }

    /** 中间自定义布局资源 */
    var centerCustomViewRes = NO_ID
        set(value) {
            field = value
            if (value != NO_ID) {
                centerCustomView = mLayoutInflater.inflate(value, mRlMain, false)
                centerCustomView?.apply {
                    if (id == View.NO_ID) {
                        id = View.generateViewId()
                    }
                }
            }
        }
    private var PADDING_5 = 5f.dp
    private var PADDING_16 = 16f.dp

    /**
     * 点击事件
     * @param v
     * @param action [MotionAction], 如 ACTION_LEFT_TEXT
     * @param extra  中间为搜索框时,如果可输入,点击键盘的搜索按钮,会返回输入关键词
     */
    var onToolbarClickListener: ((v: View, action: Int, extra: CharSequence?) -> Unit)? = null

    /**
     * 标题栏中间布局双击事件监听
     */
    var onToolbarCenterDoubleClickListener: ((v: View) -> Unit)? = null

    @SuppressLint("ObsoleteSdkInt")
    private fun loadAttributes() {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommonToolbar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // notice: 未引入沉浸式标题栏之前,隐藏标题栏撑起布局
            fillStatusBar = typedArray.getBoolean(R.styleable.CommonToolbar_fillStatusBar, fillStatusBar)
        }
        toolbarColor = typedArray.getColor(R.styleable.CommonToolbar_toolbarColor, toolbarColor)
        toolbarHeight = typedArray.getDimensionPixelSize(R.styleable.CommonToolbar_toolbarHeight, toolbarHeight)
        statusBarColor = typedArray.getColor(R.styleable.CommonToolbar_statusBarColor, statusBarColor)
        statusBarMode = typedArray.getInt(R.styleable.CommonToolbar_statusBarMode, statusBarMode)
        showBottomLine = typedArray.getBoolean(R.styleable.CommonToolbar_showBottomLine, showBottomLine)
        bottomLineColor = typedArray.getColor(R.styleable.CommonToolbar_bottomLineColor, bottomLineColor)
        bottomShadowHeight = typedArray.getDimension(R.styleable.CommonToolbar_bottomShadowHeight, bottomShadowHeight)
        leftType = typedArray.getInt(R.styleable.CommonToolbar_leftType, leftType)
        rightType = typedArray.getInt(R.styleable.CommonToolbar_rightType, rightType)
        centerType = typedArray.getInt(R.styleable.CommonToolbar_centerType, centerType)
        typedArray.recycle()
    }

    /**
     * 初始化全局视图
     */
    private fun initGlobalViews() {
        val globalParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        layoutParams = globalParams
        val transparentStatusBar = StatusBarUtil.supportTransparentStatusBar()
        // 构建标题栏
        if (fillStatusBar && transparentStatusBar) {
            addView(mStatusBar)
        }
        // 构建主视图
        addView(mRlMain)
        // 构建底部分割线视图
        if (showBottomLine) {
            addView(mBottomLine)
        }
        if (bottomShadowHeight > 0f) {
            addView(mBottomShadow)
        }
    }

    /**
     * 初始化主视图左边部分
     * -- add: adaptive RTL
     */
    @SuppressLint("ObsoleteSdkInt")
    private fun initLeftViews() {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommonToolbar)
        when (leftType) {
            TYPE_LEFT_IMAGE_BUTTON -> {
                // 初始化左边 ImageButton
                initLeftImageButton(typedArray)
            }
            TYPE_LEFT_TEXT_VIEW -> {
                // 初始化左边 TextView
                initLeftTextView(typedArray)
            }
            TYPE_LEFT_CUSTOM_VIEW -> {
                // 初始化左边自定义布局
                initLeftCustomView(typedArray)
            }
        }
        typedArray.recycle()
    }

    /**
     * 初始化左边 ImageButton
     */
    private fun initLeftImageButton(typedArray: TypedArray) {
        leftImageRes =
            typedArray.getResourceId(R.styleable.CommonToolbar_leftImageRes, leftImageRes)
        leftImageTint = runCatching { typedArray.getColorOrThrow(R.styleable.CommonToolbar_leftImageTint) }.getOrNull()
        mRlMain.addView(leftImageButton, mLeftLayoutParams)
    }

    /**
     * 初始化左边 TextView
     */
    private fun initLeftTextView(typedArray: TypedArray) {
        leftText = typedArray.getString(R.styleable.CommonToolbar_leftText)
        leftTextColor = typedArray.getColor(R.styleable.CommonToolbar_leftTextColor, leftTextColor)
        leftTextSize = typedArray.getDimension(R.styleable.CommonToolbar_leftTextSize, leftTextSize)
        leftTextFontFamily =
            typedArray.getResourceId(R.styleable.CommonToolbar_leftTextFontFamily, leftTextFontFamily)
        leftTextBold = typedArray.getBoolean(R.styleable.CommonToolbar_leftTextBold, leftTextBold)
        leftTextDrawableRes =
            typedArray.getResourceId(R.styleable.CommonToolbar_leftTextDrawableRes, leftTextDrawableRes)
        leftTextDrawablePadding =
            typedArray.getDimensionPixelSize(R.styleable.CommonToolbar_leftTextDrawablePadding, leftTextDrawablePadding)
        mRlMain.addView(leftTextView, mLeftLayoutParams)
    }

    /**
     * 初始化左边自定义布局
     */
    private fun initLeftCustomView(typedArray: TypedArray) {
        leftCustomViewRes =
            typedArray.getResourceId(R.styleable.CommonToolbar_leftCustomView, leftCustomViewRes)
        leftCustomView?.let {
            mRlMain.addView(it, mLeftLayoutParams)
        }
    }

    /**
     * 初始化主视图中间部分
     */
    private fun initCenterViews() {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommonToolbar)
        when (centerType) {
            TYPE_CENTER_TEXT_VIEW -> {
                // 初始化中间标题 TextView
                initCenterTextView(typedArray)
            }
            TYPE_CENTER_SEARCH_VIEW -> {
                // 初始化中间通用搜索框
                initCenterSearchView(typedArray)
            }
            TYPE_CENTER_CUSTOM_VIEW -> {
                // 初始化中间自定义布局
                initCenterCustomView(typedArray)
            }
        }
        typedArray.recycle()
    }

    /**
     * 初始化中间标题 TextView
     */
    private fun initCenterTextView(typedArray: TypedArray) {
        centerText = typedArray.getString(R.styleable.CommonToolbar_centerText)
        // 如果当前上下文对象是 Activity，就获取 Activity 的标题
        if (centerText.isNullOrBlank() && context is Activity) {
            // 获取清单文件中的 android:label 属性值
            val label = (context as Activity).title
            if (label.isNullOrBlank().not()) {
                try {
                    val packageManager = context.packageManager
                    val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
                    // 如果当前 Activity 没有设置 android:label 属性，则默认会返回 APP 名称，则需要过滤掉
                    if (label.toString() != packageInfo.applicationInfo.loadLabel(packageManager).toString()) {
                        // 设置标题
                        centerText = label.toString()
                    }
                } catch (ignored: PackageManager.NameNotFoundException) {
                }
            }
        }
        centerTextColor = typedArray.getColor(R.styleable.CommonToolbar_centerTextColor, centerTextColor)
        centerTextSize = typedArray.getDimension(R.styleable.CommonToolbar_centerTextSize, centerTextSize)
        centerTextFontFamily =
            typedArray.getResourceId(R.styleable.CommonToolbar_centerTextFontFamily, centerTextFontFamily)
        centerTextBold = typedArray.getBoolean(R.styleable.CommonToolbar_centerTextBold, centerTextBold)
        centerTextMarquee =
            typedArray.getBoolean(R.styleable.CommonToolbar_centerTextMarquee, centerTextMarquee)
        centerSubText = typedArray.getString(R.styleable.CommonToolbar_centerSubText)
        centerSubTextColor =
            typedArray.getColor(R.styleable.CommonToolbar_centerSubTextColor, centerSubTextColor)
        centerSubTextSize =
            typedArray.getDimension(R.styleable.CommonToolbar_centerSubTextSize, centerSubTextSize)
        centerSubTextFontFamily =
            typedArray.getResourceId(R.styleable.CommonToolbar_centerSubTextFontFamily, centerSubTextFontFamily)
        centerSubTextBold =
            typedArray.getBoolean(R.styleable.CommonToolbar_centerSubTextBold, centerSubTextBold)

        centerLayout.addView(centerTextView)
        // 初始化副标题栏
        centerLayout.addView(centerSubTextView)
        // 初始化中间子布局
        mRlMain.addView(centerLayout)
        // 初始化进度条, 显示于标题栏左边
        mRlMain.addView(progressCenter)
    }

    /**
     * 初始化中间通用搜索框
     */
    private fun initCenterSearchView(typedArray: TypedArray) {
        centerSearchEditable =
            typedArray.getBoolean(R.styleable.CommonToolbar_centerSearchEditable, centerSearchEditable)
        centerSearchHintText = typedArray.getString(R.styleable.CommonToolbar_centerSearchHintText)
        centerSearchHintTextColor =
            typedArray.getColor(R.styleable.CommonToolbar_centerSearchHintTextColor, centerSearchHintTextColor)
        centerSearchTextColor =
            typedArray.getColor(R.styleable.CommonToolbar_centerSearchTextColor, centerSearchTextColor)
        centerSearchTextSize =
            typedArray.getDimension(R.styleable.CommonToolbar_centerSearchTextSize, centerSearchTextSize)
        centerSearchBgRes =
            typedArray.getResourceId(R.styleable.CommonToolbar_centerSearchBgRes, centerSearchBgRes)
        centerSearchBgColor =
            typedArray.getColor(R.styleable.CommonToolbar_centerSearchBgColor, centerSearchBgColor)
        centerSearchBgCornerRadius =
            typedArray.getDimension(R.styleable.CommonToolbar_centerSearchBgCornerRadius, centerSearchBgCornerRadius)
        centerSearchLeftIconTint =
            runCatching { typedArray.getColorOrThrow(R.styleable.CommonToolbar_centerSearchLeftIconTint) }.getOrNull()
        centerSearchRightType =
            typedArray.getInt(R.styleable.CommonToolbar_centerSearchRightType, centerSearchRightType)
        centerSearchRightVoiceRes =
            typedArray.getResourceId(R.styleable.CommonToolbar_centerSearchRightVoiceRes, centerSearchRightVoiceRes)
        centerSearchRightDeleteRes =
            typedArray.getResourceId(R.styleable.CommonToolbar_centerSearchRightDeleteRes, centerSearchRightDeleteRes)
        centerSearchRightVoiceTint =
            runCatching { typedArray.getColorOrThrow(R.styleable.CommonToolbar_centerSearchRightVoiceTint) }.getOrNull()
        centerSearchRightDeleteTint =
            runCatching { typedArray.getColorOrThrow(R.styleable.CommonToolbar_centerSearchRightDeleteTint) }.getOrNull()

        mRlMain.addView(centerSearchView)
        // 初始化搜索框搜索 ImageView
        centerSearchView.addView(centerSearchLeftImageView)
        // 初始化搜索框语音 ImageView
        centerSearchView.addView(centerSearchRightImageView)
        // 初始化文字输入框
        centerSearchView.addView(centerSearchEditText)
    }

    /**
     * 初始化中间自定义布局
     */
    private fun initCenterCustomView(typedArray: TypedArray) {
        centerCustomViewRes =
            typedArray.getResourceId(R.styleable.CommonToolbar_centerCustomView, centerCustomViewRes)
        centerCustomView?.let {
            mRlMain.addView(it, mCenterCustomLayoutParams)
        }
    }

    /**
     * 初始化主视图右边部分
     * -- add: adaptive RTL
     */
    private fun initRightViews() {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommonToolbar)
        when (rightType) {
            TYPE_RIGHT_TEXT_VIEW -> {
                // 初始化右边 TextView
                initRightTextView(typedArray)
            }
            TYPE_RIGHT_IMAGE_BUTTON -> {
                // 初始化右边 ImageButton
                initRightImageButton(typedArray)
            }
            TYPE_RIGHT_CUSTOM_VIEW -> {
                // 初始化右边自定义布局
                initRightCustomView(typedArray)
            }
        }
        typedArray.recycle()
    }

    private fun initRightTextView(typedArray: TypedArray) {
        rightText = typedArray.getString(R.styleable.CommonToolbar_rightText)
        rightTextColor = typedArray.getColor(R.styleable.CommonToolbar_rightTextColor, rightTextColor)
        rightTextSize = typedArray.getDimension(R.styleable.CommonToolbar_rightTextSize, rightTextSize)
        rightTextFontFamily =
            typedArray.getResourceId(R.styleable.CommonToolbar_rightTextFontFamily, rightTextFontFamily)
        rightTextBold = typedArray.getBoolean(R.styleable.CommonToolbar_rightTextBold, rightTextBold)
        mRlMain.addView(rightTextView, mRightLayoutParams)
    }

    /**
     * 初始化右边 ImageButton
     */
    private fun initRightImageButton(typedArray: TypedArray) {
        rightImageRes =
            typedArray.getResourceId(R.styleable.CommonToolbar_rightImageRes, rightImageRes)
        rightImageTint =
            runCatching { typedArray.getColorOrThrow(R.styleable.CommonToolbar_rightImageTint) }.getOrNull()
        mRlMain.addView(rightImageButton, mRightLayoutParams)
    }

    /**
     * 初始化右边自定义布局
     */
    private fun initRightCustomView(typedArray: TypedArray) {
        rightCustomViewRes =
            typedArray.getResourceId(R.styleable.CommonToolbar_rightCustomView, rightCustomViewRes)
        rightCustomView?.let {
            mRlMain.addView(it, mRightLayoutParams)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isInEditMode.not()) {
            setUpImmersionToolbar()
        }
    }

    private fun setUpImmersionToolbar() {
        val window = window ?: return
        // 设置状态栏背景透明
        StatusBarUtil.transparentStatusBar(window)
        // 设置图标主题
        if (statusBarMode == 0) {
            StatusBarUtil.setDarkMode(window)
        } else {
            StatusBarUtil.setLightMode(window)
        }
    }

    private val window: Window?
        get() {
            val activity: Activity = when (val context = context) {
                is Activity -> {
                    context
                }
                else -> {
                    (context as ContextWrapper).baseContext as Activity
                }
            }
            return activity.window
        }

    private val centerSearchWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence,
            start: Int,
            count: Int,
            after: Int,
        ) {
        }

        override fun onTextChanged(
            s: CharSequence,
            start: Int,
            before: Int,
            count: Int,
        ) {
        }

        override fun afterTextChanged(s: Editable) {
            if (centerSearchRightType == TYPE_CENTER_SEARCH_RIGHT_VOICE) {
                if (s.isEmpty()) {
                    centerSearchRightImageView.setImageResource(centerSearchRightVoiceRes)
                    centerSearchRightVoiceTint?.let { centerSearchRightImageView.setTintColor(it) }
                } else {
                    centerSearchRightImageView.setImageResource(centerSearchRightDeleteRes)
                    centerSearchRightDeleteTint?.let { centerSearchRightImageView.setTintColor(it) }
                }
            } else {
                centerSearchRightImageView.isGone = s.isEmpty()
            }
        }
    }

    /**
     * 双击事件中，上次被点击时间
     */
    private var lastClickMillis: Long = 0

    override fun onClick(v: View) {
        when (v) {
            centerSearchRightImageView -> {
                centerSearchEditText.setText("")
            }
        }
        onToolbarClickListener?.apply {
            when (v) {
                centerLayout -> {
                    onToolbarCenterDoubleClickListener?.let {
                        val currentClickMillis = System.currentTimeMillis()
                        if (currentClickMillis - lastClickMillis < 500) {
                            it.invoke(v)
                        }
                        lastClickMillis = currentClickMillis
                    } ?: invoke(v, MotionAction.ACTION_CENTER_LAYOUT, null)
                }
                leftTextView -> {
                    invoke(v, MotionAction.ACTION_LEFT_TEXT, null)
                }
                leftImageButton -> {
                    invoke(v, MotionAction.ACTION_LEFT_BUTTON, null)
                }
                rightTextView -> {
                    invoke(v, MotionAction.ACTION_RIGHT_TEXT, null)
                }
                rightImageButton -> {
                    invoke(v, MotionAction.ACTION_RIGHT_BUTTON, null)
                }
                centerSearchEditText, centerSearchLeftImageView -> {
                    invoke(v, MotionAction.ACTION_SEARCH, null)
                }
                centerSearchRightImageView -> {
                    centerSearchEditText.editableText?.clear()
                    if (centerSearchRightType == TYPE_CENTER_SEARCH_RIGHT_VOICE) {
                        // 语音按钮被点击
                        invoke(v, MotionAction.ACTION_SEARCH_VOICE, null)
                    } else {
                        // 删除按钮被点击
                        invoke(v, MotionAction.ACTION_SEARCH_DELETE, null)
                    }
                }
                centerTextView -> {

                }
            }
        }
    }

    /**
     * 切换状态栏模式
     */
    fun toggleStatusBarMode() {
        val window = window ?: return
        StatusBarUtil.transparentStatusBar(window)
        ((if (statusBarMode == 0) {
            1
        } else {
            0
        })).also { statusBarMode = it }
    }

    /**
     * @param leftView
     */
    fun setLeftView(leftView: View) {
        if (leftView.id == View.NO_ID) {
            leftView.id = View.generateViewId()
        }
        val leftInnerParams = LayoutParams(WRAP_CONTENT, MATCH_PARENT)
        leftInnerParams.addRule(ALIGN_PARENT_START)
        leftInnerParams.addRule(CENTER_VERTICAL)
        mRlMain.addView(leftView, leftInnerParams)
    }

    /**
     * @param centerView
     */
    fun setCenterView(centerView: View) {
        if (centerView.id == View.NO_ID) {
            centerView.id = View.generateViewId()
        }
        val centerInnerParams = LayoutParams(WRAP_CONTENT, MATCH_PARENT)
        centerInnerParams.addRule(CENTER_IN_PARENT)
        centerInnerParams.addRule(CENTER_VERTICAL)
        mRlMain.addView(centerView, centerInnerParams)
    }

    /**
     * @param rightView
     */
    fun setRightView(rightView: View) {
        if (rightView.id == View.NO_ID) {
            rightView.id = View.generateViewId()
        }
        val rightInnerParams = LayoutParams(WRAP_CONTENT, MATCH_PARENT)
        rightInnerParams.addRule(ALIGN_PARENT_END)
        rightInnerParams.addRule(CENTER_VERTICAL)
        mRlMain.addView(rightView, rightInnerParams)
    }

    /**
     * 显示中间进度条
     */
    fun showCenterProgress() {
        progressCenter.isVisible = true
    }

    /**
     * 隐藏中间进度条
     */
    fun dismissCenterProgress() {
        progressCenter.isGone = true
    }

    /**
     * 显示或隐藏输入法,centerType="searchView"模式下有效
     */
    fun showSoftInputKeyboard(show: Boolean) {
        if (centerType == TYPE_CENTER_SEARCH_VIEW) {
            if (centerSearchEditable && show) {
                centerSearchEditText.setFocus(true)
                centerSearchEditText.showSoftInput()
            } else {
                centerSearchEditText.hideSoftInput()
            }
        }
    }

    /**
     * 获取 SearchView 输入结果
     */
    val searchKey: CharSequence
        get() = centerSearchEditText.textString

    @Target(
        AnnotationTarget.FIELD,
        AnnotationTarget.ANNOTATION_CLASS,
        AnnotationTarget.CLASS,
        AnnotationTarget.VALUE_PARAMETER
    )
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class MotionAction {
        companion object {
            /** 左边TextView被点击 */
            var ACTION_LEFT_TEXT = 1

            /** 左边ImageBtn被点击 */
            var ACTION_LEFT_BUTTON = 2

            /** 右边TextView被点击 */
            var ACTION_RIGHT_TEXT = 3

            /** 右边ImageBtn被点击 */
            var ACTION_RIGHT_BUTTON = 4

            /** 搜索框被点击,搜索框不可输入的状态下会被触发 */
            var ACTION_SEARCH = 5

            /** 搜索框输入状态下,键盘提交触发 */
            var ACTION_SEARCH_SUBMIT = 6

            /** 语音按钮被点击 */
            var ACTION_SEARCH_VOICE = 7

            /** 搜索删除按钮被点击 */
            var ACTION_SEARCH_DELETE = 8

            /** 中间区域点击 */
            var ACTION_CENTER_LAYOUT = 9
        }
    }

    companion object {
        private const val MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT
        private const val WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT
        const val TYPE_LEFT_NONE = 0
        const val TYPE_LEFT_TEXT_VIEW = 1
        const val TYPE_LEFT_IMAGE_BUTTON = 2
        const val TYPE_LEFT_CUSTOM_VIEW = 3
        const val TYPE_RIGHT_NONE = 0
        const val TYPE_RIGHT_TEXT_VIEW = 1
        const val TYPE_RIGHT_IMAGE_BUTTON = 2
        const val TYPE_RIGHT_CUSTOM_VIEW = 3
        const val TYPE_CENTER_NONE = 0
        const val TYPE_CENTER_TEXT_VIEW = 1
        const val TYPE_CENTER_SEARCH_VIEW = 2
        const val TYPE_CENTER_CUSTOM_VIEW = 3
        const val TYPE_CENTER_SEARCH_RIGHT_VOICE = 0
        const val TYPE_CENTER_SEARCH_RIGHT_DELETE = 1
    }

    init {
        loadAttributes()
        initGlobalViews()
    }
}
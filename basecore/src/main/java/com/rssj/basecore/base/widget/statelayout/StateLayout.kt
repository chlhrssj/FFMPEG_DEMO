package com.rssj.basecore.base.widget.statelayout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import com.rssj.basecore.base.ext.click
import com.rssj.basecore.R
import com.rssj.basecore.base.widget.statelayout.*


class StateLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    //showContentView layout
    private var contentLayout: View? = null
    //showLoading layout
    private var loadingLayout: View? = null
    //error layout
    private var errorLayout: View? = null
    //empty layout
    private var emptyLayout: View? = null

    private var state: State = State.CONTENT

    @LayoutRes
    private var loadingLayoutRes: Int = R.layout.layout_state_loading
    @LayoutRes
    private var errorLayoutRes: Int = R.layout.layout_state_error
    @LayoutRes
    private var emptyLayoutRes: Int = R.layout.layout_state_empty

    private var loadingAnimation: Animation? = null
    private var loadingWithContentAnimation: Animation? = null

    //显示内容加载为空时的点击事件
    lateinit var onEmptyClick: (View) -> Unit

    //加载内容出错时的点击事件
    lateinit var onErrorClick: (View) -> Unit


    init {
        if (isInEditMode) {
            state = State.CONTENT
        }

        context.theme.obtainStyledAttributes(attrs, R.styleable.StateLayout, 0, 0)
            .apply {
                try {
                    state =
                        State.values()[getInteger(
                            R.styleable.StateLayout_sl_state,
                            State.NONE.ordinal
                        )]
                    loadingLayoutRes = getResourceId(
                        R.styleable.StateLayout_sl_loadingLayout,
                        R.layout.layout_state_loading
                    )
                    errorLayoutRes = getResourceId(
                        R.styleable.StateLayout_sl_infoLayout,
                        R.layout.layout_state_error
                    )
                    emptyLayoutRes = getResourceId(
                        R.styleable.StateLayout_sl_loadingWithContentLayout,
                        R.layout.layout_state_empty
                    )

                    getResourceId(R.styleable.StateLayout_sl_loadingAnimation, 0).notZero {
                        loadingAnimation = AnimationUtils.loadAnimation(context, it)
                    }
                    getResourceId(
                        R.styleable.StateLayout_sl_loadingWithContentAnimation,
                        0
                    ).notZero {
                        loadingWithContentAnimation = AnimationUtils.loadAnimation(context, it)
                    }
                } finally {
                    recycle()
                }
            }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        setupContentState()
        setupLoadingState()
        setupErrorState()
        setupEmptyContentState()

        updateWithState()
        checkChildCount()
    }

    /**
     * 获取当前加载状态
     */
    fun getState(): State {
        return state
    }

    //初始设置content不显示
    private fun setupContentState() {
        contentLayout = getChildAt(0)
        contentLayout?.visibility = View.INVISIBLE
        contentLayout?.alpha = 0f
    }

    private fun setupLoadingState() {
        loadingLayout = inflate(loadingLayoutRes)
        loadingLayout?.visibility = View.INVISIBLE
        loadingLayout?.alpha = 0f
        addView(loadingLayout)
    }

    private fun setupErrorState() {
        errorLayout = inflate(errorLayoutRes)
        errorLayout?.visibility = View.INVISIBLE
        errorLayout?.alpha = 0f
        errorLayout?.click {
            if (::onErrorClick.isInitialized) {
                showLoading()
                onErrorClick(it)
            }

        }
        addView(errorLayout)
    }

    private fun setupEmptyContentState() {
        emptyLayout = inflate(emptyLayoutRes)
        emptyLayout?.visibility = View.INVISIBLE
        emptyLayout?.alpha = 0f
        emptyLayout?.click {
            if (::onEmptyClick.isInitialized) {
                onEmptyClick(it)
            }

        }
        addView(emptyLayout)
    }

    private fun updateWithState() {
        when (state) {
            State.LOADING -> showLoading()
            State.CONTENT -> showContentView()
            State.INFO, State.ERROR, State.EMPTY -> showErrorView()
            State.EMPTY_CONTENT -> showEmpty()
            State.NONE -> hideAll()
        }
    }

    private fun checkChildCount() {
        if (childCount > 4 || childCount == 0) {
            throwChildCountException()
        }
    }

    private fun hideAll() {
        updateLoadingVisibility(View.INVISIBLE)
        contentLayout.gone()
        errorLayout.gone()
        updateEmptyContentVisibility(View.INVISIBLE)
    }

    private fun updateLoadingVisibility(visibility: Int) =
        when (visibility) {
            View.VISIBLE -> {
                loadingLayout.visible {}
            }
            else -> {
                loadingLayout.gone { }
            }
        }

    private fun updateEmptyContentVisibility(visibility: Int) =
        when (visibility) {
            View.VISIBLE -> emptyLayout.visible { }
            else -> emptyLayout.gone {}
        }

    private fun throwChildCountException(): Nothing =
        throw IllegalStateException("StateLayout can host only one direct child")

    /**
     * 设置初始化，刚加载完成时的状态
     */
    fun initialState(state: State) {
        this.state = state
    }

    /**
     * 设置加载时的显示的message
     */
    fun loadingMessage(message: String): StateLayout {
        loadingLayout.findView<TextView>(R.id.textView_state_layout_loading_message) {
            text = message
            visibility = View.VISIBLE
        }
        return showLoading()
    }

    fun loadingAnimation(animation: Animation): StateLayout {
        loadingAnimation = animation
        return showLoading()
    }

    /**
     * 展示loading状态
     * @param color 提示文字的颜色
     */
    fun showLoading(@ColorInt color: Int? = null): StateLayout {
        state = State.LOADING
        updateLoadingVisibility(View.VISIBLE)
        contentLayout.gone()
        errorLayout.gone()
        updateEmptyContentVisibility(View.INVISIBLE)
        color?.let {
            loadingLayout?.findView<TextView>(R.id.textView_state_layout_loading_message) {
                this.setTextColor(color)
            }
        }

        return this
    }

    fun showLoading(@LayoutRes layoutId: Int) {
        this.loadingLayoutRes = layoutId
        removeView(loadingLayout)
        setupLoadingState()
        showState(provideLoadingStateInfo())
    }

    /**
     * 展示content
     */
    fun showContentView(): StateLayout {
        state = State.CONTENT
        updateLoadingVisibility(View.INVISIBLE)
        contentLayout.visible()
        errorLayout.gone()
        updateEmptyContentVisibility(View.INVISIBLE)
        return this
    }

    /**
     * 设置加载错误时的Img
     */
    fun errorImage(imageRes: Int): StateLayout {
        errorLayout.findView<ImageView>(R.id.imageView_state_layout_info) {
            setImageResource(imageRes)
            visibility = View.VISIBLE
        }
        return showErrorView()
    }

    /**
     * 设置加载error时的Message
     */
    fun errorMessage(message: String): StateLayout {
        errorLayout.findView<TextView>(R.id.textView_state_layout_info_message) {
            text = message
            visibility = View.VISIBLE
        }
        return showErrorView()
    }


    fun showErrorView(@ColorInt color: Int? = null): StateLayout {
        state = State.INFO
        updateLoadingVisibility(View.INVISIBLE)
        contentLayout.gone()
        errorLayout.visible()
        updateEmptyContentVisibility(View.INVISIBLE)
        color?.let {
            errorLayout?.findView<TextView>(R.id.textView_state_layout_info_message) {
                this.setTextColor(color)
            }
        }
        return this
    }

    //test
//    fun showErrorView(): StateLayout {
//        state = INFO
//        updateLoadingVisibility(View.INVISIBLE)
//        contentLayout.visible()
//        errorLayout.gone()
//        updateEmptyContentVisibility(View.INVISIBLE)
//        return this
//    }

    fun showErrorView(@LayoutRes layoutId: Int) {
        this.errorLayoutRes = layoutId
        removeView(errorLayout)
        setupErrorState()
        showState(provideInfoStateInfo())
    }

    fun showErrorView(view: View) {
        removeView(errorLayout)
        errorLayout = view
        errorLayout?.visibility = View.INVISIBLE
        errorLayout?.alpha = 0f
        addView(errorLayout)
        showErrorView()
    }

    fun loadingWithContentAnimation(animation: Animation): StateLayout {
        loadingWithContentAnimation = animation
        return showEmpty()
    }

    fun showEmpty(@ColorInt color: Int? = null): StateLayout {
        state = State.EMPTY_CONTENT
        updateLoadingVisibility(View.INVISIBLE)
        contentLayout.gone()
        errorLayout.gone()
        updateEmptyContentVisibility(View.VISIBLE)
        color?.let {
            emptyLayout?.findView<TextView>(R.id.textView_state_layout_empty_message) {
                this.setTextColor(color)
            }
        }
        return this
    }

    fun showEmpty(@LayoutRes layoutId: Int) {
        this.emptyLayoutRes = layoutId
        removeView(emptyLayout)
        setupEmptyContentState()
        showState(provideLoadingWithContentStateInfo())
    }

    fun showEmpty(view: View) {
        removeView(emptyLayout)
        emptyLayout = view
        emptyLayout?.visibility = View.INVISIBLE
        emptyLayout?.alpha = 0f
        addView(emptyLayout)
        showEmpty()
    }

    fun showLoading(stateInfo: StateInfo?) = showState(stateInfo)

    fun showContentView(stateInfo: StateInfo?) = showState(stateInfo)

    fun showInfo(stateInfo: StateInfo?) = showState(stateInfo)

    fun showLoadingWithContent(stateInfo: StateInfo?) = showState(stateInfo)

    fun showErrorView(stateInfo: StateInfo?) = showState(stateInfo)

    fun showEmpty(stateInfo: StateInfo?) = showState(stateInfo)

    fun showState(stateInfo: StateInfo?) {
        loadingAnimation = stateInfo?.loadingAnimation
        loadingWithContentAnimation = stateInfo?.loadingWithContentAnimation
        when (stateInfo?.state) {
            State.LOADING -> showLoading()
            State.CONTENT -> showContentView()
            State.EMPTY_CONTENT -> showEmpty()
            State.INFO, State.ERROR, State.EMPTY -> {
                stateInfo.infoImage?.let { errorImage(it) }
                stateInfo.infoMessage?.let { errorMessage(it) }
            }
            null, State.NONE -> hideAll()
        }
    }

    companion object {
        @JvmStatic
        fun provideLoadingStateInfo() = StateInfo(state = State.LOADING)

        @JvmStatic
        fun provideContentStateInfo() = StateInfo(state = State.CONTENT)

        @JvmStatic
        fun provideErrorStateInfo() = StateInfo(state = State.ERROR)

        @JvmStatic
        fun provideLoadingWithContentStateInfo() = StateInfo(state = State.EMPTY_CONTENT)

        @JvmStatic
        fun provideInfoStateInfo() = StateInfo(state = State.INFO)

        @JvmStatic
        fun provideEmptyStateInfo() = StateInfo(state = State.EMPTY)

        @JvmStatic
        fun provideNoneStateInfo() = StateInfo(state = State.NONE)
    }

    interface OnStateLayoutListener {
        fun onStateLayoutInfoButtonClick()
    }

    enum class State {
        LOADING, CONTENT, INFO, EMPTY_CONTENT, ERROR, EMPTY, NONE
    }

    data class StateInfo(
        val infoImage: Int? = null,
        val infoMessage: String? = null,
        val state: State = State.INFO,
        val onInfoButtonClick: (() -> Unit)? = null,
        val loadingAnimation: Animation? = null,
        val loadingWithContentAnimation: Animation? = null
    )
}

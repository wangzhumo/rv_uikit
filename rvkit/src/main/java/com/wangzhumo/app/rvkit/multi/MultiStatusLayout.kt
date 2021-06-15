package com.wangzhumo.app.rvkit.multi

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import com.wangzhumo.app.rvkit.R
import com.wangzhumo.app.rvkit.listener.OnLoadMoreListener

/**
 * If you have any questions, you can contact by email {wangzhumoo@gmail.com}
 *
 * @author 王诛魔 2021/6/15  8:58 下午
 *
 * 可以展示多种效果的View
 * empty
 * error
 * loading
 */
class MultiStatusLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    style: Int = 0
) : FrameLayout(context, attributeSet, style) {

    private lateinit var mInflater: LayoutInflater
    private lateinit var mLoadMoreView: LoadMoreView
    private var isLoadMoreShow = true
    @ViewState
    private var mViewState: Int = ViewState.VIEW_LOAD_MORE

    private var mLoadingView: View? = null

    private var mErrorView: View? = null

    private var mEmptyView: View? = null

    private var loadMoreListener: OnLoadMoreListener? = null





    init {
        initView(attributeSet)
    }

    private fun initView(attrs: AttributeSet?) {
        mInflater = LayoutInflater.from(context)
        mLoadMoreView = LoadMoreView(context)
        addView(mLoadMoreView)

        // 如果为空，终止操作
        if (attrs == null) return

        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.MultiStatusLayout)
        val loadingViewResId = a.getResourceId(R.styleable.MultiStatusLayout_loadingView, -1)
        if (loadingViewResId > -1) {
            mLoadingView = mInflater.inflate(loadingViewResId, this, false)
            mLoadingView?.run {
                this@MultiStatusLayout.addView(this,layoutParams)
            }
        }

        val emptyViewResId = a.getResourceId(R.styleable.MultiStatusLayout_emptyView, -1)
        if (emptyViewResId > -1) {
            mEmptyView = mInflater.inflate(emptyViewResId, this, false)
            mEmptyView?.run {
                this@MultiStatusLayout.addView(this,layoutParams)
            }
        }

        val errorViewResId = a.getResourceId(R.styleable.MultiStatusLayout_errorView, -1)
        if (errorViewResId > -1) {
            mErrorView = mInflater.inflate(errorViewResId, this, false)
            mErrorView?.run {
                this@MultiStatusLayout.addView(this,layoutParams)
                setOnClickListener {
                    loadMoreListener?.retryLoad()
                }
            }
        }

        when (a.getInt(R.styleable.MultiStatusLayout_viewState, ViewState.VIEW_LOAD_MORE)) {
            ViewState.VIEW_LOAD_MORE -> mViewState =
                ViewState.VIEW_LOAD_MORE
            ViewState.VIEW_STATE_ERROR -> mViewState =
                ViewState.VIEW_STATE_ERROR
            ViewState.VIEW_STATE_EMPTY -> mViewState =
                ViewState.VIEW_STATE_EMPTY
            ViewState.VIEW_STATE_LOADING -> mViewState =
                ViewState.VIEW_STATE_LOADING
        }
        a.recycle()
    }

    /**
     * 获取对应的View
     */
    fun getView(@ViewState state: Int): View? {
        return when (state) {
            ViewState.VIEW_STATE_LOADING -> mLoadingView
            ViewState.VIEW_LOAD_MORE -> mLoadMoreView
            ViewState.VIEW_STATE_EMPTY -> mEmptyView
            ViewState.VIEW_STATE_ERROR -> mErrorView
            else -> null
        }
    }

    /**
     * Sets the current [FooterLayout.mViewState]
     *
     * @param state ViewState
     */
    fun setViewState(@ViewState state: Int) {
        if (state != mViewState) {
            mViewState = state
            setView()
        }
    }

    /**
     * Shows the [View] based on the []
     */
    private fun setView() {
        changeViewParam()
        when (mViewState) {
            ViewState.VIEW_STATE_LOADING -> {
                mLoadingView?.run {
                    visibility = VISIBLE
                }
                mLoadMoreView?.run {
                    visibility = GONE
                }
                mErrorView?.run {
                    visibility = GONE
                }
                mEmptyView?.run {
                    visibility = GONE
                }
            }
            ViewState.VIEW_STATE_EMPTY -> {
                mLoadingView?.run {
                    visibility = GONE
                }
                mLoadMoreView?.run {
                    visibility = GONE
                }
                mErrorView?.run {
                    visibility = GONE
                }
                mEmptyView?.run {
                    visibility = VISIBLE
                }
            }
            ViewState.VIEW_STATE_ERROR -> {
                mErrorView?.run {
                    visibility = VISIBLE
                }
                mLoadingView?.run {
                    visibility = GONE
                }
                mLoadMoreView?.run {
                    visibility = GONE
                }
                mEmptyView?.run {
                    visibility = GONE
                }
            }
            ViewState.VIEW_LOAD_MORE -> {
                mErrorView?.run {
                    visibility = GONE
                }
                mLoadingView?.run {
                    visibility = GONE
                }
                mLoadMoreView?.run {
                    visibility = VISIBLE
                }
                mEmptyView?.run {
                    visibility = GONE
                }
            }
            else -> {
                mErrorView?.run {
                    visibility = GONE
                }
                mLoadingView?.run {
                    visibility = GONE
                }
                mLoadMoreView?.run {
                    visibility = VISIBLE
                }
                mEmptyView?.run {
                    visibility = GONE
                }
            }
        }
    }

    private fun changeViewParam() {
        val layoutParams = layoutParams
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        if (mViewState == ViewState.VIEW_LOAD_MORE) {
            if (isLoadMoreShow) {
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            } else {
                layoutParams.height = 0
            }
        } else {
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        }
        setLayoutParams(layoutParams)
    }

    /**
     * Sets the view for the given view state
     *
     * @param view          The [View] to use
     * @param state         The []to set
     * @param switchToState If the [] should be switched to
     */
    fun setViewForState(view: View, @ViewState state: Int, switchToState: Boolean) {
        when (state) {
            ViewState.VIEW_STATE_LOADING -> {
                if (mLoadingView != null) removeView(mLoadingView)
                mLoadingView = view
                addView(mLoadingView)
            }
            ViewState.VIEW_STATE_EMPTY -> {
                if (mEmptyView != null) removeView(mEmptyView)
                mEmptyView = view
                addView(mEmptyView)
            }
            ViewState.VIEW_STATE_ERROR -> {
                if (mErrorView != null) removeView(mErrorView)
                mErrorView = view
                mErrorView?.setOnClickListener {
                    loadMoreListener?.retryLoad()
                }
                addView(mErrorView)
            }
            ViewState.VIEW_LOAD_MORE -> {
                removeView(mLoadMoreView)
                mLoadMoreView = view as LoadMoreView
                addView(mLoadMoreView)
            }
        }
        if (switchToState) setViewState(state)
    }


    /**
     * Sets the view for the given view state
     *
     * @param view          The [View] to use
     * @param state         The []to set
     */
    fun addViewForState(view: View, @ViewState state: Int, params: ViewGroup.LayoutParams?) {
        when (state) {
            ViewState.VIEW_STATE_LOADING -> {
                mLoadingView?.run {
                    this@MultiStatusLayout.removeView(mLoadingView)
                }
                mLoadingView = view
                if (params != null) {
                    addView(mLoadingView, params)
                } else {
                    addView(mLoadingView)
                }
            }
            ViewState.VIEW_STATE_EMPTY -> {
                mEmptyView?.run {
                    this@MultiStatusLayout.removeView(mEmptyView)
                }
                mEmptyView = view
                if (params != null) {
                    addView(mEmptyView, params)
                } else {
                    addView(mEmptyView)
                }
            }
            ViewState.VIEW_STATE_ERROR -> {
                mErrorView?.run {
                    this@MultiStatusLayout.removeView(mErrorView)
                }
                mErrorView = view
                mErrorView?.setOnClickListener {
                    loadMoreListener?.retryLoad()
                }
                if (params != null) {
                    addView(mErrorView, params)
                } else {
                    addView(mEmptyView)
                }
            }
            ViewState.VIEW_LOAD_MORE -> {
                removeView(mLoadMoreView)
                mLoadMoreView = view as LoadMoreView
                if (params != null) {
                    addView(mLoadMoreView, params)
                } else {
                    addView(mLoadMoreView)
                }
            }
        }
    }

    /**
     * Sets the [View] for the given []
     *
     * @param layoutRes Layout resource id
     * @param state     The [View] state to set
     */
    fun setViewForState(@LayoutRes layoutRes: Int, @ViewState state: Int) {
        setViewForState(layoutRes, state, false)
    }

    /**
     * Sets the [View] for the given []
     *
     * @param layoutRes     Layout resource id
     * @param state         The [] to set
     * @param switchToState If the [] should be switched to
     */
    fun setViewForState(@LayoutRes layoutRes: Int, @ViewState state: Int, switchToState: Boolean) {
        val view = mInflater.inflate(layoutRes, this, false)
        setViewForState(view, state, switchToState)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setView()
    }


    /**
     * Returns the current [ViewState]
     * @return ViewState
     */
    @ViewState
    fun getViewState(): Int {
        return mViewState
    }

    fun setOnLoadMoreListener(listener: OnLoadMoreListener?) {
        loadMoreListener = listener
        mLoadMoreView?.setOnLoadMoreListener(loadMoreListener)
    }

    fun loadMoreStatus(state: Int) {
        mLoadMoreView?.changeStatus(state)
    }

    fun setLoadingText(loadingText: String?) {
        mLoadMoreView?.setLoadingText(loadingText)
    }

    fun setLoadMoreVisible(isShowMoreView: Boolean) {
        isLoadMoreShow = isShowMoreView
    }


}
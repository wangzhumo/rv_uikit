package com.wangzhumo.app.rvkit.multi

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.wangzhumo.app.rvkit.R
import com.wangzhumo.app.rvkit.listener.OnLoadMoreListener

/**
 * If you have any questions, you can contact by email {wangzhumoo@gmail.com}
 *
 * @author 王诛魔 2021/6/15  8:40 下午
 *
 * 最后一项的展示效果
 */
class LoadMoreView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {


    private lateinit var mViewContent: View
    private lateinit var mViewLoading: View
    private lateinit var mViewFail: View
    private lateinit var mViewEnd: View
    private lateinit var mViewEmpty: View
    private lateinit var mDiving: View
    private lateinit var mLoadingText: TextView
    private lateinit var mImgLoading: ProgressBar

    private var onLoadMoreListener: OnLoadMoreListener? = null

    init {
        initView()
    }

    private fun initView() {
        val rootView = inflate(context, R.layout.rvkit_layout_load_more, this)
        mDiving = rootView.findViewById(R.id.diving)
        mViewContent = rootView.findViewById(R.id.linMoreContent)
        mViewLoading = rootView.findViewById(R.id.linMoreLoading)
        mLoadingText = rootView.findViewById(R.id.loading_text)
        mViewFail = rootView.findViewById(R.id.linMoreFail)
        mViewEnd = rootView.findViewById(R.id.linMoreEnd)
        mViewEmpty = rootView.findViewById(R.id.linMoreEmpty)
        mImgLoading = rootView.findViewById(R.id.imgLoading)
        mViewContent.setOnClickListener(loadMoreClick)
        mViewFail.setOnClickListener(loadMoreClick)
        changeStatus(LOAD_STATUS_HIDE)
    }

    private val loadMoreClick = OnClickListener { view ->
        changeStatus(LOAD_STATUS_LOADING)
        if (view === mViewContent) {
            onLoadMoreListener?.run {
                loadNext()
            }
        } else if (view === mViewFail) {
            onLoadMoreListener?.run {
                retryLoad()
            }
        }
    }

    fun changeStatus(status: Int) {
        mViewContent.visibility = GONE
        mViewLoading.visibility = GONE
        mViewFail.visibility = GONE
        mViewEnd.visibility = GONE
        mViewEmpty.visibility = GONE
        mDiving.visibility = GONE
        when (status) {
            LOAD_STATUS_HIDE -> mDiving.visibility = GONE
            LOAD_STATUS_CONTENT -> mViewContent.visibility = VISIBLE
            LOAD_STATUS_LOADING -> mViewLoading.visibility = VISIBLE
            LOAD_STATUS_FAIL -> mViewFail.visibility = VISIBLE
            LOAD_STATUS_END -> mViewEnd.visibility = VISIBLE
            LOAD_STATUS_EMPTY -> mViewEmpty.visibility = VISIBLE
        }
    }

    fun setEmptyString(empty: String?) {
        val textView = mViewEmpty.findViewById<TextView>(R.id.textMoreEmpty)
        textView.text = empty
    }

    fun setLoadingText(textView: String?) {
        mLoadingText.text = textView
    }

    fun setOnLoadMoreListener(onLoadMoreListener: OnLoadMoreListener?) {
        this.onLoadMoreListener = onLoadMoreListener
    }

    companion object {
        const val LOAD_STATUS_HIDE = 0
        const val LOAD_STATUS_CONTENT = 1
        const val LOAD_STATUS_LOADING = 2
        const val LOAD_STATUS_FAIL = 3
        const val LOAD_STATUS_END = 4
        const val LOAD_STATUS_EMPTY = 5

    }
}
package com.wangzhumo.app.rvkit

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.drakeet.multitype.MultiTypeAdapter
import com.wangzhumo.app.rvkit.adapter.RecyclerViewAdapterWrapper
import com.wangzhumo.app.rvkit.listener.OnChildScrollUpCallback
import com.wangzhumo.app.rvkit.listener.OnLoadMoreListener
import com.wangzhumo.app.rvkit.listener.OnRefreshAndLoadMoreListener
import com.wangzhumo.app.rvkit.listener.OnRefreshListener
import com.wangzhumo.app.rvkit.multi.LoadMoreView
import com.wangzhumo.app.rvkit.multi.MultiStatusLayout
import com.wangzhumo.app.rvkit.multi.ViewState

/**
 * If you have any questions, you can contact by email {wangzhumoo@gmail.com}
 *
 * @author 王诛魔 2021/6/15  9:33 下午
 *
 * 刷新加载帮助类
 */
class RefreshAndLoadHelper {

    //临时存储正在加载第几页，成功后真正保存
    private var tempPage = 1

    //加载到第几页
    private var mLoadPage = 1
    private var mLastId: Long = 0

    //是否可以继续加载
    private var mHasMore = false

    //是否正在加载
    private var mLoading = false

    //是否使用MultiStateView
    private var mUseMultiStateView = false

    //上下文
    private val mContext: Context? = null

    //数据源.
    private val mData: MutableList<Any> = mutableListOf()

    //下拉刷新控件
    private var mRefreshLayout: RefreshInterface? = null

    //多态View控件
    private var mMultiStatusLayout: MultiStatusLayout? = null

    //RecyclerView
    private val mRecyclerView: RecyclerView

    //外部的，实际上使用的Adapter
    private val mAdapter : MultiTypeAdapter

    //头部尾部VIEW（会包装在mAdapter外面）
    private val mHeaderFooterAdapter: RecyclerViewAdapterWrapper


    //通知界面来进行获取数据的回调
    private val mListener: OnRefreshAndLoadMoreListener

    /**
     * @param  adapter 适配器
     * @param  recyclerView   recyclerView
     * @param  listener  OnRefreshAndLoadMoreListener
     */
    constructor(adapter: MultiTypeAdapter, recyclerView: RecyclerView,
                listener: OnRefreshAndLoadMoreListener) {


        /*
         * 设置头部、尾部Adapter,包在自定义Adapter外面
         */
        this.mHeaderFooterAdapter = RecyclerViewAdapterWrapper(adapter)
        this.mListener = listener
        this.mRecyclerView = recyclerView
        this.mAdapter = adapter
        /*
         * 设置线性布局
         */
        val linManager = recyclerView.layoutManager as LinearLayoutManager

        /*
         * 头部尾部Adapter更新
         */
        adapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onChanged() {
                mHeaderFooterAdapter!!.notifyDataSetChanged()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                mHeaderFooterAdapter!!.notifyItemRangeChanged(positionStart, itemCount, payload)
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                mHeaderFooterAdapter!!.notifyItemMoved(fromPosition, toPosition)
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                mHeaderFooterAdapter!!.notifyItemRangeRemoved(positionStart, itemCount)
            }
        })

        /**
         * 设置下滑加载更多
         */
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastVisibleItem: Int = linManager.findLastVisibleItemPosition()
                val totalItemCount: Int = linManager.getItemCount()
                if (lastVisibleItem >= totalItemCount - 2 && dy > 0) {
                    if (!mLoading && mHasMore) {
                        val page = mLoadPage + 1
                        loadingStart(mLastId, page, false)
                    }
                }
            }

        })

        // 设置为mHeaderFooterAdapter
        recyclerView.adapter = mHeaderFooterAdapter
    }


    fun addSwipeRefresh(refreshLayout: RefreshInterface) {
        addSwipeRefresh(refreshLayout, null)
    }


    fun addSwipeRefresh(refreshLayout: RefreshInterface, onRefreshListener: OnRefreshListener?) {
        this.mRefreshLayout = refreshLayout
        /*
         * 设置下拉刷新回调
         */
        mRefreshLayout?.setOnRefreshListener(object : OnRefreshListener{
            override fun onRefresh() {
                onRefreshListener?.onRefresh()
                loadingStart(0, 1, true)
            }

        })
        mRefreshLayout?.setOnChildScrollUpCallback(object : OnChildScrollUpCallback {

            override fun canChildScrollUp(view: View, child: View):Boolean {
                return mListener.checkCanDoRefresh()
            }

        })
    }


    /**
     * 为Helper添加MultiStatusLayout
     *
     */
    fun addLoadMoreView() {
        addLoadMoreView(true)
    }


    /**
     * 为Helper添加MultiStatusLayout
     *
     * @param isShowMoreView 是否展示LoadMoreView
     */
    fun addLoadMoreView(isShowMoreView: Boolean) {
        /*
         * 添加尾部
         */
        mMultiStatusLayout = MultiStatusLayout(mRecyclerView.context)
        mMultiStatusLayout?.apply {
            setLoadMoreVisible(isShowMoreView)
            setViewForState(R.layout.rvkit_layout_view_loading, ViewState.VIEW_STATE_LOADING)
            setViewForState(R.layout.rvkit_layout_view_error, ViewState.VIEW_STATE_ERROR)
            setViewForState(R.layout.rvkit_layout_view_empty, ViewState.VIEW_STATE_EMPTY)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        mHeaderFooterAdapter.addFooter(mMultiStatusLayout)
        /*
         * 设置尾部点击事件
         */
        mMultiStatusLayout?.setOnLoadMoreListener(object : OnLoadMoreListener {

            override fun loadNext() {
                val page = mLoadPage + 1
                loadingStart(mLastId, page, false)
            }

            override fun retryLoad() {
                mListener.loadListData(mLastId, tempPage)
            }
        })
        mUseMultiStateView = true
    }


    fun setLoadMoreVisibility(isShow: Boolean) {
        mMultiStatusLayout?.run {
            mHeaderFooterAdapter.setFooterVisibility(isShow)
        }
    }


    /**
     * 获取加载更多View
     *
     * @return
     */
    fun getLoadMoreView(): MultiStatusLayout? {
        return mMultiStatusLayout
    }

    /**
     * 重置
     */
    fun resetView() {
        if (mMultiStatusLayout != null) {
            mUseMultiStateView = true
        }
        mLoadPage = 1
        tempPage = 1
        mHasMore = true
        //清空数据
        mAdapter.items = emptyList()
        mAdapter.notifyDataSetChanged()
    }


    fun setFirstLoadingAndFailView(multiStatusLayout: MultiStatusLayout) {
        mUseMultiStateView = true
        this.mMultiStatusLayout = multiStatusLayout
        mMultiStatusLayout?.setViewState(ViewState.VIEW_STATE_LOADING)
    }

    fun setFirstLoadingAndFailViewDefault(multiStatusLayout: MultiStatusLayout) {
        mUseMultiStateView = true
        this.mMultiStatusLayout = multiStatusLayout
        mMultiStatusLayout?.run {
            setViewForState(R.layout.rvkit_layout_view_loading, ViewState.VIEW_STATE_LOADING)
            setViewForState(R.layout.rvkit_layout_view_error, ViewState.VIEW_STATE_ERROR)
            setViewForState(R.layout.rvkit_layout_view_empty, ViewState.VIEW_STATE_EMPTY)
            getView(ViewState.VIEW_STATE_ERROR)?.findViewById<TextView>(R.id.retry)
                ?.setOnClickListener {
                    loadingStart(mLastId, mLoadPage, false)
                }
            setViewState(ViewState.VIEW_STATE_LOADING)
        }
    }

    fun addHeader(view: View) {
        mHeaderFooterAdapter.addHeader(view)
    }

    fun addFooter(view: View) {
        mHeaderFooterAdapter.addFooter(view)
    }

    fun getLayoutManager(): LinearLayoutManager? {
        return mRecyclerView.layoutManager as LinearLayoutManager?
    }

    fun getLoadPage(): Int {
        return tempPage
    }

    /**
     * 显示Loading操作，自动判断显示下拉Loading还是加载更多Loading
     *
     * @param last_id
     */
    fun loadingStart(last_id: Long) {
        loadingStart(last_id, false)
    }

    /**
     * @param last_id
     * @param hasRefreshAnimation 是否需要下拉刷新的动画
     */
    fun loadingStart(last_id: Long, hasRefreshAnimation: Boolean) {
        loadingStart(last_id, 1, hasRefreshAnimation)
    }

    /**
     * @param page
     * @param hasRefreshAnimation 是否需要下拉刷新的动画
     */
    fun loadingStart(last_id: Long, page: Int, hasRefreshAnimation: Boolean) {
        mLoading = true
        tempPage = page
        mLastId = last_id
        if (mRefreshLayout != null && hasRefreshAnimation) {
            mRefreshLayout?.setRefreshing(true)
            mListener.loadListData(mLastId, tempPage)
        } else if (mMultiStatusLayout != null) {
            if (page == 1) {
                mMultiStatusLayout?.setViewState(ViewState.VIEW_STATE_LOADING)
            } else {
                mMultiStatusLayout?.loadMoreStatus(LoadMoreView.LOAD_STATUS_LOADING)
                mMultiStatusLayout?.setViewState(ViewState.VIEW_LOAD_MORE)
            }
            mListener.loadListData(mLastId, tempPage)
        } else {
            mListener.loadListData(mLastId, tempPage)
        }
    }


    fun setLoadingText(loadingText: String?) {
        mMultiStatusLayout?.setLoadingText(loadingText)
    }


    fun loadingSuccess(list: List<Any>?, last_id: Long, hasMore: Boolean) {
        loadingSuccess(list, last_id, hasMore, false)
    }


    /**
     * Loading成功操作
     */
    fun loadingSuccess(
        list: List<Any>?,
        last_id: Long,
        hasMore: Boolean,
        needShowLoadingEnd: Boolean
    ) {
        mLoading = false
        mLoadPage = tempPage
        mLastId = last_id
        mHasMore = hasMore
        if (hasMore) {
            if (mMultiStatusLayout != null) {
                mMultiStatusLayout?.loadMoreStatus(LoadMoreView.LOAD_STATUS_CONTENT)
            }
        } else {
            if (mMultiStatusLayout != null) {
                if (needShowLoadingEnd) {
                    mMultiStatusLayout?.loadMoreStatus(LoadMoreView.LOAD_STATUS_END)
                } else {
                    mMultiStatusLayout?.loadMoreStatus(LoadMoreView.LOAD_STATUS_HIDE)
                }
            }
        }
        if (mLoadPage == 1) {
            //如果加载的是第一页 ，并且数据为空的话，显示emptyView
            if (list == null) {
                if (mUseMultiStateView && mMultiStatusLayout != null) {
                    mMultiStatusLayout?.setViewState(ViewState.VIEW_STATE_EMPTY)
                }
                if (mMultiStatusLayout != null) {
                    mMultiStatusLayout?.loadMoreStatus(LoadMoreView.LOAD_STATUS_EMPTY)
                }
            } else if (list.isEmpty()) {
                if (mUseMultiStateView && mMultiStatusLayout != null) {
                    mMultiStatusLayout?.setViewState(ViewState.VIEW_STATE_EMPTY)
                }
                mMultiStatusLayout?.loadMoreStatus(LoadMoreView.LOAD_STATUS_EMPTY)
            } else if (list.size <= 3) {
                if (mUseMultiStateView && mMultiStatusLayout != null) {
                    mMultiStatusLayout?.loadMoreStatus(LoadMoreView.LOAD_STATUS_HIDE)
                    mMultiStatusLayout?.setViewState(ViewState.VIEW_LOAD_MORE)
                }
            } else {
                if (mUseMultiStateView && mMultiStatusLayout != null) {
                    mMultiStatusLayout?.setViewState(ViewState.VIEW_LOAD_MORE)
                }
            }
            mRefreshLayout?.setRefreshing(false)
            // 清空数据，并且把所有的数据添加进来
            mData.clear()
            if (list != null) {
                mData.addAll(list)
                mAdapter.items = mData
                mAdapter.notifyDataSetChanged()
            }
        } else {
            // 如果不是第一次，直接走addAll
            if (list != null) {
                mData.addAll(list)
                mAdapter.items = mData
                mAdapter.notifyDataSetChanged()
            }
        }
    }

    fun loadingFail() {
        loadingFail("")
    }

    /**
     * Loading失败操作
     *
     * @param failMsg
     */
    fun loadingFail(failMsg: String?) {
        mLoading = false
        if (tempPage == 1) {
            mRefreshLayout?.setRefreshing(false)
            if (mAdapter.itemCount > 0) {
                return
            }
            if (mUseMultiStateView && mMultiStatusLayout != null) {
                mMultiStatusLayout?.setViewState(ViewState.VIEW_STATE_ERROR)
            } else {
                mMultiStatusLayout?.loadMoreStatus(LoadMoreView.LOAD_STATUS_FAIL)
            }
        } else {
            mMultiStatusLayout?.loadMoreStatus(LoadMoreView.LOAD_STATUS_FAIL)
        }
    }

    fun loadingComplete() {
        mLoading = false
        mRefreshLayout?.setRefreshing(false)
    }

    fun getData(): List<Any> {
        return mData
    }

    fun getItemCount(): Int {
        return mHeaderFooterAdapter.itemCount
    }
}
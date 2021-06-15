package com.wangzhumo.app.rvkit

import android.content.Context
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.wangzhumo.app.rvkit.multi.MultiStatusLayout

/**
 * If you have any questions, you can contact by email {wangzhumoo@gmail.com}
 *
 * @author 王诛魔 2021/6/15  9:33 下午
 *
 * 刷新加载帮助类
 */
class RefreshAndLoadHelper {

    //临时存储正在加载第几页，成功后真正保存
    private val tempPage = 1

    //加载到第几页
    private val mLoadPage = 1
    private val mLastId: Long = 0

    //是否可以继续加载
    private val mHasMore = false

    //是否正在加载
    private val mLoading = false

    //是否使用MultiStateView
    private val mUseMultiStateView = false

    //上下文
    private val mContext: Context? = null

    //数据源.
    private val mData: MutableList<Any> = mutableListOf()


    //头部尾部VIEW（会包装在mAdapter外面）
    private val mHeaderFooterAdapter: HeaderFooterAdapter<MultiTypeAdapter>? = null

    //RecyclerView
    private val recyclerView: RecyclerView? = null

    //下拉刷新控件
    private val mSwipeLayout: SwipeRefreshLayout? = null

    //多态View控件
    private val mFooterLayout: MultiStatusLayout? = null


    /**
     * @param  adapter 适配器
     * @param  recyclerView   recyclerView
     */
    constructor(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
                recyclerView: RecyclerView) {
        this.adapter = adapter
    }


}
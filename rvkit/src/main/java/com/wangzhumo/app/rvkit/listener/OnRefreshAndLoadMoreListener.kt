package com.wangzhumo.app.rvkit.listener

/**
 * If you have any questions, you can contact by email {wangzhumoo@gmail.com}
 *
 * @author 王诛魔 2021/6/16  11:02 上午
 *
 * 加载的帮助类
 */
interface OnRefreshAndLoadMoreListener {

    fun loadListData(last_id: Long, page: Int)

    fun checkCanDoRefresh(): Boolean
}
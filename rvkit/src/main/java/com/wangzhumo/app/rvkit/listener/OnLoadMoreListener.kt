package com.wangzhumo.app.rvkit.listener

/**
 * If you have any questions, you can contact by email {wangzhumoo@gmail.com}
 *
 * @author 王诛魔 2021/6/15  8:50 下午
 *
 * 加载更多的Listener
 */
interface OnLoadMoreListener {

    fun loadNext()

    fun retryLoad()
}
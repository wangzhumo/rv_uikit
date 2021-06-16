package com.wangzhumo.app.rvkit

import com.wangzhumo.app.rvkit.listener.OnChildScrollUpCallback
import com.wangzhumo.app.rvkit.listener.OnRefreshListener

/**
 * If you have any questions, you can contact by email {wangzhumoo@gmail.com}
 *
 * @author 王诛魔 2021/6/16  10:07 上午
 *
 * 刷新控件需要遵守的方法
 */
interface RefreshInterface {


    /**
     * 是否在刷新状态
     */
    fun setRefreshing(refresh:Boolean)

    /**
     * 是否可以刷新
     */
    fun checkCanDoRefresh() : Boolean

    /**
     * Set a callback to override [canChildScrollUp] method. Non-null
     * callback will return the value provided by the callback and ignore all internal logic.
     * @param callback Callback that should be called when canChildScrollUp() is called.
     */
    fun setOnChildScrollUpCallback(callback: OnChildScrollUpCallback)

    /**
     * 加入一个监听 - 开始刷新
     */
    fun setOnRefreshListener(listener: OnRefreshListener)
}
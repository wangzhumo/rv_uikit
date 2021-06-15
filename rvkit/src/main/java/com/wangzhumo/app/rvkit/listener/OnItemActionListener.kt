package com.wangzhumo.app.rvkit.listener

/**
 * If you have any questions, you can contact by email {wangzhumoo@gmail.com}
 *
 * @author 王诛魔 2021/6/15  7:40 下午
 *
 * 一般用于RecyclerView的Item点击事件
 */
public interface OnItemActionListener {

    fun onItemAction(position:Int = 0,action:Int = 0,data :Any)
}
package com.wangzhumo.app.rvkit.listener

import android.view.View
import androidx.annotation.NonNull
import androidx.annotation.Nullable

/**
 * If you have any questions, you can contact by email {wangzhumoo@gmail.com}
 *
 * @author 王诛魔 2021/6/16  10:58 上午
 *
 * Classes that wish to override {@link #canChildScrollUp()} method
 * behavior should implement this interface.
 */
interface OnChildScrollUpCallback {

    /**
     * Callback that will be called when {@link #canChildScrollUp()} method
     * is called to allow the implementer to override its behavior.
     *
     * @param parent SwipeRefreshLayout that this callback is overriding.
     * @param child The child view of SwipeRefreshLayout.
     *
     * @return Whether it is possible for the child view of parent layout to scroll up.
     */
    fun canChildScrollUp(@NonNull view:View, @Nullable child: View ) : Boolean
}
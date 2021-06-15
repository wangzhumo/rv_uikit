package com.wangzhumo.app.rvkit.multi;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * If you have any questions, you can contact by email {wangzhumoo@gmail.com}
 *
 * @author 王诛魔 2021/6/15  9:01 下午
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({ViewState.VIEW_LOAD_MORE, ViewState.VIEW_STATE_ERROR, ViewState.VIEW_STATE_EMPTY, ViewState.VIEW_STATE_LOADING})
public @interface ViewState {
    int VIEW_LOAD_MORE = 0;

    int VIEW_STATE_ERROR = 1;

    int VIEW_STATE_EMPTY = 2;

    int VIEW_STATE_LOADING = 3;
}
package com.wangzhumo.app.rvkit.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import java.util.*


/**
 * If you have any questions, you can contact by email {wangzhumoo@gmail.com}
 *
 * @author 王诛魔 2021/6/15  7:42 下午
 *
 * 一个Adapter的包装 (外部需要给定一个已有的Adapter)
 *
 * 这个Adapter什么也不做，只是对原来的BaseAdapter又包装了一层
 * 1.可以添加Footer，Header
 * 2.可以做一个MultiTypeStatus的封装
 * 3.当position到了Footer，Header的时候才会去处理，其他时候给原来的Adapter去处理
 *
 * 目的是分离处理逻辑，不对原来的Adapter做任何的更改
 */
class RecyclerViewAdapterWrapper constructor(private val baseAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {



    /**
     * Defines available view type integers for headers and footers.
     *
     * This means that you're safe as long as the base adapter doesn't use negative view types,
     * and as long as you have fewer than 1000 headers and footers
     */
    private val mHeaders: MutableList<View> = mutableListOf()
    private val mFooters: MutableList<View> = mutableListOf()


    /**
     * Gets the base adapter that this is wrapping.
     */
    fun getWrappedAdapter(): RecyclerView.Adapter<*> {
        return baseAdapter
    }


    /**
     * Adds a header view.
     */
    fun addHeader(view: View?) {
        requireNotNull(view) { "You can't have a null header!" }
        mHeaders.add(view)
    }

    /**
     * Adds a footer view.
     */
    fun addFooter(view: View?) {
        requireNotNull(view) { "You can't have a null footer!" }
        mFooters.add(view)
    }

    /**
     * Toggles the visibility of the header views.
     */
    fun setHeaderVisibility(shouldShow: Boolean) {
        for (header in mHeaders) {
            header.visibility = if (shouldShow) View.VISIBLE else View.GONE
        }
    }

    /**
     * Toggles the visibility of the footer views.
     */
    fun setFooterVisibility(shouldShow: Boolean) {
        for (footer in mFooters) {
            footer.visibility = if (shouldShow) View.VISIBLE else View.GONE
        }
    }


    /**
     * @return the number of headers.
     */
    fun getHeaderCount(): Int {
        return mHeaders.size
    }

    /**
     * @return the number of footers.
     */
    fun getFooterCount(): Int {
        return mFooters.size
    }

    /**
     * Gets the indicated header, or null if it doesn't exist.
     */
    fun getHeader(i: Int): View? {
        return if (i < mHeaders.size) mHeaders[i] else null
    }

    /**
     * Gets the indicated footer, or null if it doesn't exist.
     */
    fun getFooter(i: Int): View? {
        return if (i < mFooters.size) mFooters[i] else null
    }


    private fun isHeader(viewType: Int): Boolean {
        return viewType >= HEADER_VIEW_TYPE && viewType < HEADER_VIEW_TYPE + mHeaders.size
    }

    private fun isFooter(viewType: Int): Boolean {
        return viewType >= FOOTER_VIEW_TYPE && viewType < FOOTER_VIEW_TYPE + mFooters.size
    }




    /**
     * Called when RecyclerView needs a new [ViewHolder] of the given type to represent
     * an item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when {
            isHeader(viewType) -> {
                val whichHeader = Math.abs(viewType - HEADER_VIEW_TYPE)
                val headerView = mHeaders[whichHeader]
                object : RecyclerView.ViewHolder(headerView) {}
            }
            isFooter(viewType) -> {
                val whichFooter = Math.abs(viewType - FOOTER_VIEW_TYPE)
                val footerView = mFooters[whichFooter]
                object : RecyclerView.ViewHolder(footerView) {}
            }
            else -> {
                baseAdapter.onCreateViewHolder(parent, viewType)
            }
        }
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the [ViewHolder.itemView] to reflect the item at the given
     * position.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when {
            position < mHeaders.size -> {
                // Headers don't need anything special
            }
            position < mHeaders.size + baseAdapter.itemCount -> {
                // This is a real position, not a header or footer. Bind it.
                baseAdapter.onBindViewHolder(holder, position - mHeaders.size)
            }
            else -> {
                // Footers don't need anything special
            }
        }
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the [ViewHolder.itemView] to reflect the item at the given
     * position.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<Any?>) {
        when {
            position < mHeaders.size -> {
                // Headers don't need anything special
            }
            position < mHeaders.size + baseAdapter.itemCount -> {
                // This is a real position, not a header or footer. Bind it.
                baseAdapter.onBindViewHolder(holder, position - mHeaders.size, payloads)
            }
            else -> {
                // Footers don't need anything special
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        when {
            holder.adapterPosition < mHeaders.size -> {
                // Headers don't need anything special
                super.onViewDetachedFromWindow(holder)
            }
            holder.adapterPosition < mHeaders.size + baseAdapter.itemCount -> {
                // This is a real position, not a header or footer. Bind it.
                baseAdapter.onViewDetachedFromWindow(holder)
            }
            else -> {
                // Footers don't need anything special
                super.onViewDetachedFromWindow(holder)
            }
        }
    }



    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        when {
            holder.adapterPosition < mHeaders.size -> {
                // Headers don't need anything special
                super.onViewDetachedFromWindow(holder)
            }
            holder.adapterPosition < mHeaders.size + baseAdapter.itemCount -> {
                // This is a real position, not a header or footer. Bind it.
                baseAdapter.onViewAttachedToWindow(holder)
            }
            else -> {
                // Footers don't need anything special
                super.onViewDetachedFromWindow(holder)
            }
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int {
        return mHeaders.size + baseAdapter.itemCount + mFooters.size
    }


    /**
     * Return the view type of the item at `position` for the purposes
     * of view recycling.
     *
     *
     * The default implementation of this method returns 0, making the assumption of
     * a single view type for the adapter. Unlike ListView adapters, types need not
     * be contiguous. Consider using id resources to uniquely identify item view types.
     *
     * @param position position to query
     * @return integer value identifying the type of the view needed to represent the item at
     * `position`. Type codes need not be contiguous.
     */
    override fun getItemViewType(position: Int): Int {
        return when {
            position < mHeaders.size -> {
                HEADER_VIEW_TYPE + position
            }
            position < mHeaders.size + baseAdapter.itemCount -> {
                baseAdapter.getItemViewType(position - mHeaders.size)
            }
            else -> {
                FOOTER_VIEW_TYPE + position - mHeaders.size - baseAdapter.itemCount
            }
        }
    }

    /**
     * GridLayoutManager
     *
     * @param spanCount      spanCount
     * @param position       当前position
     * @param spanSizeLookup 注意它是你自己设置的spanSizeLookup,通过RefreshAndLoadMoreHelper传入
     * @return sizeLookup
     */
    fun setSpanSizeLookup(position: Int, spanCount: Int, spanSizeLookup: SpanSizeLookup): Int {
        return when {
            position < mHeaders.size -> {
                spanCount
            }
            position < mHeaders.size + baseAdapter.getItemCount() -> {
                spanSizeLookup.getSpanSize(position)
            }
            else -> {
                spanCount
            }
        }
    }

    companion object{

        /**
         * - Regular views use view types starting from 0, counting upwards
         * - Header views use view types starting from -1000, counting upwards
         * - Footer views use view types starting from -2000, counting upwards
         */
        const val HEADER_VIEW_TYPE = -1000
        const val FOOTER_VIEW_TYPE = -2000
    }

}




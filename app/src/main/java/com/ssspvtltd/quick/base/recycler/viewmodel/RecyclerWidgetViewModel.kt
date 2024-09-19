package com.ssspvtltd.quick.base.recycler.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ssspvtltd.quick.base.BaseViewModel
import com.ssspvtltd.quick.base.recycler.data.BaseWidget
import com.ssspvtltd.quick.base.recycler.data.BottomProgressWidget
import com.ssspvtltd.quick.base.recycler.data.CommonViewType
import com.ssspvtltd.quick.base.recycler.data.ProgressWidget
import com.ssspvtltd.quick.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Abhishek Singh on April 29,2023.
 */
@HiltViewModel
open class RecyclerWidgetViewModel @Inject constructor() : BaseViewModel() {

    /**
     * List of widgets that will show on recycler
     */
    var widgetList = listOf<BaseWidget>()
        private set

    /**
     * Value to notify RecyclerView about list changes
     */
    private val _isListAvailable by lazy { SingleLiveEvent<Boolean>() }
    val isListAvailable: LiveData<Boolean> get() = _isListAvailable

    /**
     * Notify item changed in RecyclerView
     */
     fun listDataChanged() {
        _isListAvailable.value = true
    }

    /**
     * Add a full size progress bar in RecyclerView
     */
    protected fun addProgressBar() {
        clearWidgetList()
        widgetList = widgetList + ProgressWidget()
        listDataChanged()
    }

    /**
     * Add a bottom progress bar in RecyclerView
     */
    protected fun addBottomProgressBar() {
        widgetList = widgetList + BottomProgressWidget()
        listDataChanged()
    }

    /**
     * Remove Progress from Widget List
     */
    protected fun removeProgressBar() = synchronized(widgetList) {
        widgetList = widgetList.filter {
            it.viewType != CommonViewType.BOTTOM_PROGRESS_BAR &&
                    it.viewType != CommonViewType.PROGRESS_BAR
        }
    }

    /**
     * Add a list to widgetList
     */
    protected fun changeAllWidgetList(list: List<BaseWidget>) = synchronized(widgetList) {
        widgetList = list
    }

    /**
     * Add a list to widgetList
     */
    protected fun addItemToWidgetList(item: List<BaseWidget>) = synchronized(widgetList) {
        widgetList = widgetList + item
    }

    /**
     * Add a list to widgetList from a position
     */
    protected fun addItemToWidgetList(
        position: Int, item: List<BaseWidget>
    ) = synchronized(widgetList) {
        val list = ArrayList(widgetList)
        list.addAll(position, item)
        widgetList = list
    }

    /**
     * Add a item to list
     */
    protected fun addItemToWidgetList(item: BaseWidget) = synchronized(widgetList) {
        widgetList = widgetList + item
    }

    /**
     * Add a item to list on a position
     */
    protected fun addItemToWidgetList(position: Int, item: BaseWidget) = synchronized(widgetList) {
        val list = ArrayList(widgetList)
        list.add(position, item)
        widgetList = list
    }

    /**
     * replace a item to list on a position
     */
    protected fun replaceItemInWidgetList(
        position: Int, widget: BaseWidget
    ) = synchronized(widgetList) {
        val list = ArrayList(widgetList)
        list[position] = widget
        widgetList = list
    }

    /**
     * replace a item from list with old item
     */
    protected fun replaceItemInWidgetList(
        oldItem: BaseWidget, newItem: BaseWidget
    ) = synchronized(widgetList) {
        widgetList = widgetList.map { if (it == oldItem) newItem else it }
    }


    /**
     * remove a item from list on a position
     */
    protected fun removeItemFromWidgetList(position: Int) = synchronized(widgetList) {
        val list = ArrayList(widgetList)
        list.removeAt(position)
        widgetList = list
    }

    protected fun removeItemFromWidgetList(
        predicate: (BaseWidget) -> Boolean
    ) = synchronized(widgetList) {
        val list = ArrayList(widgetList)
        list.removeAll(predicate)
        widgetList = list
    }

    protected fun findItemFromWidgetList(
        predicate: (BaseWidget) -> Boolean
    ): BaseWidget? = synchronized(widgetList) {
        return@synchronized widgetList.find(predicate)
    }

    protected fun findIndexFromWidgetList(
        predicate: (BaseWidget) -> Boolean
    ): Int = synchronized(widgetList) {
        return@synchronized widgetList.indexOfFirst(predicate)
    }

    protected fun findItemFromWidgetList(position: Int): BaseWidget? = synchronized(widgetList) {
        return@synchronized widgetList.getOrNull(position)
    }

    /**
     * Remove Progress from Widget List
     */
    fun clearWidgetList() {
        widgetList = listOf()
    }
}
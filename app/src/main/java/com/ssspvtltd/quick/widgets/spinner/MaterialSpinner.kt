package com.ssspvtltd.quick.widgets.spinner

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.widget.AdapterView
import android.widget.Filterable
import android.widget.ListAdapter
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.ssspvtltd.quick.R

class MaterialSpinner : AppCompatAutoCompleteTextView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        com.google.android.material.R.attr.autoCompleteTextViewStyle
    )

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MaterialSpinner, defStyleAttr, 0)
        emptyText = a.getText(R.styleable.MaterialSpinner_emptyText)
        a.recycle()
    }

    /**
     * These listeners will receive updates when the selected position changes. Can be used e. g.
     * by adapters to update the selected item's UI.
     */
    private val selectedPositionListeners = mutableSetOf<(Int) -> Unit>()
    private var _selectedPosition: Int = 0
        set(value) {
            field = value
            notifyListenersAboutSelectedPosition()
        }

    private fun notifyListenersAboutSelectedPosition() {
        selectedPositionListeners.forEach {
            it.invoke(_selectedPosition)
        }
    }

    /**
     * Currently selected position in the spinner
     */
    var selectedPosition: Int
        get() {
            return _selectedPosition
        }
        set(value) {
            _selectedPosition = value
            setCurrentPositionItemText()
        }

    /**
     * Empty text will be shown if there are no items in the adapter or if the [selectedPosition]
     * does not exist in the adapter.
     */
    var emptyText: CharSequence? = null
        set(value) {
            field = value
            setCurrentPositionItemText()
        }
    private var wrappedItemClickListener: AdapterView.OnItemClickListener? = null

    init {
        super.setOnItemClickListener { parent, view, position, id ->
            _selectedPosition = position
            wrappedItemClickListener?.onItemClick(parent, view, position, id)
        }
        inputType = InputType.TYPE_NULL
    }

    override fun setOnItemClickListener(l: AdapterView.OnItemClickListener?) {
        wrappedItemClickListener = l
    }

    override fun <T> setAdapter(adapter: T) where T : ListAdapter?, T : Filterable? {
        super.setAdapter(adapter)
        setCurrentPositionItemText()
        if (adapter is MaterialSpinnerAdapter<*>) {
            addSelectedPositionListener(adapter::selectedItemPosition::set)
        }
    }

    fun addSelectedPositionListener(listener: (Int) -> Unit) {
        selectedPositionListeners.add(listener)
        listener.invoke(_selectedPosition)
    }

    fun removeSelectedPositionListener(listener: (Int) -> Unit) {
        selectedPositionListeners.remove(listener)
    }

    private fun setCurrentPositionItemText() {
        adapter?.let { adapter ->
            setText(
                if (adapter.count > selectedPosition) {
                    adapter.getItem(selectedPosition)?.toString() ?: emptyText
                } else {
                    emptyText
                }, false
            )
        }
    }
}
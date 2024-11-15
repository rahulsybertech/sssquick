package com.ssspvtltd.quick.widgets.spinner

import android.content.Context
import android.view.LayoutInflater
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable

/**
 * Custom adapter for [MaterialSpinner]. Just implement [getView] to provide the given View, e. g.
 * by using [inflater]. In case of data binding, the binding object can be set as the given View's
 * tag to be able to retrieve it when reusing the given View and implement custom viewholder pattern.
 * [selectedItemPosition] can be used for updating the selected View's UI. When used together with
 * [MaterialSpinner], [selectedItemPosition] is updated automatically and [notifyDataSetChanged] is called.
 *
 * Created by Milos Cernilovsky on 4/21/21
 */
abstract class MaterialSpinnerAdapter<T>(context: Context) : BaseAdapter(), Filterable {

    protected var objects = listOf<T>()

    // fake filter to implement Filterable (required by MaterialSpinner's AutocompleteTextView parent)
    private val _filter: Filter by lazy {
        object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                return FilterResults()
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

            }
        }
    }
    val inflater: LayoutInflater = LayoutInflater.from(context)
    var selectedItemPosition = 0
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getCount(): Int = objects.size

    override fun getItem(position: Int): T = objects[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getFilter(): Filter = _filter

    fun submitList(objects: List<T>) {
        this.objects = objects
        notifyDataSetChanged()
    }
}
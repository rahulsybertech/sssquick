package com.ssspvtltd.quick.ui.create_gr.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.google.gson.Gson
import com.ssspvtltd.quick.base.recycler.viewmodel.RecyclerWidgetViewModel
import com.ssspvtltd.quick.ui.checkincheckout.repository.CheckInCheckOutRepository
import com.ssspvtltd.quick.ui.order.add.repositry.AddOrderRepositry
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
@HiltViewModel
class CreateGRViewModel@Inject constructor(
    private val gson: Gson,
    private val repository: CheckInCheckOutRepository,
    @ApplicationContext private val context: Context
) : RecyclerWidgetViewModel() {
}
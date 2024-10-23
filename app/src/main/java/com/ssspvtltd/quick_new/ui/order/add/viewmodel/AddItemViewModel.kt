package com.ssspvtltd.quick_new.ui.order.add.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ssspvtltd.quick_new.base.recycler.viewmodel.RecyclerWidgetViewModel
import com.ssspvtltd.quick_new.model.ARG_ADD_ITEM_LIST
import com.ssspvtltd.quick_new.model.order.add.ItemsData
import com.ssspvtltd.quick_new.model.order.add.PackTypeData
import com.ssspvtltd.quick_new.model.order.add.additem.PackType
import com.ssspvtltd.quick_new.model.order.add.additem.PackTypeItem
import com.ssspvtltd.quick_new.model.progress.ProgressConfig
import com.ssspvtltd.quick_new.networking.ResultWrapper
import com.ssspvtltd.quick_new.ui.order.add.repositry.AddOrderRepositry
import com.ssspvtltd.quick_new.utils.SingleLiveEvent
import com.ssspvtltd.quick_new.utils.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddItemViewModel @Inject constructor(
    private val repository: AddOrderRepositry,
    savedStateHandle: SavedStateHandle,
) : RecyclerWidgetViewModel() {

    var packTypeDataList = listOf<PackType>()

    val packTypeSuggestions = MutableLiveData<List<PackTypeData>>()
    val packTypeItemSuggestions = MutableLiveData<List<ItemsData>>()

    var bottomSheetIndex = -1
    var bottomSheetPackData: PackTypeData? = null
    var bottomSheetPackQuantity: String? = null
    var bottomSheetPackAmount: String? = null
    val bottomSheetSubmitted = SingleLiveEvent<Boolean>()
    val bottomSheetPrefilledPackType = SingleLiveEvent<PackType>()

    init {
        packTypeDataList = savedStateHandle.get<ArrayList<PackType>>(ARG_ADD_ITEM_LIST).orEmpty()
    }

    fun getPackDataList() = viewModelScope.launch {
        clearWidgetList()
        addItemToWidgetList(packTypeDataList)
        listDataChanged()
    }

    fun getItemsList(purchasePartyId: String) = viewModelScope.launch {
        showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
        when (val response = repository.itemList(purchasePartyId)) {
            is ResultWrapper.Failure -> apiErrorData(response.error)
            is ResultWrapper.Success -> withContext(Dispatchers.Main) {
                response.value.data?.let {
                    packTypeItemSuggestions.postValue(it)
                    hideProgressBar()
                }
            }
        }
    }

    fun getPackType() = viewModelScope.launch {
        showProgressBar(ProgressConfig("Fetching Data\nPlease wait..."))
        when (val response = repository.packType()) {
            is ResultWrapper.Failure -> apiErrorData(response.error)
            is ResultWrapper.Success -> withContext(Dispatchers.Main) {
                response.value.data?.let {
                    packTypeSuggestions.postValue(it)
                    hideProgressBar()
                }
            }
        }
    }

    fun setupForEditBottomSheet() {
        val packType = packTypeDataList.getOrNull(bottomSheetIndex)
        packType?.let { bottomSheetPrefilledPackType.postValue(it) }
    }

    fun submitData(packTypeItems: List<PackTypeItem>) = viewModelScope.launch {
        Log.i("TaG","add item -=-=-=-=-=-=-=-= ${packTypeItems}")

        if (bottomSheetPackData?.id.isNullOrBlank() || bottomSheetPackData?.value.isNullOrBlank()) {
            showToast("Please select a pack type")
        } else if ((bottomSheetPackAmount ?: "0").toDouble() <= 0) {
            showToast("Please select pack type amount")
        } else if (packTypeItems.any {
                it.itemName.isNullOrBlank() || it.itemID.isNullOrBlank() || (it.itemQuantity ?: "0").toDouble() <= 0
            }) {

            showToast("Please input all fields")
        } else {
            withContext(Dispatchers.Default) {
                val packTypeNew = PackType(
                    pcsId = bottomSheetPackData?.id!!,
                    packName = bottomSheetPackData?.value,
                    amount = bottomSheetPackAmount,
                    qty = bottomSheetPackQuantity,
                    itemDetail = packTypeItems
                )
                if (bottomSheetIndex in 0..packTypeItems.lastIndex) {
                    val itemIndexIfExist =
                        packTypeDataList.indexOfFirst { it.pcsId == packTypeNew.pcsId }
                    var indexToRemove: Int = -1
                    val indexToReplace: Int
                    if (itemIndexIfExist > -1 && bottomSheetIndex != itemIndexIfExist) {
                        indexToRemove = bottomSheetIndex
                        indexToReplace = itemIndexIfExist
                    } else {
                        indexToReplace = bottomSheetIndex
                    }
                    packTypeDataList = packTypeDataList.mapIndexed { index, packType ->
                        if (index == indexToReplace) packTypeNew
                        else packType.copy()
                    }
                    if (indexToRemove > -1) {
                        packTypeDataList = packTypeDataList
                            .filterIndexed { index, _ -> index != indexToRemove }
                    }
                } else {
                    val itemIndexIfExist =
                        packTypeDataList.indexOfFirst { it.pcsId == packTypeNew.pcsId }
                    packTypeDataList = if (itemIndexIfExist > -1) {
                        packTypeDataList.mapIndexed { index, packType ->
                            if (index == itemIndexIfExist) packTypeNew
                            else packType.copy()
                        }
                    } else {
                        packTypeDataList + packTypeNew
                    }
                }

                withContext(Dispatchers.Main) {
                    bottomSheetSubmitted.postValue(true)
                    getPackDataList()
                }
            }
        }
    }

    fun deletePackTypeData(packType: PackType) {
        packTypeDataList = packTypeDataList.filter { it.pcsId != packType.pcsId }
        getPackDataList()
    }
}

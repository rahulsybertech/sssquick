package com.ssspvtltd.quick.ui.order.add.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ssspvtltd.quick.application.MainApplication
import com.ssspvtltd.quick.base.recycler.viewmodel.RecyclerWidgetViewModel
import com.ssspvtltd.quick.model.ARG_ADD_IMAGE_LIST
import com.ssspvtltd.quick.model.ImageViewType
import com.ssspvtltd.quick.model.order.add.addImage.ImageModel
import com.ssspvtltd.quick.utils.MediaUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddImageViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : RecyclerWidgetViewModel() {

    fun prepareList() = viewModelScope.launch(Dispatchers.Default) {
        val arrayList = savedStateHandle.get<ArrayList<ImageModel>>(ARG_ADD_IMAGE_LIST).orEmpty()
        val list = mutableListOf<ImageModel>()
        arrayList.forEach {
             val file = it.filePath?.let { it1 -> File(it1) }
            if (file?.exists() == true) {
                list.add(ImageModel(it.filePath, ImageViewType.IMAGE))
            }
         }
        list.add(ImageModel(null, ImageViewType.ADD_IMAGE))
        addItemToWidgetList(list)
        withContext(Dispatchers.Main) { listDataChanged() }
    }

    fun addFilesToList(vararg filePaths: Uri) = viewModelScope.launch(Dispatchers.Default) {
        val addIndex = widgetList.indexOfFirst { it.viewType == ImageViewType.ADD_IMAGE }
        if (addIndex > -1) {
            val list = mutableListOf<ImageModel>()
            filePaths.forEach {
                withContext(Dispatchers.IO) {
                    return@withContext MediaUtils.copyFileToInternalStorage(
                        it, MainApplication.localeContext
                    )
                }?.let {
                    list.add(ImageModel(it, ImageViewType.IMAGE))
                }
            }
            addItemToWidgetList(addIndex, list)
            withContext(Dispatchers.Main) { listDataChanged() }
        }
    }

    fun deleteFileFromList(imageModel: ImageModel) = viewModelScope.launch(Dispatchers.Default) {
        withContext(Dispatchers.IO) {
            MediaUtils.deleteFile(MainApplication.localeContext, imageModel.filePath)
        }
        removeItemFromWidgetList { it is ImageModel && it == imageModel }
        withContext(Dispatchers.Main) { listDataChanged() }
    }

    fun getImageModelList(): ArrayList<ImageModel> {
        val arrayList = ArrayList<ImageModel>()
        widgetList.forEach {
            if (it.viewType == ImageViewType.IMAGE && it is ImageModel) arrayList.add(it)
        }
        return arrayList
    }
}
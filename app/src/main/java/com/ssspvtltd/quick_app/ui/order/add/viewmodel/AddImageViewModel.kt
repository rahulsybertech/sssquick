package com.ssspvtltd.quick_app.ui.order.add.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.ssspvtltd.quick_app.application.MainApplication
import com.ssspvtltd.quick_app.base.recycler.viewmodel.RecyclerWidgetViewModel
import com.ssspvtltd.quick_app.model.ARG_ADD_IMAGE_LIST
import com.ssspvtltd.quick_app.model.ImageViewType
import com.ssspvtltd.quick_app.model.order.add.addImage.ImageModel
import com.ssspvtltd.quick_app.utils.MediaUtils
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

    fun setEditImageList(uris: List<Uri>) = viewModelScope.launch(Dispatchers.Default) {
        val data = mutableListOf<ImageModel>()
        uris.forEach { uri ->
            val filePath = uri.path  // This returns the file path as a string
            val file = filePath?.let { File(it) }
            data.add(ImageModel(filePath, ImageViewType.IMAGE))
            /*if (file?.exists() == true) {

            }*/
        }
        val addIndex = data.indexOfFirst { it.viewType == ImageViewType.IMAGE }
        val list = mutableListOf<ImageModel>()
        uris.forEach {
            withContext(Dispatchers.IO) {
                list.add(ImageModel(it.toString(), ImageViewType.IMAGE))
            }
        }
        addItemToWidgetList(addIndex, list)
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

    fun deleteFileFromList(imageModel: ImageModel, position: Int) = viewModelScope.launch(Dispatchers.Default) {
        withContext(Dispatchers.IO) {
            MediaUtils.deleteFile(MainApplication.localeContext, imageModel.filePath)
        }
        //removeItemFromWidgetList { it is ImageModel && it == imageModel }
        removeItemFromWidgetList(position)
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
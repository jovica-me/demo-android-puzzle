package me.jovica.myapplication

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel:ViewModel() {


    private val _photos = MutableStateFlow(photos)

    fun reset() {
        _photos.value = photos
        _photos.value = _photos.value.shuffled()
    }
     fun swapItems(imageId1: Int, imageId2: Int) {
        val list = _photos.value.toMutableList()
        val index1 = list.indexOfFirst { it.id == imageId1 }
        val index2 = list.indexOfFirst { it.id == imageId2 }
        val temp = list[index1]
        list[index1] = list[index2]
        list[index2] = temp
        _photos.value = list
    }

    val photosList = _photos.asStateFlow()

    init {
        reset()
    }
}
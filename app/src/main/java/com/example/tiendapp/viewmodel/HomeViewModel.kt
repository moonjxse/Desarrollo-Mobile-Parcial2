package com.example.tiendapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tiendapp.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {

    private val carouselImages = listOf(
        R.drawable.logo,
        R.drawable.tecno,
        R.drawable.tecno3
    )


    private val _currentImageIndex = MutableStateFlow(0)
    val currentImageIndex: StateFlow<Int> = _currentImageIndex.asStateFlow()

    fun getCarouselImages(): List<Int> = carouselImages

    fun nextImage() {
        _currentImageIndex.value = (_currentImageIndex.value + 1) % carouselImages.size
    }

    fun previousImage() {
        val newIndex = _currentImageIndex.value - 1
        _currentImageIndex.value = if (newIndex < 0) carouselImages.size - 1 else newIndex
    }

    fun goToImage(index: Int) {
        if (index in carouselImages.indices) {
            _currentImageIndex.value = index
        }
    }
}

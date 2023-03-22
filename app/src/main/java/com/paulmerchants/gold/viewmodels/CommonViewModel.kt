package com.paulmerchants.gold.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CommonViewModel @Inject constructor() : ViewModel() {
    init {

    }

    override fun onCleared() {
        super.onCleared()
    }
}
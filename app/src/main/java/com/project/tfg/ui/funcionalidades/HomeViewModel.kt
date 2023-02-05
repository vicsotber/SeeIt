package com.project.tfg.ui.funcionalidades

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Este es el Fragment de Funcionalidades"
    }
    val text: LiveData<String> = _text
}
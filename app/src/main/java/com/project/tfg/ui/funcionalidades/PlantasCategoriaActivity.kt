package com.project.tfg.ui.funcionalidades

import android.net.Uri
import android.os.Bundle
import com.project.tfg.R

class PlantasCategoriaActivity : CategoriaEspecificaActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title = getString(R.string.category_plants)
    }

    override fun functionality(data: Uri) {
        super.categoryRecognitionMethod(data, "plants.tflite")
    }
}
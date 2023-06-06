package com.project.tfg.ui.funcionalidades

import android.net.Uri
import android.os.Bundle
import com.project.tfg.R

class AvesCategoriaActivity : CategoriaEspecificaActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title = getString(R.string.category_birds)
    }

    override fun functionality(data: Uri) {
        super.categoryRecognitionMethod(data, "birds.tflite")
    }
}
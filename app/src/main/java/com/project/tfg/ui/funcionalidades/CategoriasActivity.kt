package com.project.tfg.ui.funcionalidades

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.tfg.R

class CategoriasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.nombre_funcionalidad_categorias)

        setContentView(R.layout.activity_categorias)
    }
}
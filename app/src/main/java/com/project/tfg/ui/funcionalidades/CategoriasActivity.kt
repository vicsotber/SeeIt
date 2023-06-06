package com.project.tfg.ui.funcionalidades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.project.tfg.R

class CategoriasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.nombre_funcionalidad_categorias)

        setContentView(R.layout.activity_categorias)

        val foodView: View? = findViewById(R.id.category_food_view)
        foodView?.setOnClickListener {
            val foodCategory = Intent(this, ComidaCategoriaActivity::class.java)
            startActivity(foodCategory)
        }

        val birdsView: View? = findViewById(R.id.category_birds_view)
        birdsView?.setOnClickListener {
            val birdsCategory = Intent(this, AvesCategoriaActivity::class.java)
            startActivity(birdsCategory)
        }

        val insectsView: View? = findViewById(R.id.category_insects_view)
        insectsView?.setOnClickListener {
            val insectsCategory = Intent(this, InsectosCategoriaActivity::class.java)
            startActivity(insectsCategory)
        }

        val plantsView: View? = findViewById(R.id.category_plants_view)
        plantsView?.setOnClickListener {
            val plantasCategory = Intent(this, PlantasCategoriaActivity::class.java)
            startActivity(plantasCategory)
        }
    }
}
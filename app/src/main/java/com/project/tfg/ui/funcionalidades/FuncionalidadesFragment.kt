package com.project.tfg.ui.funcionalidades

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.project.tfg.R
import com.project.tfg.databinding.FragmentFuncionalidadesBinding

class FuncionalidadesFragment : Fragment() {

    private var _binding: FragmentFuncionalidadesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFuncionalidadesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(views:View, savedInstanceState: Bundle?)
    {
        val categoriasView: View? = view?.findViewById(R.id.categorias_view)
        categoriasView?.setOnClickListener {
            val categorias = Intent(this.context, CategoriasActivity::class.java)
            startActivity(categorias)
        }

        val textoView: View? = view?.findViewById(R.id.texto_view)
        textoView?.setOnClickListener {
            val texto = Intent(this.context, TextoActivity::class.java)
            startActivity(texto)
        }

        val escenaView: View? = view?.findViewById(R.id.escena_view)
        escenaView?.setOnClickListener {
            val escena = Intent(this.context, EscenaActivity::class.java)
            startActivity(escena)
        }

        val traduccionView: View? = view?.findViewById(R.id.traducir_view)
        traduccionView?.setOnClickListener {
            val traducir = Intent(this.context, TraducirActivity::class.java)
            startActivity(traducir)
        }
    }
}
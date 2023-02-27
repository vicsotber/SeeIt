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
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(views:View, savedInstanceState: Bundle?)
    {
        val categoriasView: View? = view?.findViewById(R.id.CategoriasView)
        categoriasView?.setOnClickListener(View.OnClickListener {
            val categorias = Intent(this.context, CategoriasActivity::class.java)
            startActivity(categorias)
        })

        val textoView: View? = view?.findViewById(R.id.TextoView)
        textoView?.setOnClickListener(View.OnClickListener {
            val texto = Intent(this.context, TextoActivity::class.java)
            startActivity(texto)
        })

        val escenaView: View? = view?.findViewById(R.id.EscenaView)
        escenaView?.setOnClickListener(View.OnClickListener {
            val escena = Intent(this.context, EscenaActivity::class.java)
            startActivity(escena)
        })
    }
}
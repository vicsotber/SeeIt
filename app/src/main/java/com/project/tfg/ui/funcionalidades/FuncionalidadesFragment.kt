package com.project.tfg.ui.funcionalidades

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tfg.R
import com.example.tfg.databinding.FragmentFuncionalidadesBinding

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

        //val homeViewModel =
        //    ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentFuncionalidadesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //val funcionalidad_escena_nombre: TextView = binding.textHome
        //homeViewModel.text.observe(viewLifecycleOwner) {
        //    funcionalidad_escena_nombre.text = it
        //}

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
    }

}
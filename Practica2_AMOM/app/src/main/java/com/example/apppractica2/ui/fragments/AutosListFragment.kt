package com.example.apppractica2.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apppractica2.R
import com.example.apppractica2.application.AutoRFApp
import com.example.apppractica2.data.AutoRepository
import com.example.apppractica2.databinding.FragmentAutosListBinding
import com.example.apppractica2.ui.adapters.AutoAdapter
import com.example.apppractica2.utils.Constants
import kotlinx.coroutines.launch
import com.example.apppractica2.utils.isNetworkAvailable


class AutosListFragment : Fragment() {

    private var _binding: FragmentAutosListBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: AutoRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAutosListBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Aquí ya está el fragment en pantalla
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Se instancia el repositorio desde la clase AutoRFApp
        repository = (requireActivity().application as AutoRFApp).repository

        lifecycleScope.launch {
            if (!isNetworkAvailable(requireContext())) {
                Toast.makeText(requireContext(), R.string.connection_error, Toast.LENGTH_SHORT).show()
                binding.pbLoading.visibility = View.GONE
                return@launch
            }

            try {
                val autos = repository.getAutosApiary()

                binding.rvAutos.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = AutoAdapter(autos, requireContext()) { selectedAuto ->
                        // Click de cada auto
                        Log.d(Constants.LOGTAG, getString(R.string.log_click_auto, selectedAuto.name))

                        // Se pasa al siguiente fragment con el id del auto seleccionado
                        selectedAuto.id?.let { id ->
                            requireActivity().supportFragmentManager.beginTransaction()
                                .replace(
                                    R.id.fragment_container,
                                    AutosDetailFragment.newInstance(id)
                                )
                                .addToBackStack(null)
                                .commit()
                        }
                    }
                }

            } catch (_: Exception) {

            } finally {
                binding.pbLoading.visibility = View.GONE
            }
        }
    }

    // Evitar fugas de memoria
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
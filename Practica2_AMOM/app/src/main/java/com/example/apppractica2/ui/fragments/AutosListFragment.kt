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
import com.example.apppractica2.utils.NetworkUtils
import kotlinx.coroutines.launch


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

        repository = (requireActivity().application as AutoRFApp).repository

        // Listener botón "Intentar de nuevo"
        binding.btnRetry.setOnClickListener {
            cargarAutos()
        }

        // Primera carga
        cargarAutos()
    }

    private fun cargarAutos() {
        binding.pbLoading.visibility = View.VISIBLE
        binding.rvAutos.visibility = View.GONE
        binding.noInternetLayout.visibility = View.GONE

        lifecycleScope.launch {
            if (!NetworkUtils.isNetworkAvailable(requireContext())) {
                binding.pbLoading.visibility = View.GONE
                binding.noInternetLayout.visibility = View.VISIBLE
                return@launch
            }

            try {
                val autos = repository.getAutosApiary()

                binding.rvAutos.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = AutoAdapter(autos, requireContext()) { selectedAuto ->
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

                binding.rvAutos.visibility = View.VISIBLE
                binding.noInternetLayout.visibility = View.GONE

            } catch (e: Exception) {
                Log.e("AutosListFragment", getString(R.string.log_loading_error), e)
                Toast.makeText(requireContext(), R.string.error_loading_data, Toast.LENGTH_SHORT).show()
                binding.noInternetLayout.visibility = View.VISIBLE

            } finally {
                binding.pbLoading.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
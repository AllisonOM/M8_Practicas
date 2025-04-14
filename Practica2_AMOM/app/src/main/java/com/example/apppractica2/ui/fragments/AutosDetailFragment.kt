package com.example.apppractica2.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.apppractica2.R
import com.example.apppractica2.application.AutoRFApp
import com.example.apppractica2.data.AutoRepository
import com.example.apppractica2.databinding.FragmentAutosDetailBinding
import com.example.apppractica2.databinding.FragmentAutosListBinding
import com.example.apppractica2.utils.Constants
import kotlinx.coroutines.launch

private const val ARG_AUTOID = "id"

class AutosDetailFragment : Fragment() {

    private var _binding: FragmentAutosDetailBinding? = null
    private val binding get() = _binding!!

    private var autoId: String? = null

    private lateinit var repository: AutoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            autoId = args.getString(ARG_AUTOID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Aquí se infla la vista correspondiente
        _binding = FragmentAutosDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Se instancia el repositorio desde la clase AutoRFApp
        repository = (requireActivity().application as AutoRFApp).repository

        lifecycleScope.launch {
            try {
                val autoDetail = repository.getAutosDetailApiary(autoId)
                binding.apply {
                    tvTitle.text = autoDetail.title
                    Glide.with(requireActivity())
                        .load(autoDetail.image)
                        .into(ivImage)
                    tvBrandD.text = autoDetail.brand
                    tvModelD.text = autoDetail.model
                    tvVersionD.text = autoDetail.version
                    tvPowerD.text = autoDetail.power
                    tvAccelerationD.text = autoDetail.acceleration
                    tvTopSpeedD.text = autoDetail.topSpeed
                    tvDrivetrainD.text = autoDetail.drivetrain
                    tvEngineD.text = autoDetail.engine
                }

            } catch (_: Exception) {

            } finally {
                binding.pbLoading.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(id: String) =
            // Se instancia al fragment
            AutosDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_AUTOID, id)
                }
            }
    }
}
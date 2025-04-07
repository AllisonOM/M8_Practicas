package com.example.apppractica1.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.apppractica1.R
import com.example.apppractica1.application.AutosBDApp
import com.example.apppractica1.data.AutoRepository
import com.example.apppractica1.data.db.model.AutoEntity
import com.example.apppractica1.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Para el listado de autos a leer en la bd
    private var autos: MutableList<AutoEntity> = mutableListOf()
    // Para el repositorio
    private lateinit var repository: AutoRepository

    // Para el adapter del recycler view
    private lateinit var autoAdapter: AutoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        repository = (application as AutosBDApp).repository

        autoAdapter = AutoAdapter { selectedAuto ->
            // Click a un registro de auto
//            Toast.makeText(
//                this,
//                "Click en el auto ${selectedAuto.brand}",
//                Toast.LENGTH_SHORT
//            ).show()

            val dialog = AutoDialog(newAuto = false, auto = selectedAuto) {
                updateUI()
            }

            dialog.show(supportFragmentManager, "dialog2")

        }

        binding.apply {
            rvGames.layoutManager = LinearLayoutManager(this@MainActivity)
            rvGames.adapter = autoAdapter
        }

        updateUI()
    }

    private fun updateUI() {
        lifecycleScope.launch {
            // Obtenemos todos los juegos
            autos = repository.getAllAutos()

            // Indicar que no hay registros
            binding.tvSinRegistros.visibility =
                if (autos.isNotEmpty()) View.INVISIBLE else View.VISIBLE

            autoAdapter.updateList(autos)
        }
    }

    fun click(view: View){
//        val auto = AutoEntity(
//            brand = "Kia",
//            model = "Rio",
//            color = "Gray",
//            year = "2019"
//        )
//
//        lifecycleScope.launch(Dispatchers.IO) {
//            repository.insertAuto(auto)
//        }
//
//        updateUI()

        // Mostrar el dialogo
        val dialog = AutoDialog{
            updateUI()
        }
        dialog.show(supportFragmentManager, "dialog1")
    }
}
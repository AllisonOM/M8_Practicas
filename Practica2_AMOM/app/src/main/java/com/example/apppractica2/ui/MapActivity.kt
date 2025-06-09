package com.example.apppractica2.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.apppractica2.R
import android.Manifest
import androidx.appcompat.app.AlertDialog
import android.widget.Toast
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.hardware.camera2.CameraDevice.CameraDeviceSetup
import android.widget.ImageButton
import com.example.apppractica2.databinding.ActivityMainBinding
import com.example.apppractica2.databinding.ActivityMapBinding
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapBinding

    // Permiso de localización
    private var fineLocationPermissionGranted = false

    // Para GoogleMaps
    private lateinit var googleMap: GoogleMap

    private val permissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted ->
        if(isGranted){
            //Se concedió el permiso
            actionPermissionGranted()
        }else{
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.permission_required_title))
                    .setMessage(getString(R.string.permission_required_message))
                    .setPositiveButton(getString(R.string.understood_button)) { _, _ ->
                        updateOrRequestPermissions()
                    }
                    .setNegativeButton(getString(R.string.exit_button)) { dialog, _ ->
                        dialog.dismiss()
                        finish()
                    }
                    .create()
                    .show()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.location_permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Usamos esto en lugar de parentFragmentManager
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun actionPermissionGranted() {  }

    private fun updateOrRequestPermissions() {
        //Revisando el permiso
        fineLocationPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineLocationPermissionGranted) {
            //Pedimos el permiso
            permissionsLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }else{
            //Tenemos los permisos
            actionPermissionGranted()
        }

    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        val cLatitude = intent.getStringExtra(getString(R.string.extra_latitude_key))?.toDoubleOrNull()
        val cLongitude = intent.getStringExtra(getString(R.string.extra_longitude_key))?.toDoubleOrNull()

        if (cLatitude != null && cLongitude != null) {
            val coordinates = LatLng(cLatitude, cLongitude)
            createMarker(coordinates)
        } else {
            Toast.makeText(this, getString(R.string.invalid_coordinates_map), Toast.LENGTH_SHORT).show()
        }
    }

    private fun createMarker(coordinates: LatLng) {
        val marker = MarkerOptions()
            .position(coordinates)
            .title(getString(R.string.marker_title))
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.logo_img))

        googleMap.addMarker(marker)
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordinates, 16f),
            1_500,
            null
        )
    }
}
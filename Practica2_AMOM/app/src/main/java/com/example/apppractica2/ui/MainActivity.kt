package com.example.apppractica2.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.apppractica2.R
import com.example.apppractica2.databinding.ActivityMainBinding
import com.example.apppractica2.ui.fragments.AutosListFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var internetLayout: RelativeLayout
    private lateinit var noInternetLayout: RelativeLayout
    private lateinit var tryAgainButton: Button

    // Para Firebase Auth
    private lateinit var firebaseAuth: FirebaseAuth
    private var user: FirebaseUser? = null

    @SuppressLint("DetachAndAttachSameFragment")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()

        // SplashScreen
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {true}

        CoroutineScope(Dispatchers.Main).launch {
            delay(1500L)
            splashScreen.setKeepOnScreenCondition {false}
        }

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        internetLayout = findViewById(R.id.internetLayout)
        noInternetLayout = findViewById(R.id.noInternetLayout)
        tryAgainButton = findViewById(R.id.try_again_button)

        drawLayout()

        tryAgainButton.setOnClickListener {
            if (isNetworkAvailable()) {
                drawLayout()
                val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
                currentFragment?.let {
                    supportFragmentManager.beginTransaction()
                        .detach(it)
                        .attach(it)
                        .commit()
                }
            } else {
                Toast.makeText(this, R.string.connection_error, Toast.LENGTH_SHORT).show()
            }
        }


        // Primera ejecución de la actividad
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    AutosListFragment()
                )
                .commit()
        }

        // Instanciamos el objeto firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        user = firebaseAuth.currentUser

        binding.btnlogout.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, Login::class.java))
            message(getString(R.string.logout_success))
            finish()
        }
    }

    private fun drawLayout() {
        if (isNetworkAvailable()) {
            internetLayout.visibility = VISIBLE
            noInternetLayout.visibility = GONE

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AutosListFragment())
                .commit()

        } else {
            noInternetLayout.visibility = VISIBLE
            internetLayout.visibility = GONE
        }
    }

    @SuppressLint("ServiceCast")
    private fun isNetworkAvailable(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
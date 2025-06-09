package com.example.apppractica2.ui

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.apppractica2.R
import com.example.apppractica2.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    // Para Firebase Auth
    private lateinit var firebaseAuth: FirebaseAuth

    // Pripiedades para el email y la contraseña
    private var email = ""
    private var contrasena = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }

        // Bloque a la vista y especifica la vista en modo retrato
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        // Se instancia el objeto firebae auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Si el usuario estaba previamente autenticado se envia directamente a la pantalla principal
        if(firebaseAuth.currentUser != null)
            actionLoginSuccessful()

        binding.btnLogin.setOnClickListener {
            if(!validateFields()) return@setOnClickListener

            binding.progressBar.visibility = View.VISIBLE
            authenticateUser(email, contrasena)
        }

        binding.btnRegistrarse.setOnClickListener {
            if(!validateFields()) return@setOnClickListener

            binding.progressBar.visibility = View.VISIBLE
            createUser(email, contrasena)
        }

        binding.tvRestablecerPassword.setOnClickListener {
            resetPassword()
        }
    }

    private fun validateFields(): Boolean{
        email = binding.tietEmail.text.toString().trim()  //Elimina los espacios en blanco
        contrasena = binding.tietContrasena.text.toString().trim()

        //Verifica que el campo de correo no esté vacío
        if(email.isEmpty()){
            binding.tietEmail.error = getString(R.string.required_email)
            binding.tietEmail.requestFocus()
            return false
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.tietEmail.error = getString(R.string.invalid_email_format)
            binding.tietEmail.requestFocus()
            return false
        }

        //Verifica que el campo de la contraseña no esté vacía y tenga al menos 6 caracteres
        if(contrasena.isEmpty()){
            binding.tietContrasena.error = getString(R.string.required_password)
            binding.tietContrasena.requestFocus()
            return false
        }else if(contrasena.length < 6){
            binding.tietContrasena.error = getString(R.string.password_min_length)
            binding.tietContrasena.requestFocus()
            return false
        }
        return true
    }

    private fun handleErrors(task: Task<AuthResult>){
        var errorCode = ""

        try{
            errorCode = (task.exception as FirebaseAuthException).errorCode
        }catch(e: Exception){
            e.printStackTrace()
        }

        when(errorCode){
            "ERROR_INVALID_EMAIL" -> {
                message(getString(R.string.error_invalid_email))
                binding.tietEmail.error = getString(R.string.error_invalid_email)
                binding.tietEmail.requestFocus()
            }
            "ERROR_WRONG_PASSWORD" -> {
                message(getString(R.string.error_wrong_password))
                binding.tietContrasena.error = getString(R.string.error_wrong_password)
                binding.tietContrasena.requestFocus()
                binding.tietContrasena.setText("")

            }
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> {
                //An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.
                message(getString(R.string.error_existing_account_different_credential))
            }
            "ERROR_EMAIL_ALREADY_IN_USE" -> {
                message(getString(R.string.error_email_in_use))
                binding.tietEmail.error = getString(R.string.error_email_in_use)
                binding.tietEmail.requestFocus()
            }
            "ERROR_USER_TOKEN_EXPIRED" -> {
                message(getString(R.string.error_session_expired))
            }
            "ERROR_USER_NOT_FOUND" -> {
                message(getString(R.string.error_user_not_found))
            }
            "ERROR_WEAK_PASSWORD" -> {
                message(getString(R.string.error_weak_password))
                binding.tietContrasena.error = getString(R.string.error_password_too_short)
                binding.tietContrasena.requestFocus()
            }
            "NO_NETWORK" -> {
                message(getString(R.string.error_no_network))
            }
            else -> {
                message(getString(R.string.error_generic))
            }
        }
    }

    private fun actionLoginSuccessful(){
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun authenticateUser(usr: String, psw: String){
        firebaseAuth.signInWithEmailAndPassword(usr, psw).addOnCompleteListener{ authResult ->
            if(authResult.isSuccessful){
                message(getString(R.string.login_successful))
                actionLoginSuccessful()
            } else {
                // Para que no se muestre
                binding.progressBar.visibility = View.GONE
                handleErrors(authResult)
            }

        }
    }

    private fun createUser(usr: String, psw: String){
        firebaseAuth.createUserWithEmailAndPassword(usr, psw).addOnCompleteListener { authResult ->
            if(authResult.isSuccessful){
                // Sí se pudo registrar el usuario nuevo
                // Mandamos un correo de verificación
                firebaseAuth.currentUser?.sendEmailVerification()?.addOnSuccessListener {
                    message(getString(R.string.user_created))
                }?.addOnFailureListener {
                    message(getString(R.string.verification_email_sent))
                }
                message(getString(R.string.verification_email_failed))
                    actionLoginSuccessful()
            }else{
                binding.progressBar.visibility = View.GONE
                handleErrors(authResult)
            }
        }
    }

    private fun resetPassword(){
        // Genero un edit text programaticamente
        val resetMail = EditText(this)
        resetMail.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.reset_password_title))
            .setMessage(getString(R.string.reset_password_message))
            .setView(resetMail)
            .setPositiveButton(getString(R.string.reset_password_send)) { _, _ ->
                val mail = resetMail.text.toString()
                if(mail.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                    // Envio de enlace de establecimiento
                    firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener {
                        message(getString(R.string.reset_password_email_sent))
                    }.addOnFailureListener {
                        message(getString(R.string.reset_password_email_failed))
                    }
                }else {
                    message(getString(R.string.invalid_email_entered))
                }
            }
            .setNeutralButton(getString(R.string.reset_password_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}
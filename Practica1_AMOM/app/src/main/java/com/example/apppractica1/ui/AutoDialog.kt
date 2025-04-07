package com.example.apppractica1.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.apppractica1.application.AutosBDApp
import com.example.apppractica1.data.AutoRepository
import com.example.apppractica1.data.db.model.AutoEntity
import com.example.apppractica1.databinding.AutoDialogBinding
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOException

class AutoDialog(
    private val newAuto: Boolean = true,
    private var auto: AutoEntity = AutoEntity(
        brand = "",
        model = "",
        color = "",
        year = ""
    ),
    private val updateUI: () -> Unit
): DialogFragment() {

    // Para agregar viewbinding al fragment
    private var _binding: AutoDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var dialog: Dialog

    // Para poder acceder al botón de guardado
    private var saveButton: Button? = null

    // Para el repositorio
    private lateinit var repository: AutoRepository

    // Creación y ocnfiguración del diálogo
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        super.onCreateDialog(savedInstanceState)

        _binding = AutoDialogBinding.inflate(requireActivity().layoutInflater)

        binding.apply {
            tietTitle.setText(auto.brand)
            tietGenre.setText(auto.model)
            tietDeveloper.setText(auto.color)
        }

        dialog = if (newAuto)
            buildDialog("Guardar", "Cancelar", {
                // Guardar

                binding.apply {
                    auto.brand = tietTitle.text.toString()
                    auto.model = tietGenre.text.toString()
                    auto.color = tietDeveloper.text.toString()
//                    auto.year = tietDeveloper.text.toString()
                }

                try {
                    lifecycleScope.launch {

                        val result = async {
                            repository.insertAuto(auto)
                        }
                        result.await()

                        Toast.makeText(
                            requireContext(),
                            "Registro del auto guardado exitosamente",
                            Toast.LENGTH_SHORT
                        ).show()

                        updateUI()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(
                        requireContext(),
                        "Error al guardar el registro del auto",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }, {
                // Cancelar

            })
        else
            buildDialog("Actualizar", "Eliminar", {
                // Actuliazar
            }, {
                // Eliminar

            })

        return dialog
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    // Se llama después de que se muestra el diálogo en pantalla
    override fun onStart() {

        // Instanciamoento del repositorio
        repository = (requireContext().applicationContext as AutosBDApp).repository

        super.onStart()

        saveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        saveButton?.isEnabled = false

        binding.apply {
            setupTextWatcher(
                tietTitle,
                tietGenre,
                tietDeveloper
            )
        }

        binding.tietTitle.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                saveButton?.isEnabled = validateFields()
            }

        })

        binding.tietGenre.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                saveButton?.isEnabled = validateFields()
            }

        })

        binding.tietDeveloper.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                saveButton?.isEnabled = validateFields()
            }

        })
    }

    private fun validateFields(): Boolean =
        binding.tietTitle.text.toString().isNotEmpty()
                && binding.tietGenre.text.toString().isNotEmpty()
                && binding.tietDeveloper.text.toString().isNotEmpty()

    private fun setupTextWatcher(vararg textFields: TextInputEditText){
        val textWatcher = object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                saveButton?.isEnabled = validateFields()
            }
        }

        textFields.forEach { it.addTextChangedListener(textWatcher) }
    }

    private fun buildDialog(
        btn1Text: String,
        btn2Text: String,
        positiveButton: () -> Unit,
        negativeButton: () -> Unit,
    ): Dialog =
        AlertDialog.Builder(requireContext()).setView(binding.root)
            .setTitle("Auto")
            .setPositiveButton(btn1Text) { _, _ ->
                positiveButton()
            }
            .setNegativeButton(btn2Text) { _, _ ->
                negativeButton()
            }
            .create()
}
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
import com.example.apppractica1.R
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
    private val updateUI: () -> Unit,
    private val message: (String) -> Unit
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
            tietBrand.setText(auto.brand)
            tietModel.setText(auto.model)
            tietColor.setText(auto.color)
            tietYear.setText(auto.year)
        }

        dialog = if (newAuto)
            buildDialog(getString(R.string.save), getString(R.string.cancel), {

                // Guardar
                binding.apply {
                    auto.brand = tietBrand.text.toString()
                    auto.model = tietModel.text.toString()
                    auto.color = tietColor.text.toString()
                    auto.year = tietYear.text.toString()
                }

                try {
                    lifecycleScope.launch {

                        val result = async {
                            repository.insertAuto(auto)
                        }
                        result.await()

                        message(getString(R.string.register_save))

                        updateUI()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    message(getString(R.string.register_error))

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }, {
                // Cancelar

            })
        else
            buildDialog(getString(R.string.update), getString(R.string.delete), {

                // Actuliazar
                binding.apply {
                    auto.brand = tietBrand.text.toString()
                    auto.model = tietModel.text.toString()
                    auto.color = tietColor.text.toString()
                    auto.year = tietYear.text.toString()
                }

                try {
                    lifecycleScope.launch {

                        val result = async {
                            repository.updateAuto(auto)
                        }
                        result.await()

                        message(getString(R.string.register_update))

                        updateUI()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    message(getString(R.string.register_error_update))

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }, {

                // Eliminar

                // Guardado del contexto antes de la corrutina para que no se pierda
                val contrxt = requireContext()

                // Diálogo para confirmar la eliminación
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.confirmation))
                    .setMessage(getString(R.string.delete_auto_confirm, auto.brand))
                    .setPositiveButton(getString(R.string.accept)) { _, _ ->
                        try {
                            lifecycleScope.launch {

                                val result = async {
                                    repository.deleteAuto(auto)
                                }
                                result.await()

                                message(contrxt.getString(R.string.register_delete))

                                updateUI()
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                            message(getString(R.string.register_error_delete))

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    .create()
                    .show()

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
                tietBrand,
                tietModel,
                tietColor,
                tietYear
            )
        }

        binding.tietBrand.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                saveButton?.isEnabled = validateFields()
            }

        })

        binding.tietModel.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                saveButton?.isEnabled = validateFields()
            }

        })

        binding.tietColor.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                saveButton?.isEnabled = validateFields()
            }

        })

        binding.tietYear.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                saveButton?.isEnabled = validateFields()
            }

        })
    }

    private fun validateFields(): Boolean =
        binding.tietBrand.text.toString().isNotEmpty()
                && binding.tietModel.text.toString().isNotEmpty()
                && binding.tietColor.text.toString().isNotEmpty()
                && binding.tietYear.text.toString().isNotEmpty()

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
            .setTitle(getString(R.string.auto))
            .setPositiveButton(btn1Text) { _, _ ->
                positiveButton()
            }
            .setNegativeButton(btn2Text) { _, _ ->
                negativeButton()
            }
            .create()
}
package com.example.apppractica2.data

import com.example.apppractica2.data.remote.AutosApi
import com.example.apppractica2.data.remote.model.AutoDetailDto
import com.example.apppractica2.data.remote.model.AutoDto
import retrofit2.Retrofit

class AutoRepository (
    private val retrofit: Retrofit
) {

    // Creación de la instancia al api para acceder a los endpoints
    private val autosApi = retrofit.create(AutosApi::class.java)

    suspend fun getAutosApiary(): List<AutoDto> = autosApi.getAutosApiary()

    suspend fun getAutosDetailApiary(id: String?): AutoDetailDto = autosApi.getAutoDetailApiary(id)

}
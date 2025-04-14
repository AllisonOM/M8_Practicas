package com.example.apppractica2.data.remote

import com.example.apppractica2.data.remote.model.AutoDetailDto
import com.example.apppractica2.data.remote.model.AutoDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

// Análogo al Dao en Room
interface AutosApi {

    // Aquí se definen los endpoints a consumir

    @GET
    suspend fun getAutos(
        @Url
        url: String?
    ): List<AutoDto>


    // Listado de autos en apiary
    @GET("autos/autos_list")
    suspend fun getAutosApiary(): List<AutoDto>


    // Detalles de cada auto en apiary
    @GET("autos/autos_detail/{id}")
    suspend fun getAutoDetailApiary(
        @Path("id")
        id: String?
    ): AutoDetailDto

}
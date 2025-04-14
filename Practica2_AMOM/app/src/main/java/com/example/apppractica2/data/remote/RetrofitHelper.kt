package com.example.apppractica2.data.remote

import com.example.apppractica2.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Ayuda a obtener la instancia a retrofit
class RetrofitHelper {

    val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Respuesta al nivel del body en la operación de red
    }

    val client = OkHttpClient.Builder().apply {
        addInterceptor(interceptor)
    } .build()

    fun getRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

}
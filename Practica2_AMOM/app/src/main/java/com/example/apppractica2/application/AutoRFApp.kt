package com.example.apppractica2.application

import android.app.Application
import com.example.apppractica2.data.AutoRepository
import com.example.apppractica2.data.remote.RetrofitHelper

class AutoRFApp: Application() {

    private val retrofit by lazy {
        RetrofitHelper().getRetrofit()
    }

    val repository by lazy {
        AutoRepository(retrofit)
    }

}
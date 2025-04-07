package com.example.apppractica1.application

import android.app.Application
import com.example.apppractica1.data.AutoRepository
import com.example.apppractica1.data.db.AutoDataBase

class AutosBDApp: Application() {

    private val database by lazy {
        AutoDataBase.getDatabase(this@AutosBDApp)
    }

    val repository by lazy {
        AutoRepository(database.autoDao())
    }

}
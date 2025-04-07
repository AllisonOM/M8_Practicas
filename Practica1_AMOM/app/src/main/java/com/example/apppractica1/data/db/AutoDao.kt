package com.example.apppractica1.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.apppractica1.data.db.model.AutoEntity
import com.example.apppractica1.util.Constants

@Dao
interface AutoDao {

    // Create
    @Insert
    suspend fun insertAuto(auto: AutoEntity)

    @Insert
    suspend fun insertAuto(auto: MutableList<AutoEntity>)

    //  Read
    @Query("SELECT * FROM ${Constants.DATABASE_AUTO_TABLE}")
    suspend fun getAllAutos(): MutableList<AutoEntity>

    // Update
    @Update
    suspend fun updateAuto(auto: AutoEntity)

    @Update
    suspend fun updateAuto(auto: MutableList<AutoEntity>)

    // Delete
    @Delete
    suspend fun deleteAuto(auto: AutoEntity)

}
package com.example.apppractica1.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.apppractica1.util.Constants

@Entity(tableName = Constants.DATABASE_AUTO_TABLE)
data class AutoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "auto_id")
    var id: Long = 0,
    @ColumnInfo(name = "auto_brand")
    var brand: String,
    @ColumnInfo(name = "auto_model")
    var model: String,
    @ColumnInfo(name = "auto_color")
    var color: String,
    @ColumnInfo(name = "auto_year", defaultValue = "unknow")
    var year: String
)

package com.example.apppractica1.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.apppractica1.data.db.model.AutoEntity
import com.example.apppractica1.util.Constants

@Database(
    entities = [AutoEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AutoDataBase: RoomDatabase() {

    // Aquí va el DAO
    abstract fun autoDao(): AutoDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AutoDataBase? = null

        fun getDatabase(context: Context): AutoDataBase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AutoDataBase::class.java,
                    Constants.DATABASE_NAME
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

}
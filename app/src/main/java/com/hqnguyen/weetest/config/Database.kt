package com.hqnguyen.weetest.data.dao

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hqnguyen.weetest.data.MediaEntity

@Database(entities = [MediaEntity::class], exportSchema = false, version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun mediaDao(): MediaDao

    companion object {
        private val DB_NAME = "Local_Database"
        private var databaseManager: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (databaseManager == null) {
                databaseManager = Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return databaseManager!!
        }
    }
}

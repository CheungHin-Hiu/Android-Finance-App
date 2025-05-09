package com.example.androidfinanceapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AssetTotal::class], version = 1, exportSchema = false)
abstract class AssetTotalDatabase: RoomDatabase() {
    abstract fun assetTotalDao(): AssetTotalDao

    companion object {
        @Volatile var Instance: AssetTotalDatabase? = null

        fun getDatabase(context: Context): AssetTotalDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AssetTotalDatabase::class.java, "asset_total_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
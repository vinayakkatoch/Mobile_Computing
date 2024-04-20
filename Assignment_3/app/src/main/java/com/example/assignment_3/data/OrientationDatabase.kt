package com.example.assignment_3.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [OrientationEntity::class], version = 1)
abstract class OrientationDatabase: RoomDatabase() {
    abstract fun OrientationDao(): OrientationDao

    companion object{
        @Volatile
        private var INSTANCE: OrientationDatabase? = null

        fun getDatabase(context: Context): OrientationDatabase {
            val tempInstance = INSTANCE
            if(tempInstance != null) return tempInstance

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OrientationDatabase::class.java,
                    "orientation_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
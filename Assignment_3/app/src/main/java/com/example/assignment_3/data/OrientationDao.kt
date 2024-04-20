package com.example.assignment_3.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.assignment_3.data.OrientationEntity

@Dao
interface OrientationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(orientationEntity: OrientationEntity)

    @Query("SELECT * FROM orientation_data")
    suspend fun getAllOrientationData(): List<OrientationEntity>

    @Query("DELETE FROM orientation_data")
    suspend fun clearDatabase()
}
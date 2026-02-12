package com.challengetracker.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.challengetracker.data.local.database.entities.WeeklyReflectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeeklyReflectionDao {
    @Query("SELECT * FROM weekly_reflections WHERE challengeId = :challengeId ORDER BY weekNumber ASC")
    fun getReflectionsForChallenge(challengeId: String): Flow<List<WeeklyReflectionEntity>>

    @Query("SELECT * FROM weekly_reflections WHERE challengeId = :challengeId AND weekNumber = :weekNumber LIMIT 1")
    fun getReflectionForWeek(challengeId: String, weekNumber: Int): Flow<WeeklyReflectionEntity?>

    @Query("SELECT * FROM weekly_reflections WHERE challengeId = :challengeId AND weekNumber = :weekNumber LIMIT 1")
    suspend fun getReflectionForWeekOnce(challengeId: String, weekNumber: Int): WeeklyReflectionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReflection(reflection: WeeklyReflectionEntity)

    @Update
    suspend fun updateReflection(reflection: WeeklyReflectionEntity)

    @Query("DELETE FROM weekly_reflections WHERE challengeId = :challengeId")
    suspend fun deleteReflectionsForChallenge(challengeId: String)

    @Query("SELECT * FROM weekly_reflections")
    suspend fun getAllReflectionsOnce(): List<WeeklyReflectionEntity>
}

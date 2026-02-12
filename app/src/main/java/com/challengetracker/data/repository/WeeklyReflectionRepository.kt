package com.challengetracker.data.repository

import com.challengetracker.data.local.database.dao.WeeklyReflectionDao
import com.challengetracker.data.local.database.entities.WeeklyReflectionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeeklyReflectionRepository @Inject constructor(
    private val reflectionDao: WeeklyReflectionDao
) {
    fun getReflectionsForChallenge(challengeId: String): Flow<List<WeeklyReflectionEntity>> =
        reflectionDao.getReflectionsForChallenge(challengeId)

    fun getReflectionForWeek(challengeId: String, weekNumber: Int): Flow<WeeklyReflectionEntity?> =
        reflectionDao.getReflectionForWeek(challengeId, weekNumber)

    suspend fun saveReflection(reflection: WeeklyReflectionEntity) {
        val existing = reflectionDao.getReflectionForWeekOnce(reflection.challengeId, reflection.weekNumber)
        if (existing != null) {
            reflectionDao.updateReflection(reflection.copy(id = existing.id))
        } else {
            reflectionDao.insertReflection(reflection)
        }
    }

    suspend fun getAllReflectionsOnce(): List<WeeklyReflectionEntity> = reflectionDao.getAllReflectionsOnce()
}

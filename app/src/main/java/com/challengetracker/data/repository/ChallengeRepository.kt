package com.challengetracker.data.repository

import com.challengetracker.data.local.database.dao.ChallengeDao
import com.challengetracker.data.local.database.entities.ChallengeEntity
import com.challengetracker.data.local.database.entities.ChallengeStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChallengeRepository @Inject constructor(
    private val challengeDao: ChallengeDao
) {
    fun getAllChallenges(): Flow<List<ChallengeEntity>> = challengeDao.getAllChallenges()

    fun getActiveChallenges(): Flow<List<ChallengeEntity>> = challengeDao.getActiveChallenges()

    fun getUpcomingChallenges(): Flow<List<ChallengeEntity>> = challengeDao.getUpcomingChallenges()

    fun getCompletedAndArchived(): Flow<List<ChallengeEntity>> = challengeDao.getCompletedAndArchivedChallenges()

    fun getChallengeById(id: String): Flow<ChallengeEntity?> = challengeDao.getChallengeById(id)

    suspend fun getChallengeByIdOnce(id: String): ChallengeEntity? = challengeDao.getChallengeByIdOnce(id)

    fun getActiveChallengeCount(): Flow<Int> = challengeDao.getActiveChallengeCount()

    suspend fun createChallenge(challenge: ChallengeEntity) {
        challengeDao.insertChallenge(challenge)
    }

    suspend fun updateChallenge(challenge: ChallengeEntity) {
        challengeDao.updateChallenge(challenge.copy(modifiedAt = LocalDateTime.now()))
    }

    suspend fun deleteChallenge(id: String) {
        challengeDao.deleteChallengeById(id)
    }

    suspend fun archiveChallenge(id: String) {
        val challenge = challengeDao.getChallengeByIdOnce(id) ?: return
        challengeDao.updateChallenge(
            challenge.copy(
                status = ChallengeStatus.ARCHIVED,
                modifiedAt = LocalDateTime.now()
            )
        )
    }

    suspend fun updateChallengeStatuses() {
        val today = LocalDate.now()
        val challenges = challengeDao.getAllChallengesOnce()
        for (challenge in challenges) {
            val newStatus = when {
                challenge.status == ChallengeStatus.ARCHIVED -> ChallengeStatus.ARCHIVED
                today.isBefore(challenge.startDate) -> ChallengeStatus.UPCOMING
                today.isAfter(challenge.endDate) -> ChallengeStatus.COMPLETED
                else -> ChallengeStatus.ACTIVE
            }
            if (newStatus != challenge.status) {
                challengeDao.updateChallenge(
                    challenge.copy(status = newStatus, modifiedAt = LocalDateTime.now())
                )
            }
        }
    }

    suspend fun getAllChallengesOnce(): List<ChallengeEntity> = challengeDao.getAllChallengesOnce()
}

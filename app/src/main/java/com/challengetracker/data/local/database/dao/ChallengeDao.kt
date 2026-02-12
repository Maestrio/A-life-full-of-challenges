package com.challengetracker.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.challengetracker.data.local.database.entities.ChallengeEntity
import com.challengetracker.data.local.database.entities.ChallengeStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeDao {
    @Query("SELECT * FROM challenges ORDER BY startDate ASC")
    fun getAllChallenges(): Flow<List<ChallengeEntity>>

    @Query("SELECT * FROM challenges WHERE status = :status ORDER BY startDate ASC")
    fun getChallengesByStatus(status: ChallengeStatus): Flow<List<ChallengeEntity>>

    @Query("SELECT * FROM challenges WHERE status = 'ACTIVE' ORDER BY endDate ASC")
    fun getActiveChallenges(): Flow<List<ChallengeEntity>>

    @Query("SELECT * FROM challenges WHERE status = 'UPCOMING' ORDER BY startDate ASC")
    fun getUpcomingChallenges(): Flow<List<ChallengeEntity>>

    @Query("SELECT * FROM challenges WHERE status IN ('COMPLETED', 'ARCHIVED') ORDER BY endDate DESC")
    fun getCompletedAndArchivedChallenges(): Flow<List<ChallengeEntity>>

    @Query("SELECT * FROM challenges WHERE id = :id")
    fun getChallengeById(id: String): Flow<ChallengeEntity?>

    @Query("SELECT * FROM challenges WHERE id = :id")
    suspend fun getChallengeByIdOnce(id: String): ChallengeEntity?

    @Query("SELECT COUNT(*) FROM challenges WHERE status = 'ACTIVE'")
    fun getActiveChallengeCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(challenge: ChallengeEntity)

    @Update
    suspend fun updateChallenge(challenge: ChallengeEntity)

    @Delete
    suspend fun deleteChallenge(challenge: ChallengeEntity)

    @Query("DELETE FROM challenges WHERE id = :id")
    suspend fun deleteChallengeById(id: String)

    @Query("SELECT * FROM challenges")
    suspend fun getAllChallengesOnce(): List<ChallengeEntity>
}

package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserFlow(id: Long = 1L): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUser(id: Long = 1L): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)
}

@Dao
interface JobDao {
    @Query("SELECT * FROM jobs ORDER BY createdAt DESC")
    fun getAllJobs(): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE status = 'PENDING' ORDER BY urgencyLevel DESC, distanceKm ASC")
    fun getAvailableJobs(): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE clientId = :clientId ORDER BY createdAt DESC")
    fun getClientJobs(clientId: Long): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE workerId = :workerId AND (status = 'ACCEPTED' OR status = 'WORKER_COMPLETED' OR status = 'DISPUTED') LIMIT 1")
    fun getWorkerActiveJob(workerId: Long): Flow<JobEntity?>

    @Query("SELECT * FROM jobs WHERE workerId = :workerId AND status = 'COMPLETED' ORDER BY createdAt DESC")
    fun getWorkerHistory(workerId: Long): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE id = :id LIMIT 1")
    fun getJobById(id: Long): Flow<JobEntity?>

    @Query("SELECT * FROM jobs WHERE id = :id LIMIT 1")
    suspend fun getJobDirect(id: Long): JobEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: JobEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(jobs: List<JobEntity>)

    @Update
    suspend fun updateJob(job: JobEntity)
}

@Dao
interface TradeProfileDao {
    @Query("SELECT * FROM trade_profiles WHERE isActive = 1 ORDER BY rating DESC")
    fun getAllActiveProfiles(): Flow<List<TradeProfileEntity>>

    @Query("SELECT * FROM trade_profiles WHERE tradeCategory = :category AND isActive = 1 ORDER BY rating DESC")
    fun getProfilesByCategory(category: String): Flow<List<TradeProfileEntity>>

    @Query("SELECT * FROM trade_profiles WHERE workerId = :workerId ORDER BY id DESC")
    fun getMyTradeProfiles(workerId: Long): Flow<List<TradeProfileEntity>>

    @Query("SELECT * FROM trade_profiles WHERE id = :id LIMIT 1")
    fun getProfileById(id: Long): Flow<TradeProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: TradeProfileEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(profiles: List<TradeProfileEntity>)

    @Update
    suspend fun updateProfile(profile: TradeProfileEntity)

    @Query("DELETE FROM trade_profiles WHERE id = :id")
    suspend fun deleteProfile(id: Long)
}

@Dao
interface ServiceRequestDao {
    @Query("SELECT * FROM service_requests WHERE clientId = :clientId ORDER BY createdAt DESC")
    fun getClientRequests(clientId: Long): Flow<List<ServiceRequestEntity>>

    @Query("SELECT * FROM service_requests WHERE workerId = :workerId ORDER BY createdAt DESC")
    fun getWorkerRequests(workerId: Long): Flow<List<ServiceRequestEntity>>

    @Query("SELECT * FROM service_requests WHERE id = :id LIMIT 1")
    fun getRequestById(id: Long): Flow<ServiceRequestEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: ServiceRequestEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(requests: List<ServiceRequestEntity>)

    @Update
    suspend fun updateRequest(request: ServiceRequestEntity)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()
}

@Dao
interface ReviewDao {
    @Query("SELECT * FROM reviews WHERE workerId = :workerId ORDER BY id DESC")
    fun getReviewsForWorker(workerId: Long): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reviews: List<ReviewEntity>)
}

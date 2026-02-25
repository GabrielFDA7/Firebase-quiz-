package com.quizapp.firebase.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.quizapp.firebase.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operações de banco de dados do usuário.
 * Usa Flow para observar mudanças reativas nos dados.
 */
@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE uid = :uid")
    suspend fun getUserById(uid: String): UserEntity?

    @Query("SELECT * FROM users WHERE uid = :uid")
    fun observeUser(uid: String): Flow<UserEntity?>

    @Query("SELECT * FROM users ORDER BY bestScore DESC")
    fun getAllUsersRanked(): Flow<List<UserEntity>>

    @Query("DELETE FROM users WHERE uid = :uid")
    suspend fun deleteUser(uid: String)

    @Query("UPDATE users SET totalQuizzes = totalQuizzes + 1, totalCorrect = totalCorrect + :correct, bestScore = CASE WHEN :score > bestScore THEN :score ELSE bestScore END WHERE uid = :uid")
    suspend fun updateStats(uid: String, correct: Int, score: Int)
}

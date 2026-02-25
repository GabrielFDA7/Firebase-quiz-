package com.quizapp.firebase.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.quizapp.firebase.data.local.entity.QuizResultEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operações com os resultados de quiz.
 * Fornece queries para histórico pessoal e estatísticas.
 */
@Dao
interface QuizResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: QuizResultEntity): Long

    @Query("SELECT * FROM quiz_results WHERE userId = :userId ORDER BY timestamp DESC")
    fun getResultsByUser(userId: String): Flow<List<QuizResultEntity>>

    @Query("SELECT * FROM quiz_results WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentResults(userId: String, limit: Int): Flow<List<QuizResultEntity>>

    @Query("SELECT COUNT(*) FROM quiz_results WHERE userId = :userId")
    fun getTotalQuizCount(userId: String): Flow<Int>

    @Query("SELECT AVG(percentage) FROM quiz_results WHERE userId = :userId")
    fun getAverageScore(userId: String): Flow<Double?>

    @Query("SELECT MAX(score) FROM quiz_results WHERE userId = :userId")
    fun getBestScore(userId: String): Flow<Int?>

    @Query("SELECT SUM(correctAnswers) FROM quiz_results WHERE userId = :userId")
    fun getTotalCorrectAnswers(userId: String): Flow<Int?>

    @Query("SELECT SUM(totalQuestions) FROM quiz_results WHERE userId = :userId")
    fun getTotalQuestionsAnswered(userId: String): Flow<Int?>

    @Query("DELETE FROM quiz_results WHERE userId = :userId")
    suspend fun deleteResultsByUser(userId: String)
}

package com.quizapp.firebase.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.quizapp.firebase.data.local.entity.QuestionEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operações com as questões do quiz.
 * Suporta sincronização incremental via campo version.
 */
@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity)

    @Query("SELECT * FROM questions WHERE category = :category ORDER BY RANDOM()")
    suspend fun getQuestionsByCategory(category: String): List<QuestionEntity>

    @Query("SELECT * FROM questions ORDER BY RANDOM()")
    suspend fun getAllQuestions(): List<QuestionEntity>

    @Query("SELECT * FROM questions ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomQuestions(limit: Int): List<QuestionEntity>

    @Query("SELECT DISTINCT category FROM questions ORDER BY category")
    fun getAllCategories(): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getQuestionCount(): Int

    @Query("SELECT MAX(version) FROM questions")
    suspend fun getLatestVersion(): Long?

    @Query("DELETE FROM questions")
    suspend fun deleteAllQuestions()

    @Query("SELECT * FROM questions WHERE category = :category ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomQuestionsByCategory(category: String, limit: Int): List<QuestionEntity>
}

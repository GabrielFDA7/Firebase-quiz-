package com.quizapp.firebase.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade Room que armazena o resultado de cada sessão de quiz.
 * Vinculada ao [userId] do usuário e sincronizada com Firestore.
 */
@Entity(tableName = "quiz_results")
data class QuizResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val odId: String = "",
    val userId: String,
    val quizCategory: String,
    val score: Int,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val timeTakenSeconds: Long,
    val percentage: Double,
    val timestamp: Long = System.currentTimeMillis()
)

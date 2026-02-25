package com.quizapp.firebase.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade Room que representa uma questão do quiz.
 * Sincronizada com a coleção "questions" no Firestore.
 * O campo [version] é usado para controle de sincronização incremental.
 */
@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey
    val id: String,
    val category: String,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctAnswer: String,
    val difficulty: String = "medium",
    val version: Long = 1
)

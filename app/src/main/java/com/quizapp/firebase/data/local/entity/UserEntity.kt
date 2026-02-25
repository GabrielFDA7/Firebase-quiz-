package com.quizapp.firebase.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade Room que representa o perfil de um usuário.
 * Sincronizada com o documento correspondente no Firestore (coleção "users").
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val uid: String,
    val email: String,
    val displayName: String,
    val photoUrl: String? = null,
    val totalQuizzes: Int = 0,
    val totalCorrect: Int = 0,
    val bestScore: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

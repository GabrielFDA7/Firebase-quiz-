package com.quizapp.firebase.data.model

/**
 * Modelos usados para serialização/deserialização com o Firebase Firestore.
 * Estes são separados das entidades Room para manter o desacoplamento
 * entre a camada local e a remota.
 */

/**
 * Representa um usuário no Firestore (coleção "users").
 * Construtor sem argumentos necessário para deserialização do Firestore.
 */
data class FirestoreUser(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val totalQuizzes: Int = 0,
    val totalCorrect: Int = 0,
    val bestScore: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Representa uma questão no Firestore (coleção "questions").
 */
data class FirestoreQuestion(
    val id: String = "",
    val category: String = "",
    val questionText: String = "",
    val optionA: String = "",
    val optionB: String = "",
    val optionC: String = "",
    val optionD: String = "",
    val correctAnswer: String = "",
    val difficulty: String = "medium",
    val version: Long = 1
)

/**
 * Representa um resultado de quiz no Firestore (subcoleção "results" em "users").
 */
data class FirestoreQuizResult(
    val odId: String = "",
    val userId: String = "",
    val quizCategory: String = "",
    val score: Int = 0,
    val totalQuestions: Int = 0,
    val correctAnswers: Int = 0,
    val timeTakenSeconds: Long = 0,
    val percentage: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)

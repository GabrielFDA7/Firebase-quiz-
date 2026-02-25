package com.quizapp.firebase.data.model

import com.quizapp.firebase.data.local.entity.QuestionEntity
import com.quizapp.firebase.data.local.entity.QuizResultEntity
import com.quizapp.firebase.data.local.entity.UserEntity

/**
 * Funções de extensão para converter entre modelos Room (local) e Firestore (remoto).
 * Mantém o código de conversão centralizado e testável.
 */

// ===== User Mappers =====

fun UserEntity.toFirestore(): FirestoreUser = FirestoreUser(
    uid = uid,
    email = email,
    displayName = displayName,
    photoUrl = photoUrl,
    totalQuizzes = totalQuizzes,
    totalCorrect = totalCorrect,
    bestScore = bestScore,
    createdAt = createdAt
)

fun FirestoreUser.toEntity(): UserEntity = UserEntity(
    uid = uid,
    email = email,
    displayName = displayName,
    photoUrl = photoUrl,
    totalQuizzes = totalQuizzes,
    totalCorrect = totalCorrect,
    bestScore = bestScore,
    createdAt = createdAt
)

// ===== Question Mappers =====

fun QuestionEntity.toFirestore(): FirestoreQuestion = FirestoreQuestion(
    id = id,
    category = category,
    questionText = questionText,
    optionA = optionA,
    optionB = optionB,
    optionC = optionC,
    optionD = optionD,
    correctAnswer = correctAnswer,
    difficulty = difficulty,
    version = version
)

fun FirestoreQuestion.toEntity(): QuestionEntity = QuestionEntity(
    id = id,
    category = category,
    questionText = questionText,
    optionA = optionA,
    optionB = optionB,
    optionC = optionC,
    optionD = optionD,
    correctAnswer = correctAnswer,
    difficulty = difficulty,
    version = version
)

// ===== QuizResult Mappers =====

fun QuizResultEntity.toFirestore(): FirestoreQuizResult = FirestoreQuizResult(
    odId = odId,
    userId = userId,
    quizCategory = quizCategory,
    score = score,
    totalQuestions = totalQuestions,
    correctAnswers = correctAnswers,
    timeTakenSeconds = timeTakenSeconds,
    percentage = percentage,
    timestamp = timestamp
)

fun FirestoreQuizResult.toEntity(): QuizResultEntity = QuizResultEntity(
    odId = odId,
    userId = userId,
    quizCategory = quizCategory,
    score = score,
    totalQuestions = totalQuestions,
    correctAnswers = correctAnswers,
    timeTakenSeconds = timeTakenSeconds,
    percentage = percentage,
    timestamp = timestamp
)

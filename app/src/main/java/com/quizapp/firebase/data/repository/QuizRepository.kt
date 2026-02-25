package com.quizapp.firebase.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.quizapp.firebase.data.local.AppDatabase
import com.quizapp.firebase.data.local.entity.QuizResultEntity
import com.quizapp.firebase.data.model.toFirestore
import kotlinx.coroutines.tasks.await

/**
 * Repositório para gerenciar resultados de quiz.
 * Salva resultados tanto localmente (Room) quanto remotamente (Firestore).
 */
class QuizRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val database: AppDatabase
) {
    private val quizResultDao = database.quizResultDao()
    private val userDao = database.userDao()

    /**
     * Salva o resultado de um quiz localmente e no Firestore.
     * Também atualiza as estatísticas do usuário.
     */
    suspend fun saveResult(result: QuizResultEntity): Result<Long> {
        return try {
            // Salvar localmente
            val localId = quizResultDao.insertResult(result)

            // Atualizar stats do usuário localmente
            userDao.updateStats(
                uid = result.userId,
                correct = result.correctAnswers,
                score = result.score
            )

            // Salvar no Firestore
            try {
                val firestoreResult = result.copy(id = localId).toFirestore()
                firestore.collection("users")
                    .document(result.userId)
                    .collection("results")
                    .add(firestoreResult)
                    .await()

                // Atualizar stats do usuário no Firestore
                val userDoc = firestore.collection("users").document(result.userId)
                firestore.runTransaction { transaction ->
                    val snapshot = transaction.get(userDoc)
                    val currentQuizzes = snapshot.getLong("totalQuizzes") ?: 0
                    val currentCorrect = snapshot.getLong("totalCorrect") ?: 0
                    val currentBest = snapshot.getLong("bestScore") ?: 0

                    transaction.update(userDoc, mapOf(
                        "totalQuizzes" to currentQuizzes + 1,
                        "totalCorrect" to currentCorrect + result.correctAnswers,
                        "bestScore" to maxOf(currentBest, result.score.toLong())
                    ))
                }.await()
            } catch (e: Exception) {
                // Falha no Firestore não impede salvar localmente
                android.util.Log.e("QuizRepository", "Erro ao salvar no Firestore: ${e.message}")
            }

            Result.success(localId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

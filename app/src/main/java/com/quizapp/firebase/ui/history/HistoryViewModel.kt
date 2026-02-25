package com.quizapp.firebase.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import com.quizapp.firebase.data.local.AppDatabase
import com.quizapp.firebase.data.local.entity.QuizResultEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Estado das estatísticas agregadas do usuário.
 */
data class UserStats(
    val totalQuizzes: Int = 0,
    val averageScore: Double = 0.0,
    val bestScore: Int = 0,
    val totalCorrect: Int = 0,
    val totalAnswered: Int = 0
) {
    val overallPercentage: Double
        get() = if (totalAnswered > 0) (totalCorrect.toDouble() / totalAnswered) * 100 else 0.0
}

/**
 * ViewModel para telas de Histórico e Estatísticas.
 * Expõe Flows reativos dos resultados e stats do usuário logado.
 */
class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val quizResultDao = database.quizResultDao()
    private val auth = FirebaseAuth.getInstance()

    private val userId: String
        get() = auth.currentUser?.uid ?: ""

    /** Flow reativo com todos os resultados do usuário (ordem: mais recente primeiro). */
    val results: Flow<List<QuizResultEntity>>
        get() = if (userId.isNotEmpty()) quizResultDao.getResultsByUser(userId)
        else flowOf(emptyList())

    /** Flow com os 5 resultados mais recentes. */
    val recentResults: Flow<List<QuizResultEntity>>
        get() = if (userId.isNotEmpty()) quizResultDao.getRecentResults(userId, 5)
        else flowOf(emptyList())

    /** Flow com o total de quizzes feitos. */
    val totalQuizCount: Flow<Int>
        get() = if (userId.isNotEmpty()) quizResultDao.getTotalQuizCount(userId)
        else flowOf(0)

    /** Flow com a média de pontuação. */
    val averageScore: Flow<Double?>
        get() = if (userId.isNotEmpty()) quizResultDao.getAverageScore(userId)
        else flowOf(null)

    /** Flow com a melhor pontuação. */
    val bestScore: Flow<Int?>
        get() = if (userId.isNotEmpty()) quizResultDao.getBestScore(userId)
        else flowOf(null)

    /** Flow com o total de respostas corretas. */
    val totalCorrect: Flow<Int?>
        get() = if (userId.isNotEmpty()) quizResultDao.getTotalCorrectAnswers(userId)
        else flowOf(null)

    /** Flow com o total de questões respondidas. */
    val totalAnswered: Flow<Int?>
        get() = if (userId.isNotEmpty()) quizResultDao.getTotalQuestionsAnswered(userId)
        else flowOf(null)

    /** Formata timestamp para data legível. */
    fun formatDate(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale("pt", "BR"))
        return sdf.format(java.util.Date(timestamp))
    }

    /** Formata segundos para "MM:SS". */
    fun formatTime(seconds: Long): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return "%02d:%02d".format(minutes, secs)
    }
}

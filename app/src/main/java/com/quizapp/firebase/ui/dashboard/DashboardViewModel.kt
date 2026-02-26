package com.quizapp.firebase.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.quizapp.firebase.data.local.AppDatabase
import com.quizapp.firebase.data.local.entity.UserEntity
import com.quizapp.firebase.data.repository.QuestionRepository
import com.quizapp.firebase.data.repository.SeedQuestions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Dados de um jogador no ranking.
 */
data class RankingEntry(
    val position: Int,
    val displayName: String,
    val totalScore: Int,
    val bestScore: Int,
    val totalQuizzes: Int,
    val isCurrentUser: Boolean = false
)

/**
 * ViewModel para Dashboard e Ranking.
 * Gerencia categorias, sync de questões, e ranking global do Firestore.
 */
class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val questionRepository = QuestionRepository(database = database)
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    /** Categorias disponíveis (Flow reativo do Room). */
    val categories: Flow<List<String>> = questionRepository.getCategories()

    private val _ranking = MutableStateFlow<List<RankingEntry>>(emptyList())
    val ranking: StateFlow<List<RankingEntry>> = _ranking.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    init {
        loadUserName()
        syncQuestions()
    }

    /** Carrega o nome do usuário logado. */
    private fun loadUserName() {
        val user = auth.currentUser
        _userName.value = user?.displayName ?: user?.email ?: "Jogador"
    }

    /** Sincroniza questões e faz seed se necessário. */
    private fun syncQuestions() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Seed inicial se Firestore estiver vazio
                SeedQuestions.seedIfEmpty(firestore)
                // Sincronizar Firestore → Room
                questionRepository.syncQuestions()
            } catch (_: Exception) { }
            _isLoading.value = false
        }
    }

    /** Carrega ranking global do Firestore (top 20). */
    fun loadRanking() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val snapshot = firestore.collection("users")
                    .orderBy("totalScore", Query.Direction.DESCENDING)
                    .limit(20)
                    .get()
                    .await()

                val currentUid = auth.currentUser?.uid
                val entries = snapshot.documents.mapIndexed { index, doc ->
                    RankingEntry(
                        position = index + 1,
                        displayName = doc.getString("displayName") ?: "Anônimo",
                        totalScore = doc.getLong("totalScore")?.toInt() ?: 0,
                        bestScore = doc.getLong("bestScore")?.toInt() ?: 0,
                        totalQuizzes = doc.getLong("totalQuizzes")?.toInt() ?: 0,
                        isCurrentUser = doc.id == currentUid
                    )
                }
                _ranking.value = entries
            } catch (_: Exception) {
                // Fallback: usar ranking local
                loadLocalRanking()
            }
            _isLoading.value = false
        }
    }

    /** Ranking local do Room como fallback offline. */
    private suspend fun loadLocalRanking() {
        // Usa o observeUser para pegar dados locais do usuário atual
        val uid = auth.currentUser?.uid ?: return
        val user = database.userDao().getUserById(uid) ?: return
        _ranking.value = listOf(
            RankingEntry(
                position = 1,
                displayName = user.displayName,
                totalScore = user.bestScore * user.totalQuizzes,
                bestScore = user.bestScore,
                totalQuizzes = user.totalQuizzes,
                isCurrentUser = true
            )
        )
    }

    /** Verifica se há questões disponíveis. */
    suspend fun hasQuestions(): Boolean = questionRepository.hasLocalQuestions()

    /** Força re-sync das questões. */
    fun refreshQuestions() {
        viewModelScope.launch {
            _isLoading.value = true
            questionRepository.syncQuestions()
            _isLoading.value = false
        }
    }
}

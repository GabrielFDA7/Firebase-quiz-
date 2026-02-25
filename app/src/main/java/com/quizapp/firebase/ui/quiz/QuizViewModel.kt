package com.quizapp.firebase.ui.quiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.quizapp.firebase.data.local.AppDatabase
import com.quizapp.firebase.data.local.entity.QuestionEntity
import com.quizapp.firebase.data.local.entity.QuizResultEntity
import com.quizapp.firebase.data.repository.QuestionRepository
import com.quizapp.firebase.data.repository.QuizRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estado da UI durante a execução do quiz.
 */
data class QuizUiState(
    val isLoading: Boolean = true,
    val questions: List<QuestionEntity> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswer: String? = null,
    val isAnswerConfirmed: Boolean = false,
    val correctAnswersCount: Int = 0,
    val score: Int = 0,
    val timerSeconds: Long = 0,
    val isQuizFinished: Boolean = false,
    val quizCategory: String = "",
    val errorMessage: String? = null
) {
    val currentQuestion: QuestionEntity?
        get() = questions.getOrNull(currentQuestionIndex)

    val progress: Float
        get() = if (questions.isEmpty()) 0f
        else (currentQuestionIndex + 1).toFloat() / questions.size

    val totalQuestions: Int
        get() = questions.size

    val percentage: Double
        get() = if (questions.isEmpty()) 0.0
        else (correctAnswersCount.toDouble() / questions.size) * 100
}

/**
 * ViewModel que controla toda a lógica de execução do quiz:
 * timer, navegação entre questões, pontuação e salvamento de resultados.
 */
class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val questionRepository = QuestionRepository(database = database)
    private val quizRepository = QuizRepository(database = database)
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    /**
     * Inicia um quiz com questões de uma categoria específica.
     * Se [category] for "Todas", pega questões aleatórias de todas as categorias.
     */
    fun startQuiz(category: String, questionCount: Int = 10) {
        viewModelScope.launch {
            _uiState.value = QuizUiState(isLoading = true, quizCategory = category)

            try {
                val questions = if (category == "Todas") {
                    questionRepository.getRandomQuestions(questionCount)
                } else {
                    questionRepository.getRandomQuestionsByCategory(category, questionCount)
                }

                if (questions.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Nenhuma questão disponível para esta categoria"
                    )
                    return@launch
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    questions = questions,
                    currentQuestionIndex = 0,
                    selectedAnswer = null,
                    isAnswerConfirmed = false,
                    correctAnswersCount = 0,
                    score = 0,
                    timerSeconds = 0
                )

                startTimer()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Erro ao carregar questões: ${e.message}"
                )
            }
        }
    }

    /**
     * Seleciona uma resposta (sem confirmar ainda).
     */
    fun selectAnswer(answer: String) {
        if (_uiState.value.isAnswerConfirmed) return
        _uiState.value = _uiState.value.copy(selectedAnswer = answer)
    }

    /**
     * Confirma a resposta selecionada e calcula pontuação.
     */
    fun confirmAnswer() {
        val state = _uiState.value
        val selected = state.selectedAnswer ?: return
        if (state.isAnswerConfirmed) return

        val question = state.currentQuestion ?: return
        val isCorrect = selected == question.correctAnswer

        // Pontuação: 100 pontos por acerto, com bônus por tempo rápido
        val timeBonus = maxOf(0, 30 - state.timerSeconds.toInt()) * 2
        val pointsEarned = if (isCorrect) 100 + timeBonus else 0

        _uiState.value = state.copy(
            isAnswerConfirmed = true,
            correctAnswersCount = state.correctAnswersCount + if (isCorrect) 1 else 0,
            score = state.score + pointsEarned
        )
    }

    /**
     * Avança para a próxima questão ou finaliza o quiz.
     */
    fun nextQuestion() {
        val state = _uiState.value
        if (!state.isAnswerConfirmed) return

        if (state.currentQuestionIndex + 1 >= state.questions.size) {
            finishQuiz()
        } else {
            _uiState.value = state.copy(
                currentQuestionIndex = state.currentQuestionIndex + 1,
                selectedAnswer = null,
                isAnswerConfirmed = false
            )
        }
    }

    /**
     * Finaliza o quiz, para o timer e salva o resultado.
     */
    private fun finishQuiz() {
        timerJob?.cancel()

        val state = _uiState.value
        _uiState.value = state.copy(isQuizFinished = true)

        // Salvar resultado
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            val result = QuizResultEntity(
                userId = userId,
                quizCategory = state.quizCategory,
                score = state.score,
                totalQuestions = state.totalQuestions,
                correctAnswers = state.correctAnswersCount,
                timeTakenSeconds = state.timerSeconds,
                percentage = state.percentage
            )
            quizRepository.saveResult(result)
        }
    }

    /** Inicia o timer global do quiz. */
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _uiState.value = _uiState.value.copy(
                    timerSeconds = _uiState.value.timerSeconds + 1
                )
            }
        }
    }

    /** Formata segundos para "MM:SS". */
    fun formatTime(seconds: Long): String {
        val minutes = seconds / 60
        val secs = seconds % 60
        return "%02d:%02d".format(minutes, secs)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

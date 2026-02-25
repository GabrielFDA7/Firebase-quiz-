package com.quizapp.firebase.ui.quiz

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quizapp.firebase.data.local.entity.QuestionEntity
import com.quizapp.firebase.ui.theme.QuizError
import com.quizapp.firebase.ui.theme.QuizSuccess

/**
 * Tela de execução do quiz.
 * Exibe questões uma a uma com opções, timer e barra de progresso.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    uiState: QuizUiState,
    onSelectAnswer: (String) -> Unit,
    onConfirmAnswer: () -> Unit,
    onNextQuestion: () -> Unit,
    onNavigateBack: () -> Unit,
    onQuizFinished: () -> Unit,
    formatTime: (Long) -> String
) {
    // Navegar para resultados quando quiz terminar
    LaunchedEffect(uiState.isQuizFinished) {
        if (uiState.isQuizFinished) onQuizFinished()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.quizCategory,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                },
                actions = {
                    // Timer
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = "Tempo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = formatTime(uiState.timerSeconds),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            )
        }
    ) { paddingValues ->

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        if (uiState.errorMessage != null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.errorMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error
                )
            }
            return@Scaffold
        }

        val question = uiState.currentQuestion ?: return@Scaffold

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Barra de progresso
            LinearProgressIndicator(
                progress = { uiState.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Questão ${uiState.currentQuestionIndex + 1} de ${uiState.totalQuestions}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Texto da questão
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = question.questionText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(20.dp),
                    lineHeight = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Opções de resposta
            val options = listOf(
                "A" to question.optionA,
                "B" to question.optionB,
                "C" to question.optionC,
                "D" to question.optionD
            )

            options.forEach { (letter, text) ->
                AnswerOption(
                    letter = letter,
                    text = text,
                    isSelected = uiState.selectedAnswer == letter,
                    isConfirmed = uiState.isAnswerConfirmed,
                    isCorrect = letter == question.correctAnswer,
                    onClick = { onSelectAnswer(letter) }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botão de ação
            Button(
                onClick = {
                    if (uiState.isAnswerConfirmed) onNextQuestion()
                    else onConfirmAnswer()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = uiState.selectedAnswer != null,
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = when {
                        !uiState.isAnswerConfirmed -> "Confirmar"
                        uiState.currentQuestionIndex + 1 >= uiState.totalQuestions -> "Ver Resultado"
                        else -> "Próxima Questão"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * Componente de opção de resposta com feedback visual.
 */
@Composable
fun AnswerOption(
    letter: String,
    text: String,
    isSelected: Boolean,
    isConfirmed: Boolean,
    isCorrect: Boolean,
    onClick: () -> Unit
) {
    val containerColor = when {
        isConfirmed && isCorrect -> QuizSuccess.copy(alpha = 0.15f)
        isConfirmed && isSelected && !isCorrect -> QuizError.copy(alpha = 0.15f)
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }

    val borderColor = when {
        isConfirmed && isCorrect -> QuizSuccess
        isConfirmed && isSelected && !isCorrect -> QuizError
        isSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    OutlinedCard(
        onClick = { if (!isConfirmed) onClick() },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(containerColor = containerColor),
        border = BorderStroke(
            width = if (isSelected || (isConfirmed && isCorrect)) 2.dp else 1.dp,
            color = borderColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Badge da letra
            Surface(
                shape = MaterialTheme.shapes.small,
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = letter,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

package com.quizapp.firebase.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Tela de estatísticas gerais do usuário.
 * Exibe total de quizzes, média, melhor pontuação e percentual de acertos.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: HistoryViewModel,
    onNavigateBack: () -> Unit
) {
    val totalQuizzes by viewModel.totalQuizCount.collectAsState(initial = 0)
    val avgScore by viewModel.averageScore.collectAsState(initial = null)
    val bestScore by viewModel.bestScore.collectAsState(initial = null)
    val totalCorrect by viewModel.totalCorrect.collectAsState(initial = null)
    val totalAnswered by viewModel.totalAnswered.collectAsState(initial = null)

    val overallPercentage = if ((totalAnswered ?: 0) > 0)
        ((totalCorrect ?: 0).toDouble() / (totalAnswered ?: 1)) * 100 else 0.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estatísticas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header com percentual geral
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Desempenho Geral",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "%.1f%%".format(overallPercentage),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "de acertos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Grid de stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatsCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.QuestionAnswer,
                    label = "Quizzes",
                    value = "$totalQuizzes"
                )
                StatsCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.TrendingUp,
                    label = "Média",
                    value = "%.1f%%".format(avgScore ?: 0.0)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatsCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.EmojiEvents,
                    label = "Melhor Score",
                    value = "${bestScore ?: 0}"
                )
                StatsCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.CheckCircle,
                    label = "Acertos",
                    value = "${totalCorrect ?: 0}/${totalAnswered ?: 0}"
                )
            }
        }
    }
}

/**
 * Card individual para exibir uma estatística.
 */
@Composable
fun StatsCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

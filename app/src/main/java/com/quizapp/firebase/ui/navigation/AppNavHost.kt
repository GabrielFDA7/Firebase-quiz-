package com.quizapp.firebase.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.quizapp.firebase.ui.auth.AuthViewModel
import com.quizapp.firebase.ui.auth.LoginScreen
import com.quizapp.firebase.ui.auth.RegisterScreen
import com.quizapp.firebase.ui.dashboard.DashboardScreen
import com.quizapp.firebase.ui.dashboard.DashboardViewModel
import com.quizapp.firebase.ui.dashboard.RankingScreen
import com.quizapp.firebase.ui.history.HistoryScreen
import com.quizapp.firebase.ui.history.HistoryViewModel
import com.quizapp.firebase.ui.history.StatsScreen
import com.quizapp.firebase.ui.quiz.QuizScreen
import com.quizapp.firebase.ui.quiz.QuizUiState
import com.quizapp.firebase.ui.quiz.QuizViewModel
import com.quizapp.firebase.ui.quiz.ResultScreen

/**
 * NavHost principal do app.
 * Controla toda a navegação entre telas.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    val authState by authViewModel.uiState.collectAsState()
    val startDestination = if (authState.isLoggedIn) Screen.Dashboard.route else Screen.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ===== AUTENTICAÇÃO =====
        composable(Screen.Login.route) {
            LoginScreen(
                uiState = authState,
                onSignIn = { email, password -> authViewModel.signIn(email, password) },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onClearError = { authViewModel.clearError() }
            )

            LaunchedEffect(authState.isLoggedIn) {
                if (authState.isLoggedIn) {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                uiState = authState,
                onSignUp = { name, email, password, confirm ->
                    authViewModel.signUp(name, email, password, confirm)
                },
                onNavigateBack = { navController.popBackStack() },
                onClearError = { authViewModel.clearError() }
            )

            LaunchedEffect(authState.isLoggedIn) {
                if (authState.isLoggedIn) {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }
        }

        // ===== DASHBOARD =====
        composable(Screen.Dashboard.route) {
            val dashboardViewModel: DashboardViewModel = viewModel()
            val categories by dashboardViewModel.categories.collectAsState(initial = emptyList())
            val userName by dashboardViewModel.userName.collectAsState()
            val isLoading by dashboardViewModel.isLoading.collectAsState()

            DashboardScreen(
                userName = userName,
                categories = categories,
                isLoading = isLoading,
                onStartQuiz = { category ->
                    navController.navigate(Screen.Quiz.createRoute(category))
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                onNavigateToStats = {
                    navController.navigate(Screen.Stats.route)
                },
                onNavigateToRanking = {
                    dashboardViewModel.loadRanking()
                    navController.navigate(Screen.Ranking.route)
                },
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ===== QUIZ =====
        composable(
            route = Screen.Quiz.route,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: "Todas"
            val quizViewModel: QuizViewModel = viewModel()

            LaunchedEffect(category) {
                quizViewModel.startQuiz(category)
            }

            val quizState by quizViewModel.uiState.collectAsState()

            QuizScreen(
                uiState = quizState,
                onSelectAnswer = { quizViewModel.selectAnswer(it) },
                onConfirmAnswer = { quizViewModel.confirmAnswer() },
                onNextQuestion = { quizViewModel.nextQuestion() },
                onNavigateBack = { navController.popBackStack() },
                onQuizFinished = {
                    // Navegar passando os resultados como argumentos
                    val state = quizState
                    navController.navigate(
                        Screen.Result.createRoute(
                            score = state.score,
                            correct = state.correctAnswersCount,
                            total = state.totalQuestions,
                            time = state.timerSeconds,
                            percentage = state.percentage,
                            category = state.quizCategory
                        )
                    ) {
                        popUpTo(Screen.Dashboard.route)
                    }
                },
                formatTime = { quizViewModel.formatTime(it) }
            )
        }

        // ===== RESULTADO =====
        composable(
            route = Screen.Result.route,
            arguments = listOf(
                navArgument("score") { type = NavType.IntType },
                navArgument("correct") { type = NavType.IntType },
                navArgument("total") { type = NavType.IntType },
                navArgument("time") { type = NavType.LongType },
                navArgument("percentage") { type = NavType.StringType },
                navArgument("category") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val args = backStackEntry.arguments!!
            val totalQ = args.getInt("total")

            // Reconstruir o estado a partir dos argumentos
            val resultState = QuizUiState(
                isLoading = false,
                score = args.getInt("score"),
                correctAnswersCount = args.getInt("correct"),
                timerSeconds = args.getLong("time"),
                quizCategory = args.getString("category") ?: "",
                isQuizFinished = true,
                // Criar lista fictícia para que totalQuestions e percentage funcionem
                questions = List(totalQ) {
                    com.quizapp.firebase.data.local.entity.QuestionEntity(
                        id = "", category = "", questionText = "",
                        optionA = "", optionB = "", optionC = "", optionD = "",
                        correctAnswer = ""
                    )
                }
            )

            ResultScreen(
                uiState = resultState,
                formatTime = { seconds ->
                    val min = seconds / 60
                    val sec = seconds % 60
                    "%02d:%02d".format(min, sec)
                },
                onPlayAgain = {
                    navController.popBackStack(Screen.Dashboard.route, false)
                },
                onGoHome = {
                    navController.popBackStack(Screen.Dashboard.route, false)
                }
            )
        }

        // ===== HISTÓRICO =====
        composable(Screen.History.route) {
            val historyViewModel: HistoryViewModel = viewModel()

            HistoryScreen(
                viewModel = historyViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ===== ESTATÍSTICAS =====
        composable(Screen.Stats.route) {
            val historyViewModel: HistoryViewModel = viewModel()

            StatsScreen(
                viewModel = historyViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ===== RANKING =====
        composable(Screen.Ranking.route) {
            val dashboardViewModel: DashboardViewModel = viewModel()
            val ranking by dashboardViewModel.ranking.collectAsState()
            val isLoading by dashboardViewModel.isLoading.collectAsState()

            LaunchedEffect(Unit) {
                dashboardViewModel.loadRanking()
            }

            RankingScreen(
                ranking = ranking,
                isLoading = isLoading,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

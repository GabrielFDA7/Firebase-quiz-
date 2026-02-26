package com.quizapp.firebase.ui.navigation

import android.net.Uri

/**
 * Rotas de navegação do app.
 * Cada objeto representa uma tela/destino no NavHost.
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object Quiz : Screen("quiz/{category}") {
        fun createRoute(category: String): String {
            return "quiz/${Uri.encode(category)}"
        }
    }
    object Result : Screen("result/{score}/{correct}/{total}/{time}/{percentage}/{category}") {
        fun createRoute(
            score: Int,
            correct: Int,
            total: Int,
            time: Long,
            percentage: Double,
            category: String
        ): String {
            return "result/$score/$correct/$total/$time/$percentage/${Uri.encode(category)}"
        }
    }
    object History : Screen("history")
    object Stats : Screen("stats")
    object Ranking : Screen("ranking")
}

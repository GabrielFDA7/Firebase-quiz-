package com.quizapp.firebase.ui.navigation

import java.net.URLEncoder

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
            val encoded = URLEncoder.encode(category, "UTF-8")
            return "quiz/$encoded"
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
            val encoded = URLEncoder.encode(category, "UTF-8")
            return "result/$score/$correct/$total/$time/$percentage/$encoded"
        }
    }
    object History : Screen("history")
    object Stats : Screen("stats")
    object Ranking : Screen("ranking")
}

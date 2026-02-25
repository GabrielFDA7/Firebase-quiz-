package com.quizapp.firebase.ui.navigation

/**
 * Rotas de navegação do app.
 * Cada objeto representa uma tela/destino no NavHost.
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object Quiz : Screen("quiz/{category}") {
        fun createRoute(category: String) = "quiz/$category"
    }
    object Result : Screen("result")
    object History : Screen("history")
    object Stats : Screen("stats")
    object Ranking : Screen("ranking")
}

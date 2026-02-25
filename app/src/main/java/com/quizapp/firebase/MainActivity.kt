package com.quizapp.firebase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.quizapp.firebase.ui.auth.AuthViewModel
import com.quizapp.firebase.ui.navigation.AppNavHost
import com.quizapp.firebase.ui.theme.QuizAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuizAppTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()

                AppNavHost(
                    navController = navController,
                    authViewModel = authViewModel
                )
            }
        }
    }
}

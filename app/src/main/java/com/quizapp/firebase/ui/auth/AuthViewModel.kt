package com.quizapp.firebase.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.quizapp.firebase.data.local.AppDatabase
import com.quizapp.firebase.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estado da UI de autenticação.
 */
data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null,
    val userName: String = ""
)

/**
 * ViewModel para telas de Login e Cadastro.
 * Gerencia estado da UI e delega operações ao AuthRepository.
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val authRepository = AuthRepository(database = database)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        // Verificar se já está logado ao iniciar
        _uiState.value = AuthUiState(isLoggedIn = authRepository.isLoggedIn)
    }

    /**
     * Faz login com e-mail e senha.
     */
    fun signIn(email: String, password: String) {
        if (!validateInput(email, password)) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = authRepository.signIn(email.trim(), password)
            result.fold(
                onSuccess = { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        userName = user.displayName ?: ""
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = mapFirebaseError(error)
                    )
                }
            )
        }
    }

    /**
     * Cadastra novo usuário com nome, e-mail e senha.
     */
    fun signUp(name: String, email: String, password: String, confirmPassword: String) {
        if (name.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Nome é obrigatório")
            return
        }
        if (!validateInput(email, password)) return
        if (password != confirmPassword) {
            _uiState.value = _uiState.value.copy(errorMessage = "As senhas não coincidem")
            return
        }
        if (password.length < 6) {
            _uiState.value = _uiState.value.copy(errorMessage = "A senha deve ter pelo menos 6 caracteres")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = authRepository.signUp(name.trim(), email.trim(), password)
            result.fold(
                onSuccess = { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        userName = user.displayName ?: name
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = mapFirebaseError(error)
                    )
                }
            )
        }
    }

    /**
     * Faz logout do usuário.
     */
    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _uiState.value = AuthUiState(isLoggedIn = false)
        }
    }

    /** Limpa mensagem de erro da UI. */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /** Valida campos de e-mail e senha. */
    private fun validateInput(email: String, password: String): Boolean {
        if (email.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "E-mail é obrigatório")
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            _uiState.value = _uiState.value.copy(errorMessage = "E-mail inválido")
            return false
        }
        if (password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Senha é obrigatória")
            return false
        }
        return true
    }

    /** Mapeia erros do Firebase para mensagens amigáveis em PT-BR. */
    private fun mapFirebaseError(error: Throwable): String {
        return when {
            error.message?.contains("INVALID_LOGIN_CREDENTIALS") == true ->
                "E-mail ou senha incorretos"
            error.message?.contains("EMAIL_EXISTS") == true ||
            error.message?.contains("email address is already in use") == true ->
                "Este e-mail já está cadastrado"
            error.message?.contains("WEAK_PASSWORD") == true ->
                "A senha é muito fraca. Use pelo menos 6 caracteres"
            error.message?.contains("INVALID_EMAIL") == true ->
                "Formato de e-mail inválido"
            error.message?.contains("network") == true ->
                "Sem conexão com a internet"
            error.message?.contains("TOO_MANY_ATTEMPTS") == true ->
                "Muitas tentativas. Tente novamente mais tarde"
            else -> error.message ?: "Erro desconhecido"
        }
    }
}

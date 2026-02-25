package com.quizapp.firebase.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.quizapp.firebase.data.local.AppDatabase
import com.quizapp.firebase.data.local.entity.UserEntity
import com.quizapp.firebase.data.model.FirestoreUser
import com.quizapp.firebase.data.model.toEntity
import kotlinx.coroutines.tasks.await

/**
 * Repositório de autenticação que abstrai Firebase Auth, Firestore e Room.
 * Responsável por:
 * - Login e cadastro via Firebase Authentication
 * - Salvar/atualizar perfil no Firestore (coleção "users")
 * - Manter cópia local do perfil no Room Database
 */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val database: AppDatabase
) {

    /** Retorna o usuário Firebase logado atualmente, ou null. */
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    /** Verifica se há um usuário logado. */
    val isLoggedIn: Boolean
        get() = auth.currentUser != null

    /**
     * Cadastra um novo usuário com e-mail e senha.
     * Salva o perfil tanto no Firestore quanto localmente.
     */
    suspend fun signUp(name: String, email: String, password: String): Result<FirebaseUser> {
        return try {
            // Criar conta no Firebase Auth
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("Falha ao criar usuário")

            // Atualizar displayName no perfil do Firebase Auth
            val profileUpdate = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            user.updateProfile(profileUpdate).await()

            // Criar perfil no Firestore e Room
            val userProfile = FirestoreUser(
                uid = user.uid,
                email = email,
                displayName = name,
                photoUrl = user.photoUrl?.toString()
            )
            saveUserProfile(userProfile)

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Faz login com e-mail e senha.
     * Após login, sincroniza o perfil do Firestore para o Room.
     */
    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("Falha ao fazer login")

            // Sincronizar perfil do Firestore para Room
            syncUserProfile(user.uid)

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Faz logout e limpa dados sensíveis locais. */
    suspend fun signOut() {
        auth.signOut()
    }

    /**
     * Salva o perfil do usuário tanto no Firestore quanto no Room.
     */
    private suspend fun saveUserProfile(firestoreUser: FirestoreUser) {
        // Salvar no Firestore
        firestore.collection("users")
            .document(firestoreUser.uid)
            .set(firestoreUser)
            .await()

        // Salvar localmente
        database.userDao().insertUser(firestoreUser.toEntity())
    }

    /**
     * Sincroniza perfil do Firestore para Room.
     * Chamado após login para garantir dados atualizados localmente.
     */
    private suspend fun syncUserProfile(uid: String) {
        try {
            val doc = firestore.collection("users").document(uid).get().await()
            val firestoreUser = doc.toObject(FirestoreUser::class.java)
            if (firestoreUser != null) {
                database.userDao().insertUser(firestoreUser.toEntity())
            }
        } catch (e: Exception) {
            // Se falhar sync (offline), usa dados locais existentes
        }
    }

    /** Obtém o perfil local do usuário logado. */
    suspend fun getLocalUserProfile(): UserEntity? {
        val uid = currentUser?.uid ?: return null
        return database.userDao().getUserById(uid)
    }
}

package com.quizapp.firebase.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.quizapp.firebase.data.local.AppDatabase
import com.quizapp.firebase.data.local.entity.QuestionEntity
import com.quizapp.firebase.data.model.FirestoreQuestion
import com.quizapp.firebase.data.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

/**
 * Repositório de questões que unifica acesso local (Room) e remoto (Firestore).
 *
 * Lógica de sincronização:
 * 1. Na primeira execução, baixa todas as questões do Firestore e salva no Room.
 * 2. Em execuções seguintes, compara a versão local com a remota.
 * 3. Se houver versão mais nova no Firestore, atualiza o banco local.
 * 4. O quiz sempre lê do Room, garantindo funcionamento offline.
 */
class QuestionRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val database: AppDatabase
) {
    private val questionDao = database.questionDao()

    companion object {
        private const val TAG = "QuestionRepository"
        private const val COLLECTION_QUESTIONS = "questions"
        private const val COLLECTION_META = "metadata"
        private const val DOC_QUESTIONS_META = "questions_info"
    }

    /**
     * Sincroniza questões do Firestore para o Room.
     * Chamado na inicialização do app e quando o usuário solicita atualização.
     *
     * @return true se houve atualização, false se já estava atualizado ou falhou
     */
    suspend fun syncQuestions(): Boolean {
        return try {
            val localVersion = questionDao.getLatestVersion() ?: 0L
            val remoteVersion = getRemoteVersion()

            if (remoteVersion > localVersion || localVersion == 0L) {
                // Baixar todas as questões do Firestore
                val snapshot = firestore.collection(COLLECTION_QUESTIONS)
                    .get()
                    .await()

                val questions = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(FirestoreQuestion::class.java)?.copy(id = doc.id)
                }

                if (questions.isNotEmpty()) {
                    // Substituir banco local com novas questões
                    questionDao.deleteAllQuestions()
                    questionDao.insertQuestions(questions.map { it.toEntity() })
                    Log.d(TAG, "Sincronizadas ${questions.size} questões (v$remoteVersion)")
                    return true
                }
            }

            Log.d(TAG, "Questões já atualizadas (v$localVersion)")
            false
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao sincronizar questões: ${e.message}")
            false
        }
    }

    /**
     * Obtém a versão remota das questões a partir do documento de metadata.
     */
    private suspend fun getRemoteVersion(): Long {
        return try {
            val doc = firestore.collection(COLLECTION_META)
                .document(DOC_QUESTIONS_META)
                .get()
                .await()
            doc.getLong("version") ?: 1L
        } catch (e: Exception) {
            1L // Assume versão 1 se não conseguir ler metadata
        }
    }

    /**
     * Verifica se há questões locais disponíveis.
     * Usado para decidir se o quiz pode ser executado offline.
     */
    suspend fun hasLocalQuestions(): Boolean {
        return questionDao.getQuestionCount() > 0
    }

    /**
     * Retorna questões de uma categoria específica.
     * Lê sempre do banco local (Room).
     */
    suspend fun getQuestionsByCategory(category: String): List<QuestionEntity> {
        return questionDao.getQuestionsByCategory(category)
    }

    /**
     * Retorna questões aleatórias, opcionalmente limitadas.
     */
    suspend fun getRandomQuestions(limit: Int = 10): List<QuestionEntity> {
        return questionDao.getRandomQuestions(limit)
    }

    /**
     * Retorna questões aleatórias de uma categoria, com limite.
     */
    suspend fun getRandomQuestionsByCategory(category: String, limit: Int = 10): List<QuestionEntity> {
        return questionDao.getRandomQuestionsByCategory(category, limit)
    }

    /**
     * Retorna todas as categorias disponíveis como Flow reativo.
     */
    fun getCategories(): Flow<List<String>> {
        return questionDao.getAllCategories()
    }

    /**
     * Retorna o total de questões armazenadas localmente.
     */
    suspend fun getQuestionCount(): Int {
        return questionDao.getQuestionCount()
    }
}

package com.quizapp.firebase.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Utilitário para popular o Firestore com questões de exemplo.
 * Execute apenas UMA VEZ para seed inicial do banco de questões.
 *
 * Uso: chamar SeedQuestions.seedIfEmpty() na inicialização do app
 * quando conectado à internet.
 */
object SeedQuestions {

    private const val TAG = "SeedQuestions"

    /**
     * Popula o Firestore com questões de exemplo SE a coleção estiver vazia.
     */
    suspend fun seedIfEmpty(firestore: FirebaseFirestore = FirebaseFirestore.getInstance()) {
        try {
            val snapshot = firestore.collection("questions").limit(1).get().await()
            if (!snapshot.isEmpty) {
                Log.d(TAG, "Questões já existem no Firestore, seed ignorado")
                return
            }

            Log.d(TAG, "Populando Firestore com questões de exemplo...")

            val questions = getSampleQuestions()
            val batch = firestore.batch()

            questions.forEach { question ->
                val docRef = firestore.collection("questions").document()
                batch.set(docRef, question)
            }

            // Criar documento de metadata com versão
            val metaRef = firestore.collection("metadata").document("questions_info")
            batch.set(metaRef, mapOf("version" to 1L, "totalQuestions" to questions.size))

            batch.commit().await()
            Log.d(TAG, "Seed concluído: ${questions.size} questões adicionadas")
        } catch (e: Exception) {
            Log.e(TAG, "Erro no seed: ${e.message}")
        }
    }

    private fun getSampleQuestions(): List<Map<String, Any>> = listOf(
        // ===== Categoria: Programação =====
        mapOf(
            "category" to "Programação",
            "questionText" to "Qual linguagem é usada nativamente no desenvolvimento Android?",
            "optionA" to "Python",
            "optionB" to "Kotlin",
            "optionC" to "Ruby",
            "optionD" to "PHP",
            "correctAnswer" to "B",
            "difficulty" to "easy",
            "version" to 1L
        ),
        mapOf(
            "category" to "Programação",
            "questionText" to "O que significa a sigla 'API'?",
            "optionA" to "Application Programming Interface",
            "optionB" to "Advanced Program Integration",
            "optionC" to "Automated Processing Input",
            "optionD" to "Application Process Interpreter",
            "correctAnswer" to "A",
            "difficulty" to "easy",
            "version" to 1L
        ),
        mapOf(
            "category" to "Programação",
            "questionText" to "Qual padrão arquitetural separa Model, View e ViewModel?",
            "optionA" to "MVC",
            "optionB" to "MVP",
            "optionC" to "MVVM",
            "optionD" to "VIPER",
            "correctAnswer" to "C",
            "difficulty" to "medium",
            "version" to 1L
        ),
        mapOf(
            "category" to "Programação",
            "questionText" to "Em Kotlin, qual keyword é usada para declarar uma variável imutável?",
            "optionA" to "var",
            "optionB" to "val",
            "optionC" to "const",
            "optionD" to "let",
            "correctAnswer" to "B",
            "difficulty" to "easy",
            "version" to 1L
        ),
        mapOf(
            "category" to "Programação",
            "questionText" to "Qual banco de dados local é recomendado pelo Google para Android?",
            "optionA" to "SQLite puro",
            "optionB" to "Realm",
            "optionC" to "Room",
            "optionD" to "MongoDB",
            "correctAnswer" to "C",
            "difficulty" to "medium",
            "version" to 1L
        ),

        // ===== Categoria: Ciência da Computação =====
        mapOf(
            "category" to "Ciência da Computação",
            "questionText" to "Qual a complexidade de tempo do algoritmo de busca binária?",
            "optionA" to "O(n)",
            "optionB" to "O(n²)",
            "optionC" to "O(log n)",
            "optionD" to "O(1)",
            "correctAnswer" to "C",
            "difficulty" to "medium",
            "version" to 1L
        ),
        mapOf(
            "category" to "Ciência da Computação",
            "questionText" to "Qual estrutura de dados usa o princípio FIFO?",
            "optionA" to "Pilha (Stack)",
            "optionB" to "Fila (Queue)",
            "optionC" to "Árvore",
            "optionD" to "Grafo",
            "correctAnswer" to "B",
            "difficulty" to "easy",
            "version" to 1L
        ),
        mapOf(
            "category" to "Ciência da Computação",
            "questionText" to "O que é um 'deadlock' em sistemas operacionais?",
            "optionA" to "Um erro de memória",
            "optionB" to "Uma falha de rede",
            "optionC" to "Quando processos ficam bloqueados esperando uns pelos outros",
            "optionD" to "Um tipo de vírus",
            "correctAnswer" to "C",
            "difficulty" to "medium",
            "version" to 1L
        ),
        mapOf(
            "category" to "Ciência da Computação",
            "questionText" to "Quantos bits tem 1 byte?",
            "optionA" to "4",
            "optionB" to "8",
            "optionC" to "16",
            "optionD" to "32",
            "correctAnswer" to "B",
            "difficulty" to "easy",
            "version" to 1L
        ),
        mapOf(
            "category" to "Ciência da Computação",
            "questionText" to "Qual protocolo é usado para transferência segura de dados na web?",
            "optionA" to "HTTP",
            "optionB" to "FTP",
            "optionC" to "HTTPS",
            "optionD" to "SMTP",
            "correctAnswer" to "C",
            "difficulty" to "easy",
            "version" to 1L
        ),

        // ===== Categoria: Conhecimentos Gerais =====
        mapOf(
            "category" to "Conhecimentos Gerais",
            "questionText" to "Quem é considerado o pai da computação?",
            "optionA" to "Bill Gates",
            "optionB" to "Steve Jobs",
            "optionC" to "Alan Turing",
            "optionD" to "Linus Torvalds",
            "correctAnswer" to "C",
            "difficulty" to "easy",
            "version" to 1L
        ),
        mapOf(
            "category" to "Conhecimentos Gerais",
            "questionText" to "Em que ano o primeiro iPhone foi lançado?",
            "optionA" to "2005",
            "optionB" to "2007",
            "optionC" to "2009",
            "optionD" to "2010",
            "correctAnswer" to "B",
            "difficulty" to "medium",
            "version" to 1L
        ),
        mapOf(
            "category" to "Conhecimentos Gerais",
            "questionText" to "Qual empresa desenvolveu o sistema operacional Android?",
            "optionA" to "Apple",
            "optionB" to "Microsoft",
            "optionC" to "Google",
            "optionD" to "Samsung",
            "correctAnswer" to "C",
            "difficulty" to "easy",
            "version" to 1L
        ),
        mapOf(
            "category" to "Conhecimentos Gerais",
            "questionText" to "O que significa 'open source'?",
            "optionA" to "Software gratuito",
            "optionB" to "Software com código-fonte aberto",
            "optionC" to "Software sem licença",
            "optionD" to "Software do governo",
            "correctAnswer" to "B",
            "difficulty" to "easy",
            "version" to 1L
        ),
        mapOf(
            "category" to "Conhecimentos Gerais",
            "questionText" to "Qual o nome do mascote do Linux?",
            "optionA" to "Pinguim Tux",
            "optionB" to "Robô Andy",
            "optionC" to "Clippy",
            "optionD" to "Gopher",
            "correctAnswer" to "A",
            "difficulty" to "easy",
            "version" to 1L
        )
    )
}

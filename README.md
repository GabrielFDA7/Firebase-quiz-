# Quiz App Firebase

Aplicativo Android de quiz com Firebase, desenvolvido como trabalho prático da disciplina de Computação — UFU.

## Funcionalidades

- Autenticação com Firebase (e-mail/senha)
- Questões armazenadas no Firestore com sincronização local (Room)
- Quiz com timer, barra de progresso e feedback visual por resposta
- Histórico pessoal com lista dos quizzes realizados
- Estatísticas de desempenho (percentual, média, melhor score, acertos)
- Dashboard com categorias e ações rápidas
- Ranking global por pontuação total acumulada
- Material Design 3 com suporte a tema claro/escuro
- Funcionamento offline com cache local via Room

## Stack

| Camada | Tecnologia |
|---|---|
| Linguagem | Kotlin |
| UI | Jetpack Compose |
| Arquitetura | MVVM |
| Auth | Firebase Authentication |
| Banco Remoto | Cloud Firestore |
| Banco Local | Room Database |
| Navegação | Jetpack Navigation Compose |
| Async | Kotlin Coroutines + Flow |
| Build | Gradle Kotlin DSL + KSP |

## Estrutura do Projeto

```
app/src/main/java/com/quizapp/firebase/
├── MainActivity.kt
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt
│   │   ├── dao/
│   │   │   ├── UserDao.kt
│   │   │   ├── QuestionDao.kt
│   │   │   └── QuizResultDao.kt
│   │   └── entity/
│   │       ├── UserEntity.kt
│   │       ├── QuestionEntity.kt
│   │       └── QuizResultEntity.kt
│   ├── model/
│   │   ├── FirestoreModels.kt
│   │   └── Mappers.kt
│   └── repository/
│       ├── AuthRepository.kt
│       ├── QuestionRepository.kt
│       ├── QuizRepository.kt
│       └── SeedQuestions.kt
└── ui/
    ├── auth/
    │   ├── AuthViewModel.kt
    │   ├── LoginScreen.kt
    │   └── RegisterScreen.kt
    ├── quiz/
    │   ├── QuizViewModel.kt
    │   ├── QuizScreen.kt
    │   └── ResultScreen.kt
    ├── dashboard/
    │   ├── DashboardViewModel.kt
    │   ├── DashboardScreen.kt
    │   └── RankingScreen.kt
    ├── history/
    │   ├── HistoryViewModel.kt
    │   ├── HistoryScreen.kt
    │   └── StatsScreen.kt
    ├── navigation/
    │   ├── Screen.kt
    │   └── AppNavHost.kt
    └── theme/
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

## Como rodar

### Pre-requisitos
- Android Studio Hedgehog ou superior
- JDK 21
- Conta no [Firebase Console](https://console.firebase.google.com/)

### Setup

1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/Firebase-quiz-.git
   ```

2. Configure o Firebase:
   - Crie um projeto no Firebase Console
   - Ative **Authentication** com o método E-mail/Senha
   - Ative **Cloud Firestore** no modo de teste
   - Registre o app Android com pacote `com.quizapp.firebase`
   - Baixe o `google-services.json` e substitua o arquivo em `app/`

3. Regras do Firestore (recomendado para produção):
   ```
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /users/{userId} {
         allow read: if request.auth != null;
         allow write: if request.auth != null && request.auth.uid == userId;
         match /results/{resultId} {
           allow read, write: if request.auth != null && request.auth.uid == userId;
         }
       }
       match /questions/{questionId} {
         allow read: if request.auth != null;
         allow write: if false;
       }
     }
   }
   ```

4. Abra no Android Studio e execute no emulador ou dispositivo físico.

### Build APK

```bash
./gradlew assembleDebug
```

O APK sera gerado em `app/build/outputs/apk/debug/app-debug.apk`.

## Fluxo do App

```
Login/Cadastro -> Dashboard -> Selecionar Categoria -> Quiz -> Resultado
                    |-- Historico
                    |-- Estatisticas
                    |-- Ranking Global
```

1. **Login/Cadastro:** autenticacao via e-mail e senha
2. **Dashboard:** tela principal com categorias e acoes rapidas
3. **Quiz:** 10 questoes por rodada, com timer e pontuacao com bonus por velocidade
4. **Resultado:** pontuacao final, acertos e tempo
5. **Historico:** lista dos quizzes realizados
6. **Estatisticas:** percentual geral, media, melhor score e total de acertos
7. **Ranking:** top 20 jogadores ordenados por pontuacao total acumulada

## Colecoes do Firestore

| Colecao | Campos principais |
|---|---|
| `users/{uid}` | `displayName`, `email`, `totalQuizzes`, `totalCorrect`, `bestScore`, `totalScore`, `createdAt` |
| `users/{uid}/results/{id}` | `quizCategory`, `score`, `correctAnswers`, `totalQuestions`, `timeTakenSeconds`, `percentage`, `timestamp` |
| `questions/{id}` | `category`, `questionText`, `optionA`-`optionD`, `correctAnswer`, `difficulty`, `version` |

## Categorias

- Ciencia da Computacao
- Conhecimentos Gerais
- Ciencia
- Matematica

As questoes sao carregadas automaticamente no Firestore na primeira execucao e sincronizadas para o dispositivo local.

## Equipe

| Membro | Papel |
|---|---|
| Estudante A | Interface e UX |
| Estudante B | Firebase / Autenticacao |
| Estudante C | Dados Locais / Sincronizacao |
| Estudante D | Testes / Integracao |

## Licenca

Projeto academico — UFU 2025/2026.

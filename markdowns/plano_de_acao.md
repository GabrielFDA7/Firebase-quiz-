# 📋 Plano de Ação — Quiz App Colaborativo com Firebase

> **Disciplina:** Universidade Federal de Uberlândia — Faculdade de Computação  
> **Professor:** Alexsandro Santos Soares  
> **Valor:** 25 pontos  
> **Entrega e Apresentação:** 26/02/2025 (quinta-feira), no horário da aula  
> **Modo de entrega:** Link do repositório GitHub  

---

## 📌 Visão Geral do Projeto

Desenvolver um **aplicativo Android de quiz** moderno e funcional, utilizando **Firebase** para autenticação, armazenamento de dados e hospedagem das questões. O app deve permitir acesso individualizado com login, manter histórico de desempenho localmente e na nuvem, e seguir boas práticas de **Material Design**.

---

## 👥 Divisão de Tarefas do Grupo

| Papel | Responsabilidade Principal |
|---|---|
| **Estudante A** | Interface e Experiência do Usuário (UI/UX) |
| **Estudante B** | Integração com Firebase / Autenticação |
| **Estudante C** | Gerenciamento de Dados Locais e Sincronização |
| **Estudante D** | Comunicação, Testes, Integração e Apoio Geral |

> **Nota:** A divisão acima é uma sugestão. O grupo pode redistribuir conforme necessário, desde que todos contribuam de forma equilibrada.

---

## 🏗️ Arquitetura do Projeto

```
app/
├── data/
│   ├── local/          # Room Database (DAOs, Entities)
│   ├── remote/         # Firebase (Firestore/Realtime DB, Auth)
│   └── repository/     # Repositórios que unificam local + remoto
├── ui/
│   ├── auth/           # Telas de Login e Cadastro
│   ├── home/           # Tela Inicial (lista de quizzes)
│   ├── quiz/           # Tela de Execução do Quiz
│   ├── result/         # Tela de Resultado / Desempenho
│   ├── history/        # Tela de Histórico e Estatísticas
│   ├── dashboard/      # Dashboard do Usuário
│   └── ranking/        # Ranking Global
├── model/              # Classes de modelo (User, Question, QuizResult)
├── util/               # Utilitários e helpers
└── di/                 # Injeção de dependência (Hilt/Koin, se utilizado)
```

**Padrão arquitetural recomendado:** MVVM (Model-View-ViewModel)

---

## ✅ Funcionalidades Requeridas (Requisitos Mínimos)

### 1. 🔐 Login e Autenticação Individual

- [ ] Configurar **Firebase Authentication** no projeto Android
- [ ] Implementar login com **e-mail/senha**
- [ ] Implementar login com **Google Sign-In** (provedor adicional)
- [ ] Criar tela de **cadastro de novo usuário**
- [ ] Criar tela de **login**
- [ ] Salvar perfil do usuário no **Firebase Firestore/Realtime DB**
- [ ] Salvar perfil do usuário **localmente** (SharedPreferences ou Room)
- [ ] Implementar **logout**
- [ ] Implementar tratamento de erros de autenticação (credenciais inválidas, conta inexistente, etc.)
- [ ] Validar campos de entrada (e-mail válido, senha com requisitos mínimos)

**Boas práticas de segurança:**
- Nunca armazenar senhas em texto puro localmente
- Utilizar tokens do Firebase Authentication
- Implementar regras de segurança no Firebase Console
- Validar entrada do usuário tanto no cliente como no servidor

---

### 2. 📥 Download e Armazenamento Local das Questões

- [ ] Criar estrutura de dados das questões no **Firebase** (Realtime Database ou Cloud Firestore)
- [ ] Modelar entidade `Question` com campos:
  - `id`, `category`, `questionText`, `options[]`, `correctAnswer`, `difficulty`, `version`
- [ ] Implementar download das questões na **primeira execução** do app
- [ ] Armazenar questões localmente usando **Room Database**
- [ ] Criar DAO (Data Access Object) para operações CRUD locais
- [ ] Implementar mecanismo de **sincronização**: verificar se há atualizações no Firebase e atualizar o banco local
- [ ] Utilizar campo de versão/timestamp para controle de atualizações
- [ ] Garantir que o quiz funcione **offline** (usando dados locais)

**Boas práticas:**
- Usar transações do Room para operações que envolvem múltiplas inserções
- Implementar checagem de conectividade antes de sincronizar
- Usar `Flow` ou `LiveData` do Room para observar mudanças nos dados

---

### 3. 🎯 Execução do Quiz e Controle de Progresso

- [ ] Criar tela de execução do quiz com **UI dinâmica**
- [ ] Carregamento das questões a partir do **banco local (Room)**
- [ ] Exibir questões uma a uma com opções de resposta
- [ ] Implementar **timer** por questão ou por quiz completo
- [ ] Registrar resposta selecionada pelo usuário
- [ ] Navegar entre questões (próxima/anterior ou somente próxima)
- [ ] Calcular pontuação ao final: **acertos, percentual, tempo total**
- [ ] Exibir tela de **resultado/desempenho** ao finalizar
- [ ] Salvar resultado da sessão **localmente** (Room Database)
- [ ] Salvar resultado da sessão **na nuvem** (Firebase) vinculado ao perfil do usuário
- [ ] Impedir que o usuário refaça o mesmo quiz sem querer (controle de sessão)

**Boas práticas:**
- Usar ViewModel para manter o estado do quiz durante rotações de tela
- Implementar confirmação antes de sair do quiz em andamento
- Desabilitar botão de resposta após seleção para evitar duplo clique

---

### 4. 📊 Histórico Pessoal

- [ ] Criar tela de **histórico de quizzes respondidos**
- [ ] Listar sessões anteriores com: data, pontuação, percentual de acertos, tempo
- [ ] Implementar visualização de **estatísticas gerais**:
  - Total de quizzes respondidos
  - Média de acertos
  - Melhor pontuação
  - Evolução ao longo do tempo
- [ ] Buscar dados do histórico tanto do **Room (local)** quanto do **Firebase (nuvem)**
- [ ] Permitir comparação de desempenho entre diferentes sessões
- [ ] Exibir gráficos simples (opcional, mas recomendado: usar biblioteca como `MPAndroidChart`)

---

### 5. 🎨 Interface e Experiência (UI/UX)

- [ ] **Tela de Login** — Design limpo com campos de e-mail/senha e botão Google
- [ ] **Tela Inicial (Home)** — Lista de quizzes disponíveis com categorias
- [ ] **Tela de Execução do Quiz** — UI fluida com progresso visual
- [ ] **Tela de Resultado** — Pontuação detalhada com animações
- [ ] **Tela de Histórico/Estatísticas** — Listagem e gráficos
- [ ] **Dashboard** — Visão geral do desempenho do usuário
- [ ] **Ranking** — Classificação geral entre todos os usuários

**Diretrizes de Design:**
- [ ] Seguir **Material Design 3** (Material You)
- [ ] Usar componentes do **Material Components for Android**
- [ ] Garantir **responsividade** para diferentes tamanhos de tela
- [ ] Implementar **tema claro e escuro** (opcional, mas diferencial)
- [ ] Usar cores consistentes, tipografia adequada e espaçamento correto
- [ ] Adicionar animações e transições suaves entre telas

**Tecnologia recomendada:** Jetpack Compose (moderno) ou XML com View Binding

---

### 6. 🤝 Desenvolvimento em Equipe

- [ ] Criar repositório no **GitHub** com README descritivo
- [ ] Configurar **branches** por funcionalidade (feature branches)
- [ ] Usar **Pull Requests** para revisão de código entre membros
- [ ] Documentar commits de forma clara e padronizada
- [ ] Manter comunicação ativa via **Teams** e/ou **WhatsApp**
- [ ] Seguir o documento `github.pdf` para estratégias de versionamento
- [ ] Documentar decisões técnicas em arquivos markdown no repositório

---

## 🌟 Funcionalidades Extras (Diferenciais)

> Implementar para se destacar e potencialmente ganhar pontos extras.

- [ ] **Tema Claro/Escuro** — Toggle de tema com persistência da preferência
- [ ] **Gráficos de Desempenho** — Usar MPAndroidChart para gráficos de evolução
- [ ] **Categorias de Quiz** — Permitir filtrar quizzes por categoria/tema
- [ ] **Dificuldade Progressiva** — Questões ficam mais difíceis conforme pontuação
- [ ] **Notificações Push** — Notificar quando novos quizzes estiverem disponíveis (Firebase Cloud Messaging)
- [ ] **Animações Avançadas** — Transições Lottie, feedback visual de acerto/erro
- [ ] **Compartilhamento de Resultado** — Compartilhar resultado nas redes sociais
- [ ] **Sistema de Conquistas/Badges** — Desbloqueáveis por quantidade de quizzes, acertos, etc.
- [ ] **Modo Multiplayer em Tempo Real** — Dois ou mais jogadores respondendo simultaneamente
- [ ] **Feedback por Questão** — Mostrar a resposta correta e uma breve explicação

---

## 📦 Entregáveis

| Entregável | Status | Responsável |
|---|---|---|
| Código-fonte documentado no GitHub | ⬜ Pendente | Todos |
| APK instalável (no GitHub) | ⬜ Pendente | Estudante D |
| Relatório explicativo (decisões, papéis, dificuldades) | ⬜ Pendente | Todos |
| Vídeo demonstrativo (3–5 min) | ⬜ Pendente | Todos |
| Slides da apresentação (máx. 7 slides) | ⬜ Pendente | Todos |

---

## 🎤 Apresentação — Checklist

- [ ] Máximo de **7 slides**
- [ ] Imagens, textos e códigos **legíveis** (fontes grandes, sem imagens borradas)
- [ ] **Não usar capturas de tela para código** — copiar e colar o código nos slides
- [ ] Tom de voz adequado e comportamento cordial
- [ ] Incluir **2 slides** sobre:
  - [ ] Dificuldades encontradas durante o desenvolvimento
  - [ ] Observações sobre LLMs usadas (nomes, prompts importantes, opinião geral)
- [ ] **Demonstração ao vivo** do app no smartphone
- [ ] Se possível, executar no **emulador** ou **espelhar tela do celular** na projeção

---

## 🛠️ Stack Tecnológica Recomendada

| Camada | Tecnologia |
|---|---|
| **Linguagem** | Kotlin |
| **UI** | Jetpack Compose ou XML + View Binding |
| **Arquitetura** | MVVM + Repository Pattern |
| **Autenticação** | Firebase Authentication |
| **Banco Remoto** | Cloud Firestore ou Realtime Database |
| **Banco Local** | Room Database |
| **Preferências** | SharedPreferences / DataStore |
| **Injeção de Dependência** | Hilt (recomendado) ou Koin |
| **Navegação** | Jetpack Navigation Component |
| **Gráficos (opcional)** | MPAndroidChart |
| **Controle de Versão** | Git + GitHub |

---

## 📅 Cronograma Sugerido

| Período | Atividade | Responsável |
|---|---|---|
| **Semana 1** | Setup do projeto, Firebase, arquitetura base, modelagem de dados | Todos |
| **Semana 1** | Implementar autenticação (login/cadastro) | Estudante B |
| **Semana 1** | Criar schemas do Room Database e entidades | Estudante C |
| **Semana 1** | Prototipar telas principais (Login, Home) | Estudante A |
| **Semana 2** | Download e sincronização de questões Firebase ↔ Room | Estudante C |
| **Semana 2** | Tela de execução do quiz e lógica de controle | Estudante A + D |
| **Semana 2** | Tela de resultado e salvamento de desempenho | Estudante B + D |
| **Semana 3** | Histórico pessoal e estatísticas | Estudante C |
| **Semana 3** | Dashboard e Ranking | Estudante A |
| **Semana 3** | Testes e ajustes de integração | Estudante D |
| **Semana 4** | Polimento de UI, animações, tema | Estudante A |
| **Semana 4** | Gerar APK, relatório, vídeo, slides | Todos |
| **Pré-entrega** | Revisão final, ensaio da apresentação | Todos |

---

## 🔒 Boas Práticas de Segurança

1. **Firebase Security Rules:** configurar regras que permitam leitura/escrita apenas para usuários autenticados
2. **Validação de Dados:** validar inputs tanto no cliente quanto nas regras do Firestore
3. **Tokens de Autenticação:** nunca expor tokens ou chaves de API no código-fonte; usar `google-services.json` de forma segura
4. **Arquivo `.gitignore`:** garantir que `google-services.json`, chaves de API e arquivos sensíveis **não** sejam commitados
5. **ProGuard/R8:** obfuscar o código na build de release
6. **HTTPS:** todas as comunicações com Firebase já utilizam HTTPS por padrão
7. **SharedPreferences seguro:** se armazenar dados sensíveis localmente, usar `EncryptedSharedPreferences`
8. **Logout seguro:** limpar dados locais sensíveis ao deslogar

---

## 📚 Referências Úteis

### Tutoriais em Vídeo
- [ONLINE Quiz App with FIREBASE — Android Studio (2024)](https://www.youtube.com/watch?v=yjNAnjqm_50)
- [Build a Quiz app in Android Studio Project](https://www.youtube.com/watch?v=L6Noa0_k7hg)
- Buscar: "Firebase Authentication Android Kotlin" no YouTube
- Buscar: "Room Database Android Kotlin" no YouTube

### Documentação Oficial
- [Firebase para Android — Setup](https://firebase.google.com/docs/android/setup)
- [Firebase Authentication](https://firebase.google.com/docs/auth/android/start)
- [Firebase Realtime Database](https://firebase.google.com/docs/database/android/start)
- [Cloud Firestore](https://firebase.google.com/docs/firestore/quickstart)
- [Room Persistence Library](https://developer.android.com/training/data-storage/room)
- [Material Design](https://material.io/develop/android)

### Repositórios de Exemplo
- [Quiz App com Firebase — JSON de questões](https://github.com/bimalkaf/Android_QuizAppWithFirebase/blob/main/question.json)
- [Quiz App — Jetpack Compose](https://github.com/worldsat/project247)

---

## ⚡ Dicas Finais

1. **Comecem pelo setup:** Configure o projeto Android, integre o Firebase e crie a estrutura de pastas antes de qualquer funcionalidade.
2. **Testem frequentemente:** Não acumulem funcionalidades sem testar. Façam testes incrementais.
3. **Documentem tudo:** Cada decisão técnica, cada dificuldade. Isso será útil no relatório e na apresentação.
4. **Comuniquem-se:** Usem as ferramentas de comunicação (Teams/WhatsApp) para alinhar expectativas e resolver bloqueios.
5. **Usem o Git corretamente:** Commits pequenos e frequentes, com mensagens descritivas. Feature branches e Pull Requests.
6. **Preparem a apresentação com antecedência:** Ensaiem e garantam que o app funciona para a demonstração ao vivo.

---

> 📝 *Este plano de ação foi gerado com base no documento `trabalho4.pdf` do Prof. Alexsandro Santos Soares — UFU.*

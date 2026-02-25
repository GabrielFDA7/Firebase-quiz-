# 🧠 Quiz App Firebase

Aplicativo Android de quiz colaborativo com Firebase, desenvolvido como trabalho prático da disciplina de Computação — UFU.

## 📋 Funcionalidades

- **Autenticação** com Firebase (e-mail/senha + Google)
- **Questões** armazenadas no Firestore com sincronização local (Room)
- **Quiz dinâmico** com timer e controle de progresso
- **Histórico pessoal** e estatísticas de desempenho
- **Dashboard** e **Ranking** global
- **Material Design 3** com suporte a tema claro/escuro

## 🛠️ Stack

| Camada | Tecnologia |
|---|---|
| Linguagem | Kotlin |
| UI | Jetpack Compose |
| Arquitetura | MVVM |
| Auth | Firebase Authentication |
| Banco Remoto | Cloud Firestore |
| Banco Local | Room Database |
| Navegação | Jetpack Navigation Compose |

## 🚀 Setup

1. Clone o repositório
2. Registre o app no [Firebase Console](https://console.firebase.google.com/)
3. Ative **Authentication** (e-mail/senha + Google) e **Cloud Firestore**
4. Baixe o `google-services.json` e substitua o placeholder em `app/`
5. Abra no Android Studio e execute

## 👥 Equipe

| Membro | Papel |
|---|---|
| Estudante A | Interface e UX |
| Estudante B | Firebase / Autenticação |
| Estudante C | Dados Locais / Sincronização |
| Estudante D | Testes / Integração |

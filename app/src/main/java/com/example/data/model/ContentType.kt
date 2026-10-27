package com.example.data.model

enum class ContentType {
    TEXT,
    IMAGE,
    QUIZ,
    FLASHCARD,
    FILE,
    CODE
}

data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class QuizPayload(
    val title: String,
    val questions: List<QuizQuestion>
)

data class FlashcardItem(
    val id: Int,
    val front: String,
    val back: String,
    val difficulty: String = "Moyen" // Facile, Moyen, Difficile
)

data class FlashcardPayload(
    val title: String,
    val cards: List<FlashcardItem>
)

data class CodePayload(
    val language: String,
    val code: String,
    val explanation: String
)

data class FilePayload(
    val title: String,
    val fileType: String, // PDF, DOC, TXT
    val pageCount: Int,
    val summary: String,
    val keyPoints: List<String>
)

data class ImagePayload(
    val prompt: String,
    val imageUrl: String,
    val aspect: String = "1:1",
    val style: String = "Digital Art Neo-Futurism"
)

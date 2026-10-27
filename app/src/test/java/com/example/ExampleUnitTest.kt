package com.example

import com.example.data.model.CodePayload
import com.example.data.model.ContentType
import com.example.data.model.FilePayload
import com.example.data.model.FlashcardItem
import com.example.data.model.FlashcardPayload
import com.example.data.model.ImagePayload
import com.example.data.model.QuizPayload
import com.example.data.model.QuizQuestion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testContentTypeEnum() {
        assertEquals("TEXT", ContentType.TEXT.name)
        assertEquals("IMAGE", ContentType.IMAGE.name)
        assertEquals("QUIZ", ContentType.QUIZ.name)
        assertEquals("FLASHCARD", ContentType.FLASHCARD.name)
        assertEquals("FILE", ContentType.FILE.name)
        assertEquals("CODE", ContentType.CODE.name)
    }

    @Test
    fun testQuizDataModel() {
        val q1 = QuizQuestion(
            id = 1,
            question = "Qu'est-ce que NEURA AI ?",
            options = listOf("Assistant Personnel Intelligent", "Jeu vidéo", "Navigateur"),
            correctIndex = 0,
            explanation = "NEURA est un assistant personnel intelligent multimodal."
        )
        val quiz = QuizPayload(
            title = "Test Quiz",
            questions = listOf(q1)
        )

        assertEquals("Test Quiz", quiz.title)
        assertEquals(1, quiz.questions.size)
        assertEquals(0, quiz.questions[0].correctIndex)
        assertEquals(3, quiz.questions[0].options.size)
    }

    @Test
    fun testFlashcardDataModel() {
        val card = FlashcardItem(
            id = 1,
            front = "Concept de fonction pure",
            back = "Fonction sans effet de bord",
            difficulty = "Facile"
        )
        val payload = FlashcardPayload(
            title = "Fiche Révision",
            cards = listOf(card)
        )

        assertEquals("Fiche Révision", payload.title)
        assertEquals("Facile", payload.cards[0].difficulty)
    }

    @Test
    fun testCodeAndFileModels() {
        val code = CodePayload("Python", "print('NEURA')", "Affiche le nom")
        assertEquals("Python", code.language)

        val file = FilePayload("Cours.pdf", "PDF", 42, "Résumé", listOf("Point 1"))
        assertEquals(42, file.pageCount)
        assertEquals("PDF", file.fileType)

        val img = ImagePayload("Neural brain", "", "1:1", "Digital Art")
        assertEquals("1:1", img.aspect)
    }
}


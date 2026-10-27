package com.example.data.repository

import com.example.data.local.SettingsManager
import com.example.data.local.dao.ConversationDao
import com.example.data.local.dao.CreationDao
import com.example.data.local.dao.MemoryDao
import com.example.data.local.dao.MessageDao
import com.example.data.local.entities.ConversationEntity
import com.example.data.local.entities.CreationEntity
import com.example.data.local.entities.MemoryEntity
import com.example.data.local.entities.MessageEntity
import com.example.data.model.ContentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class NeuraRepository(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao,
    private val memoryDao: MemoryDao,
    private val creationDao: CreationDao,
    val settingsManager: SettingsManager,
    private val aiEngineService: AiEngineService
) {

    val allConversations: Flow<List<ConversationEntity>> = conversationDao.getAllConversations()
    val allMemories: Flow<List<MemoryEntity>> = memoryDao.getAllMemories()
    val allCreations: Flow<List<CreationEntity>> = creationDao.getAllCreations()

    fun getMessagesForConversation(conversationId: String): Flow<List<MessageEntity>> {
        return messageDao.getMessagesForConversation(conversationId)
    }

    suspend fun createNewConversation(initialTitle: String = "Nouvelle conversation"): String {
        val id = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        val conversation = ConversationEntity(
            id = id,
            title = initialTitle,
            createdAt = now,
            updatedAt = now,
            lastMessagePreview = "Conversation démarrée",
            messageCount = 0
        )
        conversationDao.insertOrUpdate(conversation)
        return id
    }

    suspend fun sendMessage(
        conversationId: String,
        userText: String,
        attachedFileName: String? = null
    ): MessageEntity {
        val now = System.currentTimeMillis()

        // 1. Save user message
        val userMsgId = UUID.randomUUID().toString()
        val userMessage = MessageEntity(
            id = userMsgId,
            conversationId = conversationId,
            isUser = true,
            contentType = ContentType.TEXT.name,
            textContent = userText,
            payloadJson = "",
            timestamp = now
        )
        messageDao.insertMessage(userMessage)

        // Update conversation summary
        val conv = conversationDao.getConversationById(conversationId)
        val updatedConv = conv?.copy(
            updatedAt = now,
            lastMessagePreview = userText.take(40),
            messageCount = conv.messageCount + 1,
            title = if (conv.messageCount == 0 && userText.isNotBlank()) userText.take(30) else conv.title
        ) ?: ConversationEntity(
            id = conversationId,
            title = userText.take(30).ifBlank { "Discussion NEURA" },
            createdAt = now,
            updatedAt = now,
            lastMessagePreview = userText.take(40),
            messageCount = 1
        )
        conversationDao.insertOrUpdate(updatedConv)

        // 2. Fetch active memories for contextual augmentation
        val currentMemories = memoryDao.getAllMemories().firstOrNull()?.map { "${it.category} : ${it.key} - ${it.value}" } ?: emptyList()

        // 3. Orchestrate response
        val result = aiEngineService.orchestrateResponse(
            prompt = userText,
            attachedFileName = attachedFileName,
            activeMemories = currentMemories
        )

        // 4. Save AI message
        val aiMsgId = UUID.randomUUID().toString()
        val aiMessage = MessageEntity(
            id = aiMsgId,
            conversationId = conversationId,
            isUser = false,
            contentType = result.contentType.name,
            textContent = result.textMessage,
            payloadJson = result.payloadJson,
            timestamp = System.currentTimeMillis()
        )
        messageDao.insertMessage(aiMessage)

        // 5. If generated content is a creation, save to creations library
        if (result.contentType != ContentType.TEXT && result.payloadJson.isNotBlank()) {
            val creationTitle = when (result.contentType) {
                ContentType.IMAGE -> "Illustration : ${userText.take(24)}"
                ContentType.QUIZ -> "QCM : ${userText.take(24)}"
                ContentType.FLASHCARD -> "Fiche : ${userText.take(24)}"
                ContentType.CODE -> "Code : ${userText.take(24)}"
                ContentType.FILE -> "Document : ${attachedFileName ?: userText.take(24)}"
                else -> "Création NEURA"
            }
            creationDao.insertCreation(
                CreationEntity(
                    id = UUID.randomUUID().toString(),
                    type = result.contentType.name,
                    title = creationTitle,
                    subtitle = "Généré par NEURA AI",
                    contentJson = result.payloadJson,
                    previewUrl = "",
                    createdAt = System.currentTimeMillis()
                )
            )
        }

        // Update conversation message count again
        conversationDao.insertOrUpdate(
            updatedConv.copy(
                updatedAt = System.currentTimeMillis(),
                lastMessagePreview = result.textMessage.take(40),
                messageCount = updatedConv.messageCount + 1
            )
        )

        return aiMessage
    }

    suspend fun deleteConversation(id: String) {
        messageDao.deleteMessagesForConversation(id)
        conversationDao.deleteConversation(id)
    }

    suspend fun addMemory(key: String, value: String, category: String, importance: String = "Haute"): Long {
        return memoryDao.insertMemory(
            MemoryEntity(
                key = key,
                value = value,
                category = category,
                importance = importance,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun updateMemory(memory: MemoryEntity) {
        memoryDao.updateMemory(memory)
    }

    suspend fun deleteMemory(id: Long) {
        memoryDao.deleteMemory(id)
    }

    suspend fun deleteCreation(id: String) {
        creationDao.deleteCreation(id)
    }

    suspend fun testConnection(): ConnectionTestResult {
        return aiEngineService.testConnection()
    }

    suspend fun testAgnesConnection(): ConnectionTestResult {
        return aiEngineService.testAgnesConnection()
    }

    suspend fun seedInitialDataIfEmpty() {
        // Seed memories if empty
        val existingMemories = memoryDao.getAllMemories().firstOrNull()
        if (existingMemories.isNullOrEmpty()) {
            addMemory("Identité", settingsManager.userName, "Profil", "Haute")
            addMemory("Domaine", "Étudiant en géomatique & informatique", "Profil", "Haute")
            addMemory("Style de réponse", "Explications synthétiques avec analogies concrètes", "Préférences", "Haute")
            addMemory("Objectif 2026", "Concevoir une application mobile avec IA multimodale", "Objectifs", "Haute")
            addMemory("Projet en cours", "NEURA AI - Assistant personnel intelligent", "Projets", "Haute")
            addMemory("Format d'apprentissage", "Cartes mémoires et quiz interactifs réguliers", "Méthode", "Moyenne")
        }

        // Seed sample creations if empty
        val existingCreations = creationDao.getAllCreations().firstOrNull()
        if (existingCreations.isNullOrEmpty()) {
            val sampleImageJson = "{\"prompt\":\"Digital neural brain network with neon cyan and electric purple\",\"imageUrl\":\"\",\"aspect\":\"1:1\",\"style\":\"Digital Art Neo-Futurism\"}"
            creationDao.insertCreation(
                CreationEntity(
                    id = UUID.randomUUID().toString(),
                    type = ContentType.IMAGE.name,
                    title = "Illustration Cerveau Numérique",
                    subtitle = "Généré via Agnes AI v2.1",
                    contentJson = sampleImageJson,
                    previewUrl = "",
                    createdAt = System.currentTimeMillis() - 7200000
                )
            )

            val sampleQuizJson = "{\"title\":\"Quiz Python Avancé\",\"questions\":[{\"id\":1,\"question\":\"Quel est le coût temporel de l'accès à un élément par clé dans un dictionnaire ?\",\"options\":[\"O(1)\",\"O(N)\",\"O(log N)\",\"O(N^2)\"],\"correctIndex\":0,\"explanation\":\"Un dictionnaire s'appuie sur une table de hachage O(1).\"}]}"
            creationDao.insertCreation(
                CreationEntity(
                    id = UUID.randomUUID().toString(),
                    type = ContentType.QUIZ.name,
                    title = "Quiz Fondamentaux Python",
                    subtitle = "3 questions interactives",
                    contentJson = sampleQuizJson,
                    previewUrl = "",
                    createdAt = System.currentTimeMillis() - 3600000
                )
            )
        }
    }
}

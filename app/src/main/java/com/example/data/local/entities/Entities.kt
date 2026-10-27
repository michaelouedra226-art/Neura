package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val createdAt: Long,
    val updatedAt: Long,
    val lastMessagePreview: String,
    val messageCount: Int = 0
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey
    val id: String,
    val conversationId: String,
    val isUser: Boolean,
    val contentType: String, // TEXT, IMAGE, QUIZ, FLASHCARD, FILE, CODE
    val textContent: String,
    val payloadJson: String = "",
    val timestamp: Long
)

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val key: String,
    val value: String,
    val category: String, // Profil, Préférences, Objectifs, Projets, Méthode
    val importance: String = "Haute", // Haute, Moyenne, Basse
    val createdAt: Long
)

@Entity(tableName = "creations")
data class CreationEntity(
    @PrimaryKey
    val id: String,
    val type: String, // IMAGE, FLASHCARD, QUIZ, FILE, CODE
    val title: String,
    val subtitle: String,
    val contentJson: String,
    val previewUrl: String = "",
    val createdAt: Long
)

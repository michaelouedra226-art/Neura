package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.ConversationDao
import com.example.data.local.dao.CreationDao
import com.example.data.local.dao.MemoryDao
import com.example.data.local.dao.MessageDao
import com.example.data.local.entities.ConversationEntity
import com.example.data.local.entities.CreationEntity
import com.example.data.local.entities.MemoryEntity
import com.example.data.local.entities.MessageEntity

@Database(
    entities = [
        ConversationEntity::class,
        MessageEntity::class,
        MemoryEntity::class,
        CreationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NeuraDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    abstract fun memoryDao(): MemoryDao
    abstract fun creationDao(): CreationDao

    companion object {
        @Volatile
        private var INSTANCE: NeuraDatabase? = null

        fun getDatabase(context: Context): NeuraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NeuraDatabase::class.java,
                    "neura_ai_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

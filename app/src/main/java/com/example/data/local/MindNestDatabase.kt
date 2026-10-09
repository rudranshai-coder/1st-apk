package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ChatMessageEntity
import com.example.data.model.DocumentEntity
import com.example.data.model.FlashcardEntity
import com.example.data.model.QuizAttemptEntity
import com.example.data.model.SavedItemEntity
import com.example.data.model.StudySessionEntity
import com.example.data.model.TopicProgressEntity

@Database(
    entities = [
        ChatMessageEntity::class,
        DocumentEntity::class,
        SavedItemEntity::class,
        FlashcardEntity::class,
        StudySessionEntity::class,
        QuizAttemptEntity::class,
        TopicProgressEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class MindNestDatabase : RoomDatabase() {
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun documentDao(): DocumentDao
    abstract fun savedItemDao(): SavedItemDao
    abstract fun flashcardDao(): FlashcardDao
    abstract fun studySessionDao(): StudySessionDao
    abstract fun quizAttemptDao(): QuizAttemptDao
    abstract fun topicProgressDao(): TopicProgressDao

    companion object {
        @Volatile
        private var INSTANCE: MindNestDatabase? = null

        fun getDatabase(context: Context): MindNestDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MindNestDatabase::class.java,
                    "mindnest_database"
                )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

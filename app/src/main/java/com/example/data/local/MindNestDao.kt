package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ChatMessageEntity
import com.example.data.model.DocumentEntity
import com.example.data.model.FlashcardEntity
import com.example.data.model.QuizAttemptEntity
import com.example.data.model.SavedItemEntity
import com.example.data.model.StudySessionEntity
import com.example.data.model.SubjectStudyStat
import com.example.data.model.TopicProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getMessagesForSession(sessionId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("UPDATE chat_messages SET isBookmarked = :bookmarked WHERE id = :id")
    suspend fun setBookmark(id: String, bookmarked: Boolean)

    @Query("DELETE FROM chat_messages")
    suspend fun clearAll()
}

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents ORDER BY dateAdded DESC")
    fun getAllDocuments(): Flow<List<DocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: DocumentEntity)

    @Query("DELETE FROM documents WHERE id = :id")
    suspend fun deleteDocument(id: String)
}

@Dao
interface SavedItemDao {
    @Query("SELECT * FROM saved_items ORDER BY dateSaved DESC")
    fun getAllSavedItems(): Flow<List<SavedItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedItem(item: SavedItemEntity)

    @Query("DELETE FROM saved_items WHERE id = :id")
    suspend fun deleteSavedItem(id: String)
}

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcards WHERE semester = :semester")
    fun getFlashcardsBySemester(semester: Int): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcards")
    fun getAllFlashcards(): Flow<List<FlashcardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcards(cards: List<FlashcardEntity>)

    @Update
    suspend fun updateFlashcard(card: FlashcardEntity)
}

@Dao
interface StudySessionDao {
    @Query("SELECT * FROM study_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<StudySessionEntity>>

    @Query("SELECT * FROM study_sessions WHERE subject = :subject ORDER BY timestamp DESC")
    fun getSessionsBySubject(subject: String): Flow<List<StudySessionEntity>>

    @Query("SELECT * FROM study_sessions WHERE id = :id LIMIT 1")
    suspend fun getSessionById(id: String): StudySessionEntity?

    @Query("SELECT SUM(durationMinutes) FROM study_sessions WHERE subject = :subject")
    fun getTotalMinutesBySubject(subject: String): Flow<Int?>

    @Query("SELECT SUM(durationMinutes) FROM study_sessions")
    fun getTotalMinutesAll(): Flow<Int?>

    @Query("SELECT subject, SUM(durationMinutes) AS totalMinutes, COUNT(*) AS sessionCount FROM study_sessions GROUP BY subject ORDER BY totalMinutes DESC")
    fun getSubjectStudyStats(): Flow<List<SubjectStudyStat>>

    @Query("SELECT * FROM study_sessions WHERE timestamp >= :sinceTimestamp ORDER BY timestamp DESC")
    fun getRecentSessions(sinceTimestamp: Long): Flow<List<StudySessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSessions(sessions: List<StudySessionEntity>)

    @Update
    suspend fun updateSession(session: StudySessionEntity)

    @Delete
    suspend fun deleteSession(session: StudySessionEntity)

    @Query("DELETE FROM study_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: String)

    @Query("DELETE FROM study_sessions")
    suspend fun clearAllSessions()
}

@Dao
interface QuizAttemptDao {
    @Query("SELECT * FROM quiz_attempts ORDER BY timestamp ASC")
    fun getAllAttemptsAsc(): Flow<List<QuizAttemptEntity>>

    @Query("SELECT * FROM quiz_attempts ORDER BY timestamp DESC")
    fun getRecentAttempts(): Flow<List<QuizAttemptEntity>>

    @Query("SELECT * FROM quiz_attempts WHERE subject = :subject ORDER BY timestamp ASC")
    fun getAttemptsBySubject(subject: String): Flow<List<QuizAttemptEntity>>

    @Query("SELECT COUNT(*) FROM quiz_attempts")
    fun getTotalAttemptsCount(): Flow<Int>

    @Query("SELECT AVG(percentage) FROM quiz_attempts")
    fun getAveragePercentage(): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: QuizAttemptEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempts(attempts: List<QuizAttemptEntity>)

    @Query("DELETE FROM quiz_attempts WHERE id = :id")
    suspend fun deleteAttempt(id: String)
}

@Dao
interface TopicProgressDao {
    @Query("SELECT * FROM topic_progress ORDER BY subject ASC, topicName ASC")
    fun getAllTopics(): Flow<List<TopicProgressEntity>>

    @Query("SELECT * FROM topic_progress WHERE subject = :subject ORDER BY topicName ASC")
    fun getTopicsBySubject(subject: String): Flow<List<TopicProgressEntity>>

    @Query("SELECT * FROM topic_progress WHERE isCompleted = 1")
    fun getCompletedTopics(): Flow<List<TopicProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopic(topic: TopicProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopics(topics: List<TopicProgressEntity>)

    @Query("UPDATE topic_progress SET isCompleted = :completed, completedTimestamp = :timestamp WHERE id = :id")
    suspend fun updateCompletion(id: String, completed: Boolean, timestamp: Long?)

    @Query("SELECT COUNT(*) FROM topic_progress WHERE isCompleted = 1")
    fun getCompletedTopicCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM topic_progress")
    fun getTotalTopicCount(): Flow<Int>
}



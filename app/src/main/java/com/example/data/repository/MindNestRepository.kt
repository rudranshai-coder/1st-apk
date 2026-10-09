package com.example.data.repository

import com.example.data.local.MindNestDatabase
import com.example.data.model.AttachmentInfo
import com.example.data.model.BcaSubject
import com.example.data.model.ChatMessage
import com.example.data.model.ChatMessageEntity
import com.example.data.model.DocumentEntity
import com.example.data.model.ExamPaper
import com.example.data.model.FlashcardEntity
import com.example.data.model.McqQuestion
import com.example.data.model.MessageRole
import com.example.data.model.QuizAttemptEntity
import com.example.data.model.SavedItemEntity
import com.example.data.model.StudySessionEntity
import com.example.data.model.SubjectStudyStat
import com.example.data.model.TopicProgressEntity
import com.example.data.model.UserSession
import com.example.data.remote.BcaKnowledgeBase
import com.example.data.remote.GeminiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MindNestRepository(private val db: MindNestDatabase) {

    private var geminiService = GeminiService()

    fun updateCustomApiKey(apiKey: String) {
        geminiService = GeminiService(apiKey)
    }

    // Messages
    fun getChatMessages(): Flow<List<ChatMessage>> {
        return db.chatMessageDao().getAllMessages().map { entities ->
            entities.map { entity ->
                ChatMessage(
                    id = entity.id,
                    sessionId = entity.sessionId,
                    role = if (entity.role == "USER") MessageRole.USER else MessageRole.ASSISTANT,
                    text = entity.text,
                    timestamp = entity.timestamp,
                    subjectTag = entity.subjectTag,
                    codeSnippet = entity.codeSnippet,
                    codeLanguage = entity.codeLanguage,
                    isBookmarked = entity.isBookmarked
                )
            }
        }
    }

    suspend fun saveMessage(msg: ChatMessage) {
        val entity = ChatMessageEntity(
            id = msg.id,
            sessionId = msg.sessionId,
            role = msg.role.name,
            text = msg.text,
            timestamp = msg.timestamp,
            subjectTag = msg.subjectTag,
            codeSnippet = msg.codeSnippet,
            codeLanguage = msg.codeLanguage,
            isBookmarked = msg.isBookmarked,
            attachmentNames = msg.attachments.joinToString(",") { it.fileName }
        )
        db.chatMessageDao().insertMessage(entity)
    }

    suspend fun toggleBookmark(id: String, isBookmarked: Boolean) {
        db.chatMessageDao().setBookmark(id, isBookmarked)
    }

    suspend fun clearChat() {
        db.chatMessageDao().clearAll()
    }

    // Documents
    fun getAllDocuments(): Flow<List<DocumentEntity>> = db.documentDao().getAllDocuments()

    suspend fun addDocument(doc: DocumentEntity) = db.documentDao().insertDocument(doc)

    suspend fun deleteDocument(id: String) = db.documentDao().deleteDocument(id)

    // Saved Items
    fun getAllSavedItems(): Flow<List<SavedItemEntity>> = db.savedItemDao().getAllSavedItems()

    suspend fun saveItem(item: SavedItemEntity) = db.savedItemDao().insertSavedItem(item)

    suspend fun deleteSavedItem(id: String) = db.savedItemDao().deleteSavedItem(id)

    // Flashcards
    fun getFlashcards(semester: Int): Flow<List<FlashcardEntity>> =
        db.flashcardDao().getFlashcardsBySemester(semester)

    suspend fun initDefaultFlashcards() {
        db.flashcardDao().insertFlashcards(BcaKnowledgeBase.defaultFlashcards)
    }

    suspend fun updateFlashcard(card: FlashcardEntity) = db.flashcardDao().updateFlashcard(card)

    // Gemini Query
    suspend fun askNesty(
        prompt: String,
        history: List<ChatMessage>,
        attachments: List<AttachmentInfo>,
        subjectContext: String? = null
    ): Result<String> {
        return geminiService.generateResponse(prompt, history, attachments, subjectContext)
    }

    suspend fun generateQuizWithGemini(
        subject: String,
        topic: String? = null,
        difficulty: String = "University Exam Standard",
        questionCount: Int = 5
    ): Result<List<McqQuestion>> {
        return geminiService.generateQuizQuestions(subject, topic, difficulty, questionCount)
    }

    // Knowledge Base Access
    fun getSubjects(): List<BcaSubject> = BcaKnowledgeBase.subjects
    fun getSampleMcqs(): List<McqQuestion> = BcaKnowledgeBase.sampleMcqs
    fun getPastYearPapers(): List<ExamPaper> = BcaKnowledgeBase.pastYearPapers

    // Study Sessions & Hours Tracking
    fun getAllStudySessions(): Flow<List<StudySessionEntity>> = db.studySessionDao().getAllSessions()

    fun getStudySessionsBySubject(subject: String): Flow<List<StudySessionEntity>> =
        db.studySessionDao().getSessionsBySubject(subject)

    fun getSubjectStudyStats(): Flow<List<SubjectStudyStat>> =
        db.studySessionDao().getSubjectStudyStats()

    fun getTotalStudyMinutes(): Flow<Int?> =
        db.studySessionDao().getTotalMinutesAll()

    suspend fun saveStudySession(session: StudySessionEntity) {
        db.studySessionDao().insertSession(session)
    }

    suspend fun deleteStudySession(id: String) {
        db.studySessionDao().deleteSessionById(id)
    }

    suspend fun initDefaultStudySessions() {
        val initialSessions = listOf(
            StudySessionEntity(
                id = "study_dpa_norm",
                subject = "DPA",
                topic = "Relational Model & 1NF-BCNF Normalization",
                durationMinutes = 90,
                timestamp = System.currentTimeMillis() - 86400000L * 1, // 1 day ago
                notes = "Mastered lossless join decomposition and candidate key superkey verification algorithms.",
                focusRating = 5
            ),
            StudySessionEntity(
                id = "study_os_deadlock",
                subject = "Operating Systems",
                topic = "Deadlock Prevention & Banker's Algorithm",
                durationMinutes = 75,
                timestamp = System.currentTimeMillis() - 86400000L * 2, // 2 days ago
                notes = "Practiced resource allocation matrix safety checks and circular wait conditions.",
                focusRating = 4
            ),
            StudySessionEntity(
                id = "study_math_logic",
                subject = "Discrete Mathematics",
                topic = "Propositional Logic, Tautologies & Truth Tables",
                durationMinutes = 60,
                timestamp = System.currentTimeMillis() - 86400000L * 3, // 3 days ago
                notes = "Proved De Morgan's laws and simplified boolean expressions for exam prep.",
                focusRating = 5
            ),
            StudySessionEntity(
                id = "study_dpa_sql",
                subject = "DPA",
                topic = "SQL Complex Nested Subqueries & Joins",
                durationMinutes = 60,
                timestamp = System.currentTimeMillis() - 86400000L * 4,
                notes = "Solved 15 practical queries on Student and Department schemas.",
                focusRating = 5
            )
        )
        db.studySessionDao().insertSessions(initialSessions)
    }

    // Quiz Attempts
    fun getAllQuizAttemptsAsc(): Flow<List<QuizAttemptEntity>> =
        db.quizAttemptDao().getAllAttemptsAsc()

    fun getRecentQuizAttempts(): Flow<List<QuizAttemptEntity>> =
        db.quizAttemptDao().getRecentAttempts()

    fun getTotalQuizCount(): Flow<Int> =
        db.quizAttemptDao().getTotalAttemptsCount()

    fun getAverageQuizPercentage(): Flow<Double?> =
        db.quizAttemptDao().getAveragePercentage()

    suspend fun saveQuizAttempt(attempt: QuizAttemptEntity) {
        db.quizAttemptDao().insertAttempt(attempt)
    }

    suspend fun initDefaultQuizAttempts() {
        val now = System.currentTimeMillis()
        val oneDay = 86400000L
        val initialAttempts = listOf(
            QuizAttemptEntity(
                id = "quiz_att_1",
                quizTitle = "DPA SQL & Relational Models",
                subject = "DPA",
                score = 3,
                totalQuestions = 5,
                percentage = 60,
                timestamp = now - (oneDay * 9),
                timeSpentSeconds = 140
            ),
            QuizAttemptEntity(
                id = "quiz_att_2",
                quizTitle = "OS CPU Process Scheduling",
                subject = "Operating Systems",
                score = 3,
                totalQuestions = 5,
                percentage = 60,
                timestamp = now - (oneDay * 7),
                timeSpentSeconds = 160
            ),
            QuizAttemptEntity(
                id = "quiz_att_3",
                quizTitle = "Discrete Math Propositional Logic",
                subject = "Discrete Mathematics",
                score = 4,
                totalQuestions = 5,
                percentage = 80,
                timestamp = now - (oneDay * 5),
                timeSpentSeconds = 130
            ),
            QuizAttemptEntity(
                id = "quiz_att_4",
                quizTitle = "DPA 1NF to BCNF Normalization",
                subject = "DPA",
                score = 4,
                totalQuestions = 5,
                percentage = 80,
                timestamp = now - (oneDay * 4),
                timeSpentSeconds = 110
            ),
            QuizAttemptEntity(
                id = "quiz_att_5",
                quizTitle = "OS Deadlock Banker's Algorithm",
                subject = "Operating Systems",
                score = 4,
                totalQuestions = 5,
                percentage = 80,
                timestamp = now - (oneDay * 3),
                timeSpentSeconds = 125
            ),
            QuizAttemptEntity(
                id = "quiz_att_6",
                quizTitle = "C & Java Pointer & Data Structures",
                subject = "Programming",
                score = 5,
                totalQuestions = 5,
                percentage = 100,
                timestamp = now - (oneDay * 2),
                timeSpentSeconds = 95
            ),
            QuizAttemptEntity(
                id = "quiz_att_7",
                quizTitle = "DPA ACID Transactions & Concurrency",
                subject = "DPA",
                score = 5,
                totalQuestions = 5,
                percentage = 100,
                timestamp = now - (oneDay * 1),
                timeSpentSeconds = 105
            ),
            QuizAttemptEntity(
                id = "quiz_att_8",
                quizTitle = "Discrete Math Graph Theory & Trees",
                subject = "Discrete Mathematics",
                score = 5,
                totalQuestions = 5,
                percentage = 100,
                timestamp = now - (3600000L * 3),
                timeSpentSeconds = 90
            )
        )
        db.quizAttemptDao().insertAttempts(initialAttempts)
    }

    // Topic Progress Tracking
    fun getAllTopicProgress(): Flow<List<TopicProgressEntity>> =
        db.topicProgressDao().getAllTopics()

    fun getCompletedTopicCount(): Flow<Int> =
        db.topicProgressDao().getCompletedTopicCount()

    fun getTotalTopicCount(): Flow<Int> =
        db.topicProgressDao().getTotalTopicCount()

    suspend fun toggleTopicCompletion(id: String, completed: Boolean) {
        val timestamp = if (completed) System.currentTimeMillis() else null
        db.topicProgressDao().updateCompletion(id, completed, timestamp)
    }

    suspend fun initDefaultTopics() {
        val now = System.currentTimeMillis()
        val defaultTopics = listOf(
            // Semester 1 (Completed)
            TopicProgressEntity("top_c_1", "C Programming & Problem Solving", "Pointers & Dynamic Memory", isCompleted = true, completedTimestamp = now - 86400000L * 180, masteryPercentage = 95),
            TopicProgressEntity("top_c_2", "C Programming & Problem Solving", "Structures & Unions", isCompleted = true, completedTimestamp = now - 86400000L * 175, masteryPercentage = 90),
            TopicProgressEntity("top_c_3", "C Programming & Problem Solving", "File I/O & Streams", isCompleted = true, completedTimestamp = now - 86400000L * 170, masteryPercentage = 90),
            TopicProgressEntity("top_c_4", "C Programming & Problem Solving", "Recursion & Functions", isCompleted = true, completedTimestamp = now - 86400000L * 165, masteryPercentage = 95),
            TopicProgressEntity("top_c_5", "C Programming & Problem Solving", "Bitwise Operations", isCompleted = true, completedTimestamp = now - 86400000L * 160, masteryPercentage = 85),

            TopicProgressEntity("top_m_1", "Discrete Mathematics", "Set Theory & Relations", isCompleted = true, completedTimestamp = now - 86400000L * 180, masteryPercentage = 90),
            TopicProgressEntity("top_m_2", "Discrete Mathematics", "Propositional Logic & Truth Tables", isCompleted = true, completedTimestamp = now - 86400000L * 170, masteryPercentage = 95),
            TopicProgressEntity("top_m_3", "Discrete Mathematics", "Boolean Algebra & Logic Gates", isCompleted = true, completedTimestamp = now - 86400000L * 160, masteryPercentage = 85),
            TopicProgressEntity("top_m_4", "Discrete Mathematics", "Graph Theory & Trees", isCompleted = true, completedTimestamp = now - 86400000L * 150, masteryPercentage = 80),
            TopicProgressEntity("top_m_5", "Discrete Mathematics", "Recurrence Relations & Counting", isCompleted = true, completedTimestamp = now - 86400000L * 140, masteryPercentage = 85),

            // Semester 2 (Completed)
            TopicProgressEntity("top_dsa_1", "Data Structures & Algorithms", "Linked Lists & Stacks", isCompleted = true, completedTimestamp = now - 86400000L * 100, masteryPercentage = 95),
            TopicProgressEntity("top_dsa_2", "Data Structures & Algorithms", "Queues & Circular Queues", isCompleted = true, completedTimestamp = now - 86400000L * 95, masteryPercentage = 90),
            TopicProgressEntity("top_dsa_3", "Data Structures & Algorithms", "Binary Trees & BST", isCompleted = true, completedTimestamp = now - 86400000L * 85, masteryPercentage = 85),
            TopicProgressEntity("top_dsa_4", "Data Structures & Algorithms", "AVL Trees & Heaps", isCompleted = true, completedTimestamp = now - 86400000L * 75, masteryPercentage = 80),
            TopicProgressEntity("top_dsa_5", "Data Structures & Algorithms", "Sorting & Graph Traversals", isCompleted = true, completedTimestamp = now - 86400000L * 65, masteryPercentage = 90),

            TopicProgressEntity("top_coa_1", "Computer Organization & Architecture", "Logic Gates & Karnaugh Maps", isCompleted = true, completedTimestamp = now - 86400000L * 90, masteryPercentage = 90),
            TopicProgressEntity("top_coa_2", "Computer Organization & Architecture", "ALU & Register Transfer", isCompleted = true, completedTimestamp = now - 86400000L * 80, masteryPercentage = 85),
            TopicProgressEntity("top_coa_3", "Computer Organization & Architecture", "Instruction Cycle & Pipelining", isCompleted = true, completedTimestamp = now - 86400000L * 70, masteryPercentage = 80),
            TopicProgressEntity("top_coa_4", "Computer Organization & Architecture", "Memory Hierarchy & Cache", isCompleted = true, completedTimestamp = now - 86400000L * 60, masteryPercentage = 85),
            TopicProgressEntity("top_coa_5", "Computer Organization & Architecture", "I/O Organization & Interrupts", isCompleted = true, completedTimestamp = now - 86400000L * 50, masteryPercentage = 80),

            // Semester 3 (Current - Active Progress)
            TopicProgressEntity("top_dpa_1", "DPA", "ER Diagrams & Relational Model", isCompleted = true, completedTimestamp = now - 86400000L * 8, masteryPercentage = 95),
            TopicProgressEntity("top_dpa_2", "DPA", "Normalization (1NF to BCNF)", isCompleted = true, completedTimestamp = now - 86400000L * 5, masteryPercentage = 90),
            TopicProgressEntity("top_dpa_3", "DPA", "SQL Joins & Subqueries", isCompleted = true, completedTimestamp = now - 86400000L * 3, masteryPercentage = 85),
            TopicProgressEntity("top_dpa_4", "DPA", "Transactions & ACID Properties", isCompleted = true, completedTimestamp = now - 86400000L * 1, masteryPercentage = 90),
            TopicProgressEntity("top_dpa_5", "DPA", "Concurrency & Indexing", isCompleted = false, completedTimestamp = null, masteryPercentage = 40),

            TopicProgressEntity("top_os_1", "Operating Systems", "Process Scheduling (FCFS, SJF, RR)", isCompleted = true, completedTimestamp = now - 86400000L * 7, masteryPercentage = 85),
            TopicProgressEntity("top_os_2", "Operating Systems", "Deadlocks & Banker's Algorithm", isCompleted = true, completedTimestamp = now - 86400000L * 4, masteryPercentage = 90),
            TopicProgressEntity("top_os_3", "Operating Systems", "Virtual Memory & Paging", isCompleted = true, completedTimestamp = now - 86400000L * 2, masteryPercentage = 80),
            TopicProgressEntity("top_os_4", "Operating Systems", "Page Replacement (FIFO, LRU)", isCompleted = false, completedTimestamp = null, masteryPercentage = 50),
            TopicProgressEntity("top_os_5", "Operating Systems", "File Systems & Disk Scheduling", isCompleted = false, completedTimestamp = null, masteryPercentage = 30),

            TopicProgressEntity("top_java_1", "Java Programming & OOP", "OOP Principles (Inheritance, Polymorphism)", isCompleted = true, completedTimestamp = now - 86400000L * 6, masteryPercentage = 90),
            TopicProgressEntity("top_java_2", "Java Programming & OOP", "Exception Handling & Multithreading", isCompleted = true, completedTimestamp = now - 86400000L * 3, masteryPercentage = 85),
            TopicProgressEntity("top_java_3", "Java Programming & OOP", "Java Collections Framework", isCompleted = true, completedTimestamp = now - 86400000L * 1, masteryPercentage = 80),
            TopicProgressEntity("top_java_4", "Java Programming & OOP", "JDBC & Database Connectivity", isCompleted = false, completedTimestamp = null, masteryPercentage = 45),
            TopicProgressEntity("top_java_5", "Java Programming & OOP", "Streams & Lambda Expressions", isCompleted = false, completedTimestamp = null, masteryPercentage = 30),

            // Semester 4 (Upcoming)
            TopicProgressEntity("top_net_1", "Computer Networks", "OSI 7 Layers vs TCP/IP", isCompleted = false, completedTimestamp = null, masteryPercentage = 0),
            TopicProgressEntity("top_net_2", "Computer Networks", "IP Addressing & Subnetting", isCompleted = false, completedTimestamp = null, masteryPercentage = 0),
            TopicProgressEntity("top_net_3", "Computer Networks", "Routing Protocols (Distance Vector, Link State)", isCompleted = false, completedTimestamp = null, masteryPercentage = 0),
            TopicProgressEntity("top_net_4", "Computer Networks", "TCP vs UDP & Flow Control", isCompleted = false, completedTimestamp = null, masteryPercentage = 0),
            TopicProgressEntity("top_net_5", "Computer Networks", "DNS, HTTP, SSL/TLS", isCompleted = false, completedTimestamp = null, masteryPercentage = 0),

            TopicProgressEntity("top_web_1", "Web Technologies & JavaScript", "DOM Manipulation & ES6", isCompleted = false, completedTimestamp = null, masteryPercentage = 0),
            TopicProgressEntity("top_web_2", "Web Technologies & JavaScript", "Async/Await & Promises", isCompleted = false, completedTimestamp = null, masteryPercentage = 0),
            TopicProgressEntity("top_web_3", "Web Technologies & JavaScript", "REST APIs & JSON", isCompleted = false, completedTimestamp = null, masteryPercentage = 0),
            TopicProgressEntity("top_web_4", "Web Technologies & JavaScript", "Node.js & Express Basics", isCompleted = false, completedTimestamp = null, masteryPercentage = 0),
            TopicProgressEntity("top_web_5", "Web Technologies & JavaScript", "Full-Stack Architecture", isCompleted = false, completedTimestamp = null, masteryPercentage = 0),

            // Semester 5 (Upcoming)
            TopicProgressEntity("top_ai_1", "Artificial Intelligence & Data Science", "Search Algorithms (A*, BFS, DFS)", isCompleted = false, completedTimestamp = null, masteryPercentage = 0),
            TopicProgressEntity("top_ai_2", "Artificial Intelligence & Data Science", "Machine Learning Basics", isCompleted = false, completedTimestamp = null, masteryPercentage = 0),
            TopicProgressEntity("top_ai_3", "Artificial Intelligence & Data Science", "Neural Networks & Backpropagation", isCompleted = false, completedTimestamp = null, masteryPercentage = 0),
            TopicProgressEntity("top_ai_4", "Artificial Intelligence & Data Science", "Python for Data Analysis (Pandas/NumPy)", isCompleted = false, completedTimestamp = null, masteryPercentage = 0),
            TopicProgressEntity("top_ai_5", "Artificial Intelligence & Data Science", "NLP & LLM Concepts", isCompleted = false, completedTimestamp = null, masteryPercentage = 0),

            TopicProgressEntity("top_se_1", "Software Engineering & Agile", "SDLC Models (Waterfall, Spiral, Agile Scrum)", isCompleted = false, completedTimestamp = null, masteryPercentage = 0),
            TopicProgressEntity("top_se_2", "Software Engineering & Agile", "SRS Documentation", isCompleted = false, completedTimestamp = null, masteryPercentage = 0),
            TopicProgressEntity("top_se_3", "Software Engineering & Agile", "Testing (Black Box vs White Box)", isCompleted = false, completedTimestamp = null, masteryPercentage = 0),
            TopicProgressEntity("top_se_4", "Software Engineering & Agile", "UML Class & Sequence Diagrams", isCompleted = false, completedTimestamp = null, masteryPercentage = 0),
            TopicProgressEntity("top_se_5", "Software Engineering & Agile", "CI/CD & DevOps Basics", isCompleted = false, completedTimestamp = null, masteryPercentage = 0),

            // Semester 6 (Upcoming)
            TopicProgressEntity("top_sec_1", "Cyber Security & Cryptography", "Symmetric vs Asymmetric Encryption", isCompleted = false, completedTimestamp = null, masteryPercentage = 0),
            TopicProgressEntity("top_sec_2", "Cyber Security & Cryptography", "Digital Signatures & PKI", isCompleted = false, completedTimestamp = null, masteryPercentage = 0),
            TopicProgressEntity("top_sec_3", "Cyber Security & Cryptography", "Network Attacks & Firewalls", isCompleted = false, completedTimestamp = null, masteryPercentage = 0),
            TopicProgressEntity("top_sec_4", "Cyber Security & Cryptography", "SQL Injection & XSS Mitigation", isCompleted = false, completedTimestamp = null, masteryPercentage = 0),
            TopicProgressEntity("top_sec_5", "Cyber Security & Cryptography", "Cyber Laws & IT Act India", isCompleted = false, completedTimestamp = null, masteryPercentage = 0),

            TopicProgressEntity("top_cld_1", "Cloud Computing & DevOps", "IaaS, PaaS, SaaS Architectures", isCompleted = false, completedTimestamp = null, masteryPercentage = 0),
            TopicProgressEntity("top_cld_2", "Cloud Computing & DevOps", "Virtualization & Containers (Docker)", isCompleted = false, completedTimestamp = null, masteryPercentage = 0),
            TopicProgressEntity("top_cld_3", "Cloud Computing & DevOps", "Kubernetes Orchestration", isCompleted = false, completedTimestamp = null, masteryPercentage = 0),
            TopicProgressEntity("top_cld_4", "Cloud Computing & DevOps", "Serverless Computing", isCompleted = false, completedTimestamp = null, masteryPercentage = 0),
            TopicProgressEntity("top_cld_5", "Cloud Computing & DevOps", "Cloud Security & Multi-tenancy", isCompleted = false, completedTimestamp = null, masteryPercentage = 0)
        )
        db.topicProgressDao().insertTopics(defaultTopics)
    }
}

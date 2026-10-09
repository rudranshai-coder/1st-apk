package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.MindNestDatabase
import com.example.data.local.UserPreferencesDataStore
import com.example.data.model.ActiveQuizSession
import com.example.data.model.AttachmentInfo
import com.example.data.model.AttachmentType
import com.example.data.model.BcaOverallProgress
import com.example.data.model.BcaSubject
import com.example.data.model.ChatMessage
import com.example.data.model.DayStudyBarData
import com.example.data.model.DocumentEntity
import com.example.data.model.ExamPaper
import com.example.data.model.FlashcardEntity
import com.example.data.model.McqQuestion
import com.example.data.model.MessageRole
import com.example.data.model.QuizAttemptEntity
import com.example.data.model.QuizTrendPoint
import com.example.data.model.SavedItemEntity
import com.example.data.model.SemesterRoadmapProgress
import com.example.data.model.SemesterStatus
import com.example.data.model.StudySessionEntity
import com.example.data.model.SubjectRoadmapProgress
import com.example.data.model.SubjectStudyStat
import com.example.data.model.SubjectTopicProgress
import com.example.data.model.TopicProgressEntity
import com.example.data.model.UserSession
import com.example.data.remote.BcaKnowledgeBase
import com.example.data.repository.MindNestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

enum class Screen {
    AUTH,
    DASHBOARD,
    CHAT,
    DOCUMENTS,
    STUDY_TOOLS,
    EXAM_PREP,
    HISTORY,
    SAVED,
    PROFILE,
    SETTINGS,
    ROADMAP,
    QUIZ
}

class MindNestViewModel(application: Application) : AndroidViewModel(application) {

    private val db = MindNestDatabase.getDatabase(application)
    private val repository = MindNestRepository(db)

    // Navigation state
    private val _currentScreen = MutableStateFlow(Screen.DASHBOARD)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _screenBackStack = mutableListOf(Screen.DASHBOARD)

    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            _screenBackStack.add(screen)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (_screenBackStack.size > 1) {
            _screenBackStack.removeAt(_screenBackStack.lastIndex)
            _currentScreen.value = _screenBackStack.last()
            return true
        }
        return false
    }

    // User session with persistent SharedPreferences and Jetpack DataStore storage
    private val prefs = application.getSharedPreferences("mindnest_user_prefs", android.content.Context.MODE_PRIVATE)
    private val userPreferencesDataStore = UserPreferencesDataStore(application)

    private val _userSession = MutableStateFlow(loadPersistedUserSession())
    val userSession: StateFlow<UserSession> = _userSession.asStateFlow()

    private fun loadPersistedUserSession(): UserSession {
        val savedName = prefs.getString("user_name", null)
        val savedEmail = prefs.getString("user_email", null)
        val savedSem = prefs.getInt("user_semester", 3)
        val savedUni = prefs.getString("user_university", "BCA Program") ?: "BCA Program"
        val savedIsGuest = prefs.getBoolean("user_is_guest", false)
        val savedStreak = prefs.getInt("user_streak", 14)
        val savedSolved = prefs.getInt("user_questions_solved", 142)

        return UserSession(
            name = savedName ?: "Rudransh Goyal",
            email = savedEmail ?: "rudranshgoyal44@gmail.com",
            semester = savedSem,
            university = savedUni,
            isGuest = savedIsGuest,
            studyStreakDays = savedStreak,
            questionsSolved = savedSolved
        )
    }

    private fun persistUserSession(session: UserSession) {
        prefs.edit()
            .putString("user_name", session.name)
            .putString("user_email", session.email)
            .putInt("user_semester", session.semester)
            .putString("user_university", session.university)
            .putBoolean("user_is_guest", session.isGuest)
            .putInt("user_streak", session.studyStreakDays)
            .putInt("user_questions_solved", session.questionsSolved)
            .apply()

        viewModelScope.launch {
            userPreferencesDataStore.saveUserSession(session)
        }
    }

    fun updateSemester(sem: Int) {
        val updated = _userSession.value.copy(semester = sem)
        _userSession.value = updated
        persistUserSession(updated)
    }

    fun updateUniversity(uni: String) {
        val updated = _userSession.value.copy(university = uni)
        _userSession.value = updated
        persistUserSession(updated)
    }

    fun updateProfile(name: String, email: String, university: String, semester: Int) {
        val updated = _userSession.value.copy(
            name = name,
            email = email,
            university = university,
            semester = semester,
            isGuest = false
        )
        _userSession.value = updated
        persistUserSession(updated)
    }

    fun continueAsGuest() {
        val updated = _userSession.value.copy(
            name = "Guest Student",
            email = "guest@mindnest.app",
            isGuest = true
        )
        _userSession.value = updated
        persistUserSession(updated)
        navigateTo(Screen.DASHBOARD)
    }

    // Chat messages stream from Room
    val chatMessages: StateFlow<List<ChatMessage>> = repository.getChatMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Pending attachments for the current message
    private val _pendingAttachments = MutableStateFlow<List<AttachmentInfo>>(emptyList())
    val pendingAttachments: StateFlow<List<AttachmentInfo>> = _pendingAttachments.asStateFlow()

    // Thinking state
    private val _isThinking = MutableStateFlow(false)
    val isThinking: StateFlow<Boolean> = _isThinking.asStateFlow()

    // Selected Subject filter for chat/solver
    private val _selectedSubject = MutableStateFlow<BcaSubject?>(null)
    val selectedSubject: StateFlow<BcaSubject?> = _selectedSubject.asStateFlow()

    fun selectSubject(subject: BcaSubject?) {
        _selectedSubject.value = subject
    }

    fun selectSubjectByName(name: String?) {
        if (name.isNullOrBlank()) {
            _selectedSubject.value = null
        } else {
            val found = com.example.data.remote.BcaKnowledgeBase.subjects.find { 
                it.name.contains(name, ignoreCase = true) || name.contains(it.name, ignoreCase = true) || it.code.contains(name, ignoreCase = true)
            }
            _selectedSubject.value = found ?: BcaSubject(
                id = name.lowercase().replace(" ", "_"),
                name = name,
                code = "BCA",
                semester = 1,
                iconName = "school",
                topics = emptyList()
            )
        }
    }

    fun addAttachment(att: AttachmentInfo) {
        _pendingAttachments.value = _pendingAttachments.value + att
    }

    fun removeAttachment(attId: String) {
        _pendingAttachments.value = _pendingAttachments.value.filter { it.id != attId }
    }

    fun clearAttachments() {
        _pendingAttachments.value = emptyList()
    }

    // Send chat query to Nesty (Gemini AI)
    fun sendMessage(promptText: String) {
        if (promptText.isBlank() && _pendingAttachments.value.isEmpty()) return

        val attachmentsToSend = _pendingAttachments.value
        clearAttachments()

        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            role = MessageRole.USER,
            text = promptText,
            timestamp = System.currentTimeMillis(),
            subjectTag = _selectedSubject.value?.name,
            attachments = attachmentsToSend
        )

        viewModelScope.launch {
            repository.saveMessage(userMsg)
            _isThinking.value = true

            val responseResult = repository.askNesty(
                prompt = promptText,
                history = chatMessages.value,
                attachments = attachmentsToSend,
                subjectContext = _selectedSubject.value?.let { "${it.code} - ${it.name}" }
            )

            val replyText = responseResult.getOrElse { "Sorry, Nesty encountered an issue answering this question. Please try again." }

            val assistantMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                role = MessageRole.ASSISTANT,
                text = replyText,
                timestamp = System.currentTimeMillis(),
                subjectTag = _selectedSubject.value?.name
            )

            repository.saveMessage(assistantMsg)
            _isThinking.value = false

            // Increase questions solved stat
            _userSession.value = _userSession.value.copy(
                questionsSolved = _userSession.value.questionsSolved + 1
            )
        }
    }

    fun toggleMessageBookmark(msg: ChatMessage) {
        viewModelScope.launch {
            repository.toggleBookmark(msg.id, !msg.isBookmarked)
            if (!msg.isBookmarked) {
                repository.saveItem(
                    SavedItemEntity(
                        id = UUID.randomUUID().toString(),
                        title = "Nesty Explanation: ${msg.text.take(45)}...",
                        type = "Note",
                        subject = msg.subjectTag ?: "BCA General",
                        content = msg.text
                    )
                )
            }
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            repository.clearChat()
        }
    }

    // Documents
    val documents: StateFlow<List<DocumentEntity>> = repository.getAllDocuments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addDocument(title: String, subject: String, fileType: String, sizeText: String, summary: String, content: String) {
        viewModelScope.launch {
            val doc = DocumentEntity(
                id = UUID.randomUUID().toString(),
                title = title,
                subject = subject,
                fileType = fileType,
                sizeText = sizeText,
                summary = summary,
                contentText = content
            )
            repository.addDocument(doc)
        }
    }

    fun deleteDocument(id: String) {
        viewModelScope.launch {
            repository.deleteDocument(id)
        }
    }

    fun analyzeDocumentWithNesty(doc: DocumentEntity) {
        addAttachment(
            AttachmentInfo(
                fileName = doc.title,
                fileType = when (doc.fileType.uppercase()) {
                    "PDF" -> AttachmentType.PDF
                    "CODE" -> AttachmentType.CODE
                    "IMAGE" -> AttachmentType.IMAGE
                    "NOTES" -> AttachmentType.NOTES
                    else -> AttachmentType.DOC
                },
                sizeText = doc.sizeText,
                contentSnippet = doc.contentText ?: doc.summary
            )
        )
        navigateTo(Screen.CHAT)
    }

    // Saved Items
    val savedItems: StateFlow<List<SavedItemEntity>> = repository.getAllSavedItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveStudySnippet(title: String, type: String, subject: String, content: String, codeLang: String? = null) {
        viewModelScope.launch {
            repository.saveItem(
                SavedItemEntity(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    type = type,
                    subject = subject,
                    content = content,
                    codeLanguage = codeLang
                )
            )
        }
    }

    fun deleteSavedItem(id: String) {
        viewModelScope.launch {
            repository.deleteSavedItem(id)
        }
    }

    // Flashcards
    val flashcards: StateFlow<List<FlashcardEntity>> = repository.getFlashcards(_userSession.value.semester)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleMastered(card: FlashcardEntity) {
        viewModelScope.launch {
            repository.updateFlashcard(card.copy(isMastered = !card.isMastered))
        }
    }

    // Knowledge Base Queries
    val subjects: List<BcaSubject> = repository.getSubjects()
    val mcqQuestions: List<McqQuestion> = repository.getSampleMcqs()
    val pastYearPapers: List<ExamPaper> = repository.getPastYearPapers()

    // Gemini AI Quiz Generation & Active Session State
    private val _activeQuizSession = MutableStateFlow<ActiveQuizSession?>(null)
    val activeQuizSession: StateFlow<ActiveQuizSession?> = _activeQuizSession.asStateFlow()

    private val _isQuizGenerating = MutableStateFlow(false)
    val isQuizGenerating: StateFlow<Boolean> = _isQuizGenerating.asStateFlow()

    private val _quizGenerationError = MutableStateFlow<String?>(null)
    val quizGenerationError: StateFlow<String?> = _quizGenerationError.asStateFlow()

    private val _selectedQuizSubject = MutableStateFlow("DPA")
    val selectedQuizSubject: StateFlow<String> = _selectedQuizSubject.asStateFlow()

    fun setSelectedQuizSubject(subject: String) {
        _selectedQuizSubject.value = subject
    }

    fun startGeneratingQuiz(
        subject: String = _selectedQuizSubject.value,
        topic: String? = null,
        difficulty: String = "University Exam Standard",
        questionCount: Int = 5
    ) {
        _selectedQuizSubject.value = subject
        _isQuizGenerating.value = true
        _quizGenerationError.value = null

        viewModelScope.launch {
            try {
                val result = repository.generateQuizWithGemini(subject, topic, difficulty, questionCount)
                val generatedQuestions = result.getOrNull() ?: emptyList()

                if (generatedQuestions.isNotEmpty()) {
                    _activeQuizSession.value = ActiveQuizSession(
                        subject = subject,
                        topic = topic,
                        difficulty = difficulty,
                        questions = generatedQuestions,
                        totalQuestions = generatedQuestions.size,
                        currentQuestionIndex = 0
                    )
                } else {
                    _quizGenerationError.value = "Unable to generate questions. Please try again."
                }
            } catch (e: Exception) {
                _quizGenerationError.value = e.localizedMessage ?: "Quiz generation failed"
            } finally {
                _isQuizGenerating.value = false
            }
        }
    }

    fun selectQuizOption(questionIndex: Int, optionIndex: Int) {
        val session = _activeQuizSession.value ?: return
        val currentAnswers = session.userAnswers.toMutableMap()
        currentAnswers[questionIndex] = optionIndex
        _activeQuizSession.value = session.copy(userAnswers = currentAnswers)
    }

    fun checkCurrentQuestionAnswer(questionIndex: Int) {
        val session = _activeQuizSession.value ?: return
        val submitted = session.submittedQuestions.toMutableSet()
        submitted.add(questionIndex)
        _activeQuizSession.value = session.copy(submittedQuestions = submitted)
    }

    fun nextQuizQuestion() {
        val session = _activeQuizSession.value ?: return
        if (session.currentQuestionIndex < session.questions.lastIndex) {
            _activeQuizSession.value = session.copy(currentQuestionIndex = session.currentQuestionIndex + 1)
        }
    }

    fun previousQuizQuestion() {
        val session = _activeQuizSession.value ?: return
        if (session.currentQuestionIndex > 0) {
            _activeQuizSession.value = session.copy(currentQuestionIndex = session.currentQuestionIndex - 1)
        }
    }

    fun finishQuiz(timeSpentSec: Int = 120) {
        val session = _activeQuizSession.value ?: return
        val total = session.questions.size
        var correctCount = 0
        val weakTopics = mutableListOf<String>()

        session.questions.forEachIndexed { idx, q ->
            val userPick = session.userAnswers[idx]
            if (userPick == q.correctIndex) {
                correctCount++
            } else {
                weakTopics.add(q.subject)
            }
        }

        val pct = if (total > 0) ((correctCount.toDouble() / total) * 100).toInt() else 0
        val (verdict, feedback) = when {
            pct >= 90 -> "🌟 University Exam Ready (Distinction Level)" to "Outstanding mastery! You have exceptional recall and analytical precision for ${session.subject}."
            pct >= 70 -> "✅ Well Prepared (First Class Standing)" to "Solid grasp of core exam concepts. Quick revision of edge cases will push you to 90%+."
            pct >= 50 -> "⚠️ Revision Recommended" to "Moderate readiness. Focus on key definitions, algorithms, and 5-mark theorem proofs."
            else -> "🚨 High Priority Revision Needed" to "Critical gaps identified in ${session.subject}. We recommend reviewing lecture notes and asking Nesty AI for step-by-step breakdowns."
        }

        val finishedSession = session.copy(
            isFinished = true,
            score = correctCount,
            totalQuestions = total,
            percentage = pct,
            timeElapsedSeconds = timeSpentSec,
            readinessVerdict = verdict,
            feedbackMessage = feedback,
            weakTopics = weakTopics.distinct()
        )
        _activeQuizSession.value = finishedSession

        // Record attempt in Room DB so it automatically updates Recharts and Roadmap
        recordQuizAttempt(
            title = "${session.subject} Exam Readiness Quiz",
            subject = session.subject,
            score = correctCount,
            total = total,
            timeSpentSec = timeSpentSec
        )
    }

    fun resetQuizSession() {
        _activeQuizSession.value = null
        _quizGenerationError.value = null
    }

    // API Key config
    private val _customApiKey = MutableStateFlow("")
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    fun setCustomApiKey(key: String) {
        _customApiKey.value = key
        repository.updateCustomApiKey(key)
        viewModelScope.launch {
            userPreferencesDataStore.saveCustomApiKey(key)
        }
    }

    // Study Sessions & Hours Tracking
    val studySessions: StateFlow<List<StudySessionEntity>> = repository.getAllStudySessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subjectStudyStats: StateFlow<List<SubjectStudyStat>> = repository.getSubjectStudyStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalStudyMinutes: StateFlow<Int> = repository.getTotalStudyMinutes()
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun logStudySession(
        subject: String,
        topic: String,
        durationMinutes: Int,
        notes: String = "",
        rating: Int = 5
    ) {
        viewModelScope.launch {
            val session = StudySessionEntity(
                subject = subject,
                topic = topic,
                durationMinutes = durationMinutes,
                notes = notes,
                focusRating = rating
            )
            repository.saveStudySession(session)
        }
    }

    fun deleteStudySession(id: String) {
        viewModelScope.launch {
            repository.deleteStudySession(id)
        }
    }

    // Quiz Performance Tracking
    val quizAttempts: StateFlow<List<QuizAttemptEntity>> = repository.getAllQuizAttemptsAsc()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentQuizAttempts: StateFlow<List<QuizAttemptEntity>> = repository.getRecentQuizAttempts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalQuizCount: StateFlow<Int> = repository.getTotalQuizCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val averageQuizPercentage: StateFlow<Int> = repository.getAverageQuizPercentage()
        .map { it?.toInt() ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun recordQuizAttempt(
        title: String,
        subject: String,
        score: Int,
        total: Int,
        timeSpentSec: Int = 120
    ) {
        viewModelScope.launch {
            val percentage = if (total > 0) ((score.toDouble() / total) * 100).toInt() else 0
            val attempt = QuizAttemptEntity(
                quizTitle = title,
                subject = subject,
                score = score,
                totalQuestions = total,
                percentage = percentage,
                timestamp = System.currentTimeMillis(),
                timeSpentSeconds = timeSpentSec
            )
            repository.saveQuizAttempt(attempt)
        }
    }

    // Recharts Quiz Performance Trend (Line / Area Curve)
    val quizPerformanceTrend: StateFlow<List<QuizTrendPoint>> = repository.getAllQuizAttemptsAsc()
        .map { attempts ->
            val dateFormat = SimpleDateFormat("MMM d", Locale.US)
            val fullDateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)
            attempts.map { att ->
                QuizTrendPoint(
                    id = att.id,
                    dateLabel = dateFormat.format(att.timestamp),
                    fullDate = fullDateFormat.format(att.timestamp),
                    timestamp = att.timestamp,
                    quizTitle = att.quizTitle,
                    subject = att.subject,
                    score = att.score,
                    totalQuestions = att.totalQuestions,
                    percentage = att.percentage
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Topic Progress Tracking
    val allTopicProgress: StateFlow<List<TopicProgressEntity>> = repository.getAllTopicProgress()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedTopicCount: StateFlow<Int> = repository.getCompletedTopicCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalTopicCount: StateFlow<Int> = repository.getTotalTopicCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val subjectTopicProgressList: StateFlow<List<SubjectTopicProgress>> = repository.getAllTopicProgress()
        .map { allTopics ->
            val subjects = listOf("DPA", "Operating Systems", "Discrete Mathematics", "Programming")
            subjects.map { subjName ->
                val topicsForSubj = allTopics.filter { it.subject.contains(subjName, ignoreCase = true) || subjName.contains(it.subject, ignoreCase = true) }
                val completed = topicsForSubj.count { it.isCompleted }
                val total = topicsForSubj.size.coerceAtLeast(1)
                val pct = ((completed.toDouble() / total) * 100).toInt()
                SubjectTopicProgress(
                    subject = subjName,
                    completedCount = completed,
                    totalCount = topicsForSubj.size,
                    percentage = pct,
                    topics = topicsForSubj
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleTopicCompletion(topicId: String, currentCompleted: Boolean) {
        viewModelScope.launch {
            repository.toggleTopicCompletion(topicId, !currentCompleted)
        }
    }

    fun markSubjectCompleted(subjectName: String, markCompleted: Boolean) {
        viewModelScope.launch {
            // Find all topics in repository for this subject and update them
            val allTopics = repository.getAllTopicProgress()
            // We can retrieve from database and toggle
        }
    }

    // Semester-wise BCA Roadmap & Progression Plan
    val semesterRoadmapList: StateFlow<List<SemesterRoadmapProgress>> = combine(
        repository.getAllTopicProgress(),
        _userSession
    ) { allTopics, session ->
        val currentSem = session.semester
        val semestersData = listOf(
            Triple(
                1,
                "Foundation & Algorithmic Thinking",
                "Procedural Programming & Discrete Mathematics"
            ) to listOf("Procedural Programming in C", "Dynamic Memory & Pointers", "Set Theory & Relations", "Propositional Logic & Proofs"),
            Triple(
                2,
                "Core Computing & Architecture",
                "Data Structures, Algorithms & Hardware Organization"
            ) to listOf("Linear & Tree Data Structures", "Algorithm Complexity (Big-O)", "Digital Logic & CPU Registers", "Memory Hierarchy & Cache"),
            Triple(
                3,
                "Data Systems & Enterprise Engineering",
                "Database Processing (DPA), Operating Systems & Java OOP"
            ) to listOf("Relational Modeling & Normalization (1NF to BCNF)", "Complex SQL & ACID Transactions", "Process Scheduling & Deadlocks", "Java Multithreading & OOP"),
            Triple(
                4,
                "Full Stack & Distributed Networking",
                "Computer Networks & Modern Web Development"
            ) to listOf("OSI 7 Layers & TCP/IP Protocol Stack", "IP Addressing & Subnetting", "Modern JavaScript & Async/Await", "REST APIs & Full-Stack Architecture"),
            Triple(
                5,
                "Intelligent Systems & Software Engineering",
                "Artificial Intelligence, Data Science & Agile Methodologies"
            ) to listOf("Heuristic Search & Machine Learning", "Python for Data Analysis (Pandas/NumPy)", "Agile Scrum & SDLC Models", "Automated Testing & CI/CD Pipelines"),
            Triple(
                6,
                "Enterprise Cloud & Cyber Defense",
                "Cyber Security, Cloud DevOps & Capstone Project"
            ) to listOf("RSA/AES Cryptography & PKI", "Network Defense & Vulnerability Mitigation", "Containers (Docker) & Kubernetes", "Serverless Computing & Cloud Architecture")
        )

        semestersData.map { (semInfo, skills) ->
            val semNum = semInfo.first
            val semTitle = semInfo.second
            val semSubtitle = semInfo.third

            val subjectsInSem = BcaKnowledgeBase.subjects.filter { it.semester == semNum }
            val subjectProgressList = subjectsInSem.map { bcaSubj ->
                val matchedTopics = allTopics.filter {
                    it.subject.equals(bcaSubj.name, ignoreCase = true) ||
                    (bcaSubj.name == "DPA" && it.subject == "DPA") ||
                    (it.subject.contains(bcaSubj.name, ignoreCase = true)) ||
                    (bcaSubj.name.contains(it.subject, ignoreCase = true))
                }

                val finalTopics = if (matchedTopics.isNotEmpty()) {
                    matchedTopics
                } else {
                    bcaSubj.topics.mapIndexed { idx, tName ->
                        TopicProgressEntity(
                            id = "top_${bcaSubj.id}_$idx",
                            subject = bcaSubj.name,
                            topicName = tName,
                            isCompleted = semNum < currentSem,
                            completedTimestamp = if (semNum < currentSem) System.currentTimeMillis() - 86400000L * 30 else null,
                            masteryPercentage = if (semNum < currentSem) 90 else 0
                        )
                    }
                }

                val completedCount = finalTopics.count { it.isCompleted }
                val totalCount = finalTopics.size.coerceAtLeast(1)
                val pct = ((completedCount.toDouble() / totalCount) * 100).toInt()

                SubjectRoadmapProgress(
                    id = bcaSubj.id,
                    name = bcaSubj.name,
                    code = bcaSubj.code,
                    semester = semNum,
                    iconName = bcaSubj.iconName,
                    isCompleted = completedCount == totalCount,
                    totalTopics = totalCount,
                    completedTopics = completedCount,
                    progressPercentage = pct,
                    topics = finalTopics,
                    coreFocus = bcaSubj.topics.take(2).joinToString(", "),
                    credits = 4
                )
            }

            val totalSemTopics = subjectProgressList.sumOf { it.totalTopics }
            val completedSemTopics = subjectProgressList.sumOf { it.completedTopics }
            val semPercentage = if (totalSemTopics > 0) ((completedSemTopics.toDouble() / totalSemTopics) * 100).toInt() else 0
            val completedSubjects = subjectProgressList.count { it.isCompleted }

            val status = when {
                completedSemTopics == totalSemTopics && totalSemTopics > 0 -> SemesterStatus.COMPLETED
                semNum < currentSem -> SemesterStatus.COMPLETED
                semNum == currentSem -> SemesterStatus.CURRENT
                else -> SemesterStatus.UPCOMING
            }

            SemesterRoadmapProgress(
                semesterNumber = semNum,
                title = semTitle,
                subtitle = semSubtitle,
                status = status,
                totalSubjects = subjectProgressList.size,
                completedSubjects = completedSubjects,
                totalTopics = totalSemTopics,
                completedTopics = completedSemTopics,
                progressPercentage = semPercentage,
                subjects = subjectProgressList,
                careerSkills = skills
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bcaOverallProgress: StateFlow<BcaOverallProgress> = combine(
        semesterRoadmapList,
        _userSession
    ) { roadmaps, session ->
        val totalTopics = roadmaps.sumOf { it.totalTopics }
        val completedTopics = roadmaps.sumOf { it.completedTopics }
        val overallPct = if (totalTopics > 0) ((completedTopics.toDouble() / totalTopics) * 100).toInt() else 0
        val completedSems = roadmaps.count { it.status == SemesterStatus.COMPLETED }
        val allSubjects = roadmaps.flatMap { it.subjects }
        val masteredSubjects = allSubjects.count { it.isCompleted }

        val nextTopic = roadmaps.firstOrNull { it.semesterNumber == session.semester }
            ?.subjects?.flatMap { it.topics }?.firstOrNull { !it.isCompleted }
            ?: roadmaps.flatMap { it.subjects }.flatMap { it.topics }.firstOrNull { !it.isCompleted }

        BcaOverallProgress(
            totalDegreeTopics = totalTopics,
            completedDegreeTopics = completedTopics,
            overallPercentage = overallPct,
            currentSemester = session.semester,
            totalSemesters = 6,
            completedSemestersCount = completedSems,
            subjectsMasteredCount = masteredSubjects,
            totalSubjectsCount = allSubjects.size,
            nextRecommendedSubject = nextTopic?.subject ?: "DPA",
            nextRecommendedTopic = nextTopic?.topicName ?: "Concurrency & Indexing"
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        BcaOverallProgress(
            totalDegreeTopics = 30,
            completedDegreeTopics = 18,
            overallPercentage = 60,
            currentSemester = 3,
            completedSemestersCount = 2,
            subjectsMasteredCount = 6,
            totalSubjectsCount = 12
        )
    )

    // Recharts Daily Study Progress (Last 7 Days Bar Data)
    val dayStudyBarData: StateFlow<List<DayStudyBarData>> = repository.getAllStudySessions()
        .map { sessions ->
            val cal = Calendar.getInstance()
            val dayFormat = SimpleDateFormat("EEE", Locale.US)
            val dateFormat = SimpleDateFormat("MM/dd", Locale.US)
            val result = mutableListOf<DayStudyBarData>()

            // Generate last 7 days ending today
            for (i in 6 downTo 0) {
                val targetCal = Calendar.getInstance()
                targetCal.add(Calendar.DAY_OF_YEAR, -i)
                val dayStart = targetCal.apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                val dayEnd = dayStart + 86400000L

                val daySessions = sessions.filter { it.timestamp in dayStart until dayEnd }
                val totalMins = daySessions.sumOf { it.durationMinutes }
                val dpaMins = daySessions.filter { it.subject.contains("DPA", ignoreCase = true) }.sumOf { it.durationMinutes }
                val osMins = daySessions.filter { it.subject.contains("Operating", ignoreCase = true) || it.subject == "OS" }.sumOf { it.durationMinutes }
                val mathMins = daySessions.filter { it.subject.contains("Math", ignoreCase = true) }.sumOf { it.durationMinutes }
                val progMins = daySessions.filter { it.subject.contains("Prog", ignoreCase = true) || it.subject.contains("Data", ignoreCase = true) }.sumOf { it.durationMinutes }

                result.add(
                    DayStudyBarData(
                        dayLabel = if (i == 0) "Today" else dayFormat.format(dayStart),
                        dateLabel = dateFormat.format(dayStart),
                        totalMinutes = totalMins,
                        dpaMinutes = dpaMins,
                        osMinutes = osMins,
                        mathMinutes = mathMins,
                        programmingMinutes = progMins
                    )
                )
            }
            result
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Collect reactive DataStore preferences to keep session state in sync
        viewModelScope.launch {
            userPreferencesDataStore.userSessionFlow.collect { dataStoreSession ->
                if (dataStoreSession.name.isNotBlank()) {
                    _userSession.value = dataStoreSession
                }
            }
        }
        viewModelScope.launch {
            userPreferencesDataStore.customApiKeyFlow.collect { key ->
                if (key.isNotBlank()) {
                    _customApiKey.value = key
                    repository.updateCustomApiKey(key)
                }
            }
        }

        // Populate initial seed data if needed
        viewModelScope.launch {
            repository.initDefaultFlashcards()
            repository.initDefaultStudySessions()
            repository.initDefaultQuizAttempts()
            repository.initDefaultTopics()
            // Seed a welcome sample document if empty
            val initialDocs = listOf(
                DocumentEntity(
                    id = "doc_dpa_101",
                    title = "DPA Study Cheat Sheet.pdf",
                    subject = "DPA",
                    fileType = "PDF",
                    sizeText = "1.4 MB",
                    summary = "Complete summary of Data Processing & Applications, functional dependency proofs and decomposition properties.",
                    contentText = "Unit 2: Relational Model, Functional Dependencies, Armstrong's Axioms, 1NF to BCNF algorithms with lossy vs lossless join decomposition."
                ),
                DocumentEntity(
                    id = "doc_dsa_trees",
                    title = "Binary Tree & BST Lab Manual.java",
                    subject = "Data Structures",
                    fileType = "CODE",
                    sizeText = "84 KB",
                    summary = "Lab practical code in Java for BST insertion, deletion, and inorder traversal.",
                    contentText = "public class BSTLab { TreeNode root; void insert(int val) { ... } void inorder() { ... } }"
                )
            )
            for (d in initialDocs) {
                repository.addDocument(d)
            }
        }
    }
}

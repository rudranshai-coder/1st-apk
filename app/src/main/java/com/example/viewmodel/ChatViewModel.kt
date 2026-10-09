package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.local.MindNestDatabase
import com.example.data.local.UserPreferencesDataStore
import com.example.data.model.AttachmentInfo
import com.example.data.model.ChatMessage
import com.example.data.model.MessageRole
import com.example.data.model.UserSession
import com.example.data.remote.GeminiService
import com.example.data.repository.MindNestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class BcaQuestionCategory(val label: String, val badge: String) {
    ALL("All", "BCA"),
    PROGRAMMING("Code & Syntax", "💻 Code"),
    THEORY("Theory Concepts", "📖 Theory"),
    MATHS_PROOFS("Maths & Proofs", "📐 Math"),
    EXAM_PREP("Exam PYQs", "🎯 Exam")
}

data class BcaQuickPrompt(
    val title: String,
    val prompt: String,
    val category: BcaQuestionCategory,
    val subject: String
)

/**
 * Dedicated ChatViewModel interfacing directly with the Gemini API
 * to serve as the Nesty AI assistant for BCA students to ask programming
 * and theory questions.
 */
class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val db = MindNestDatabase.getDatabase(application)
    private val repository = MindNestRepository(db)
    private val userPreferencesDataStore = UserPreferencesDataStore(application)

    private var geminiService = GeminiService()

    // Reactive State
    val chatMessages: StateFlow<List<ChatMessage>> = repository.getChatMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _pendingAttachments = MutableStateFlow<List<AttachmentInfo>>(emptyList())
    val pendingAttachments: StateFlow<List<AttachmentInfo>> = _pendingAttachments.asStateFlow()

    private val _isThinking = MutableStateFlow(false)
    val isThinking: StateFlow<Boolean> = _isThinking.asStateFlow()

    private val _selectedSubject = MutableStateFlow<String?>(null)
    val selectedSubject: StateFlow<String?> = _selectedSubject.asStateFlow()

    private val _filterHistoryBySubject = MutableStateFlow(false)
    val filterHistoryBySubject: StateFlow<Boolean> = _filterHistoryBySubject.asStateFlow()

    val displayedMessages: StateFlow<List<ChatMessage>> = combine(
        chatMessages,
        _selectedSubject,
        _filterHistoryBySubject
    ) { messages, subject, filterEnabled ->
        if (filterEnabled && !subject.isNullOrBlank()) {
            val s = subject.trim().lowercase()
            val filtered = messages.filter { msg ->
                val tag = msg.subjectTag?.lowercase()
                val text = msg.text.lowercase()
                (tag != null && (tag == s || tag.contains(s) || s.contains(tag))) ||
                text.contains(s)
            }
            if (filtered.isEmpty()) messages else filtered
        } else {
            messages
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeCategory = MutableStateFlow(BcaQuestionCategory.ALL)
    val activeCategory: StateFlow<BcaQuestionCategory> = _activeCategory.asStateFlow()

    private val _userSession = MutableStateFlow(UserSession())
    val userSession: StateFlow<UserSession> = _userSession.asStateFlow()

    private val _customApiKey = MutableStateFlow("")
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    // Tracks if Gemini API key is available via BuildConfig or DataStore
    val isApiKeyConfigured: StateFlow<Boolean> = combine(_customApiKey, MutableStateFlow(BuildConfig.GEMINI_API_KEY)) { custom, config ->
        custom.isNotBlank() || (config.isNotBlank() && config != "MY_GEMINI_API_KEY")
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    private val allQuickPrompts = listOf(
        // Programming
        BcaQuickPrompt(
            title = "BST Traversals",
            prompt = "Write a complete Java program for Binary Search Tree inorder, preorder, and postorder traversals with time and space complexity explanations.",
            category = BcaQuestionCategory.PROGRAMMING,
            subject = "Data Structures"
        ),
        BcaQuickPrompt(
            title = "SQL Complex Joins",
            prompt = "Explain INNER, LEFT, RIGHT, and FULL OUTER joins in SQL with student and enrollment schema tables and practical query outputs.",
            category = BcaQuestionCategory.PROGRAMMING,
            subject = "DPA"
        ),
        BcaQuickPrompt(
            title = "C Pointers & Dynamic Memory",
            prompt = "Explain pointer arithmetic, malloc(), calloc(), realloc(), and free() in C with diagrammatic heap memory allocations and common segfault errors.",
            category = BcaQuestionCategory.PROGRAMMING,
            subject = "C Programming"
        ),
        BcaQuickPrompt(
            title = "Python OOP & Inheritance",
            prompt = "Demonstrate multiple inheritance, method resolution order (MRO), and super() in Python with an academic grading system example.",
            category = BcaQuestionCategory.PROGRAMMING,
            subject = "Python"
        ),

        // Theory
        BcaQuickPrompt(
            title = "DPA Normalization",
            prompt = "Explain 1NF, 2NF, 3NF, and BCNF step-by-step with functional dependency proofs and a practical student-course-department example table.",
            category = BcaQuestionCategory.THEORY,
            subject = "DPA"
        ),
        BcaQuickPrompt(
            title = "Deadlock & Banker's Algorithm",
            prompt = "What are the 4 necessary conditions for deadlock? Solve a Banker's algorithm resource allocation safety state problem with Available, Allocation, and Max matrices.",
            category = BcaQuestionCategory.THEORY,
            subject = "Operating Systems"
        ),
        BcaQuickPrompt(
            title = "OSI 7-Layer Architecture",
            prompt = "Explain the 7 layers of the ISO-OSI reference model with their protocols, data units, and responsibilities in computer networking.",
            category = BcaQuestionCategory.THEORY,
            subject = "Computer Networks"
        ),
        BcaQuickPrompt(
            title = "ACID Properties & Transactions",
            prompt = "Explain Atomicity, Consistency, Isolation, and Durability in database transactions. How does 2-Phase Locking (2PL) prevent concurrency anomalies?",
            category = BcaQuestionCategory.THEORY,
            subject = "DPA"
        ),

        // Maths & Proofs
        BcaQuickPrompt(
            title = "De Morgan's Laws Proof",
            prompt = "Prove De Morgan's laws for sets and propositional logic using complete step-by-step truth tables and mathematical induction.",
            category = BcaQuestionCategory.MATHS_PROOFS,
            subject = "Discrete Mathematics"
        ),
        BcaQuickPrompt(
            title = "Pigeonhole Principle",
            prompt = "State and prove the Pigeonhole Principle. Solve 3 frequently repeated BCA semester exam problems using the generalized pigeonhole principle.",
            category = BcaQuestionCategory.MATHS_PROOFS,
            subject = "Discrete Mathematics"
        ),

        // Exam Prep
        BcaQuickPrompt(
            title = "DPA 10-Mark Model Answer",
            prompt = "Provide a model 10-mark answer for: 'Discuss Relational Algebra operators (Select, Project, Cartesian Product, Join, Division) with SQL query equivalents.'",
            category = BcaQuestionCategory.EXAM_PREP,
            subject = "DPA"
        ),
        BcaQuickPrompt(
            title = "OS Process Scheduling",
            prompt = "Compare FCFS, SJF (Preemptive vs Non-preemptive), and Round Robin CPU scheduling algorithms with a Gantt chart and average waiting time calculations.",
            category = BcaQuestionCategory.EXAM_PREP,
            subject = "Operating Systems"
        )
    )

    private val _filteredPrompts = MutableStateFlow(allQuickPrompts)
    val quickPrompts: StateFlow<List<BcaQuickPrompt>> = _filteredPrompts.asStateFlow()

    init {
        // Keep session in sync with DataStore
        viewModelScope.launch {
            userPreferencesDataStore.userSessionFlow.collect { dataStoreSession ->
                if (dataStoreSession.name.isNotBlank()) {
                    _userSession.value = dataStoreSession
                }
            }
        }

        // Custom API key sync
        viewModelScope.launch {
            userPreferencesDataStore.customApiKeyFlow.collect { key ->
                if (key.isNotBlank()) {
                    _customApiKey.value = key
                    geminiService = GeminiService(key)
                    repository.updateCustomApiKey(key)
                }
            }
        }
    }

    fun setCategory(category: BcaQuestionCategory) {
        _activeCategory.value = category
        updateFilteredPrompts()
    }

    fun setSubject(subject: String?) {
        _selectedSubject.value = subject
        updateFilteredPrompts()
    }

    fun setFilterHistoryBySubject(enabled: Boolean) {
        _filterHistoryBySubject.value = enabled
    }

    fun toggleFilterHistoryBySubject() {
        _filterHistoryBySubject.value = !_filterHistoryBySubject.value
    }

    private fun updateFilteredPrompts() {
        val cat = _activeCategory.value
        val subj = _selectedSubject.value

        var prompts = if (cat == BcaQuestionCategory.ALL) {
            allQuickPrompts
        } else {
            allQuickPrompts.filter { it.category == cat }
        }

        if (!subj.isNullOrBlank()) {
            val s = subj.trim().lowercase()
            val matched = prompts.filter {
                it.subject.lowercase().contains(s) || s.contains(it.subject.lowercase())
            }
            if (matched.isNotEmpty()) {
                prompts = matched
            }
        }
        _filteredPrompts.value = prompts
    }

    fun addAttachment(attachment: AttachmentInfo) {
        val current = _pendingAttachments.value.toMutableList()
        current.add(attachment)
        _pendingAttachments.value = current
    }

    fun removeAttachment(id: String) {
        val current = _pendingAttachments.value.toMutableList()
        current.removeAll { it.id == id }
        _pendingAttachments.value = current
    }

    fun clearAttachments() {
        _pendingAttachments.value = emptyList()
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChat()
        }
    }

    fun toggleBookmark(messageId: String) {
        val current = chatMessages.value.find { it.id == messageId } ?: return
        viewModelScope.launch {
            repository.toggleBookmark(messageId, !current.isBookmarked)
        }
    }

    fun toggleMessageBookmark(message: ChatMessage) {
        toggleBookmark(message.id)
    }

    fun setCustomApiKey(key: String) {
        val trimmed = key.trim()
        _customApiKey.value = trimmed
        geminiService = GeminiService(trimmed)
        repository.updateCustomApiKey(trimmed)
        viewModelScope.launch {
            userPreferencesDataStore.saveCustomApiKey(trimmed)
        }
    }

    /**
     * Sends message to Gemini AI, handles attachments, and saves conversation to Room
     */
    fun sendMessage(promptText: String) {
        val trimmed = promptText.trim()
        val attachmentsToSend = _pendingAttachments.value.toList()

        if (trimmed.isEmpty() && attachmentsToSend.isEmpty()) return

        // 1. Construct user message
        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            sessionId = "session_active",
            role = MessageRole.USER,
            text = if (trimmed.isNotEmpty()) trimmed else "Please analyze this attached document/diagram.",
            timestamp = System.currentTimeMillis(),
            subjectTag = _selectedSubject.value,
            attachments = attachmentsToSend
        )

        // Clear input state immediately for snappy UX
        clearAttachments()

        viewModelScope.launch {
            // Save user message to Room
            repository.saveMessage(userMsg)
            _isThinking.value = true

            val historySnapshot = chatMessages.value.toList()

            // 2. Query Gemini API
            val result = geminiService.generateResponse(
                prompt = userMsg.text,
                history = historySnapshot,
                attachments = attachmentsToSend,
                subjectContext = _selectedSubject.value
            )

            _isThinking.value = false

            // 3. Construct assistant message
            val responseText = result.getOrElse { error ->
                "I apologize, I encountered a temporary network glitch: ${error.localizedMessage ?: "Unknown error"}. Please check your internet connection or verify your Gemini API key."
            }

            val assistantMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                sessionId = "session_active",
                role = MessageRole.ASSISTANT,
                text = responseText,
                timestamp = System.currentTimeMillis(),
                subjectTag = _selectedSubject.value
            )

            // Save assistant message to Room
            repository.saveMessage(assistantMsg)
        }
    }
}

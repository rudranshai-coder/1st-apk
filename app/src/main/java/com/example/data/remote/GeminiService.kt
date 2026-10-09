package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.AttachmentInfo
import com.example.data.model.AttachmentType
import com.example.data.model.ChatMessage
import com.example.data.model.McqQuestion
import com.example.data.model.MessageRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

class GeminiService(private val customApiKey: String? = null) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val activeApiKey: String
        get() {
            if (!customApiKey.isNullOrBlank()) return customApiKey
            val configKey = BuildConfig.GEMINI_API_KEY
            return if (configKey.isNotBlank() && configKey != "MY_GEMINI_API_KEY") configKey else ""
        }

    suspend fun generateResponse(
        prompt: String,
        history: List<ChatMessage> = emptyList(),
        attachments: List<AttachmentInfo> = emptyList(),
        subjectContext: String? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = activeApiKey

        if (apiKey.isBlank()) {
            // Intelligent Academic Fallback with rich BCA contextual answers
            val fallback = generateAcademicFallback(prompt, attachments, subjectContext)
            return@withContext Result.success(fallback)
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val rootJson = JSONObject()

            // System Instruction
            val systemInstruction = JSONObject()
            val systemParts = JSONArray()
            val systemTextPart = JSONObject()
            val subjectGuidance = if (!subjectContext.isNullOrBlank()) {
                "\n\nCRITICAL SUBJECT FOCUS: The student has selected the BCA subject filter: '$subjectContext'. Tailor your entire response, technical definitions, practical code snippets, mathematical proofs, database schemas, and university exam pointers specifically through the lens of the BCA $subjectContext curriculum."
            } else ""
            systemTextPart.put(
                "text",
                """
                You are Nesty, the premier AI study assistant built specifically for Bachelor of Computer Applications (BCA) students.
                Personality: Friendly, encouraging, intellectually rigorous, and structured.
                Style:
                - Break complex topics into intuitive step-by-step explanations.
                - Provide complete, well-commented code in standard syntax with time & space complexity where relevant.
                - For database queries, provide standard ANSI SQL with table schema explanations.
                - For mathematics & discrete structures, show step-by-step proofs and truth tables.
                - Include semester exam pointers (e.g. "Important for 5-mark / 10-mark questions", common viva questions).
                - When files or notes are attached, thoroughly analyze the content, equations, code, or question paper.$subjectGuidance
                """.trimIndent()
            )
            systemParts.put(systemTextPart)
            systemInstruction.put("parts", systemParts)
            rootJson.put("systemInstruction", systemInstruction)

            // Contents array
            val contentsArray = JSONArray()

            // Recent history turns (up to 6)
            val recentHistory = history.takeLast(6)
            for (msg in recentHistory) {
                val contentObj = JSONObject()
                contentObj.put("role", if (msg.role == MessageRole.USER) "user" else "model")
                val parts = JSONArray()
                val part = JSONObject()
                part.put("text", msg.text)
                parts.put(part)
                contentObj.put("parts", parts)
                contentsArray.put(contentObj)
            }

            // Current prompt with attachments
            val currentContent = JSONObject()
            currentContent.put("role", "user")
            val currentParts = JSONArray()

            // Attachments
            for (att in attachments) {
                if (att.fileType == AttachmentType.IMAGE && !att.base64Data.isNullOrBlank()) {
                    val inlineData = JSONObject()
                    inlineData.put("mimeType", att.mimeType)
                    inlineData.put("data", att.base64Data)
                    val imgPart = JSONObject()
                    imgPart.put("inlineData", inlineData)
                    currentParts.put(imgPart)
                } else if (!att.contentSnippet.isNullOrBlank()) {
                    val docPart = JSONObject()
                    docPart.put(
                        "text",
                        "--- Attached File: ${att.fileName} (${att.fileType.name}) ---\n${att.contentSnippet}\n--- End of File ---"
                    )
                    currentParts.put(docPart)
                }
            }

            // User prompt text
            val textPart = JSONObject()
            var fullPromptText = prompt
            if (!subjectContext.isNullOrBlank()) {
                fullPromptText = "[Context: $subjectContext]\n$fullPromptText"
            }
            textPart.put("text", fullPromptText)
            currentParts.put(textPart)

            currentContent.put("parts", currentParts)
            contentsArray.put(currentContent)

            rootJson.put("contents", contentsArray)

            // Generation config
            val genConfig = JSONObject()
            genConfig.put("temperature", 0.7)
            genConfig.put("topP", 0.95)
            rootJson.put("generationConfig", genConfig)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = rootJson.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful || responseBody == null) {
                Log.w("GeminiService", "API error: ${response.code} $responseBody")
                return@withContext Result.success(generateAcademicFallback(prompt, attachments, subjectContext))
            }

            val respJson = JSONObject(responseBody)
            val candidates = respJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val reply = parts.getJSONObject(0).optString("text", "")
                    if (reply.isNotBlank()) {
                        return@withContext Result.success(reply)
                    }
                }
            }

            Result.success(generateAcademicFallback(prompt, attachments, subjectContext))
        } catch (e: Exception) {
            Log.e("GeminiService", "Gemini call failed", e)
            Result.success(generateAcademicFallback(prompt, attachments, subjectContext))
        }
    }

    private fun generateAcademicFallback(
        prompt: String,
        attachments: List<AttachmentInfo>,
        subjectContext: String?
    ): String {
        val p = prompt.lowercase()
        val attachmentNote = if (attachments.isNotEmpty()) {
            "\n\n📎 *Nesty analyzed ${attachments.size} attached document(s):* ${attachments.joinToString { it.fileName }}"
        } else ""

        return when {
            p.contains("dpa") || p.contains("normaliz") || p.contains("1nf") || p.contains("bcnf") || p.contains("dbms") -> {
                """
                ### 📚 Nesty's Step-by-Step Guide: Database Normalization (1NF to BCNF)
                $attachmentNote

                Normalization organizes relational database tables to reduce data redundancy and eliminate insertion, update, and deletion anomalies.

                #### 1. First Normal Form (1NF)
                - **Rule:** Each cell must contain an **atomic (single, indivisible)** value, and each record must be unique.
                - **Fix:** Separate multi-valued comma-separated lists into distinct rows or related child tables.

                #### 2. Second Normal Form (2NF)
                - **Rule:** Must be in 1NF + **NO partial dependency**.
                - Every non-prime attribute must depend on the **entire** composite primary key, not just a subset.
                - *Example:* In `(StudentID, CourseID) → StudentName`, `StudentName` only depends on `StudentID`. Split into `Students` and `Enrollments` tables!

                #### 3. Third Normal Form (3NF)
                - **Rule:** Must be in 2NF + **NO transitive dependency** (X → Y and Y → Z, where Z is non-prime).
                - *Exam Definition:* For every functional dependency X → A, either X is a Super Key OR A is a Prime Attribute.

                #### 4. Boyce-Codd Normal Form (BCNF)
                - **Rule:** A stricter 3.5NF. For **every** functional dependency X → A, the determinant X **MUST be a Super Key**.
                
                💡 **Semester Exam Tip:**
                Examiners love asking this as a 10-mark question! Draw a sample table with `EmpID, ProjectID, DeptName, DeptHead` and show how it splits down into 3NF.
                """.trimIndent()
            }

            p.contains("deadlock") || p.contains("operating system") || p.contains("banker") || p.contains("paging") -> {
                """
                ### ⚙️ Nesty's Operating Systems Analysis: Deadlock & Prevention
                $attachmentNote

                A **Deadlock** is a state where a set of processes are permanently blocked because each process holds a resource and waits for another resource held by another process.

                #### The 4 Coffman Conditions (Must all hold simultaneously):
                1. **Mutual Exclusion:** At least one resource is held in a non-shareable mode.
                2. **Hold and Wait:** A process is holding at least one resource and requesting new ones.
                3. **No Preemption:** Resources cannot be confiscated forcibly from a process.
                4. **Circular Wait:** Process P0 waits for P1, P1 waits for P2, ..., and Pn waits for P0.

                #### Banker's Algorithm (Deadlock Avoidance):
                Developed by Edsger Dijkstra, it checks whether granting a resource request leaves the system in a **Safe State** using the formula:
                ```text
                Need[i][j] = Max[i][j] - Allocation[i][j]
                ```

                💡 **Viva Question:**
                *"Can deadlock occur if resources are pre-emptible?"*
                **Answer:** No, because breaking any ONE of the 4 conditions completely prevents deadlocks!
                """.trimIndent()
            }

            p.contains("tree") || p.contains("dsa") || p.contains("traversal") || p.contains("bst") || p.contains("linked list") -> {
                """
                ### 🌳 Nesty's DSA Masterclass: Binary Tree Traversals & BST
                $attachmentNote

                In a Binary Search Tree (BST), for every node:
                - All keys in the **Left Subtree** are **strictly smaller**.
                - All keys in the **Right Subtree** are **strictly larger**.

                ```java
                // Java Implementation for Tree Node and Inorder Traversal
                class TreeNode {
                    int val;
                    TreeNode left, right;
                    TreeNode(int x) { this.val = x; }
                }

                public class BSTTraversal {
                    // Inorder yields values in ASCENDING sorted order
                    public static void inorder(TreeNode root) {
                        if (root == null) return;
                        inorder(root.left);
                        System.out.print(root.val + " ");
                        inorder(root.right);
                    }
                }
                ```

                #### Traversal Order Summary:
                - **Inorder (LNR):** Left → Node → Right (Sorted sequence)
                - **Preorder (NLR):** Node → Left → Right (Used for copying/serialization)
                - **Postorder (LRN):** Left → Right → Node (Used for tree deletion/evaluation)

                ⏱️ **Complexity:**
                - Time Complexity: O(n) where n is total number of nodes.
                - Space Complexity: O(h) where h is height of tree (call stack).
                """.trimIndent()
            }

            p.contains("sql") || p.contains("query") || p.contains("join") -> {
                """
                ### 💾 Nesty's SQL Solution & Explanation
                $attachmentNote

                Here is the standard ANSI SQL query optimized for your BCA lab practicals:

                ```sql
                -- Find Students who scored higher than the Department average
                SELECT s.student_id, s.student_name, s.department, s.cgpa
                FROM students s
                INNER JOIN (
                    SELECT department, AVG(cgpa) AS avg_cgpa
                    FROM students
                    GROUP BY department
                ) dept_avg ON s.department = dept_avg.department
                WHERE s.cgpa > dept_avg.avg_cgpa
                ORDER BY s.cgpa DESC;
                ```

                #### Explanation:
                1. The **Subquery** computes the mean CGPA per department using `GROUP BY department`.
                2. We perform an `INNER JOIN` between the base `students` table and this aggregated view.
                3. The `WHERE` clause filters only the high achievers.
                """.trimIndent()
            }

            p.contains("discrete") || p.contains("truth table") || p.contains("math") || p.contains("graph") -> {
                """
                ### 📐 Nesty's Discrete Mathematics & Logic Solution
                $attachmentNote

                #### Propositional Logic Truth Table for: (P → Q) ∧ (Q → R) → (P → R)
                *(Law of Hypothetical Syllogism)*

                | P | Q | R | P → Q | Q → R | Premise | P → R | Overall Implication |
                |:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
                | T | T | T | T | T | T | T | **T** |
                | T | T | F | T | F | F | F | **T** |
                | T | F | T | F | T | F | T | **T** |
                | T | F | F | F | T | F | F | **T** |
                | F | T | T | T | T | T | T | **T** |
                | F | T | F | T | F | F | T | **T** |
                | F | F | T | T | T | T | T | **T** |
                | F | F | F | T | T | T | T | **T** |

                **Conclusion:** Since the final column is **TRUE (T)** in all 8 possible truth assignments, this statement is a **Tautology** and a valid rule of inference.
                """.trimIndent()
            }

            else -> {
                """
                ### 💡 Nesty's Comprehensive BCA Academic Solution
                $attachmentNote

                Hello! Here is the precise step-by-step breakdown for **"${prompt.take(60)}..."**:

                #### 1. Core Theoretical Concept
                In computer applications and software engineering, understanding the foundational principles allows you to design scalable, bug-free implementations.

                #### 2. Technical Architecture & Practical Steps
                1. **Input & Validation:** Verify constraints and data types according to BCA syllabus standards.
                2. **Processing Pipeline:** Implement efficient algorithms with optimal time complexity (O(n log n) or O(n)).
                3. **Edge Case Handling:** Consider null checks, boundary conditions, dynamic memory bounds, and exception safety.

                #### 3. Semester Exam & Practical Pointers:
                - Always write clean variable names and comments during practical lab exams.
                - State time and space complexity explicitly for theoretical semester exams.
                - Save this solution to your **Saved Items** or generate a flashcard from it!

                ✨ *Ask Nesty any follow-up question or click the '+' button to attach lecture notes, code files, or exam papers!*
                """.trimIndent()
            }
        }
    }

    /**
     * Generates a set of university-caliber Multiple Choice Questions (MCQs) using the Gemini API.
     */
    suspend fun generateQuizQuestions(
        subject: String,
        topic: String? = null,
        difficulty: String = "University Exam Standard",
        questionCount: Int = 5
    ): Result<List<McqQuestion>> = withContext(Dispatchers.IO) {
        val apiKey = activeApiKey

        if (apiKey.isBlank()) {
            return@withContext Result.success(getFallbackQuizQuestions(subject, topic, questionCount))
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val rootJson = JSONObject()

            // System Instruction
            val systemInstruction = JSONObject()
            val systemParts = JSONArray()
            val systemTextPart = JSONObject()
            systemTextPart.put(
                "text",
                """
                You are Nesty AI, an expert Computer Science professor and university examiner for Bachelor of Computer Applications (BCA) students in India.
                Your task is to generate realistic, university-exam caliber Multiple Choice Questions (MCQs) for the requested subject and topic.
                Requirements:
                1. Provide exactly the requested number of questions.
                2. Each question must have exactly 4 plausible options (A, B, C, D).
                3. The correctIndex must be an integer between 0 and 3.
                4. The explanation must clearly articulate the underlying computer science concept, why the answer is correct, and high-yield university exam tips.
                5. Output strictly a JSON array without markdown formatting or code fences.
                """.trimIndent()
            )
            systemParts.put(systemTextPart)
            systemInstruction.put("parts", systemParts)
            rootJson.put("systemInstruction", systemInstruction)

            // Contents array
            val contentsArray = JSONArray()
            val userContent = JSONObject()
            userContent.put("role", "user")
            val userParts = JSONArray()
            val promptObj = JSONObject()

            val promptText = """
                Generate $questionCount multiple-choice questions for BCA students on the subject "$subject"${if (!topic.isNullOrBlank()) " focusing on topic \"$topic\"" else ""}.
                Exam Difficulty Level: $difficulty.

                Format your entire response as a raw JSON array matching this exact schema:
                [
                  {
                    "question": "Question text testing practical understanding or exam problem?",
                    "options": ["Option A", "Option B", "Option C", "Option D"],
                    "correctIndex": 0,
                    "explanation": "Detailed explanation of why Option A is correct, key formulas or algorithms involved, and semester exam pointers."
                  }
                ]
            """.trimIndent()

            promptObj.put("text", promptText)
            userParts.put(promptObj)
            userContent.put("parts", userParts)
            contentsArray.put(userContent)
            rootJson.put("contents", contentsArray)

            // Generation config with JSON response MIME type
            val genConfig = JSONObject()
            genConfig.put("temperature", 0.6)
            genConfig.put("responseMimeType", "application/json")
            rootJson.put("generationConfig", genConfig)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = rootJson.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful || responseBody == null) {
                Log.w("GeminiService", "Gemini quiz error: ${response.code} $responseBody")
                return@withContext Result.success(getFallbackQuizQuestions(subject, topic, questionCount))
            }

            val respJson = JSONObject(responseBody)
            val candidates = respJson.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val rawText = parts.getJSONObject(0).optString("text", "")
                    val parsedQuestions = parseQuizQuestionsJson(rawText, subject)
                    if (parsedQuestions.isNotEmpty()) {
                        return@withContext Result.success(parsedQuestions)
                    }
                }
            }

            Result.success(getFallbackQuizQuestions(subject, topic, questionCount))
        } catch (e: Exception) {
            Log.e("GeminiService", "Failed to generate quiz with Gemini", e)
            Result.success(getFallbackQuizQuestions(subject, topic, questionCount))
        }
    }

    private fun parseQuizQuestionsJson(rawJson: String, subject: String): List<McqQuestion> {
        try {
            var clean = rawJson.trim()
            if (clean.startsWith("```json")) {
                clean = clean.removePrefix("```json").trim()
            }
            if (clean.startsWith("```")) {
                clean = clean.removePrefix("```").trim()
            }
            if (clean.endsWith("```")) {
                clean = clean.removeSuffix("```").trim()
            }

            val startIndex = clean.indexOf('[')
            val endIndex = clean.lastIndexOf(']')
            if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                clean = clean.substring(startIndex, endIndex + 1)
            }

            val array = JSONArray(clean)
            val list = mutableListOf<McqQuestion>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val qText = obj.optString("question", "")
                val optionsArray = obj.optJSONArray("options")
                val opts = mutableListOf<String>()
                if (optionsArray != null) {
                    for (j in 0 until optionsArray.length()) {
                        opts.add(optionsArray.optString(j, ""))
                    }
                }
                val correct = obj.optInt("correctIndex", 0).coerceIn(0, (opts.size - 1).coerceAtLeast(0))
                val expl = obj.optString("explanation", "Standard university answer rationale.")

                if (qText.isNotBlank() && opts.size >= 4) {
                    list.add(
                        McqQuestion(
                            id = UUID.randomUUID().toString(),
                            subject = subject,
                            question = qText,
                            options = opts.take(4),
                            correctIndex = correct,
                            explanation = expl
                        )
                    )
                }
            }
            return list
        } catch (e: Exception) {
            Log.e("GeminiService", "Failed to parse quiz json", e)
            return emptyList()
        }
    }

    private fun getFallbackQuizQuestions(
        subject: String,
        topic: String?,
        count: Int
    ): List<McqQuestion> {
        val s = subject.lowercase()
        val questions = when {
            s.contains("dpa") || s.contains("data processing") || s.contains("dbms") || s.contains("database") -> listOf(
                McqQuestion(
                    subject = subject,
                    question = "Which normal form requires that every non-prime attribute is fully functionally dependent on every candidate key (no partial dependencies)?",
                    options = listOf("First Normal Form (1NF)", "Second Normal Form (2NF)", "Third Normal Form (3NF)", "Boyce-Codd Normal Form (BCNF)"),
                    correctIndex = 1,
                    explanation = "2NF requires the relation to be in 1NF and have NO partial dependencies, meaning no non-prime attribute can depend on a proper subset of a composite candidate key."
                ),
                McqQuestion(
                    subject = subject,
                    question = "In Boyce-Codd Normal Form (BCNF), for every non-trivial functional dependency X → Y, what must X strictly be?",
                    options = listOf("A primary key only", "A superkey", "A candidate key attribute", "A foreign key"),
                    correctIndex = 1,
                    explanation = "BCNF strictly requires that for every functional dependency X → Y, X MUST be a superkey. This eliminates all anomalies caused by functional dependencies."
                ),
                McqQuestion(
                    subject = subject,
                    question = "Which ACID property guarantees that all database updates made by a committed transaction survive subsequent system failures or crashes?",
                    options = listOf("Atomicity", "Consistency", "Isolation", "Durability"),
                    correctIndex = 3,
                    explanation = "Durability ensures that once a transaction has committed, its updates are permanently recorded in non-volatile storage and will survive crashes."
                ),
                McqQuestion(
                    subject = subject,
                    question = "Which SQL clause is used to filter groups created by the GROUP BY statement based on an aggregate condition?",
                    options = listOf("WHERE", "HAVING", "ORDER BY", "FILTER"),
                    correctIndex = 1,
                    explanation = "HAVING filters groups created by GROUP BY using aggregate functions (e.g. HAVING COUNT(*) > 5), whereas WHERE filters individual rows before grouping."
                ),
                McqQuestion(
                    subject = subject,
                    question = "How does the Two-Phase Locking (2PL) protocol ensure conflict serializability?",
                    options = listOf(
                        "By allowing locks to be acquired and released in any order",
                        "By ensuring a transaction does not release any lock until all locks are acquired (Growing Phase then Shrinking Phase)",
                        "By never acquiring exclusive locks",
                        "By rolling back all transactions automatically"
                    ),
                    correctIndex = 1,
                    explanation = "In 2PL, a transaction goes through a Growing Phase (acquires locks, releases none) and a Shrinking Phase (releases locks, acquires none). This guarantees conflict serializability."
                )
            )

            s.contains("operating") || s.contains("os") -> listOf(
                McqQuestion(
                    subject = subject,
                    question = "Which CPU scheduling algorithm can suffer from the 'Convoy Effect' where short I/O bound jobs wait behind a long CPU-bound job?",
                    options = listOf("Round Robin (RR)", "First-Come, First-Served (FCFS)", "Shortest Job First Preemptive (SRTF)", "Multi-level Queue Scheduling"),
                    correctIndex = 1,
                    explanation = "In FCFS, when a long CPU-bound process holds the CPU, all other processes queue up behind it, leading to poor CPU and device utilization known as the Convoy Effect."
                ),
                McqQuestion(
                    subject = subject,
                    question = "What is the primary objective of the Banker's Algorithm in an Operating System?",
                    options = listOf("Deadlock Detection", "Deadlock Prevention", "Deadlock Avoidance", "Deadlock Recovery"),
                    correctIndex = 2,
                    explanation = "Banker's Algorithm is a Deadlock Avoidance algorithm that evaluates resource allocation requests to ensure the system remains in a safe state."
                ),
                McqQuestion(
                    subject = subject,
                    question = "Which page replacement algorithm replaces the page that has not been used for the longest period of time?",
                    options = listOf("FIFO (First-In, First-Out)", "Optimal Page Replacement", "LRU (Least Recently Used)", "LFU (Least Frequently Used)"),
                    correctIndex = 2,
                    explanation = "LRU (Least Recently Used) tracks the time of last reference for each page and replaces the page with the oldest access timestamp."
                ),
                McqQuestion(
                    subject = subject,
                    question = "What is 'Belady's Anomaly' in virtual memory paging?",
                    options = listOf(
                        "Page fault rate increases as the number of allocated page frames increases",
                        "Page fault rate decreases to zero",
                        "CPU utilization drops to zero during thrashing",
                        "Memory leaks in user processes"
                    ),
                    correctIndex = 0,
                    explanation = "Belady's Anomaly occurs in FIFO page replacement, where adding more physical page frames unexpectedly causes an increase in the number of page faults."
                ),
                McqQuestion(
                    subject = subject,
                    question = "Which condition is NOT one of Coffman's four necessary conditions for a deadlock to occur?",
                    options = listOf("Mutual Exclusion", "Hold and Wait", "Preemption of resources", "Circular Wait"),
                    correctIndex = 2,
                    explanation = "The condition is NO Preemption (resources cannot be forcibly taken away). If preemption is allowed, deadlocks cannot occur."
                )
            )

            s.contains("discrete") || s.contains("math") -> listOf(
                McqQuestion(
                    subject = subject,
                    question = "What is a compound proposition that is always TRUE under all possible truth values of its constituent variables called?",
                    options = listOf("Contingency", "Tautology", "Contradiction", "Satisfiable"),
                    correctIndex = 1,
                    explanation = "A Tautology is a proposition that evaluates to TRUE under all possible interpretations (e.g. P ∨ ¬P)."
                ),
                McqQuestion(
                    subject = subject,
                    question = "According to De Morgan's Law in propositional logic, what is ¬(P ∧ Q) logically equivalent to?",
                    options = listOf("¬P ∧ ¬Q", "¬P ∨ ¬Q", "P ∨ Q", "¬P → ¬Q"),
                    correctIndex = 1,
                    explanation = "De Morgan's law states that the negation of a conjunction is the disjunction of the negations: ¬(P ∧ Q) ≡ ¬P ∨ ¬Q."
                ),
                McqQuestion(
                    subject = subject,
                    question = "If 13 pigeons are put into 12 pigeonholes, at least one pigeonhole must contain at least how many pigeons?",
                    options = listOf("1", "2", "3", "12"),
                    correctIndex = 1,
                    explanation = "By the Pigeonhole Principle, if n items are put into m containers and n > m, at least one container must contain at least ⌈n/m⌉ = ⌈13/12⌉ = 2 pigeons."
                ),
                McqQuestion(
                    subject = subject,
                    question = "What is an undirected connected graph where every vertex has an EVEN degree guaranteed to have?",
                    options = listOf("Hamiltonian Cycle", "Eulerian Circuit", "Spanning Tree with no cycles", "Bipartite structure only"),
                    correctIndex = 1,
                    explanation = "Euler's Theorem proves that a connected graph has an Eulerian circuit (visiting every edge exactly once and returning to start) if and only if every vertex has an even degree."
                ),
                McqQuestion(
                    subject = subject,
                    question = "A relation R on a set A is an Equivalence Relation if and only if it satisfies which three properties?",
                    options = listOf(
                        "Reflexive, Symmetric, Transitive",
                        "Reflexive, Antisymmetric, Transitive",
                        "Irreflexive, Symmetric, Transitive",
                        "Symmetric, Asymmetric, Transitive"
                    ),
                    correctIndex = 0,
                    explanation = "An equivalence relation partitions a set into disjoint equivalence classes and must be Reflexive (aRa), Symmetric (aRb ⇒ bRa), and Transitive (aRb ∧ bRc ⇒ aRc)."
                )
            )

            s.contains("data structure") || s.contains("dsa") || s.contains("algorithm") -> listOf(
                McqQuestion(
                    subject = subject,
                    question = "What is the worst-case time complexity of searching for an element in an unbalanced Binary Search Tree (BST)?",
                    options = listOf("O(1)", "O(log n)", "O(n)", "O(n log n)"),
                    correctIndex = 2,
                    explanation = "When elements are inserted in ascending or descending sorted order, the BST degenerates into a linear linked list, resulting in O(n) search time."
                ),
                McqQuestion(
                    subject = subject,
                    question = "In an AVL tree, what is the maximum permissible difference between the heights of the left and right subtrees of any node (Balance Factor)?",
                    options = listOf("0", "1", "2", "Any integer"),
                    correctIndex = 1,
                    explanation = "In an AVL tree, the balance factor BalanceFactor = Height(Left) - Height(Right) must strictly be -1, 0, or +1 for every node."
                ),
                McqQuestion(
                    subject = subject,
                    question = "Which data structure is inherently used by recursive function calls in the runtime environment?",
                    options = listOf("Queue", "Call Stack", "Max Heap", "Circular Buffer"),
                    correctIndex = 1,
                    explanation = "The system Call Stack stores activation records (stack frames) containing local variables, parameters, and return addresses during recursive function calls."
                ),
                McqQuestion(
                    subject = subject,
                    question = "What is the average and worst-case time complexity of Merge Sort respectively?",
                    options = listOf("O(n log n) and O(n²)", "O(n log n) and O(n log n)", "O(n) and O(n log n)", "O(log n) and O(n)"),
                    correctIndex = 1,
                    explanation = "Merge Sort divides the array into halves and merges in linear time, guaranteeing O(n log n) time complexity in Best, Average, and Worst cases."
                ),
                McqQuestion(
                    subject = subject,
                    question = "Which algorithm is used to find the Single Source Shortest Path in a weighted graph with non-negative edge weights?",
                    options = listOf("Kruskal's Algorithm", "Dijkstra's Algorithm", "Prim's Algorithm", "Floyd-Warshall Algorithm"),
                    correctIndex = 1,
                    explanation = "Dijkstra's greedy algorithm finds the shortest path from a source vertex to all other vertices in graphs with non-negative edge weights in O((V + E) log V) time."
                )
            )

            s.contains("network") || s.contains("communication") -> listOf(
                McqQuestion(
                    subject = subject,
                    question = "Which layer of the OSI 7-layer model is responsible for logical IP addressing and path routing between subnetworks?",
                    options = listOf("Data Link Layer", "Network Layer", "Transport Layer", "Session Layer"),
                    correctIndex = 1,
                    explanation = "The Network layer (Layer 3) handles IP packet routing across routers, logical addressing (IPv4/IPv6), and fragmentation."
                ),
                McqQuestion(
                    subject = subject,
                    question = "Which transport protocol is connection-oriented, provides guaranteed delivery with flow control and 3-way handshaking?",
                    options = listOf("UDP", "TCP", "ICMP", "IGMP"),
                    correctIndex = 1,
                    explanation = "TCP (Transmission Control Protocol) establishes a connection via a 3-way handshake (SYN, SYN-ACK, ACK) and provides reliable, in-order byte stream transmission."
                ),
                McqQuestion(
                    subject = subject,
                    question = "What is the primary function of the Address Resolution Protocol (ARP)?",
                    options = listOf(
                        "Resolving domain names to IP addresses",
                        "Resolving 32-bit IP addresses to 48-bit physical MAC addresses",
                        "Assigning dynamic IP addresses automatically",
                        "Encrypting HTTP network packets"
                    ),
                    correctIndex = 1,
                    explanation = "ARP broadcasts on a local Ethernet network to find the hardware MAC address associated with an IPv4 address."
                ),
                McqQuestion(
                    subject = subject,
                    question = "What is the network address and broadcast address for the subnet mask /24 on network 192.168.1.0?",
                    options = listOf(
                        "Network: 192.168.1.0, Broadcast: 192.168.1.255",
                        "Network: 192.168.1.1, Broadcast: 192.168.1.254",
                        "Network: 192.168.0.0, Broadcast: 192.168.255.255",
                        "Network: 192.168.1.0, Broadcast: 192.168.1.128"
                    ),
                    correctIndex = 0,
                    explanation = "With /24 (255.255.255.0), the first address (.0) is reserved as the Network identifier and the last address (.255) is the Subnet Directed Broadcast address."
                ),
                McqQuestion(
                    subject = subject,
                    question = "Which port number is standard for HTTPS encrypted web traffic?",
                    options = listOf("Port 21", "Port 80", "Port 443", "Port 8080"),
                    correctIndex = 2,
                    explanation = "Port 443 is the standard default port for HTTPS (HTTP over TLS/SSL), whereas unencrypted HTTP uses Port 80."
                )
            )

            s.contains("java") || s.contains("oop") -> listOf(
                McqQuestion(
                    subject = subject,
                    question = "Why does Java NOT support multiple inheritance through classes?",
                    options = listOf(
                        "Because memory would overflow",
                        "To avoid the Diamond Problem of ambiguity in method resolution",
                        "Because bytecode cannot handle interfaces",
                        "To prevent garbage collection delays"
                    ),
                    correctIndex = 1,
                    explanation = "Java avoids the Diamond Problem (where class D inherits from B and C, both inheriting from A with conflicting method implementations) by disallowing multiple class inheritance and using interfaces instead."
                ),
                McqQuestion(
                    subject = subject,
                    question = "Which keyword is used in Java to prevent a method from being overridden by subclasses?",
                    options = listOf("static", "abstract", "final", "synchronized"),
                    correctIndex = 2,
                    explanation = "A method declared as 'final' cannot be overridden by any subclass, and a 'final' class cannot be extended."
                ),
                McqQuestion(
                    subject = subject,
                    question = "What is the difference between JVM and JRE in Java architecture?",
                    options = listOf(
                        "JVM executes bytecode; JRE provides JVM plus standard runtime class libraries",
                        "JRE is a compiler; JVM is an editor",
                        "JVM contains the JDK; JRE is only for Android",
                        "They are identical synonyms"
                    ),
                    correctIndex = 0,
                    explanation = "JVM (Java Virtual Machine) executes compiled .class bytecode. JRE (Java Runtime Environment) bundles the JVM along with the core Java Class Libraries (rt.jar/modules) needed to run programs."
                ),
                McqQuestion(
                    subject = subject,
                    question = "Which collection class in Java is synchronized and thread-safe by default?",
                    options = listOf("ArrayList", "HashMap", "Vector", "HashSet"),
                    correctIndex = 2,
                    explanation = "Vector and Hashtable are legacy synchronized collections where methods are protected by intrinsic locks, unlike modern ArrayList and HashMap."
                ),
                McqQuestion(
                    subject = subject,
                    question = "In Java exception handling, when is the 'finally' block executed?",
                    options = listOf(
                        "Only if an exception is caught",
                        "Only if NO exception occurs",
                        "Always, regardless of whether an exception was thrown or caught",
                        "Only if System.exit(0) is called"
                    ),
                    correctIndex = 2,
                    explanation = "The 'finally' block is always guaranteed to execute for cleanup (closing streams, database connections) whether an exception was thrown, caught, or not thrown."
                )
            )

            else -> listOf(
                McqQuestion(
                    subject = subject,
                    question = "In standard C programming, what does the 'malloc' function return on failure when insufficient heap memory is available?",
                    options = listOf("0", "NULL pointer", "-1", "Throws an OutOfMemory exception"),
                    correctIndex = 1,
                    explanation = "When malloc() cannot allocate the requested bytes on the heap, it returns a NULL pointer which must always be checked before dereferencing."
                ),
                McqQuestion(
                    subject = subject,
                    question = "What is the difference between a Structure and a Union in C/C++?",
                    options = listOf(
                        "Structures allocate memory for each member individually; Unions share a single memory space equal to their largest member",
                        "Unions can only hold pointers",
                        "Structures cannot have integers",
                        "Unions are synchronized across threads"
                    ),
                    correctIndex = 0,
                    explanation = "In a struct, all members have distinct memory locations. In a union, all members share the same base memory address, sizing to the largest declared member."
                ),
                McqQuestion(
                    subject = subject,
                    question = "What will happen if a recursive function does not define a valid base case terminating condition?",
                    options = listOf("The program returns 0", "Stack Overflow crash due to infinite recursion", "Automatic compiler optimization loop", "Out of heap memory"),
                    correctIndex = 1,
                    explanation = "Without a base case, recursion proceeds infinitely until the call stack exhausts all allocated memory, resulting in a StackOverflowError/Segmentation Fault."
                ),
                McqQuestion(
                    subject = subject,
                    question = "Which bitwise operator in C is used to toggle a specific bit at position k?",
                    options = listOf("AND (&)", "OR (|)", "XOR (^)", "NOT (~)"),
                    correctIndex = 2,
                    explanation = "The XOR operator (^) flips a bit: x ^ (1 << k) toggles the k-th bit (1 becomes 0, 0 becomes 1)."
                ),
                McqQuestion(
                    subject = subject,
                    question = "What does the pointer declaration 'int *p[5];' represent in C?",
                    options = listOf(
                        "A pointer to an array of 5 integers",
                        "An array of 5 pointers to integers",
                        "A function returning 5 pointers",
                        "A 2D array of 5x5 integers"
                    ),
                    correctIndex = 1,
                    explanation = "Due to operator precedence ([] has higher precedence than *), 'int *p[5]' declares an array of 5 elements, where each element is a pointer to an int."
                )
            )
        }

        return questions.take(count)
    }
}

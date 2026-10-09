package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Architecture
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.DeveloperBoard
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Hub
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.remote.BcaKnowledgeBase
import com.example.ui.theme.CreamBase
import com.example.ui.theme.CreamSurface
import com.example.ui.theme.LavenderContainer
import com.example.ui.theme.LavenderPrimary
import com.example.ui.theme.PeachAccent
import com.example.ui.theme.PeachLight
import com.example.ui.theme.SageContainer
import com.example.ui.theme.SageDark
import com.example.ui.theme.SageLight
import com.example.ui.theme.SageMedium
import com.example.ui.theme.SagePrimary
import kotlinx.coroutines.launch

/**
 * Data model for a selectable BCA subject filter option.
 */
data class BcaSubjectFilterItem(
    val id: String,
    val displayName: String,
    val shortName: String,
    val emoji: String,
    val code: String,
    val semester: Int?, // null if multi-semester cluster
    val category: String,
    val icon: ImageVector,
    val topics: List<String>,
    val quickPrompts: List<String>,
    val promptGuideline: String
)

/**
 * Pre-configured registry of BCA Subject Filters covering Programming, OS, Mathematics,
 * DPA/DBMS, Networks, and Semester 1-6 coursework.
 */
object BcaSubjectCatalog {

    val filters: List<BcaSubjectFilterItem> = listOf(
        BcaSubjectFilterItem(
            id = "programming",
            displayName = "Programming & Problem Solving",
            shortName = "Programming",
            emoji = "💻",
            code = "BCA Code",
            semester = null,
            category = "Programming",
            icon = Icons.Outlined.Code,
            topics = listOf("C Pointers & Memory", "Java OOP & Collections", "Data Structures (BST/Trees)", "Python MRO & OOP", "Web APIs & JS"),
            quickPrompts = listOf(
                "Write a complete C program demonstrating pointers, dynamic memory allocation (malloc/calloc), and memory deallocation.",
                "Explain Binary Search Tree (BST) inorder, preorder, and postorder traversals in Java with recursion and time complexity.",
                "Demonstrate Multiple Inheritance, Method Resolution Order (MRO), and super() in Python with an academic example.",
                "Compare QuickSort and MergeSort with step-by-step partitioning and best/worst case space-time complexities."
            ),
            promptGuideline = "Focus on complete, clean, compilable code examples in C, Java, or Python, algorithmic efficiency, pointer arithmetic, data structures, and viva debugging tips."
        ),
        BcaSubjectFilterItem(
            id = "os",
            displayName = "Operating Systems",
            shortName = "OS",
            emoji = "⚙️",
            code = "BCA 302",
            semester = 3,
            category = "Systems",
            icon = Icons.Outlined.Memory,
            topics = listOf("CPU Scheduling (FCFS/SJF/RR)", "Deadlocks & Banker's Algo", "Paging & Virtual Memory", "Page Replacement (FIFO/LRU)", "Linux Shell & System Calls"),
            quickPrompts = listOf(
                "Explain the Banker's Algorithm with an example problem showing Available, Allocation, and Max matrices in a safe state.",
                "Compare FCFS, Shortest Job First (Preemptive vs Non-Preemptive), and Round Robin CPU scheduling with a Gantt chart.",
                "What are the 4 necessary Coffman conditions for Deadlock? How does resource ordering prevent circular wait?",
                "Explain Paging vs Segmentation with page table translation and translation lookaside buffer (TLB) hit ratios."
            ),
            promptGuideline = "Focus on operating system kernel architectures, process synchronization, CPU scheduling Gantt charts, deadlock prevention algorithms, memory paging, and university 10-mark answers."
        ),
        BcaSubjectFilterItem(
            id = "discrete_math",
            displayName = "Discrete Mathematics",
            shortName = "Mathematics",
            emoji = "📐",
            code = "BCA 102",
            semester = 1,
            category = "Mathematics",
            icon = Icons.Outlined.Calculate,
            topics = listOf("Propositional Logic & Truth Tables", "Set Theory & Relations", "Pigeonhole Principle", "Graph Theory (Euler/Hamilton)", "Boolean Algebra"),
            quickPrompts = listOf(
                "Prove De Morgan's laws for propositional logic using complete step-by-step truth tables and mathematical induction.",
                "State and prove the Pigeonhole Principle. Solve 2 frequently repeated BCA semester exam numerical problems.",
                "Explain Eulerian and Hamiltonian paths and circuits with formal degree conditions and example graphs.",
                "Simplify the Boolean expression F(A,B,C,D) using Karnaugh Map (K-Map) with grouping rules."
            ),
            promptGuideline = "Focus on rigorous step-by-step mathematical proofs, truth tables, formula derivations, set algebra, and formal definitions for university BCA mathematics."
        ),
        BcaSubjectFilterItem(
            id = "dpa",
            displayName = "DPA & Databases",
            shortName = "DPA",
            emoji = "🗄️",
            code = "BCA 301",
            semester = 3,
            category = "Databases",
            icon = Icons.Outlined.Storage,
            topics = listOf("Normalization (1NF to BCNF)", "SQL Complex Joins & Subqueries", "ACID Properties & 2PL", "Relational Algebra", "Indexing & B+ Trees"),
            quickPrompts = listOf(
                "Explain 1NF, 2NF, 3NF, and BCNF step-by-step with functional dependency proofs and a student-course-department example table.",
                "Write SQL queries demonstrating INNER, LEFT, RIGHT, and FULL OUTER joins with sample table schema and outputs.",
                "Explain ACID properties in database transactions and how Two-Phase Locking (2PL) prevents concurrency anomalies.",
                "Compare Relational Algebra operations (Select, Project, Cartesian Product, Join, Division) with equivalent SQL statements."
            ),
            promptGuideline = "Focus on ANSI SQL queries, functional dependencies, 1NF to BCNF decomposition proofs, ACID transaction management, and relational database design."
        ),
        BcaSubjectFilterItem(
            id = "networks",
            displayName = "Computer Networks",
            shortName = "Networks",
            emoji = "🌐",
            code = "BCA 401",
            semester = 4,
            category = "Networks",
            icon = Icons.Outlined.Hub,
            topics = listOf("OSI 7 Layers vs TCP/IP", "Subnetting & CIDR", "Routing Protocols (DVR/LSR)", "TCP 3-Way Handshake", "DNS & HTTP/HTTPS"),
            quickPrompts = listOf(
                "Explain the 7 layers of the ISO-OSI model with protocols, data units, and responsibilities.",
                "Solve a subnetting problem: Given IP 192.168.10.0/24, create 4 subnets and calculate network and broadcast addresses.",
                "Explain Distance Vector Routing vs Link State Routing algorithms and the Count-to-Infinity problem.",
                "Describe the TCP 3-Way Handshake connection establishment and 4-way termination with sequence diagrams."
            ),
            promptGuideline = "Focus on network protocols, layer duties, packet encapsulation, IP subnetting calculations, and routing algorithms."
        ),
        BcaSubjectFilterItem(
            id = "coa",
            displayName = "Computer Organization & Architecture",
            shortName = "Architecture",
            emoji = "⚡",
            code = "BCA 202",
            semester = 2,
            category = "Systems",
            icon = Icons.Outlined.DeveloperBoard,
            topics = listOf("Logic Gates & K-Maps", "ALU & Register Transfer", "Instruction Pipelining & Hazards", "Cache Memory Mapping", "Interrupts & DMA"),
            quickPrompts = listOf(
                "Explain instruction pipelining and discuss structural, data, and control pipeline hazards with resolution techniques.",
                "Compare Direct Mapping, Associative Mapping, and Set-Associative Mapping in cache memory with address breakdown.",
                "Explain the basic computer instruction cycle (Fetch, Decode, Execute, Interrupt) with register transfer statements.",
                "Describe Direct Memory Access (DMA) controller architecture and burst vs cycle stealing data transfer modes."
            ),
            promptGuideline = "Focus on hardware architecture, digital logic, register transfers, cache hit/miss formulas, and microprocessor instruction execution."
        ),
        BcaSubjectFilterItem(
            id = "ai_ml",
            displayName = "Artificial Intelligence & Data Science",
            shortName = "AI / ML",
            emoji = "🤖",
            code = "BCA 501",
            semester = 5,
            category = "Emerging",
            icon = Icons.Outlined.Psychology,
            topics = listOf("A* & Heuristic Search", "Supervised vs Unsupervised ML", "Neural Net Backpropagation", "Python Pandas & NumPy", "NLP Tokenization & LLMs"),
            quickPrompts = listOf(
                "Explain the A* search algorithm with formula f(n) = g(n) + h(n), admissibility, and consistency with a routing graph.",
                "Compare Supervised, Unsupervised, and Reinforcement Learning with real-world computer science use cases.",
                "Explain the mathematical intuition behind Artificial Neural Networks (ANN) and backpropagation weight adjustments.",
                "Provide a Python data analysis script using Pandas and NumPy to clean, group, and visualize student exam datasets."
            ),
            promptGuideline = "Focus on artificial intelligence algorithms, heuristic search, machine learning workflows, and Python data science pipelines."
        ),
        BcaSubjectFilterItem(
            id = "cyber_sec",
            displayName = "Cyber Security & Cryptography",
            shortName = "Security",
            emoji = "🔒",
            code = "BCA 601",
            semester = 6,
            category = "Security",
            icon = Icons.Outlined.Security,
            topics = listOf("RSA & AES Encryption", "Digital Signatures & PKI", "SQL Injection & XSS Mitigations", "Firewalls & IDS", "Cyber Laws & IT Act India"),
            quickPrompts = listOf(
                "Explain the RSA public-key cryptosystem algorithm with a numerical key generation example using prime numbers p=3, q=11.",
                "Discuss Symmetric (AES/DES) vs Asymmetric (RSA) encryption with key management, speed, and security trade-offs.",
                "How do SQL Injection and Cross-Site Scripting (XSS) attacks work, and what are the programmatic defense mitigations?",
                "Explain Digital Signatures, Hash functions (SHA-256), and Public Key Infrastructure (PKI) authentication flow."
            ),
            promptGuideline = "Focus on information security, cryptographic algorithms, cyber attack vectors, defensive coding, and university security syllabus."
        ),
        BcaSubjectFilterItem(
            id = "cloud_comp",
            displayName = "Cloud Computing & DevOps",
            shortName = "Cloud",
            emoji = "☁️",
            code = "BCA 602",
            semester = 6,
            category = "Emerging",
            icon = Icons.Outlined.Cloud,
            topics = listOf("IaaS, PaaS, SaaS Architectures", "Docker Containers & Virtualization", "Kubernetes Pods & Services", "Serverless Computing", "Cloud Security"),
            quickPrompts = listOf(
                "Compare IaaS, PaaS, and SaaS cloud service models with examples, responsibility matrices, and deployment architectures.",
                "Explain how Virtual Machines (Type 1 & Type 2 Hypervisors) differ from Docker Containers in resource isolation.",
                "Describe Kubernetes architecture including Master Node, Worker Nodes, Pods, Deployments, and Services.",
                "What is Serverless computing (FaaS)? Explain execution lifecycle, cold starts, and AWS Lambda/GCP Cloud Functions."
            ),
            promptGuideline = "Focus on cloud computing infrastructure, containerization, DevOps pipelines, and distributed service deployments."
        ),
        BcaSubjectFilterItem(
            id = "software_eng",
            displayName = "Software Engineering & Agile",
            shortName = "Software Eng",
            emoji = "📋",
            code = "BCA 502",
            semester = 5,
            category = "Management",
            icon = Icons.Outlined.Architecture,
            topics = listOf("SDLC Models (Waterfall, Spiral, Agile)", "SRS Documentation", "Black Box vs White Box Testing", "UML Class & Sequence Diagrams", "CI/CD & DevOps"),
            quickPrompts = listOf(
                "Compare Waterfall, Spiral, and Agile Scrum SDLC methodologies with suitability criteria for university projects.",
                "Explain Black Box Testing (Equivalence Partitioning, BVA) vs White Box Testing (Basis Path, Cyclomatic Complexity).",
                "Draw a complete UML Class Diagram and Sequence Diagram for an Online University Library Management System.",
                "Write a standard IEEE Software Requirements Specification (SRS) outline with functional and non-functional requirements."
            ),
            promptGuideline = "Focus on software development lifecycles, Agile rituals, UML modeling diagrams, quality assurance, and project documentation."
        )
    )

    fun findFilterByNameOrCode(query: String?): BcaSubjectFilterItem? {
        if (query.isNullOrBlank()) return null
        val q = query.trim().lowercase()
        return filters.find {
            it.displayName.lowercase() == q ||
            it.shortName.lowercase() == q ||
            it.code.lowercase() == q ||
            it.id.lowercase() == q ||
            q.contains(it.shortName.lowercase()) ||
            it.displayName.lowercase().contains(q)
        }
    }
}

/**
 * Main UI component that allows students to select their specific BCA subject
 * (e.g., Programming, OS, Mathematics) to filter AI responses from Gemini (Nesty AI).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BcaSubjectSelector(
    selectedSubject: String?,
    onSelectSubject: (String?) -> Unit,
    modifier: Modifier = Modifier,
    filterHistoryEnabled: Boolean = false,
    onToggleFilterHistory: ((Boolean) -> Unit)? = null,
    onQuickPromptClick: ((String) -> Unit)? = null,
    showActiveBanner: Boolean = true
) {
    var showSubjectSheet by remember { mutableStateOf(false) }

    // Active item resolved
    val activeFilterItem = remember(selectedSubject) {
        BcaSubjectCatalog.findFilterByNameOrCode(selectedSubject)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // 1. Horizontal Quick Subject Filter Chips
        BcaSubjectFilterChips(
            selectedSubject = selectedSubject,
            onSelectSubject = onSelectSubject,
            onOpenFullPicker = { showSubjectSheet = true }
        )

        // 2. Active Subject Filter Banner (Shows when a subject filter is applied)
        if (showActiveBanner && !selectedSubject.isNullOrBlank()) {
            ActiveSubjectFilterBanner(
                selectedSubject = selectedSubject,
                filterItem = activeFilterItem,
                filterHistoryEnabled = filterHistoryEnabled,
                onToggleFilterHistory = onToggleFilterHistory,
                onChangeSubject = { showSubjectSheet = true },
                onClearFilter = { onSelectSubject(null) },
                onTopicClick = { topic ->
                    onQuickPromptClick?.invoke("Please explain $topic in $selectedSubject in depth with university exam pointers.")
                }
            )
        }

        // 3. Subject Quick Prompts Row (Optional 1-tap prompts when subject is active)
        if (activeFilterItem != null && onQuickPromptClick != null) {
            SubjectQuickPromptSuggestions(
                filterItem = activeFilterItem,
                onPromptClick = onQuickPromptClick
            )
        }
    }

    // Modal Bottom Sheet for full semester-wise subject selection
    if (showSubjectSheet) {
        BcaSubjectSelectionSheet(
            selectedSubject = selectedSubject,
            onSelectSubject = { subjectName ->
                onSelectSubject(subjectName)
                showSubjectSheet = false
            },
            onDismiss = { showSubjectSheet = false }
        )
    }
}

/**
 * A horizontal scrolling row of stylish Material 3 chips allowing instant selection
 * of core BCA subjects (All, Programming, OS, Mathematics, DPA, etc.).
 */
@Composable
fun BcaSubjectFilterChips(
    selectedSubject: String?,
    onSelectSubject: (String?) -> Unit,
    onOpenFullPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // "All Subjects" Chip (Resets AI Filter)
        val isAllSelected = selectedSubject.isNullOrBlank()
        val allBgColor by animateColorAsState(if (isAllSelected) SagePrimary else Color.White, label = "all_bg")
        val allTextColor by animateColorAsState(if (isAllSelected) Color.White else SageDark, label = "all_text")

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = allBgColor,
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = if (isAllSelected) SagePrimary else Color(0xFFE2DDD2)
            ),
            shadowElevation = if (isAllSelected) 2.dp else 0.dp,
            modifier = Modifier
                .clickable { onSelectSubject(null) }
                .testTag("subject_chip_all")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "All Subjects",
                    tint = if (isAllSelected) Color.White else SagePrimary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "All Subjects",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium,
                        color = allTextColor,
                        fontSize = 11.5.sp
                    )
                )
            }
        }

        // Top BCA Subjects (Programming, OS, Mathematics, DPA, Networks, etc.)
        BcaSubjectCatalog.filters.forEach { item ->
            val isSelected = selectedSubject.equals(item.displayName, ignoreCase = true) ||
                             selectedSubject.equals(item.shortName, ignoreCase = true) ||
                             selectedSubject.equals(item.id, ignoreCase = true)

            val bgColor by animateColorAsState(if (isSelected) SagePrimary else Color.White, label = "chip_bg_${item.id}")
            val textColor by animateColorAsState(if (isSelected) Color.White else SageDark, label = "chip_text_${item.id}")

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = bgColor,
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = if (isSelected) SagePrimary else Color(0xFFE2DDD2)
                ),
                shadowElevation = if (isSelected) 2.dp else 0.dp,
                modifier = Modifier
                    .clickable {
                        if (isSelected) {
                            onSelectSubject(null) // toggle off
                        } else {
                            onSelectSubject(item.displayName)
                        }
                    }
                    .testTag("subject_chip_${item.id}")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(text = item.emoji, fontSize = 12.sp)
                    Text(
                        text = item.shortName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = textColor,
                            fontSize = 11.5.sp
                        )
                    )
                }
            }
        }

        // "More / Browse All" Button
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = CreamSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, LavenderPrimary.copy(alpha = 0.4f)),
            modifier = Modifier
                .clickable { onOpenFullPicker() }
                .testTag("btn_browse_all_subjects")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = "Browse All Subjects",
                    tint = LavenderPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Sem 1-6 ▾",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = LavenderPrimary,
                        fontSize = 11.5.sp
                    )
                )
            }
        }
    }
}

/**
 * Animated Banner displaying the active AI subject filter state,
 * showing course code, focus guidance, quick topic pills, and reset buttons.
 */
@Composable
fun ActiveSubjectFilterBanner(
    selectedSubject: String,
    filterItem: BcaSubjectFilterItem?,
    filterHistoryEnabled: Boolean,
    onToggleFilterHistory: ((Boolean) -> Unit)?,
    onChangeSubject: () -> Unit,
    onClearFilter: () -> Unit,
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp)
                .shadow(2.dp, RoundedCornerShape(18.dp), spotColor = Color(0x1A000000))
                .border(1.2.dp, SagePrimary.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                .testTag("active_subject_filter_banner")
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(SageContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = filterItem?.icon ?: Icons.Outlined.School,
                                contentDescription = null,
                                tint = SageDark,
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF2E7D32))
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "AI Subject Filter Active",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = SageDark,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.5.sp,
                                        letterSpacing = 0.3.sp
                                    )
                                )
                            }
                            Text(
                                text = filterItem?.displayName ?: selectedSubject,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E2822),
                                    fontSize = 13.5.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Action buttons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SageLight,
                            modifier = Modifier
                                .clickable { onChangeSubject() }
                                .testTag("btn_change_subject")
                        ) {
                            Text(
                                text = "Switch",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = SageDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF5EFE6),
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { onClearFilter() }
                                .testTag("btn_clear_subject_filter")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear Subject Filter",
                                    tint = Color(0xFF7A6B5D),
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }
                }

                // AI Response Persona Subtitle
                Text(
                    text = "🎯 Nesty AI will tailor answers to this subject's syllabus, algorithms, code syntax, and university exam pointers.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF55635C),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                )

                // Quick Topic Filter Chips (if available)
                val topics = filterItem?.topics ?: emptyList()
                if (topics.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        topics.take(4).forEach { topic ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CreamBase,
                                border = androidx.compose.foundation.BorderStroke(0.6.dp, Color(0xFFDED8CD)),
                                modifier = Modifier.clickable { onTopicClick(topic) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Lightbulb,
                                        contentDescription = null,
                                        tint = PeachAccent,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Text(
                                        text = topic,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFF3B4842),
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Optional History Filter Switch
                if (onToggleFilterHistory != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF7F5F0))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Filter visible chat history to $selectedSubject",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF637069),
                                fontSize = 10.5.sp
                            )
                        )
                        Switch(
                            checked = filterHistoryEnabled,
                            onCheckedChange = onToggleFilterHistory,
                            modifier = Modifier
                                .size(width = 36.dp, height = 22.dp)
                                .testTag("switch_filter_history"),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SagePrimary,
                                uncheckedTrackColor = Color(0xFFDDD8CE)
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * 1-Tap Quick Prompt Suggestions tailored specifically for the selected subject.
 */
@Composable
fun SubjectQuickPromptSuggestions(
    filterItem: BcaSubjectFilterItem,
    onPromptClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val prompts = filterItem.quickPrompts.take(2)
    if (prompts.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        prompts.forEach { promptText ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(0.8.dp, Color(0xFFE5DFC)),
                shadowElevation = 0.5.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onPromptClick(promptText) }
                    .testTag("quick_prompt_${filterItem.id}")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        Text(text = "💡", fontSize = 12.sp)
                        Text(
                            text = promptText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF384640),
                                fontSize = 11.5.sp,
                                lineHeight = 15.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Ask Nesty",
                        tint = SagePrimary,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

/**
 * Full-featured Modal Bottom Sheet providing complete semester-wise subject selection
 * (Semesters 1 through 6) with search, course codes, syllabus topics, and category filters.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BcaSubjectSelectionSheet(
    selectedSubject: String?,
    onSelectSubject: (String?) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }
    var selectedSemesterTab by remember { mutableIntStateOf(0) } // 0 = All, 1-6 = Sem 1-6

    // Aggregate all official subjects from knowledge base + special clusters
    val officialSubjects = remember { BcaKnowledgeBase.subjects }

    val filteredSubjects = remember(searchQuery, selectedSemesterTab) {
        officialSubjects.filter { subj ->
            val matchesSem = selectedSemesterTab == 0 || subj.semester == selectedSemesterTab
            val matchesSearch = searchQuery.isBlank() ||
                subj.name.contains(searchQuery, ignoreCase = true) ||
                subj.code.contains(searchQuery, ignoreCase = true) ||
                subj.topics.any { it.contains(searchQuery, ignoreCase = true) }
            matchesSem && matchesSearch
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CreamBase,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = modifier.testTag("sheet_bca_subject_selector")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Select BCA Subject Filter",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = SageDark,
                            fontSize = 18.sp
                        )
                    )
                    Text(
                        text = "Filter Nesty AI responses to your specific syllabus",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF6B7871),
                            fontSize = 12.sp
                        )
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text("Search subjects, course codes, or topics...", fontSize = 12.5.sp, color = Color.Gray)
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search", tint = SagePrimary)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = SagePrimary,
                    unfocusedBorderColor = Color(0xFFE0DBD0)
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_search_subject")
            )

            // Primary Shortcuts: Programming, OS, Mathematics, DPA
            Text(
                text = "Core Subject Domains:",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = SageDark,
                    fontSize = 11.sp
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // "Reset to All" Option
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (selectedSubject.isNullOrBlank()) SageContainer else Color.White,
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, Color(0xFFDED8CD)),
                    modifier = Modifier.clickable { onSelectSubject(null) }
                ) {
                    Text(
                        text = "🌟 All Subjects",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SageDark,
                            fontWeight = if (selectedSubject.isNullOrBlank()) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.5.sp
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                // Core Subject Domain Chips
                listOf(
                    "Programming" to "💻 Programming",
                    "Operating Systems" to "⚙️ OS",
                    "Discrete Mathematics" to "📐 Mathematics",
                    "DPA" to "🗄️ DPA",
                    "Computer Networks" to "🌐 Networks"
                ).forEach { (canonicalName, label) ->
                    val isChosen = selectedSubject.equals(canonicalName, ignoreCase = true) ||
                                   (canonicalName == "Programming" && selectedSubject?.contains("Program", ignoreCase = true) == true) ||
                                   (canonicalName == "Operating Systems" && selectedSubject?.contains("OS", ignoreCase = true) == true) ||
                                   (canonicalName == "Discrete Mathematics" && selectedSubject?.contains("Math", ignoreCase = true) == true)

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isChosen) SagePrimary else Color.White,
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, if (isChosen) SagePrimary else Color(0xFFDED8CD)),
                        modifier = Modifier.clickable { onSelectSubject(canonicalName) }
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isChosen) Color.White else SageDark,
                                fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.5.sp
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Semester Filter Tabs (All, Sem 1 to Sem 6)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val semLabels = listOf("All Semesters", "Sem 1", "Sem 2", "Sem 3", "Sem 4", "Sem 5", "Sem 6")
                semLabels.forEachIndexed { index, semTitle ->
                    val isTabSelected = selectedSemesterTab == index
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isTabSelected) LavenderPrimary else Color(0xFFF0EBE0),
                        modifier = Modifier.clickable { selectedSemesterTab = index }
                    ) {
                        Text(
                            text = semTitle,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isTabSelected) Color.White else Color(0xFF5A524A),
                                fontWeight = if (isTabSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            // Subject Cards List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (filteredSubjects.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 30.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "🔍", fontSize = 24.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "No matching BCA subjects found",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                            )
                        }
                    }
                }

                items(filteredSubjects, key = { it.id }) { subj ->
                    val isSelected = selectedSubject.equals(subj.name, ignoreCase = true) ||
                                     selectedSubject.equals(subj.id, ignoreCase = true)

                    SubjectSelectionCard(
                        subject = subj,
                        isSelected = isSelected,
                        onClick = { onSelectSubject(subj.name) }
                    )
                }
            }
        }
    }
}

/**
 * Individual subject row card inside the modal sheet.
 */
@Composable
private fun SubjectSelectionCard(
    subject: com.example.data.model.BcaSubject,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) SageContainer.copy(alpha = 0.5f) else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.5.dp else 0.8.dp,
            color = if (isSelected) SagePrimary else Color(0xFFE2DDD2)
        ),
        shadowElevation = if (isSelected) 2.dp else 0.5.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("subject_card_${subject.id}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Icon Box
                val icon = when (subject.iconName) {
                    "code" -> Icons.Outlined.Code
                    "calculate" -> Icons.Outlined.Calculate
                    "memory" -> Icons.Outlined.Memory
                    "storage" -> Icons.Outlined.Storage
                    "hub" -> Icons.Outlined.Hub
                    "developer_board" -> Icons.Outlined.DeveloperBoard
                    "psychology" -> Icons.Outlined.Psychology
                    "security" -> Icons.Outlined.Security
                    "cloud" -> Icons.Outlined.Cloud
                    "architecture" -> Icons.Outlined.Architecture
                    else -> Icons.Outlined.School
                }

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) SagePrimary else SageLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) Color.White else SageDark,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = LavenderContainer
                        ) {
                            Text(
                                text = "${subject.code} • Sem ${subject.semester}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = LavenderPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.5.sp
                                ),
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = subject.name,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) SageDark else Color(0xFF1E2822),
                            fontSize = 13.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Topic snippet
                    if (subject.topics.isNotEmpty()) {
                        Text(
                            text = subject.topics.take(2).joinToString(" • "),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF75837B),
                                fontSize = 10.5.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Radio / Checkbox Indicator
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) SagePrimary else Color.Transparent)
                    .border(
                        1.2.dp,
                        if (isSelected) SagePrimary else Color(0xFFB8B1A5),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}

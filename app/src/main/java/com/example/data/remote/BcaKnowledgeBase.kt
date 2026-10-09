package com.example.data.remote

import com.example.data.model.BcaSubject
import com.example.data.model.ExamPaper
import com.example.data.model.ExamQuestion
import com.example.data.model.FlashcardEntity
import com.example.data.model.McqQuestion

object BcaKnowledgeBase {

    val subjects: List<BcaSubject> = listOf(
        BcaSubject(
            id = "c_prog",
            name = "C Programming & Problem Solving",
            code = "BCA 101",
            semester = 1,
            iconName = "code",
            topics = listOf("Pointers & Dynamic Memory", "Structures & Unions", "File I/O", "Recursion", "Bitwise Operations")
        ),
        BcaSubject(
            id = "discrete_math",
            name = "Discrete Mathematics",
            code = "BCA 102",
            semester = 1,
            iconName = "calculate",
            topics = listOf("Set Theory", "Propositional Logic & Truth Tables", "Relations & Functions", "Graph Theory & Trees", "Boolean Algebra")
        ),
        BcaSubject(
            id = "dsa",
            name = "Data Structures & Algorithms",
            code = "BCA 201",
            semester = 2,
            iconName = "account_tree",
            topics = listOf("Linked Lists & Stacks", "Queues & Circular Queues", "Binary Trees & BST", "AVL Trees & Heaps", "Sorting & Graph Traversals")
        ),
        BcaSubject(
            id = "coa",
            name = "Computer Organization & Architecture",
            code = "BCA 202",
            semester = 2,
            iconName = "developer_board",
            topics = listOf("Logic Gates & Karnaugh Maps", "ALU & Register Transfer", "Instruction Cycle & Pipelining", "Memory Hierarchy & Cache", "I/O Organization & Interrupts")
        ),
        BcaSubject(
            id = "dpa",
            name = "DPA",
            code = "BCA 301",
            semester = 3,
            iconName = "storage",
            topics = listOf("ER Diagrams & Relational Model", "Normalization (1NF to BCNF)", "SQL Joins & Subqueries", "Transactions & ACID Properties", "Concurrency & Indexing")
        ),
        BcaSubject(
            id = "os",
            name = "Operating Systems",
            code = "BCA 302",
            semester = 3,
            iconName = "memory",
            topics = listOf("Process Scheduling (FCFS, SJF, RR)", "Deadlocks & Bankers Algorithm", "Virtual Memory & Paging", "Page Replacement (FIFO, LRU)", "File Systems & Disk Scheduling")
        ),
        BcaSubject(
            id = "java",
            name = "Java Programming & OOP",
            code = "BCA 303",
            semester = 3,
            iconName = "coffee",
            topics = listOf("OOP Principles (Inheritance, Polymorphism)", "Exception Handling & Multithreading", "Java Collections Framework", "JDBC & Database Connectivity", "Streams & Lambda")
        ),
        BcaSubject(
            id = "networks",
            name = "Computer Networks",
            code = "BCA 401",
            semester = 4,
            iconName = "hub",
            topics = listOf("OSI 7 Layers vs TCP/IP", "IP Addressing & Subnetting", "Routing Protocols (Distance Vector, Link State)", "TCP vs UDP & Flow Control", "DNS, HTTP, SSL/TLS")
        ),
        BcaSubject(
            id = "web_dev",
            name = "Web Technologies & JavaScript",
            code = "BCA 402",
            semester = 4,
            iconName = "language",
            topics = listOf("DOM Manipulation & ES6", "Async/Await & Promises", "REST APIs & JSON", "Node.js & Express Basics", "Full-Stack Architecture")
        ),
        BcaSubject(
            id = "ai_ml",
            name = "Artificial Intelligence & Data Science",
            code = "BCA 501",
            semester = 5,
            iconName = "psychology",
            topics = listOf("Search Algorithms (A*, BFS, DFS)", "Machine Learning Basics (Supervised/Unsupervised)", "Neural Networks & Backpropagation", "Python for Data Analysis (Pandas/NumPy)", "NLP & LLM Concepts")
        ),
        BcaSubject(
            id = "software_eng",
            name = "Software Engineering & Agile",
            code = "BCA 502",
            semester = 5,
            iconName = "architecture",
            topics = listOf("SDLC Models (Waterfall, Spiral, Agile Scrum)", "SRS Documentation", "Testing (Black Box vs White Box)", "UML Class & Sequence Diagrams", "CI/CD & DevOps Basics")
        ),
        BcaSubject(
            id = "cyber_sec",
            name = "Cyber Security & Cryptography",
            code = "BCA 601",
            semester = 6,
            iconName = "security",
            topics = listOf("Symmetric vs Asymmetric Encryption (RSA, AES)", "Digital Signatures & PKI", "Network Attacks & Firewalls", "SQL Injection & XSS Mitigation", "Cyber Laws & IT Act India")
        ),
        BcaSubject(
            id = "cloud_comp",
            name = "Cloud Computing & DevOps",
            code = "BCA 602",
            semester = 6,
            iconName = "cloud",
            topics = listOf("IaaS, PaaS, SaaS Architectures", "Virtualization & Containers (Docker)", "Kubernetes Orchestration", "Serverless Computing", "Cloud Security & Multi-tenancy")
        )
    )

    val defaultFlashcards: List<FlashcardEntity> = listOf(
        FlashcardEntity(
            subject = "DPA",
            semester = 3,
            question = "What is the difference between 3NF and BCNF?",
            answer = "For every functional dependency X → Y:\nIn 3NF, either X is a super key OR Y is a prime attribute (part of a candidate key).\nIn BCNF (Boyce-Codd Normal Form), X MUST be a super key. BCNF strictly eliminates all anomalies caused by functional dependencies.",
            keyConcept = "Database Normalization"
        ),
        FlashcardEntity(
            subject = "Data Structures",
            semester = 2,
            question = "Compare QuickSort and MergeSort time & space complexities.",
            answer = "• MergeSort: Best/Avg/Worst O(n log n). Auxiliary Space: O(n). Stable sorting.\n• QuickSort: Best/Avg O(n log n), Worst O(n²) when pivot is poor. Space: O(log n) call stack. Unstable. Often faster in-memory.",
            keyConcept = "Divide and Conquer Sorting"
        ),
        FlashcardEntity(
            subject = "Operating Systems",
            semester = 3,
            question = "State the 4 necessary conditions for Deadlock.",
            answer = "1. Mutual Exclusion: Resources cannot be shared.\n2. Hold and Wait: Process holds one resource while requesting another.\n3. No Preemption: Resources cannot be forcibly taken.\n4. Circular Wait: Circular chain of processes each waiting for resource held by next.",
            keyConcept = "Coffman Conditions"
        ),
        FlashcardEntity(
            subject = "Computer Networks",
            semester = 4,
            question = "What is the purpose of ARP and how does it work?",
            answer = "ARP (Address Resolution Protocol) maps a known 32-bit IPv4 address to a 48-bit physical MAC address on a local network using broadcast requests and unicast replies.",
            keyConcept = "Data Link Layer Mapping"
        ),
        FlashcardEntity(
            subject = "Discrete Mathematics",
            semester = 1,
            question = "What is a Tautology vs Contradiction?",
            answer = "• Tautology: A compound proposition that is always TRUE regardless of the truth values of its variables (e.g., P ∨ ¬P).\n• Contradiction: A proposition that is always FALSE under all assignments (e.g., P ∧ ¬P).",
            keyConcept = "Propositional Logic"
        ),
        FlashcardEntity(
            subject = "Java",
            semester = 3,
            question = "Why is Java platform independent, but JVM is platform dependent?",
            answer = "Java source compiles into architecture-neutral bytecode (.class). However, each operating system requires a custom JVM built for its specific CPU instruction set to translate bytecode into native machine code.",
            keyConcept = "JVM & Bytecode Architecture"
        )
    )

    val sampleMcqs: List<McqQuestion> = listOf(
        McqQuestion(
            subject = "DPA",
            question = "Which normal form deals with multivalued dependencies (MVD)?",
            options = listOf("2NF", "3NF", "BCNF", "4NF"),
            correctIndex = 3,
            explanation = "4NF (Fourth Normal Form) specifically ensures that every non-trivial multivalued dependency X ↠ Y has X as a superkey."
        ),
        McqQuestion(
            subject = "Data Structures",
            question = "What is the worst-case time complexity of searching in a Binary Search Tree (BST)?",
            options = listOf("O(1)", "O(log n)", "O(n)", "O(n log n)"),
            correctIndex = 2,
            explanation = "In an unbalanced or skewed BST (e.g. elements inserted in sorted order), the tree degenerates into a linked list giving O(n) search."
        ),
        McqQuestion(
            subject = "Operating Systems",
            question = "Which CPU scheduling algorithm can suffer from the 'Convoy Effect'?",
            options = listOf("Round Robin", "FCFS (First-Come, First-Served)", "Shortest Job First (Preemptive)", "Priority Scheduling"),
            correctIndex = 1,
            explanation = "The Convoy effect occurs in FCFS when a long CPU-bound process blocks all subsequent short I/O-bound processes."
        ),
        McqQuestion(
            subject = "Computer Networks",
            question = "Which layer of the OSI model is responsible for end-to-end reliability and flow control?",
            options = listOf("Network Layer", "Data Link Layer", "Transport Layer", "Session Layer"),
            correctIndex = 2,
            explanation = "The Transport layer (Layer 4, e.g. TCP) provides end-to-end communication, segmentation, error recovery, and flow control."
        )
    )

    val pastYearPapers: List<ExamPaper> = listOf(
        ExamPaper(
            id = "sem3_dpa_2025",
            subject = "DPA",
            semester = 3,
            year = "2025 Regular",
            university = "Semester Exam",
            questions = listOf(
                ExamQuestion(
                    id = "q1",
                    marks = 10,
                    unit = "Unit 2: Relational Model & Normalization",
                    questionText = "Explain 1NF, 2NF, 3NF and BCNF with a practical Student-Course-Lecturer example table showing anomalies before and after normalization.",
                    modelAnswer = "1. First Normal Form (1NF): Eliminates repeating groups and ensures atomic attributes.\n2. Second Normal Form (2NF): Must be in 1NF and have NO partial dependency (every non-prime attribute depends on full candidate key).\n3. Third Normal Form (3NF): Must be in 2NF and have NO transitive dependencies (X → Y, Y → Z).\n4. BCNF: Stricter version where for every functional dependency X → Y, X must be a superkey.",
                    examFrequency = "Repeated in 2022, 2023, 2024, 2025 (Highest Priority)"
                ),
                ExamQuestion(
                    id = "q2",
                    marks = 10,
                    unit = "Unit 3: Transactions & Concurrency",
                    questionText = "Discuss ACID properties in detail. How does Two-Phase Locking (2PL) protocol guarantee serializability?",
                    modelAnswer = "• Atomicity: All or nothing.\n• Consistency: Database remains in valid state.\n• Isolation: Concurrent transactions do not interfere.\n• Durability: Committed updates survive system crashes.\n\n2PL has two phases:\n1. Growing Phase: Locks acquired, none released.\n2. Shrinking Phase: Locks released, none acquired.\nGuarantees conflict serializability but can cause deadlocks.",
                    examFrequency = "Appeared 5 times in last 6 years"
                )
            )
        ),
        ExamPaper(
            id = "sem2_dsa_2025",
            subject = "Data Structures & Algorithms",
            semester = 2,
            year = "2025 Annual",
            university = "Semester Exam",
            questions = listOf(
                ExamQuestion(
                    id = "dsa_q1",
                    marks = 10,
                    unit = "Unit 3: Trees & Graphs",
                    questionText = "Write algorithms for Inorder, Preorder, and Postorder tree traversals both recursively and iteratively using a Stack. Construct a BST from: 45, 15, 79, 90, 10, 55, 12, 20, 50.",
                    modelAnswer = "Recursive Traversals:\n• Inorder (Left, Root, Right) -> yields ascending order for BST.\n• Preorder (Root, Left, Right) -> useful for cloning trees.\n• Postorder (Left, Right, Root) -> useful for deleting/evaluating syntax trees.\n\nIterative uses explicit java.util.Stack.",
                    examFrequency = "Core Syllabus Question (High Priority)"
                )
            )
        )
    )
}

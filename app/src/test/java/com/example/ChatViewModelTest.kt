package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.AttachmentInfo
import com.example.data.model.AttachmentType
import com.example.viewmodel.BcaQuestionCategory
import com.example.viewmodel.ChatViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ChatViewModelTest {

    private lateinit var app: Application
    private lateinit var viewModel: ChatViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        app = ApplicationProvider.getApplicationContext()
        viewModel = ChatViewModel(app)
        testDispatcher.scheduler.advanceUntilIdle()
        ShadowLooper.idleMainLooper()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialCategoryAndQuickPrompts() {
        assertEquals(BcaQuestionCategory.ALL, viewModel.activeCategory.value)
        val initialPrompts = viewModel.quickPrompts.value
        assertTrue(initialPrompts.isNotEmpty())

        // Switch to PROGRAMMING category
        viewModel.setCategory(BcaQuestionCategory.PROGRAMMING)
        assertEquals(BcaQuestionCategory.PROGRAMMING, viewModel.activeCategory.value)
        assertTrue(viewModel.quickPrompts.value.all { it.category == BcaQuestionCategory.PROGRAMMING })

        // Switch to THEORY category
        viewModel.setCategory(BcaQuestionCategory.THEORY)
        assertEquals(BcaQuestionCategory.THEORY, viewModel.activeCategory.value)
        assertTrue(viewModel.quickPrompts.value.all { it.category == BcaQuestionCategory.THEORY })
    }

    @Test
    fun testSubjectFiltering() {
        assertEquals(null, viewModel.selectedSubject.value)

        // Select Operating Systems
        viewModel.setSubject("Operating Systems")
        assertEquals("Operating Systems", viewModel.selectedSubject.value)
        assertTrue(viewModel.quickPrompts.value.any { it.subject.contains("Operating", ignoreCase = true) || it.subject == "OS" })

        // Toggle history filtering
        viewModel.setFilterHistoryBySubject(true)
        assertTrue(viewModel.filterHistoryBySubject.value)

        // Reset subject
        viewModel.setSubject(null)
        assertEquals(null, viewModel.selectedSubject.value)
    }

    @Test
    fun testAddAndRemoveAttachment() {
        val testAttachment = AttachmentInfo(
            id = "test_doc_1",
            fileName = "BinarySearchTree.java",
            fileType = AttachmentType.CODE,
            sizeText = "4 KB"
        )

        viewModel.addAttachment(testAttachment)
        assertEquals(1, viewModel.pendingAttachments.value.size)
        assertEquals("BinarySearchTree.java", viewModel.pendingAttachments.value[0].fileName)

        viewModel.removeAttachment("test_doc_1")
        assertEquals(0, viewModel.pendingAttachments.value.size)
    }

    @Test
    fun testSendMessageAddsMessagesToChat() = runTest(testDispatcher) {
        backgroundScope.launch { viewModel.chatMessages.collect {} }

        viewModel.sendMessage("Explain DPA 1NF to BCNF Normalization")

        var found = false
        for (i in 0 until 40) {
            testDispatcher.scheduler.advanceUntilIdle()
            ShadowLooper.idleMainLooper()
            if (viewModel.chatMessages.value.any { it.text.contains("Normalization") }) {
                found = true
                break
            }
            Thread.sleep(50)
        }

        val messages = viewModel.chatMessages.value
        assertNotNull(messages)
        assertTrue("Expected message containing Normalization in chat messages, found: $messages", found)
    }
}

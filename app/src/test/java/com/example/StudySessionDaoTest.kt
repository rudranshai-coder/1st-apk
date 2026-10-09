package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.MindNestDatabase
import com.example.data.local.StudySessionDao
import com.example.data.model.StudySessionEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class StudySessionDaoTest {

    private lateinit var db: MindNestDatabase
    private lateinit var studySessionDao: StudySessionDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, MindNestDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        studySessionDao = db.studySessionDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndQueryStudySessionsForDpaOsAndMath() = runBlocking {
        val dpaSession = StudySessionEntity(
            id = "test_dpa_1",
            subject = "DPA",
            topic = "Normalization (1NF, 2NF, 3NF, BCNF)",
            durationMinutes = 90,
            notes = "Solved functional dependency decomposition",
            focusRating = 5
        )
        val osSession = StudySessionEntity(
            id = "test_os_1",
            subject = "Operating Systems",
            topic = "Banker's Algorithm & Deadlocks",
            durationMinutes = 60,
            notes = "Practiced resource allocation matrices",
            focusRating = 4
        )
        val mathSession = StudySessionEntity(
            id = "test_math_1",
            subject = "Discrete Mathematics",
            topic = "Propositional Logic & Tautologies",
            durationMinutes = 75,
            notes = "Mastered truth tables and De Morgan's laws",
            focusRating = 5
        )

        studySessionDao.insertSession(dpaSession)
        studySessionDao.insertSession(osSession)
        studySessionDao.insertSession(mathSession)

        // 1. Verify all sessions returned
        val allSessions = studySessionDao.getAllSessions().first()
        assertEquals(3, allSessions.size)

        // 2. Verify filter by subject DPA
        val dpaSessions = studySessionDao.getSessionsBySubject("DPA").first()
        assertEquals(1, dpaSessions.size)
        assertEquals("DPA", dpaSessions[0].subject)
        assertEquals(90, dpaSessions[0].durationMinutes)

        // 3. Verify total minutes query
        val totalMinutesDpa = studySessionDao.getTotalMinutesBySubject("DPA").first()
        assertEquals(90, totalMinutesDpa)

        val totalMinutesAll = studySessionDao.getTotalMinutesAll().first()
        assertEquals(225, totalMinutesAll)

        // 4. Verify aggregated subject study stats
        val stats = studySessionDao.getSubjectStudyStats().first()
        assertEquals(3, stats.size)
        val dpaStat = stats.find { it.subject == "DPA" }
        assertNotNull(dpaStat)
        assertEquals(90, dpaStat?.totalMinutes)
        assertEquals(1, dpaStat?.sessionCount)

        // 5. Delete session
        studySessionDao.deleteSessionById("test_dpa_1")
        val remaining = studySessionDao.getAllSessions().first()
        assertEquals(2, remaining.size)
        assertTrue(remaining.none { it.id == "test_dpa_1" })
    }
}

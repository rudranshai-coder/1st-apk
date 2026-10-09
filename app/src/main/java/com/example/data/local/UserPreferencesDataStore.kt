package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * Manages reactive, asynchronous local storage of user session data and preferences
 * using Jetpack DataStore Preferences.
 */
class UserPreferencesDataStore(private val context: Context) {

    private object PreferencesKeys {
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_SEMESTER = intPreferencesKey("user_semester")
        val USER_UNIVERSITY = stringPreferencesKey("user_university")
        val USER_IS_GUEST = booleanPreferencesKey("user_is_guest")
        val STUDY_STREAK = intPreferencesKey("study_streak")
        val QUESTIONS_SOLVED = intPreferencesKey("questions_solved")
        val CUSTOM_API_KEY = stringPreferencesKey("custom_api_key")
    }

    val userSessionFlow: Flow<UserSession> = context.userDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val name = preferences[PreferencesKeys.USER_NAME] ?: "Rudransh Goyal"
            val email = preferences[PreferencesKeys.USER_EMAIL] ?: "rudranshgoyal44@gmail.com"
            val semester = preferences[PreferencesKeys.USER_SEMESTER] ?: 3
            val university = preferences[PreferencesKeys.USER_UNIVERSITY] ?: "BCA Program"
            val isGuest = preferences[PreferencesKeys.USER_IS_GUEST] ?: false
            val streak = preferences[PreferencesKeys.STUDY_STREAK] ?: 14
            val solved = preferences[PreferencesKeys.QUESTIONS_SOLVED] ?: 142

            UserSession(
                name = name,
                email = email,
                semester = semester,
                university = university,
                isGuest = isGuest,
                studyStreakDays = streak,
                questionsSolved = solved
            )
        }

    val customApiKeyFlow: Flow<String> = context.userDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.CUSTOM_API_KEY] ?: ""
        }

    suspend fun saveUserSession(session: UserSession) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = session.name
            preferences[PreferencesKeys.USER_EMAIL] = session.email
            preferences[PreferencesKeys.USER_SEMESTER] = session.semester
            preferences[PreferencesKeys.USER_UNIVERSITY] = session.university
            preferences[PreferencesKeys.USER_IS_GUEST] = session.isGuest
            preferences[PreferencesKeys.STUDY_STREAK] = session.studyStreakDays
            preferences[PreferencesKeys.QUESTIONS_SOLVED] = session.questionsSolved
        }
    }

    suspend fun saveCustomApiKey(key: String) {
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.CUSTOM_API_KEY] = key
        }
    }
}

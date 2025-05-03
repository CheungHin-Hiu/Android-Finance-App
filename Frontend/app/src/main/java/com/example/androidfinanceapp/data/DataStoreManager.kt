package com.example.androidfinanceapp.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

// Datastore manager used to store and access user external id and JWT token received
class DataStoreManager(private val context: Context) {

    companion object {
        val EXTERNAL_ID_KEY = stringPreferencesKey("external_id")
        val TOKEN_KEY = stringPreferencesKey("token")
    }

    val externalIdFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[EXTERNAL_ID_KEY]
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }

    suspend fun saveLoginData(externalId: String, token: String) {
        context.dataStore.edit { preferences ->
            preferences[EXTERNAL_ID_KEY] = externalId
            preferences[TOKEN_KEY] = token
        }
    }
}
package cn.edu.seu.travelapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStorage(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("Token")
        private val USER_TOKEN_KEY = stringPreferencesKey("token")
        private val UNAME = stringPreferencesKey("uname")
        private val UPIC = stringPreferencesKey("upic")
    }

    val getAccessToken: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_TOKEN_KEY] ?: ""
    }

    val getCurrentUname: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[UNAME] ?: ""
    }

    val getCurrentUpic: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[UPIC] ?: ""
    }

    suspend fun saveCurrentUname(uname: String) {
        context.dataStore.edit { preferences ->
            preferences[UNAME] = uname
        }
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_TOKEN_KEY] = token
        }
    }

    suspend fun saveCurrentUpic(upic: String) {
        context.dataStore.edit { preferences ->
            preferences[UPIC] = upic
        }
    }

    suspend fun clearToken() = context.dataStore.edit {
        it.clear()
    }
}

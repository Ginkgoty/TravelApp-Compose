/**
 * TokenStorage.kt
 *
 * This file is about storage of token.
 * @author Li Jiawen
 * @mail   nmjbh@qq.com
 *
 */
package cn.edu.seu.travelapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TokenStorage(private val context: Context) {
    companion object {
        /**
         * Store a token in shared preferences
         */
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("Token")
        private val USER_TOKEN_KEY = stringPreferencesKey("token")
        private val UNAME = stringPreferencesKey("uname")
    }

    /**
     * get access token.
     */
    val getAccessToken: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_TOKEN_KEY] ?: ""
    }

    /**
     * get current user name
     */
    val getCurrentUname: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[UNAME] ?: ""
    }

    /**
     * save current user name
     */
    suspend fun saveCurrentUname(uname: String) {
        context.dataStore.edit { preferences ->
            preferences[UNAME] = uname
        }
    }

    /**
     * save token when sign-up/sign-in
     */
    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_TOKEN_KEY] = token
        }
    }

    /**
     * clear token in local storage
     */
    suspend fun clearToken() = context.dataStore.edit {
        it.clear()
    }
}

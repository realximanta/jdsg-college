package in.edu.jdsgcollege.bokakhat.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import in.edu.jdsgcollege.bokakhat.data.remote.User

class SessionManager(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val gson = Gson()

    fun saveSession(token: String, user: User) {
        sharedPreferences.edit()
            .putString("jwt_token", token)
            .putString("user_data", gson.toJson(user))
            .apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("jwt_token", null)
    }

    fun getUser(): User? {
        val userJson = sharedPreferences.getString("user_data", null) ?: return null
        return gson.fromJson(userJson, User::class.java)
    }

    fun clearSession() {
        sharedPreferences.edit().clear().apply()
    }
}

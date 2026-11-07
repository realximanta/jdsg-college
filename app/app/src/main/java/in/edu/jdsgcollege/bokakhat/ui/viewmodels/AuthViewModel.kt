package in.edu.jdsgcollege.bokakhat.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import in.edu.jdsgcollege.bokakhat.data.local.SessionManager
import in.edu.jdsgcollege.bokakhat.data.remote.RetrofitClient
import in.edu.jdsgcollege.bokakhat.data.remote.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val sessionManager = SessionManager(application)

    private val _currentUser = MutableStateFlow<User?>(sessionManager.getUser())
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Call this after a successful login/signup API call
    fun updateSession(token: String, user: User) {
        sessionManager.saveSession(token, user)
        _currentUser.value = user
    }

    fun logout() {
        sessionManager.clearSession()
        _currentUser.value = null
    }
    
    // Placeholder for fetching fresh profile data
    fun refreshProfile() {
        val token = sessionManager.getToken() ?: return
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.getUserProfile("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    sessionManager.saveSession(token, user)
                    _currentUser.value = user
                }
            } catch (e: Exception) {
                // Handle network error silently or log
            }
        }
    }
}

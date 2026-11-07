package in.edu.jdsgcollege.bokakhat.data.remote

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

// Matches backend models.AuthRequest
data class AuthRequest(val id_token: String)

// Matches backend map response in auth.go
data class AuthResponse(
    val exists: Boolean? = null, 
    val token: String? = null, 
    val user: User? = null, 
    val email: String? = null, 
    val name: String? = null
)

// Matches backend models.User
data class User(
    val id: String,
    val email: String,
    val full_name: String,
    val role: String,
    @SerializedName("class") val class_name: String? = null,
    val semester: String? = null,
    val roll_no: String? = null,
    val emergency_contact: String? = null,
    val department: String? = null,
    val designation: String? = null,
    val phone: String,
    val profile_pic_url: String,
    val created_at: String? = null,
    val updated_at: String? = null
)

// Matches backend handlers.Notification
data class Notification(val title: String, val url: String, val date: String)

interface ApiService {
    @POST("api/auth/google")
    suspend fun googleLogin(@Body request: AuthRequest): Response<AuthResponse>

    @GET("api/users/me")
    suspend fun getUserProfile(@Header("Authorization") token: String): Response<User>

    @PUT("api/users/me")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body user: User
    ): Response<User>

    @Multipart
    @POST("api/users/me/avatar")
    suspend fun uploadAvatar(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Response<User>

    @GET("api/notifications")
    suspend fun getNotifications(): Response<List<Notification>>
}

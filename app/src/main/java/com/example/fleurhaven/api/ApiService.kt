import com.example.fleurhaven.models.SignupRequest
import com.example.fleurhaven.models.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("signup.php")
    fun registerUser(
        @Body request: SignupRequest
    ): Call<UserResponse>

    @POST("login.php")
    fun loginUser(@Body request: SignupRequest): Call<UserResponse>
}

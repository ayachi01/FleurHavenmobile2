import com.example.fleurhaven.models.SignupRequest
import com.example.fleurhaven.models.UserResponse
import com.example.fleurhaven.models.ApiResponse
import com.example.fleurhaven.models.UpdateAddressRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @POST("signup.php")
    fun registerUser(@Body request: SignupRequest): Call<UserResponse>

    @POST("login.php")
    fun loginUser(@Body request: SignupRequest): Call<UserResponse>

    @Headers("Content-Type: application/json") // Ensures JSON request format
    @POST("update_address.php")
    fun updateAddress(@Body request: UpdateAddressRequest): Call<ApiResponse>
}

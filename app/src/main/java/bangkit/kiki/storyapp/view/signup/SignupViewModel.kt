package bangkit.kiki.storyapp.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bangkit.kiki.storyapp.data.api.ApiConfig
import bangkit.kiki.storyapp.data.data_class.ErrorResponse
import com.google.gson.Gson
import retrofit2.HttpException

class SignupViewModel: ViewModel() {
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean> = _isError

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> = _errorMessage

    suspend fun register(name: String, email: String, password: String): Boolean {
        _isLoading.value = true
        _isError.value = false

        try {
            val response = ApiConfig.getApiService().register(name, email, password)
            if (response.error == false && response.message != "") {
                return true
            }
        } catch (error: HttpException) {
            val jsonInString = error.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)

            _errorMessage.value = errorBody.message
            _isError.value = true
        } finally {
            _isLoading.value = false
        }

        return false
    }
}
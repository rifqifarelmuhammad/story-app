package bangkit.kiki.storyapp.view.addStory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import bangkit.kiki.storyapp.data.api.ApiConfig
import bangkit.kiki.storyapp.data.data_class.AddStoryResponse
import bangkit.kiki.storyapp.data.model.UserModel
import bangkit.kiki.storyapp.data.repository.UserRepository
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class AddStoryViewModel(private val repository: UserRepository): ViewModel() {
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean> = _isError

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> = _errorMessage

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    suspend fun addStory(token: String, multipartBody: MultipartBody.Part, description: RequestBody): Boolean {
        _isLoading.value = true
        _isError.value = false

        try {
            val response = ApiConfig.getApiService().addStory("Bearer $token", multipartBody, description)
            if (!response.error && response.message != "") {
                return true
            }
        } catch (error: HttpException) {
            val jsonInString = error.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, AddStoryResponse::class.java)
            _errorMessage.value = errorBody.message
            _isError.value = true
        } finally {
            _isLoading.value = false
        }

        return false
    }
}
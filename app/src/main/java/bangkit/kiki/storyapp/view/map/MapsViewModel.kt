package bangkit.kiki.storyapp.view.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import bangkit.kiki.storyapp.data.api.ApiConfig
import bangkit.kiki.storyapp.data.data_class.ErrorResponse
import bangkit.kiki.storyapp.data.data_class.ListStoryItem
import bangkit.kiki.storyapp.data.model.UserModel
import bangkit.kiki.storyapp.data.repository.UserRepository
import com.google.gson.Gson
import retrofit2.HttpException

class MapsViewModel(private val repository: UserRepository): ViewModel() {
    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    val listStory: LiveData<List<ListStoryItem>> = _listStory

    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean> = _isError

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> = _errorMessage

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    suspend fun getStories(token: String) {
        _isError.value = false

        try {
            val response = ApiConfig.getApiService().getStories("Bearer $token")
            _listStory.value = response.listStory
        } catch (error: HttpException) {
            val jsonInString = error.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)

            _errorMessage.value = errorBody.message
            _isError.value = true
        }
    }
}
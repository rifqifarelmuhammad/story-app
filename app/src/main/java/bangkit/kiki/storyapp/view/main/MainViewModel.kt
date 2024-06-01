package bangkit.kiki.storyapp.view.main

import androidx.lifecycle.*
import androidx.paging.*
import bangkit.kiki.storyapp.data.data_class.ListStoryItem
import bangkit.kiki.storyapp.data.model.UserModel
import bangkit.kiki.storyapp.data.repository.StoryRepository
import bangkit.kiki.storyapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository, private val storyRepository: StoryRepository): ViewModel() {
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean> = _isError

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> = _errorMessage

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> {
        return storyRepository.getStories(token)
    }
}
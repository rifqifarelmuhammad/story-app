package bangkit.kiki.storyapp.view.welcome

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import bangkit.kiki.storyapp.data.model.UserModel
import bangkit.kiki.storyapp.data.repository.UserRepository

class WelcomeViewModel(private val repository: UserRepository): ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }
}
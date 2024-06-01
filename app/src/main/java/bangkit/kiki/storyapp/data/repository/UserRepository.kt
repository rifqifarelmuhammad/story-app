package bangkit.kiki.storyapp.data.repository

import bangkit.kiki.storyapp.data.context.UserContext
import bangkit.kiki.storyapp.data.model.UserModel
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(private val userContext: UserContext){
    suspend fun saveSession(user: UserModel) {
        userContext.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userContext.getSession()
    }

    suspend fun logout() {
        userContext.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(
            userContext: UserContext
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userContext)
            }.also { instance = it }
    }
}
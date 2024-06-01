package bangkit.kiki.storyapp.config

import android.content.Context
import bangkit.kiki.storyapp.data.context.UserContext
import bangkit.kiki.storyapp.data.context.dataStore
import bangkit.kiki.storyapp.data.repository.UserRepository

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val context = UserContext.getInstance(context.dataStore)
        return UserRepository.getInstance(context)
    }
}
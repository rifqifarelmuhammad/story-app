package bangkit.kiki.storyapp.data.model

data class UserModel(
    val email: String,
    val token: String,
    val isLogin: Boolean = false
)
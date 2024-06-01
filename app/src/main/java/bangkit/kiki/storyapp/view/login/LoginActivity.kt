package bangkit.kiki.storyapp.view.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import bangkit.kiki.storyapp.R
import bangkit.kiki.storyapp.databinding.ActivityLoginBinding
import bangkit.kiki.storyapp.view.ViewModelFactory
import bangkit.kiki.storyapp.view.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.loginButton.text = "LOADING"
                binding.loginButton.isEnabled = false
                binding.emailEditText.isEnabled = false
                binding.passwordEditText.isEnabled = false
            } else {
                binding.loginButton.text = getString(R.string.login)
                binding.loginButton.isEnabled = true
                binding.emailEditText.isEnabled = true
                binding.passwordEditText.isEnabled = true
            }
        }

        viewModel.isError.observe(this) { isError ->
            if (isError) {
                Toast.makeText(this, viewModel.errorMessage.value, Toast.LENGTH_SHORT).show()
            }
        }

        setupView()
        setupAction()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            CoroutineScope(Dispatchers.Main).launch {
                val success = viewModel.login(email, password)
                if (success) {
                    Toast.makeText(this@LoginActivity, getString(R.string.title_welcome_page), Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
}
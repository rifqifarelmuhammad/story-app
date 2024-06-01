package bangkit.kiki.storyapp.view.signup

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import bangkit.kiki.storyapp.R
import bangkit.kiki.storyapp.databinding.ActivitySignupBinding
import bangkit.kiki.storyapp.view.login.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var viewModel: SignupViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)

        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[SignupViewModel::class.java]

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.signupButton.text = "LOADING"
                binding.signupButton.isEnabled = false
                binding.nameEditText.isEnabled = false
                binding.emailEditText.isEnabled = false
                binding.passwordEditText.isEnabled = false
            } else {
                binding.signupButton.text = getString(R.string.signup)
                binding.signupButton.isEnabled = true
                binding.emailEditText.isEnabled = true
                binding.nameEditText.isEnabled = true
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
        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(chars: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun onTextChanged(chars: CharSequence?, start: Int, before: Int, count: Int) {
                if (!chars?.let { Patterns.EMAIL_ADDRESS.matcher(it).matches() }!!) {
                    binding.emailEditTextLayout.error = "Invalid email"
                } else {
                    binding.emailEditTextLayout.error = null
                }
            }

            override fun afterTextChanged(chars: Editable?) {}
        })

        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(chars: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun onTextChanged(chars: CharSequence?, start: Int, before: Int, count: Int) {
                if (chars.toString().length < 8) {
                    binding.passwordEditTextLayout.error = "Password must be at least 8 characters long"
                } else {
                    binding.passwordEditTextLayout.error = null
                }
            }

            override fun afterTextChanged(chars: Editable?) {}
        })

        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            CoroutineScope(Dispatchers.Main).launch {
                val success = viewModel.register(name, email, password)
                if (success) {
                    Toast.makeText(this@SignupActivity, "Account has been successfully created", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                    finish()
                }
            }
        }
    }
}
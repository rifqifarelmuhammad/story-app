package bangkit.kiki.storyapp.view.main

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import bangkit.kiki.storyapp.R
import bangkit.kiki.storyapp.databinding.ActivityMainBinding
import bangkit.kiki.storyapp.view.ViewModelFactory
import bangkit.kiki.storyapp.view.addStory.AddStoryActivity
import bangkit.kiki.storyapp.view.map.MapsActivity
import bangkit.kiki.storyapp.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.rvStories.setHasFixedSize(true)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                showRecyclerList(user.token)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        viewModel.isError.observe(this) { isError ->
            if (isError) {
                Toast.makeText(this, viewModel.errorMessage.value, Toast.LENGTH_SHORT).show()
            }
        }

        setupView()
        setupAppBarMenu()
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
        binding.addStoryButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
        }
    }

    private fun setupAppBarMenu() {
        binding.appBar.topAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.logoutButton -> {
                    viewModel.logout()
                    true
                }
                R.id.mapButton -> {
                    startActivity(Intent(this@MainActivity, MapsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun showRecyclerList(token: String) {
        binding.rvStories.layoutManager = LinearLayoutManager(this)
        val storyAdapter = ListStoryAdapter()
        binding.rvStories.adapter = storyAdapter
        viewModel.getStories(token).observe(this) {
            storyAdapter.submitData(lifecycle, it)
        }
    }
}
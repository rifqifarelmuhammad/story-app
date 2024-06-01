package bangkit.kiki.storyapp.view.storyDetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import bangkit.kiki.storyapp.databinding.ActivityStoryDetailBinding
import com.bumptech.glide.Glide

class StoryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)

        setContentView(binding.root)

        intent.getStringExtra(EXTRA_PHOTO_URL)?.let {
            Glide.with(this).load(it).into(binding.imgPhoto)
        }

        intent.getStringExtra(EXTRA_NAME)?.let {
            binding.tvName.text = it
        }

        intent.getStringExtra(EXTRA_DESCRIPTION)?.let {
            binding.tvDescription.text = it
        }
    }

    companion object {
        const val EXTRA_PHOTO_URL = "extra_photo_url"
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_DESCRIPTION = "extra_description"
    }
}
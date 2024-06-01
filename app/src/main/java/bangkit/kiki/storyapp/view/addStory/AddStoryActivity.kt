package bangkit.kiki.storyapp.view.addStory

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import bangkit.kiki.storyapp.BuildConfig
import bangkit.kiki.storyapp.R
import bangkit.kiki.storyapp.databinding.ActivityAddStoryBinding
import bangkit.kiki.storyapp.view.ViewModelFactory
import bangkit.kiki.storyapp.view.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class AddStoryActivity : AppCompatActivity() {
    private val viewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var launcherIntentCamera: ActivityResultLauncher<Uri>
    private lateinit var accessToken: String
    private var currentImageUri: Uri? = null
    private val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)

        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            accessToken = user.token
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.apply {
                    buttonAdd.text = "LOADING"
                    buttonAdd.isEnabled = false
                    inputDescription.isEnabled = false
                    cameraButton.isEnabled = false
                    galleryButton.isEnabled = false
                }
            } else {
                binding.apply {
                    buttonAdd.text = getString(R.string.upload)
                    buttonAdd.isEnabled = true
                    inputDescription.isEnabled = true
                    cameraButton.isEnabled = true
                    galleryButton.isEnabled = true
                }
            }
        }

        viewModel.isError.observe(this) { isError ->
            if (isError) {
                viewModel.errorMessage.value?.let { showToast(it) }
            }
        }

        setupAction()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setupAction() {
        pickImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data ?: return@registerForActivityResult
                val selectedImageUri = data.data
                if (selectedImageUri != null) {
                    showImage(selectedImageUri)
                } else {
                    showToast("Failed to get image from gallery")
                }
            } else {
                showToast("Failed to get image from gallery")
            }
        }

        launcherIntentCamera = registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { isSuccess ->
            if (isSuccess) {
                currentImageUri?.let { showImage(it) }
            }
        }

        binding.apply {
            cameraButton.setOnClickListener{
                startCamera()
            }

            galleryButton.setOnClickListener {
                startGallery()
            }

            buttonAdd.setOnClickListener {
                addStory()
            }
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this@AddStoryActivity)
        launcherIntentCamera.launch(currentImageUri)
    }

    private fun getImageUri(context: Context): Uri {
        var uri: Uri? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "$timeStamp.jpg")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyCamera/")
            }
            uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
        }
        return uri ?: getImageUriForPreQ(context)
    }

    private fun getImageUriForPreQ(context: Context): Uri {
        val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(filesDir, "/MyCamera/$timeStamp.jpg")
        if (imageFile.parentFile?.exists() == false) imageFile.parentFile?.mkdir()
        return FileProvider.getUriForFile(
            context,
            "${BuildConfig.APPLICATION_ID}.fileprovider",
            imageFile
        )
    }

    private fun startGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(galleryIntent)
    }

    private fun showImage(uri: Uri) {
        binding.previewImageView.setImageURI(uri)
        currentImageUri = uri
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun addStory() {
        currentImageUri?.let { uri ->
            var imageFile = uriToFile(uri, this)

            lifecycleScope.launch(Dispatchers.Main) {
                imageFile = imageFile.reduceFileImage()
                val description = binding.inputDescription.text.toString()

                val requestBody = description.toRequestBody("text/plain".toMediaType())
                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "photo",
                    imageFile.name,
                    requestImageFile
                )

                val isSuccess = viewModel.addStory(accessToken, multipartBody, requestBody)
                if (isSuccess) {
                    val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
            }
        }
    }

    private fun uriToFile(imageUri: Uri, context: Context): File {
        val myFile = createCustomTempFile(context)
        val inputStream = context.contentResolver.openInputStream(imageUri) as InputStream
        val outputStream = FileOutputStream(myFile)
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) outputStream.write(buffer, 0, length)
        outputStream.close()
        inputStream.close()
        return myFile
    }

    private fun createCustomTempFile(context: Context): File {
        val filesDir = context.externalCacheDir
        return File.createTempFile(timeStamp, ".jpg", filesDir)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun File.reduceFileImage(): File {
        val file = this
        val bitmap = BitmapFactory.decodeFile(file.path).getRotatedBitmap(file)
        var compressQuality = 100
        var streamLength: Int
        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > MAXIMAL_SIZE)
        bitmap?.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun Bitmap.getRotatedBitmap(file: File): Bitmap? {
        val orientation = ExifInterface(file).getAttributeInt(
            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED
        )
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(this, 90F)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(this, 180F)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(this, 270F)
            ExifInterface.ORIENTATION_NORMAL -> this
            else -> this
        }
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height, matrix, true
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
        private const val MAXIMAL_SIZE = 1000000
    }
}
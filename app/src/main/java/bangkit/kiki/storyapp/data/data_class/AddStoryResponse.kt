package bangkit.kiki.storyapp.data.data_class

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddStoryResponse (
    @field:SerializedName("error")
    val error: Boolean,
    @field:SerializedName("message")
    val message: String
) : Parcelable
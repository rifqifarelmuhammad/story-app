package bangkit.kiki.storyapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoryCard (
    val name: String,
    val description: String,
    val photoUrl: String
) : Parcelable
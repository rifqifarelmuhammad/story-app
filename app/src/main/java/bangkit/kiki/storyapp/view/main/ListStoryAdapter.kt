package bangkit.kiki.storyapp.view.main

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import bangkit.kiki.storyapp.R
import bangkit.kiki.storyapp.data.data_class.ListStoryItem
import bangkit.kiki.storyapp.data.model.StoryCard
import bangkit.kiki.storyapp.view.storyDetail.StoryDetailActivity
import com.bumptech.glide.Glide

class ListStoryAdapter: PagingDataAdapter<ListStoryItem, ListStoryAdapter.ListViewHolder>(DIFF_CALLBACK) {
    class ListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var imgPhoto: ImageView = itemView.findViewById(R.id.img_photo)
        private var tvName: TextView = itemView.findViewById(R.id.tv_name)

        fun bind(story: StoryCard) {
            Glide.with(itemView.context).load(story.photoUrl).into(imgPhoto)
            tvName.text = story.name

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, StoryDetailActivity::class.java)
                intent.putExtra(StoryDetailActivity.EXTRA_PHOTO_URL, story.photoUrl)
                intent.putExtra(StoryDetailActivity.EXTRA_NAME, story.name)
                intent.putExtra(StoryDetailActivity.EXTRA_DESCRIPTION, story.description)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        androidx.core.util.Pair(imgPhoto, "photo"),
                        androidx.core.util.Pair(tvName, "name")
                    )

                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.story_card, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            val storyCard = story.name?.let { story.description?.let { it1 ->
                story.photoUrl?.let { it2 ->
                    StoryCard(it,
                        it1, it2
                    )
                }
            } }
            if (storyCard != null) {
                holder.bind(storyCard)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
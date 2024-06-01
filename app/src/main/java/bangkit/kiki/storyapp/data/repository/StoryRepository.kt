package bangkit.kiki.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import bangkit.kiki.storyapp.data.api.ApiConfig
import bangkit.kiki.storyapp.data.data_class.ListStoryItem
import bangkit.kiki.storyapp.data.pagination.StoryPagingSource

class StoryRepository {
    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(ApiConfig.getApiService(), token)
            }
        ).liveData
    }
}
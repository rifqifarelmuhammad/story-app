import bangkit.kiki.storyapp.data.data_class.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val quote = ListStoryItem(
                photoUrl = "https://photo.com",
                createdAt = "20/10/2003",
                name = "TEST",
                description = "TEST UPLOAD BOOSS",
                lon = 21.20,
                id = "12312c-213mlkmfb-89kjfdkdfbb",
                lat = 341.12
            )
            items.add(quote)
        }
        return items
    }
}
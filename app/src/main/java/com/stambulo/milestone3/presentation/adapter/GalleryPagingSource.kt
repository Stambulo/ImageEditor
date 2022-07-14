package com.stambulo.milestone3.presentation.adapter

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.stambulo.milestone3.data.model.MediaStoreImage
import com.stambulo.milestone3.domain.IImageRepositoryImpl

class GalleryPagingSource(private val imagesRepository: IImageRepositoryImpl) :
    PagingSource<Int, MediaStoreImage>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaStoreImage> {
        val nextPos = params.key ?: 0
        val imageResponse = imagesRepository.queryImages()
        val limit = imageResponse.size
        val imagePage = getSubList(imageResponse, nextPos, params.loadSize, limit)

        return try {
            LoadResult.Page(
                data = imagePage,
                prevKey = null,        // Only paging forward.
                nextKey = if (nextPos > limit) {
                    null
                } else {
                    nextPos + params.loadSize
                }
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private fun getSubList(
        imageResponse: List<MediaStoreImage>,
        nextPos: Int,
        loadSize: Int,
        limit: Int
    ): List<MediaStoreImage> {
        if ((nextPos + loadSize) < limit) {
            return imageResponse.subList(nextPos, nextPos + loadSize)
        }
        if ((nextPos < limit) && ((nextPos + loadSize) >= limit)) {
            return imageResponse.subList(nextPos, limit)
        }
        return emptyList()
    }

    override fun getRefreshKey(state: PagingState<Int, MediaStoreImage>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}

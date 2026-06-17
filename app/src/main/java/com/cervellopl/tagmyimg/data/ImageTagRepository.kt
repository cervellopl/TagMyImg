package com.cervellopl.tagmyimg.data

import kotlinx.coroutines.flow.Flow

class ImageTagRepository(private val dao: ImageDao) {

    fun getImagesWithTags(): Flow<List<ImageWithTags>> = dao.getImagesWithTags()

    suspend fun saveImageWithTags(uri: String, tags: List<Pair<String, Float>>) {
        val imageId = dao.insertImage(ImageEntity(uri = uri))
        val tagEntities = tags.map { (label, confidence) ->
            TagEntity(imageId = imageId, label = label, confidence = confidence)
        }
        dao.insertTags(tagEntities)
    }
}

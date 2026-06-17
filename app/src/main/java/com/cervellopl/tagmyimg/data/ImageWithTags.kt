package com.cervellopl.tagmyimg.data

import androidx.room.Embedded
import androidx.room.Relation

data class ImageWithTags(
    @Embedded val image: ImageEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "imageId"
    )
    val tags: List<TagEntity>
)

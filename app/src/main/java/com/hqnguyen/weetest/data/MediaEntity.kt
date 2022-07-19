package com.hqnguyen.weetest.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media")
data class MediaEntity (
    @PrimaryKey
    @ColumnInfo(name = "mediaId")
    val mediaId: Int,
    @ColumnInfo(name = "fileName")
    val fileName: String?,
    @ColumnInfo(name = "type")
    val typeMedia: String,
    @ColumnInfo(name = "urlInInternal")
    val url: String?
)
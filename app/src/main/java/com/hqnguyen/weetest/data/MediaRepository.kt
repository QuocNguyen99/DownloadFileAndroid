package com.hqnguyen.weetest.data

import com.hqnguyen.weetest.network.MediaService

class MediaRepository constructor(private val mediaService: MediaService) {
    suspend fun getMediaList() = mediaService.getMediaList()

    suspend fun downloadMedia(path:String) = mediaService.downloadMedia(path)

}
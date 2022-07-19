package com.hqnguyen.weetest.data

import com.hqnguyen.weetest.view.MediaViewModel

data class Media(
    val id: Int,
    val type: TypeMedia,
    val url: String,
    val fileName: String,
    var downloaded: StatusDownload = StatusDownload.LOADING
) {
    constructor(response: MediaResponse) : this(
        id = response.id,
        fileName = response.fileName,
        url = response.url,
        type = if (response.type == MediaViewModel.VIDEO_MP4) TypeMedia.VIDEO_MP3 else TypeMedia.IMAGE_PNG,
        downloaded = StatusDownload.LOADING
    )
}

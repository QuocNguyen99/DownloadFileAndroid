package com.hqnguyen.weetest.view

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.hqnguyen.weetest.data.*
import com.hqnguyen.weetest.data.dao.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*


class MediaViewModel(
    private val repository: MediaRepository,
    private val application: Application
) : ViewModel() {

    private val dbManager = AppDatabase.getInstance(application.applicationContext)

    companion object {
        val VIDEO_MP4 = "video/mp4"
    }

    var mediaListResponse = MutableLiveData<List<Media>>()
    var newListMedia = listOf<Media>()
    val errorMessage = MutableLiveData<String>()

    fun getMediaListApi() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getMediaList()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        newListMedia = response.body()?.map(::Media)!!
                        mediaListResponse.value = newListMedia!!
                        if (newListMedia != null) {
                            downloadMediaToInternalStorage(newListMedia)
                        }
                    } else {
                        onError(response.message())
                    }
                }
            } catch (ex: Exception) {
                Log.d(
                    "VIEW MODEL MEDIA ",
                    "Exception ${ex.message} "
                )
            }
        }

    }

    private fun downloadMediaToInternalStorage(newListMedia: List<Media>) {
        viewModelScope.launch(Dispatchers.IO) {
            newListMedia.forEachIndexed { index, it ->
                val pathArray = it.url.split("/").toTypedArray()
                var mediaDownloadedResponse =
                    repository.downloadMedia((pathArray[pathArray.size - 1]))

                if (mediaDownloadedResponse.isSuccessful) {
                    try {
                        val path =
                            "${application.filesDir}${File.separator}${System.currentTimeMillis()}${pathArray[pathArray.size - 1]}"
                        val file = File(path)

                        var inputStream: InputStream? = null
                        var outputStream: OutputStream? = null

                        try {
                            val fileReader = ByteArray(4096)
                            val fileSize: Long =
                                mediaDownloadedResponse.body()?.contentLength() ?: 0
                            var fileSizeDownloaded: Long = 0

                            inputStream = mediaDownloadedResponse.body()?.byteStream()
                            outputStream = FileOutputStream(file)

                            while (true) {
                                val read = inputStream?.read(fileReader)
                                if (read == -1) {
                                    break
                                }
                                if (read != null) {
                                    outputStream!!.write(fileReader, 0, read)
                                    fileSizeDownloaded += read.toLong()
                                }
                            }
                            outputStream!!.flush()
                            updateMediaList(index, StatusDownload.SUCCESS, path, it)
                            true
                        } catch (e: IOException) {
                            updateMediaList(index, StatusDownload.FAIL, null, it)
                            false
                        } finally {
                            inputStream?.close()
                            outputStream?.close()
                        }

                    } catch (ex: Exception) {
                        updateMediaList(index, StatusDownload.FAIL, null, it)
                    }
                } else {
                    Log.d(
                        "VIEW MODEL MEDIA ",
                        "VIEW MODEL MEDIA  ${mediaDownloadedResponse.message()}"
                    )
                }
            }
        }
    }

    private suspend fun updateMediaList(
        index: Int,
        status: StatusDownload,
        url: String?,
        media: Media
    ) {
        withContext(Dispatchers.Main){
            newListMedia[index]?.downloaded = status
            mediaListResponse.value = newListMedia
        }
        addMediaToLocaldb(
            MediaEntity(
                mediaId = media.id,
                fileName = media.fileName,
                url = url,
                typeMedia = media.type.name
            )
        )
    }

    private suspend fun addMediaToLocaldb(media: MediaEntity) {
        dbManager.mediaDao().insertMedia(media)
    }

    private fun onError(message: String) {
        errorMessage.value = message
    }

}
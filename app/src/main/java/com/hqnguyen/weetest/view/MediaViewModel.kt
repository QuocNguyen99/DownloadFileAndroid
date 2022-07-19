package com.hqnguyen.weetest.view

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.hqnguyen.weetest.data.Media
import com.hqnguyen.weetest.data.MediaRepository
import com.hqnguyen.weetest.data.StatusDownload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*

class MediaViewModel(
    private val repository: MediaRepository,
    private val application: Application
) : ViewModel() {

    companion object {
        val VIDEO_MP4 = "video/mp4"
    }

    var mediaListResponse = MutableLiveData<List<Media>>()
    val errorMessage = MutableLiveData<String>()

    fun getMediaListApi() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getMediaList()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    var newListMedia = response.body()?.map(::Media)
                    mediaListResponse.value = newListMedia!!
                    if (newListMedia != null) {
                        downloadMediaToInternalStorage(newListMedia)
                    }
                } else {
                    onError(response.message())
                }
            }
        }
    }

    private fun downloadMediaToInternalStorage(newListMedia: List<Media>) {
        viewModelScope.launch(Dispatchers.IO) {
            newListMedia.forEachIndexed { index,it->
                val pathArray = it.url.split("/").toTypedArray()
                var mediaDownloadedResponse =
                    repository.downloadMedia((pathArray[pathArray.size - 1]))

                if (mediaDownloadedResponse.isSuccessful) {
                    try {
                        val file =
                            File("${application.filesDir}${File.separator}${System.currentTimeMillis()}")
                        Log.d(
                            "VIEW MODEL MEDIA ",
                            "futureStudioIconFile ${file.path} "
                        )
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
                                Log.d(
                                    "VIEW MODEL MEDIA ",
                                    "VIEW MODEL MEDIA $fileSizeDownloaded of $fileSize"
                                )
                            }
                            outputStream!!.flush()
                            updateMediaList(index,StatusDownload.SUCCESS)
                            true
                        } catch (e: IOException) {
                            Log.d(
                                "VIEW MODEL MEDIA ",
                                "VIEW MODEL MEDIA 1${e.message}"
                            )
                            updateMediaList(index,StatusDownload.FAIL)
                            false
                        } finally {
                            inputStream?.close()
                            outputStream?.close()
                        }

                    } catch (ex: Exception) {
                        Log.d(
                            "VIEW MODEL MEDIA ",
                            "VIEW MODEL MEDIA 2 ${ex.message}"
                        )
                        updateMediaList(index,StatusDownload.FAIL)
                    }
                } else {
                    Log.d(
                        "VIEW MODEL MEDIA ",
                        "VIEW MODEL MEDIA 3 ${mediaDownloadedResponse.message()}"
                    )
                }
            }
        }
    }

    private fun updateMediaList(index: Int, status: StatusDownload) {
        mediaListResponse.value?.get(index)?.downloaded = status
    }

    private fun onError(message: String) {
        errorMessage.value = message
    }

}
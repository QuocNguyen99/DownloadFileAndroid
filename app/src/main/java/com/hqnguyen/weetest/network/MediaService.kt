package com.hqnguyen.weetest.network

import com.hqnguyen.weetest.data.MediaResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming

interface MediaService {
    @GET("media-list")
    suspend fun getMediaList(): Response<List<MediaResponse>>

    @GET("{media}")
    @Streaming
    suspend fun downloadMedia(@Path("media") mediaPath: String): Response<ResponseBody>

    companion object {
        var retrofitService: MediaService? = null
        fun getInstance(): MediaService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://raw.githubusercontent.com/wee-test/test2022/main/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(MediaService::class.java)
            }
            return retrofitService!!
        }

    }
}
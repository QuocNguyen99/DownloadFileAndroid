package com.hqnguyen.weetest.data

import com.google.gson.annotations.SerializedName

data class MediaResponse(
    @SerializedName("orderId")
    val id: Int,
    @SerializedName("type")
    val type: String,
    @SerializedName("md5")
    val md5: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("fileName")
    val fileName: String,
)

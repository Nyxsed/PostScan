package ru.nyxsed.postscan.common.data.models.response

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("error_msg")
    val errorMsg: String,
    @SerializedName("error_text")
    val errorText: String,
)
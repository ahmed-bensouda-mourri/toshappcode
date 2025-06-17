package com.shirttok.shirtclub_app

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("message")
    val message: MessageResult,
    val auth: Boolean
)

sealed class MessageResult {
    data class Success(val messageData: Message) : MessageResult()
    object NotFound : MessageResult()  // For the case where message is `false`
}

data class Message(
    val id: Int,
    val message_id: String,
    val member_id: String,
    val content: String?,
    val link: String,
    val image: String,
    val type: String
)
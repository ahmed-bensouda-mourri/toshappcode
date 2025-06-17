package com.shirttok.shirtclub_app

import com.google.gson.*
import java.lang.reflect.Type

class MessageResultDeserializer : JsonDeserializer<MessageResult> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): MessageResult {
        return if (json.isJsonObject) {
            val messageData = context.deserialize<Message>(json, Message::class.java)
            MessageResult.Success(messageData)
        } else if (json.isJsonPrimitive && json.asJsonPrimitive.isBoolean && !json.asBoolean) {
            MessageResult.NotFound
        } else {
            throw JsonParseException("Unexpected JSON type for message field")
        }
    }
}
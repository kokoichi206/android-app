package com.example.appscommunications.ai

import com.google.firebase.Firebase
import com.google.firebase.ai.Chat
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.FunctionDeclaration
import com.google.firebase.ai.type.FunctionResponsePart
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.Tool
import com.google.firebase.ai.type.content
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class GeminiChatService(
    private val appFunctionRunner: AppFunctionRunner,
) {
    private val chat: Chat by lazy {
        Firebase.ai(backend = GenerativeBackend.googleAI())
            .generativeModel(
                modelName = MODEL_NAME,
                // 自分のアプリ以外を登録するにはどうするか。
                tools = listOf(Tool.functionDeclarations(functionDeclarations())),
            )
            .startChat()
    }

    suspend fun sendMessage(userMessage: String): String {
        var response = chat.sendMessage(userMessage)

        while (response.functionCalls.isNotEmpty()) {
            val resultParts = response.functionCalls.map { call ->
                // 自分のアプリ以外を登録するにはどうするか。
                val result = appFunctionRunner.execute(call.name, call.args)
                FunctionResponsePart(call.name, buildJsonObject { put("result", result) })
            }
            response = chat.sendMessage(
                content(role = "function") { resultParts.forEach { part(it) } },
            )
        }

        return response.text ?: "(The assistant did not return any text.)"
    }

    private fun functionDeclarations(): List<FunctionDeclaration> = listOf(
        FunctionDeclaration(
            "incrementCounter",
            "Increase the counter by a given amount.",
            mapOf("by" to Schema.integer("The amount to add to the counter. Defaults to 1 if omitted.")),
            listOf("by"),
        ),
        FunctionDeclaration(
            "decrementCounter",
            "Decrease the counter by a given amount.",
            mapOf("by" to Schema.integer("The amount to subtract from the counter. Defaults to 1 if omitted.")),
            listOf("by"),
        ),
        FunctionDeclaration("resetCounter", "Reset the counter back to zero.", emptyMap()),
        FunctionDeclaration("getCounter", "Get the counter's current value.", emptyMap()),
    )

    companion object {
        const val MODEL_NAME = "gemini-2.5-flash"
    }
}
package com.ntz.distributor_app.data.remote

import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.InvalidAPIKeyException
import com.ntz.distributor_app.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiApiService @Inject constructor() {
    private var generativeModel: GenerativeModel? = null

    init {
        try {
            val apiKey = BuildConfig.GEMINI_API_KEY

            if (apiKey.isEmpty()) {
                Log.e("GeminiApiService", "API key is not set. Please set the GEMINI_API_KEY in your build config.")
            } else {
                generativeModel = GenerativeModel(
                    modelName = "gemini-2.0-flash-lite",
                    apiKey = apiKey
                )
                Log.d("GeminiApiService", "Gemini API initialized successfully.")
            }
        } catch (e: Exception) {
            Log.e("GeminiApiService", "Error initializing Gemini API: ${e.message}")
        }
    }

    suspend fun generateRecommendation(prompt: String): GenerateContentResponse? {
        val currentModel = generativeModel ?: throw IllegalStateException("Generative model is not initialized.")

        try {
            Log.d("GeminiApiService", "Generating recommendation with prompt: $prompt")
            val response = generativeModel!!.generateContent(prompt)
            Log.d("GeminiApiService", "Response received")
            return response
        }catch (e: InvalidAPIKeyException) {
            Log.e("GeminiApiService", "Invalid API key: ${e.message}")
            throw IllegalStateException("Invalid API key. Please check your API key.", e)
        } catch (e: IllegalStateException) {
            Log.e("GeminiApiService", "Generative model is not initialized: ${e.message}")
            throw IllegalStateException("Generative model is not initialized.", e)
        } catch (e: Exception) {
            Log.e("GeminiApiService", "Error generating recommendation: ${e.message}")
            throw RuntimeException("Error generating recommendation", e)
        }
    }
}
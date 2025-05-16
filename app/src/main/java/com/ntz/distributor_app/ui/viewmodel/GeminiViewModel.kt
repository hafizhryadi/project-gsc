package com.ntz.distributor_app.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.ntz.distributor_app.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class GeminiResponse{
    object Loading : GeminiResponse()
    object Idle : GeminiResponse()
    data class Success(val response: String) : GeminiResponse()
    data class Error(val message: String) : GeminiResponse()
}

class GeminiViewModel : ViewModel(){

    // this ViewModel for generate recommendation from Gemini API
    // this class contain for generate description, recommendation Agent amd recommendation Product

    private var _geminiResponse = MutableStateFlow<GeminiResponse>(GeminiResponse.Idle)
    var geminiResponse : StateFlow<GeminiResponse> = _geminiResponse

    private var generativeModel : GenerativeModel? = null

    init{
        try{
            val apiKey = BuildConfig.GEMINI_API_KEY
            val config = generationConfig {
                temperature = 0.7f
                topK = 1
                topP = 1f
                maxOutputTokens = 2048
            }

            generativeModel = GenerativeModel(
                modelName = "gemini-2.0-flash-lite",
                apiKey = apiKey,
                generationConfig = config
            )
        } catch (e: Exception){
            _geminiResponse.value = GeminiResponse.Error("Error initializing Gemini API: ${e.message}")
            Log.e("GeminiViewModel", "Error initializing Gemini API: ${e.message}")
        }
    }

    fun generateDescription(stuffName : String, stuffCategory : String){

        viewModelScope.launch {
            if(generativeModel == null){
                _geminiResponse.value = GeminiResponse.Error("Generative model is not initialized")
                Log.e("GeminiViewModel", "Generative model is not initialized")
                return@launch
            }

            if(stuffName.isEmpty() || stuffCategory.isEmpty()){
                _geminiResponse.value = GeminiResponse.Error("Stuff name and category cannot be empty")
                Log.e("GeminiViewModel", "Stuff name and category cannot be empty")
                return@launch
            }

            val prompt = "Buatkan deskripsi produk yang menarik dan menjual untuk sebuah \"$stuffName\" dalam kategori \"$stuffCategory\". Deskripsi harus singkat (sekitar 2-3 kalimat), menonjolkan keunggulan utama, dan menggunakan bahasa yang persuasif. Target pasar adalah konsumen umum di Indonesia."

            try{
                val response = generativeModel!!.generateContent(prompt)
                response.text?.let {
                    _geminiResponse.value = GeminiResponse.Success(it)
                    Log.d("GeminiViewModel", "Description generated successfully")
                } ?: run {
                    _geminiResponse.value = GeminiResponse.Error("No response text received")
                    Log.e("GeminiViewModel", "No response text received")
                }
            } catch (e: Exception){
                _geminiResponse.value = GeminiResponse.Error("Error generating description: ${e.message}")
                Log.e("GeminiViewModel", "Error generating description: ${e.message}")
            }
        }
    }

    fun generateRecommendationAgent(

    ){}

    fun resetDescriptionState(){
        _geminiResponse.value = GeminiResponse.Idle
    }
}
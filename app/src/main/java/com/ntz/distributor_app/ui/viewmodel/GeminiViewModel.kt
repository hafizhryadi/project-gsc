package com.ntz.distributor_app.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.SerializationException
import com.google.ai.client.generativeai.type.generationConfig
import com.ntz.distributor_app.BuildConfig
import com.ntz.distributor_app.data.model.AgentData
import com.ntz.distributor_app.data.model.GeminiRecommendation
import com.ntz.distributor_app.data.model.ProducenData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

sealed class GeminiResponse{
    object Loading : GeminiResponse()
    object Idle : GeminiResponse()
    data class Success(val response: List<GeminiRecommendation>) : GeminiResponse()
    data class Error(val message: String) : GeminiResponse()
}

sealed class GeminiResponseDescription{
    object Loading : GeminiResponseDescription()
    object Idle : GeminiResponseDescription()
    data class Success(val response: String) : GeminiResponseDescription()
    data class Error(val message: String) : GeminiResponseDescription()
}

class GeminiViewModel : ViewModel(){

    // this ViewModel for generate recommendation from Gemini API
    // this class contain for generate description, recommendation Agent amd recommendation Product

    private var _geminiResponse = MutableStateFlow<GeminiResponse>(GeminiResponse.Idle)
    var geminiResponse : StateFlow<GeminiResponse> = _geminiResponse

    private var _geminiResponseDescription = MutableStateFlow<GeminiResponseDescription>(GeminiResponseDescription.Idle)
    var geminiResponseDescription : StateFlow<GeminiResponseDescription> = _geminiResponseDescription

    private var generativeModel : GenerativeModel? = null

    private var json = Json { ignoreUnknownKeys = true }

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
                _geminiResponseDescription.value = GeminiResponseDescription.Error("Generative model is not initialized")
                Log.e("GeminiViewModel", "Generative model is not initialized")
                return@launch
            }

            if(stuffName.isEmpty() || stuffCategory.isEmpty()){
                _geminiResponseDescription.value = GeminiResponseDescription.Error("Stuff name and category cannot be empty")
                Log.e("GeminiViewModel", "Stuff name and category cannot be empty")
                return@launch
            }

            val prompt = "Buatkan deskripsi produk yang menarik dan menjual untuk sebuah \"$stuffName\" dalam kategori \"$stuffCategory\". Deskripsi harus singkat (sekitar 2-3 kalimat), menonjolkan keunggulan utama, dan menggunakan bahasa yang persuasif. Target pasar adalah konsumen umum di Indonesia."

            try{
                val response = generativeModel!!.generateContent(prompt)
                response.text?.let {
                    _geminiResponseDescription.value = GeminiResponseDescription.Success(it)
                    Log.d("GeminiViewModel", "Description generated successfully")
                } ?: run {
                    _geminiResponseDescription.value = GeminiResponseDescription.Error("No response text received")
                    Log.e("GeminiViewModel", "No response text received")
                }
            } catch (e: Exception){
                _geminiResponseDescription.value = GeminiResponseDescription.Error("Error generating description: ${e.message}")
                Log.e("GeminiViewModel", "Error generating description: ${e.message}")
            }
        }
    }

    fun generateRecommendationProducen(city: String = "", regency: String = "",roleToSearch: String = "", listData : MutableList<ProducenData>){
        viewModelScope.launch {
            _geminiResponse.value = GeminiResponse.Loading

            if(generativeModel == null){
                _geminiResponse.value = GeminiResponse.Error("Generative model is not initialized")
                Log.e("GeminiViewModel", "Generative model is not initialized")
                return@launch
            }

            if(city.isEmpty() || regency.isEmpty()){
                _geminiResponse.value = GeminiResponse.Error("City and Regency cannot be empty")
                Log.e("GeminiViewModel", "City and Regency cannot be empty")
                return@launch
            }

            val prompt = """
                  Saya mencari $roleToSearch di sekitar kota "$city" ${if (regency?.isNotBlank() == true) "dan kabupaten/kecamatan \"$regency\"" else ""}.
                  Berikut adalah daftar $roleToSearch yang tersedia beserta lokasinya dan beberapa info tambahan: ${listData}
                  Berdasarkan data di atas, berikan rekomendasi maksimal 5 $roleToSearch yang paling relevan atau terdekat dengan lokasi saya.
                  Sertakan ID, nama, kota, dan kabupaten dari $roleToSearch yang direkomendasikan dalam format JSON array.
                  Contoh format JSON: [{"id": "uid1", "nama": "Nama Produsen A", "kota": "Kota A", "kabupaten": "Kabupaten A"}]
                  Jika tidak ada yang benar-benar dekat, berikan yang paling relevan dari kota yang sama atau kabupaten terdekat.
                  Jika daftar kosong atau tidak ada yang cocok, kembalikan array kosong.
                  Pastikan output hanya berupa JSON array yang valid, tanpa teks tambahan atau markdown.
                """.trimIndent()

            try{
                val response = generativeModel!!.generateContent(prompt)
                response.text?.let {
                    try {
                        val recommendation: List<GeminiRecommendation> = json.decodeFromString(it)
                        _geminiResponse.value = GeminiResponse.Success(recommendation)
                        Log.d("GeminiViewModel", "Description generated successfully")
                        Log.d("GeminiViewModel", "Response: $it")
                    } catch (e: SerializationException) {
                        _geminiResponse.value = GeminiResponse.Error("Error parsing JSON: ${e.message}")
                        Log.e("GeminiViewModel", "Error parsing JSON: ${e.message}")
                    } catch (e: Exception) {
                        _geminiResponse.value = GeminiResponse.Error("Error generating recommendation: ${e.message}")
                        Log.e("GeminiViewModel", "Error generating recommendation: ${e.message}")
                    }
                } ?: run {
                    _geminiResponse.value = GeminiResponse.Error("No response text received")
                    Log.e("GeminiViewModel", "No response text received")
                }
            } catch (e : Exception){
                _geminiResponse.value = GeminiResponse.Error("Error generating recommendation: ${e.message}")
                Log.e("GeminiViewModel", "Error generating recommendation: ${e.message}")
            }
        }
    }

    fun resetDescriptionState(){
        _geminiResponse.value = GeminiResponse.Idle
    }
}
package com.dartscore.feature.play.data

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

// Kontrakt serwera w Pythonie (uvicorn).
interface DartboardScanApi {

    @Multipart
    @POST("score")
    suspend fun score(@Part image: MultipartBody.Part): ScanResponseDto

    @GET("health")
    suspend fun health(): HealthDto
}

// Odpowiedź /score. Pola snake_case mapujemy na idiomatyczne nazwy przez @SerializedName.
data class ScanResponseDto(
    @SerializedName("mock_mode") val mockMode: Boolean = false,
    @SerializedName("calibration_ok") val calibrationOk: Boolean = false,
    @SerializedName("dart_count") val dartCount: Int = 0,
    val darts: List<String> = emptyList(),   // jeśli serwer zwraca inną strukturę – tu skorygujemy
    val total: Int = 0,
    val detections: List<DetectionDto> = emptyList(),
    @SerializedName("annotated_image") val annotatedImage: String? = null,
)

data class DetectionDto(
    @SerializedName("class") val className: String = "",
    val x: Float = 0f,
    val y: Float = 0f,
    val confidence: Float = 0f,
)

data class HealthDto(
    val status: String = "",
    @SerializedName("model_loaded") val modelLoaded: Boolean = false,
)
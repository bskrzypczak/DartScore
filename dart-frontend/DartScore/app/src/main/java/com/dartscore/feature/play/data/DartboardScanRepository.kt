package com.dartscore.feature.play.data

import com.dartscore.feature.play.domain.ScanResult
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

interface DartboardScanRepository {
    // Wysyła zdjęcie tarczy do backendu i zwraca odczytany wynik.
    suspend fun scan(image: File): ScanResult
}

@Singleton
class RetrofitDartboardScanRepository @Inject constructor(
    private val api: DartboardScanApi,
) : DartboardScanRepository {

    override suspend fun scan(image: File): ScanResult {
        val body = image.asRequestBody("image/jpeg".toMediaType())
        // Nazwa pola "image" MUSI zgadzać się z serwerem (files={"image": ...}).
        val part = MultipartBody.Part.createFormData("image", image.name, body)
        val dto = api.score(part)
        return ScanResult(
            darts = dto.darts,
            total = dto.total,
            calibrationOk = dto.calibrationOk,
            mockMode = dto.mockMode,
        )
    }
}
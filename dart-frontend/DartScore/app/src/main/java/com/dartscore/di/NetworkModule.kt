package com.dartscore.di

import com.dartscore.feature.play.data.DartboardScanApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // EMULATOR: 10.0.2.2 = host (Twój komputer). Zostaw to do testów na emulatorze.
    // FIZYCZNY TELEFON: wpisz IP komputera w sieci LAN, np. "http://192.168.1.50:8000/"
    //   (telefon i komputer w tej samej sieci Wi-Fi). Slash na końcu jest wymagany.
    private const val BASE_URL = "http://172.20.10.3:8000/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideScanApi(retrofit: Retrofit): DartboardScanApi =
        retrofit.create(DartboardScanApi::class.java)
}
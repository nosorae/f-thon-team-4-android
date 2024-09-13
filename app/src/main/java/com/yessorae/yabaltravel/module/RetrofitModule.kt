package com.yessorae.yabaltravel.module

import com.yessorae.yabaltravel.BuildConfig
import com.yessorae.yabaltravel.common.di.FToneApiQualifier
import com.yessorae.yabaltravel.common.di.KakaoApi
import com.yessorae.yabaltravel.data.source.remote.kakao.api.FToneApi
import com.yessorae.yabaltravel.data.source.remote.kakao.api.KakaoLocalApi
import com.yessorae.yabaltravel.data.source.remote.kakao.common.FToneConstants
import com.yessorae.yabaltravel.data.source.remote.kakao.common.KakaoConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {

    @Provides
    @Singleton
    @KakaoApi
    fun provideKakaoApiRetrofit(): Retrofit {
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .baseUrl(KakaoConstants.KAKAO_API_BASE_URL)
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(
                        Interceptor { chain ->
                            val originalRequest = chain.request()
                            val newRequest = originalRequest.newBuilder()
                                .header(
                                    name = "Authorization",
                                    value = "${KakaoConstants.HEADER_KEY} ${BuildConfig.KAKAO_API_KEY}"
                                )
                                .build()
                            chain.proceed(newRequest)
                        }
                    )
                    .addInterceptor(
                        HttpLoggingInterceptor().apply {
                            if (BuildConfig.DEBUG) {
                                setLevel(HttpLoggingInterceptor.Level.BODY)
                            }
                        }
                    )
                    .build()
            )
            .addConverterFactory(json.asConverterFactory("application/json; charset=utf-8".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    @FToneApiQualifier
    fun provideFToneApiRetrofit(): Retrofit {
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .baseUrl(FToneConstants.FTone_API_BASE_URL)
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(
                        HttpLoggingInterceptor().apply {
                            if (BuildConfig.DEBUG) {
                                setLevel(HttpLoggingInterceptor.Level.BODY)
                            }
                        }
                    )
                    .build()
            )
            .addConverterFactory(json.asConverterFactory("application/json; charset=utf-8".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideKakaoLocalApi(@KakaoApi retrofit: Retrofit): KakaoLocalApi =
        retrofit.create(KakaoLocalApi::class.java)

    @Provides
    @Singleton
    fun provideFToneApi(@FToneApiQualifier retrofit: Retrofit): FToneApi =
        retrofit.create(FToneApi::class.java)
}

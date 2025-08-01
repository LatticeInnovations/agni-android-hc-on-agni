package com.heartcare.agni.di

import android.app.Application
import com.heartcare.agni.BuildConfig
import com.heartcare.agni.FhirApp.Companion.gson
import com.heartcare.agni.data.local.sharedpreferences.PreferenceStorage
import com.heartcare.agni.data.server.api.AuthenticationApiService
import com.heartcare.agni.data.server.api.AuthenticationApiServiceWithToken
import com.heartcare.agni.data.server.api.CVDApiService
import com.heartcare.agni.data.server.api.DispenseApiService
import com.heartcare.agni.data.server.api.FileUploadApiService
import com.heartcare.agni.data.server.api.LabTestAndMedRecordService
import com.heartcare.agni.data.server.api.LevelsApiService
import com.heartcare.agni.data.server.api.PatientApiService
import com.heartcare.agni.data.server.api.PrescriptionApiService
import com.heartcare.agni.data.server.api.HistoryAndTestsApiService
import com.heartcare.agni.data.server.api.ScheduleAndAppointmentApiService
import com.heartcare.agni.data.server.api.SignUpApiService
import com.heartcare.agni.data.server.api.SymptomsAndDiagnosisService
import com.heartcare.agni.data.server.api.VaccinationApiService
import com.heartcare.agni.data.server.api.VitalApiService
import com.heartcare.agni.service.authentication.TokenAuthenticator
import com.heartcare.agni.utils.constants.AuthenticationConstants.AUTHORIZATION
import com.heartcare.agni.utils.constants.AuthenticationConstants.X_ACCESS_TOKEN
import com.heartcare.agni.utils.constants.ErrorConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.IOException
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // ---------------------- AUTH CLIENTS ----------------------

    @Provides
    @Singleton
    @Named("auth_okhttp_no_token")
    fun provideAuthOkHttpClientNoToken(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .apply {
                if (BuildConfig.DEBUG) {
                    val interceptor = HttpLoggingInterceptor()
                    interceptor.level = HttpLoggingInterceptor.Level.BODY
                    addInterceptor(interceptor)
                }
            }.build()
    }

    @Provides
    @Singleton
    @Named("auth_okhttp_with_token")
    fun provideAuthOkHttpClientWithToken(preferenceStorage: PreferenceStorage): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")

                if (preferenceStorage.accessToken.isNotBlank()) {
                    requestBuilder.addHeader(AUTHORIZATION, preferenceStorage.accessToken)
                }

                chain.proceed(requestBuilder.build())
            }
            .apply {
                if (BuildConfig.DEBUG) {
                    val interceptor = HttpLoggingInterceptor()
                    interceptor.level = HttpLoggingInterceptor.Level.BODY
                    addInterceptor(interceptor)
                }
            }.build()
    }

    @Provides
    @Singleton
    @Named("auth_retrofit_no_token")
    fun provideAuthRetrofitNoToken(@Named("auth_okhttp_no_token") okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.HEARTCARE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    @Named("auth_retrofit_with_token")
    fun provideAuthRetrofitWithToken(@Named("auth_okhttp_with_token") okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.HEARTCARE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthenticationApiService(@Named("auth_retrofit_no_token") retrofit: Retrofit): AuthenticationApiService {
        return retrofit.create(AuthenticationApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthenticationApiServiceWithToken(@Named("auth_retrofit_with_token") retrofit: Retrofit): AuthenticationApiServiceWithToken {
        return retrofit.create(AuthenticationApiServiceWithToken::class.java)
    }

    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        preferenceStorage: PreferenceStorage,
        authApiService: AuthenticationApiService,
        application: Application
    ): TokenAuthenticator {
        return TokenAuthenticator(preferenceStorage, authApiService, application)
    }

    // ---------------------- MAIN OKHTTP ----------------------

    @Provides
    @Singleton
    @Named("main_okhttp")
    fun provideMainOkHttpClient(
        preferenceStorage: PreferenceStorage,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .authenticator(tokenAuthenticator)
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestBuilder = originalRequest.newBuilder()
                    .addHeader("Content-Type", "application/json")

                val url = originalRequest.url.toString()

                when {
                    url.startsWith(BuildConfig.HEARTCARE_BASE_URL) -> {
                        if (preferenceStorage.accessToken.isNotBlank()) {
                            requestBuilder.addHeader(AUTHORIZATION, preferenceStorage.accessToken)
                        }
                    }
                    else -> {
                        if (preferenceStorage.accessToken.isNotBlank()) {
                            requestBuilder.addHeader(X_ACCESS_TOKEN, preferenceStorage.accessToken)
                        }
                    }
                }

                try {
                    chain.proceed(requestBuilder.build())
                } catch (e: IOException) {
                    val errorMsg = when (e) {
                        is SocketTimeoutException -> ErrorConstants.SOCKET_TIMEOUT_EXCEPTION
                        else -> ErrorConstants.IO_EXCEPTION
                    }

                    Response.Builder()
                        .request(originalRequest)
                        .protocol(Protocol.HTTP_1_1)
                        .code(200)
                        .message("OK")
                        .body(
                            JSONObject().apply {
                                put("status", 0)
                                put("message", errorMsg)
                            }.toString().toByteArray()
                                .toResponseBody("application/json".toMediaType())
                        )
                        .build()
                }
            }.apply {
                if (BuildConfig.DEBUG) {
                    val interceptor = HttpLoggingInterceptor()
                    interceptor.level = HttpLoggingInterceptor.Level.BODY
                    addInterceptor(interceptor)
                }
            }.build()
    }

    // ---------------------- RETROFITS ----------------------

    @Provides
    @Named("agni")
    @Singleton
    fun provideAgniRetrofit(@Named("main_okhttp") okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Named("heart_care")
    @Singleton
    fun provideHeartCareRetrofit(@Named("main_okhttp") okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.HEARTCARE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // ---------------------- API SERVICES ----------------------

    @Provides
    @Singleton
    fun providePatientApiService(@Named("agni") retrofit: Retrofit): PatientApiService =
        retrofit.create(PatientApiService::class.java)

    @Provides
    @Singleton
    fun providePrescriptionApiService(@Named("agni") retrofit: Retrofit): PrescriptionApiService =
        retrofit.create(PrescriptionApiService::class.java)

    @Provides
    @Singleton
    fun provideScheduleApiService(@Named("agni") retrofit: Retrofit): ScheduleAndAppointmentApiService =
        retrofit.create(ScheduleAndAppointmentApiService::class.java)

    @Provides
    @Singleton
    fun provideFileUploadApiService(@Named("agni") retrofit: Retrofit): FileUploadApiService =
        retrofit.create(FileUploadApiService::class.java)

    @Provides
    @Singleton
    fun provideSignUpApiService(@Named("heart_care") retrofit: Retrofit): SignUpApiService =
        retrofit.create(SignUpApiService::class.java)

    @Provides
    @Singleton
    fun provideCVDApiService(@Named("agni") retrofit: Retrofit): CVDApiService =
        retrofit.create(CVDApiService::class.java)

    @Provides
    @Singleton
    fun provideVitalApiService(@Named("agni") retrofit: Retrofit): VitalApiService =
        retrofit.create(VitalApiService::class.java)

    @Provides
    @Singleton
    fun provideSymptomsAndDiagnosisService(@Named("agni") retrofit: Retrofit): SymptomsAndDiagnosisService =
        retrofit.create(SymptomsAndDiagnosisService::class.java)

    @Provides
    @Singleton
    fun provideLabAndMedRecordService(@Named("agni") retrofit: Retrofit): LabTestAndMedRecordService =
        retrofit.create(LabTestAndMedRecordService::class.java)

    @Provides
    @Singleton
    fun provideDispenseApiService(@Named("agni") retrofit: Retrofit): DispenseApiService =
        retrofit.create(DispenseApiService::class.java)

    @Provides
    @Singleton
    fun provideVaccinationApiService(@Named("agni") retrofit: Retrofit): VaccinationApiService =
        retrofit.create(VaccinationApiService::class.java)

    @Provides
    @Singleton
    fun provideLevelsApiService(@Named("agni") retrofit: Retrofit): LevelsApiService =
        retrofit.create(LevelsApiService::class.java)

    @Provides
    @Singleton
    fun providePriorDxApiService(@Named("agni") retrofit: Retrofit): HistoryAndTestsApiService =
        retrofit.create(HistoryAndTestsApiService::class.java)
}
package com.heartcare.agni.di

import com.heartcare.agni.BuildConfig
import com.heartcare.agni.FhirApp.Companion.gson
import com.heartcare.agni.data.local.sharedpreferences.PreferenceStorage
import com.heartcare.agni.data.server.api.AuthenticationApiService
import com.heartcare.agni.data.server.api.CVDApiService
import com.heartcare.agni.data.server.api.DispenseApiService
import com.heartcare.agni.data.server.api.FileUploadApiService
import com.heartcare.agni.data.server.api.LabTestAndMedRecordService
import com.heartcare.agni.data.server.api.LevelsApiService
import com.heartcare.agni.data.server.api.PatientApiService
import com.heartcare.agni.data.server.api.PrescriptionApiService
import com.heartcare.agni.data.server.api.ScheduleAndAppointmentApiService
import com.heartcare.agni.data.server.api.SignUpApiService
import com.heartcare.agni.data.server.api.VitalApiService
import com.heartcare.agni.data.server.api.SymptomsAndDiagnosisService
import com.heartcare.agni.data.server.api.VaccinationApiService
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

    @Provides
    @Singleton
    fun provideOkHttpClient(preferenceStorage: PreferenceStorage): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                try {
                    chain.proceed(chain.request().newBuilder().also { requestBuilder ->
                        requestBuilder.addHeader("Content-Type", "application/json")
                        if (preferenceStorage.token.isNotBlank()) requestBuilder.addHeader(
                            X_ACCESS_TOKEN,
                            preferenceStorage.token
                        )
                    }.build())
                } catch (e: IOException) {
                    val errorMsg: String = when (e) {
                        is SocketTimeoutException -> ErrorConstants.SOCKET_TIMEOUT_EXCEPTION
                        else -> ErrorConstants.IO_EXCEPTION
                    }
                    Response.Builder()
                        .request(chain.request())
                        .protocol(Protocol.HTTP_1_1)
                        .code(200)
                        .message("OK")
                        .body(
                            "${
                                JSONObject().run {
                                    put("status", 0)
                                    put("message", errorMsg)
                                }
                            }".toByteArray().toResponseBody("application/json".toMediaType())
                        )
                        .build()
                }
            }.also { client ->
                if (BuildConfig.DEBUG) {
                    val interceptor = HttpLoggingInterceptor()
                    interceptor.level = HttpLoggingInterceptor.Level.BODY
                    client.addInterceptor(interceptor)
                }
            }.build()
    }

    @Provides
    @Named("agni")
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Named("heart_care")
    @Singleton
    fun provideHeartCareRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.HEARTCARE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun providePatientApiService(@Named("agni") retrofit: Retrofit): PatientApiService {
        return retrofit.create(PatientApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthenticationApiService(@Named("heart_care") retrofit: Retrofit): AuthenticationApiService {
        return retrofit.create(AuthenticationApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePrescriptionApiService(@Named("agni") retrofit: Retrofit): PrescriptionApiService {
        return retrofit.create(PrescriptionApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideScheduleApiService(@Named("agni") retrofit: Retrofit): ScheduleAndAppointmentApiService {
        return retrofit.create(ScheduleAndAppointmentApiService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideFileUploadApiService(@Named("agni") retrofit: Retrofit): FileUploadApiService {
        return retrofit.create(FileUploadApiService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideSignUpApiService(@Named("heart_care") retrofit: Retrofit): SignUpApiService {
        return retrofit.create(SignUpApiService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideCVDApiService(@Named("agni") retrofit: Retrofit): CVDApiService {
        return retrofit.create(CVDApiService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideVitalAPiService(@Named("agni") retrofit: Retrofit): VitalApiService {
        return retrofit.create(VitalApiService::class.java)
    }
    @Provides
    @Singleton
    internal fun provideSymptomsAndDiagnosisAPiService(@Named("agni") retrofit: Retrofit): SymptomsAndDiagnosisService {
        return retrofit.create(SymptomsAndDiagnosisService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideLabAndMedAPiService(@Named("agni") retrofit: Retrofit): LabTestAndMedRecordService {
        return retrofit.create(LabTestAndMedRecordService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideDispenseApiService(@Named("agni") retrofit: Retrofit): DispenseApiService {
        return retrofit.create(DispenseApiService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideVaccinationApiService(@Named("agni") retrofit: Retrofit): VaccinationApiService {
        return retrofit.create(VaccinationApiService::class.java)
    }

    @Provides
    @Singleton
    internal fun provideLevelsApiService(@Named("agni") retrofit: Retrofit): LevelsApiService {
        return retrofit.create(LevelsApiService::class.java)
    }
}
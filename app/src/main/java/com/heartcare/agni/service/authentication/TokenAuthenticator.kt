package com.heartcare.agni.service.authentication

import android.app.Application
import com.heartcare.agni.FhirApp
import com.heartcare.agni.data.local.sharedpreferences.PreferenceStorage
import com.heartcare.agni.data.server.api.AuthenticationApiService
import com.heartcare.agni.data.server.model.authentication.RefreshTokenRequest
import com.heartcare.agni.utils.constants.AuthenticationConstants.AUTHORIZATION
import com.heartcare.agni.utils.constants.ErrorConstants.SESSION_EXPIRED
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    private val authApiService: AuthenticationApiService,
    private val application: Application
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 3) return null // Avoid loops

        synchronized(this) {
            val currentToken = preferenceStorage.accessToken
            if (currentToken != response.request.header(AUTHORIZATION)) {
                return response.request.newBuilder()
                    .header(AUTHORIZATION, currentToken)
                    .build()
            }

            val refreshToken = preferenceStorage.refreshToken

            return try {
                val refreshResponse = runBlocking {
                    authApiService.refreshToken(RefreshTokenRequest(refreshToken))
                }

                if (refreshResponse.isSuccessful) {
                    val newAccessToken = refreshResponse.headers()[AUTHORIZATION]!!

                    preferenceStorage.accessToken = newAccessToken

                    response.request.newBuilder()
                        .header(AUTHORIZATION, newAccessToken)
                        .build()
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        (application as FhirApp).sessionExpireFlow.postValue(
                            mapOf(
                                Pair("errorReceived", true),
                                Pair("errorMsg", SESSION_EXPIRED)
                            )
                        )
                    }
                    null
                }
            } catch (e: Exception) {
                Timber.e(e)
                null
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
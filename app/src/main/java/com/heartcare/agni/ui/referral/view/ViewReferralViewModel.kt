package com.heartcare.agni.ui.referral.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.heartcare.agni.base.viewmodel.BaseViewModel
import com.heartcare.agni.data.local.repository.healthfacility.HealthFacilityRepository
import com.heartcare.agni.data.local.repository.levels.LevelRepository
import com.heartcare.agni.data.server.model.referral.ReferralResponse
import com.heartcare.agni.di.dispatcher.IoDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

@HiltViewModel
class ViewReferralViewModel @Inject constructor(
    private val healthFacilityRepository: HealthFacilityRepository,
    private val levelRepository: LevelRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): BaseViewModel() {
    var isLaunched by mutableStateOf(false)

    var sourceFacilityName by mutableStateOf("")
    var referredFacilityName by mutableStateOf("")

    fun getDetails(referral: ReferralResponse) {
        viewModelScope.launch(ioDispatcher) {
            val sourceHealthFacility = healthFacilityRepository.getHealthFacilityList().first { it.healthFacilityId == referral.sourceHealthFacilityId }
            val sourceIsland = levelRepository.getLevelByFhirId(sourceHealthFacility.islandId)

            sourceFacilityName = "${sourceHealthFacility.name} | ${sourceIsland.name}"

            val referredHealthFacility = healthFacilityRepository.getHealthFacilityList().first { it.healthFacilityId == referral.healthFacilityId }
            val referredIsland = levelRepository.getLevelByFhirId(referredHealthFacility.islandId)
            val referredAreaCouncil = levelRepository.getLevelByFhirId(referredIsland.precedingLevelId!!)
            val referredProvince = levelRepository.getLevelByFhirId(referredAreaCouncil.precedingLevelId!!)

            referredFacilityName = "${referredHealthFacility.name}\n${referredIsland.name}, ${referredAreaCouncil.name}\n${referredProvince.name}"
        }
    }
}
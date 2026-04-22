package com.heartcare.agni.utils.common

import com.heartcare.agni.data.local.repository.preference.PreferenceRepository
import com.heartcare.agni.data.local.roomdb.dao.ScreeningSiteDao
import com.heartcare.agni.data.local.roomdb.entities.campaign.ScreeningSiteMasterEntity

class ScreeningSiteSeeder(
    private val screeningSiteDao: ScreeningSiteDao,
    private val preferenceRepository: PreferenceRepository
) {
    suspend fun seedMockData() {
        if (!preferenceRepository.isScreeningSiteSeeded()) {
            val mockSites = listOf(
                ScreeningSiteMasterEntity(
                    id = "site-001",
                    name = "Tanna Island Outreach Q2",
                    location = "",
                    areaCouncil = "Tanna Council",
                    serviceMode = "Outreach",
                    fromDate = "2025-01-01",
                    toDate = "2025-03-31",
                    teamLead = "Dr. Sarah Naupa",
                    status = "ACTIVE",
                    email = "sarah.naupa@example.com",
                    mobile = "+678 555 0101"
                ),
                ScreeningSiteMasterEntity(
                    id = "site-002",
                    name = "Port Vila Mobile Clinic",
                    location = "Central Market",
                    areaCouncil = "Vila City",
                    serviceMode = "Mobile",
                    fromDate = "2025-02-15",
                    toDate = "2025-06-30",
                    teamLead = "Nurse Jean Pierre",
                    status = "ACTIVE",
                    email = "jean.pierre@example.com",
                    mobile = "+678 555 0202"
                )
            ).toTypedArray()
            screeningSiteDao.insertScreeningSiteMaster(*mockSites)
            preferenceRepository.setScreeningSiteSeeded(true)
        }
    }
}

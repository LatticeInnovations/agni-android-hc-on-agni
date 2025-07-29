package com.heartcare.agni.data.local.enums

enum class UserRoleEnum(val display: String, val code: Int) {
    RECEPTIONIST("Receptionist", 4),
    PHYSICIAN("Physician", 5),
    PHARMACIST("Community staff", 6),
    NURSE_NURSING_STAFF("Nurse/Nursing staff", 7);

    companion object {
        fun getRoleFromId(id: Int) = run { entries.firstOrNull { it.code == id }?.display ?: "" }
    }
}
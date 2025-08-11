package com.heartcare.agni.data.local.enums

enum class QuitPlan(val code: String, val display: String) {
    YES_BRIEF_QUIT_PLAN("1", "Yes, brief quit plan"),
    YES_INTENSIVE_QUIT_PLAN("2", "Yes, intensive quit plan"),
    NO("3", "No"),
    NO_REFER_TO_INTENSIVE_COUNSELLING("4", "No, refer to intensive counselling");

    companion object {
        fun quitPlanList() = entries.map { it.display }
        fun quitPlanDisplayFromCode(code: String) = entries.firstOrNull { it.code == code }?.display
        fun quitPlanCodeFromDisplay(display: String) = entries.firstOrNull { it.display == display }?.code
    }
}
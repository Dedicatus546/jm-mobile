package com.par9uet.jm.data.models

data class SignData(
    val dailyId: Int,
    val threeDaysCoin: Int,
    val threeDaysExp: Int,
    val sevenDaysCoin: Int,
    val sevenDaysExp: Int,
    val eventName: String,
    val currentProgress: String,
    val dateMap: Map<String, DateMapValue>
) {
    data class DateMapValue(
        val isNextDaySign: Boolean,
        val isLastDaySign: Boolean,
        val isSign: Boolean,
        val isLast: Boolean,
        val hasExtraBonus: Boolean,
    )
}
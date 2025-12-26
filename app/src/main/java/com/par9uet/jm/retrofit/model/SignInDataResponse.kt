package com.par9uet.jm.retrofit.model

data class SignInDataResponse(
    val daily_id: Int,
    val three_days_coin: String,
    val three_days_exp: String,
    val seven_days_coin: String,
    val seven_days_exp: String,
    val event_name: String,
    val background_pc: String,
    val background_phone: String,
    val currentProgress: String,
    val record: List<List<RecordItem>>
) {
    data class RecordItem(
        val date: String,
        val signed: Boolean,
        val bonus: Boolean,
    )
}
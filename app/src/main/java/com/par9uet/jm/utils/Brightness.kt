package com.par9uet.jm.utils

import kotlin.math.pow

// Gamma 转换常数
private const val GAMMA = 2.2f

// 【编码】将系统底层的线性物理亮度值（0~255）转换为符合人眼感知的 Slider 进度值（0.0 ~ 1.0）
fun convertToSlider(systemBrightness: Int): Float {
    val linearFloat = (systemBrightness / 255f).coerceIn(0f, 1f)
    // 幂函数转换：linearFloat ^ (1 / 2.2)
    return linearFloat.pow(1f / GAMMA)
}


// 【解码】将用户拖动 Slider 得到的视觉进度（0.0 ~ 1.0）转换回 Window 需要的线性物理值
fun convertToSystem(sliderValue: Float): Float {
    // 幂函数逆转换：sliderValue ^ 2.2
    val linearFloat = sliderValue.coerceIn(0f, 1f).pow(GAMMA)
    return linearFloat
//    return linearFloat.coerceAtLeast(0.05f)
}
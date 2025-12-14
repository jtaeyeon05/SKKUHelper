package com.skku_team2.skku_helper.utils

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import java.security.MessageDigest

/**
 * 프로젝트 전반에서 사용되는 확장 함수 모음
 */

/**
 * 현재 Context에 맞는 Attribute 색상을 Color Int 형태로 반환
 */
fun Context.getColorAttr(@AttrRes colorAttr: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(colorAttr, typedValue, true)
    return ContextCompat.getColor(this, typedValue.resourceId)
}

/**
 * Color Int가 밝은지 어두운지 반환
 */
fun @receiver:ColorInt Int.isBright(threshold: Double = 0.5): Boolean {
    val visibleColor = if (Color.alpha(this) < 255) {
        ColorUtils.compositeColors(this, Color.WHITE)
    } else this
    val luminance = ColorUtils.calculateLuminance(visibleColor)
    return luminance > threshold
}

/**
 * 문자열을 SHA-256 해시화하여 반환
 */
fun String.toSha256(): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hashBytes = digest.digest(this.toByteArray(Charsets.UTF_8))
    return hashBytes.joinToString("") { "%02x".format(it) }
}

package com.skku_team2.skku_helper.utils

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils


fun Context.getColorAttr(@AttrRes colorAttr: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(colorAttr, typedValue, true)
    return ContextCompat.getColor(this, typedValue.resourceId)
}

fun @receiver:ColorInt Int.isBright(threshold: Double = 0.5): Boolean {
    val visibleColor = if (Color.alpha(this) < 255) {
        ColorUtils.compositeColors(this, Color.WHITE)
    } else this
    val luminance = ColorUtils.calculateLuminance(visibleColor)
    return luminance > threshold
}

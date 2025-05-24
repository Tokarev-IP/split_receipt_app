package com.example.receipt_splitter.main.basic

import com.example.receipt_splitter.receipt.data.services.DataConstantsReceipt
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.round

fun Int.isZero(): Boolean = this == 0

fun Int.isNotZero(): Boolean = this > 0

fun String.isEmail(): Boolean {
    val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
    return emailRegex.matches(this)
}

fun String.isCorrectPassword(): Boolean {

    for (char in this) {
        if (char.isWhitespace())
            return false
    }

    fun isLength(): Boolean {
        return this.length >= 10
    }

    fun isContainNumeric(): Boolean {
        for (char in this) {
            if (char.isDigit())
                return true
        }
        return false
    }

    fun isContainLowercaseLetter(): Boolean {
        for (char in this) {
            if (char.isLetter() && char.isLowerCase())
                return true
        }
        return false
    }

    fun isContainUppercaseLetter(): Boolean {
        for (char in this) {
            if (char.isLetter() && char.isUpperCase())
                return true
        }
        return false
    }

    return isLength() && isContainNumeric() &&
            isContainLowercaseLetter() && isContainUppercaseLetter()
}

fun Float.roundToTwoDecimalPlaces(): Float {
    return (round(this * 100) / 100f)
}

fun Long.convertMillisToDate(): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(this))
}

fun String.getLanguageString(
    translateTo: String?,
    languageText: String = DataConstantsReceipt.LANGUAGE_TEXT,
): String {
    return if (translateTo != null) {
        "$this $languageText $translateTo"
    } else
        this
}

fun Long.convertMillisToMinutes(): Int {
    val min = this / 1000 / 60
    return min.toInt()
}
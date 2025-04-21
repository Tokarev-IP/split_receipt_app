package com.example.receipt_splitter.main.basic

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

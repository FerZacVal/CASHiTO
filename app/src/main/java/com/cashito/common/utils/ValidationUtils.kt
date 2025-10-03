package com.cashito.common.utils

object ValidationUtils {
    fun isNonEmpty(text: String?): Boolean = !text.isNullOrBlank()
}



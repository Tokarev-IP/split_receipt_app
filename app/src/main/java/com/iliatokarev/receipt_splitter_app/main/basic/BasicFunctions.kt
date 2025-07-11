package com.iliatokarev.receipt_splitter_app.main.basic

import android.content.Context

fun getAppVersion(context: Context): String {
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName ?: ""
    } catch (e: Exception) {
        ""
    }
}
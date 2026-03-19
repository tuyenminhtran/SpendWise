package com.darling.spendwise.utils

import android.content.Context

class UserPreferences(context: Context) {

    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    var displayName: String
        get() = prefs.getString("display_name", "Người dùng") ?: "Người dùng"
        set(value) = prefs.edit().putString("display_name", value).apply()

    var phoneNumber: String
        get() = prefs.getString("phone_number", "") ?: ""
        set(value) = prefs.edit().putString("phone_number", value).apply()

    var avatarUri: String
        get() = prefs.getString("avatar_uri", "") ?: ""
        set(value) = prefs.edit().putString("avatar_uri", value).apply()

    var isDarkMode: Boolean
        get() = prefs.getBoolean("dark_mode", false)
        set(value) = prefs.edit().putBoolean("dark_mode", value).apply()
}
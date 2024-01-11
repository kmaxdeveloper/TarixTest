package uz.kmax.tarixtest.tools.other

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Build
import java.util.*

class SharedPref(var context: Context) {

    private var preferences: SharedPreferences

    private lateinit var editor: SharedPreferences.Editor

    init {
        preferences = context.getSharedPreferences("TARIX_TEST", MODE_PRIVATE)
    }

    fun getLanguage() = preferences.getString("LANG", "ru")

    fun loadLocale(context: Context) {
        setLanguage(getLanguage()!!, context)
    }

    fun setLanguage(lang: String, context: Context) {
        editor = preferences.edit()
        editor.putString("LANG", lang)
        editor.apply()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, lang)
        }
        updateResourcesLegacy(context, lang)
    }

    private fun updateResources(context: Context, language: String): Context? {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)//bu joyni uchirib kor
        return context.createConfigurationContext(configuration)
    }


    private fun updateResourcesLegacy(context: Context, language: String): Context? {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        configuration.setLayoutDirection(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
        return context
    }

    fun setWelcomeStatus(resume : Boolean) {
        editor = preferences.edit()
        editor.putBoolean("WELCOME_APP",resume)
        editor.apply()
    }

    //available

    fun getWelcomeStatus() = preferences.getBoolean("WELCOME_APP",true)

    fun setUpdateStatus(update : Boolean){
        editor = preferences.edit()
        editor.putBoolean("UPDATE_AVAILABLE",update)
        editor.apply()
    }

    fun getUpdateAvailable() = preferences.getBoolean("UPDATE_AVAILABLE",false)
}
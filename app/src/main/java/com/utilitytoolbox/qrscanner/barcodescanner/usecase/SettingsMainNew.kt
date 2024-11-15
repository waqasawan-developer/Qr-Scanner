
package com.utilitytoolbox.qrscanner.barcodescanner.usecase


import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import androidx.appcompat.app.AppCompatDelegate
import com.utilitytoolbox.qrscanner.barcodescanner.model
.SearchEngineMain
import com.utilitytoolbox.qrscanner.barcodescanner.utils.unsafeLazy
import javax.inject.Inject

class SettingsMainNew@Inject constructor(val context: Context) {

    companion object {
        const val THEME_SYSTEM_DEFAULT = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        const val THEME_LIGHT_MODE = AppCompatDelegate.MODE_NIGHT_NO
        const val THEME_DARK_MODE = AppCompatDelegate.MODE_NIGHT_YES

        private const val SHARED_PREFERENCES_NAME = "SHARED_PREFERENCES_NAME"
    }

    private enum class Key {
        SELECT_THEME,
        BROWSE_LINKS_AUTO,
        COPY_TO_CLIPBOARD,
        SIMPLE_AUTO_FOCUS,
        FLASH,
        VIBRATE,
        BEEP,
        SELECTED_CAMERA,
        SELECT_CAMERA,
        SAVE_QR_CODES_TO_HISTORY,
        DO_NOT_SAVE_DUPLICATES,
        SEARCH_ENGINE,
        BATCH_SCANNING,
        AUTO_SEARCH

    }

    private val sharedPreferences by unsafeLazy {
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    var theme: Int
        get() = get(Key.SELECT_THEME, THEME_SYSTEM_DEFAULT)
        set(value) {
            set(Key.SELECT_THEME, value)
            applyTheme(value)
        }

    val isDarkThemeEnabled: Boolean
        get() = theme == THEME_DARK_MODE || (theme == THEME_SYSTEM_DEFAULT && isSystemDarkThemeEnabled())

    val barcodeContentColor: Int
        get() = when {
            isDarkThemeEnabled -> Color.WHITE
            else -> Color.BLACK
        }

    val codeBackgroundColor: Int
        get() = when {
            isDarkThemeEnabled -> Color.WHITE
            else -> Color.TRANSPARENT
        }

    var openLinksAutomatically: Boolean
        get() = get(Key.BROWSE_LINKS_AUTO, false)
        set(value) = set(Key.BROWSE_LINKS_AUTO, value)

    var selectedCamera: Int
        get() = get(Key.SELECTED_CAMERA, 0)
        set(value) = set(Key.SELECTED_CAMERA, value)

    var copyToClipboard: Boolean
        get() = get(Key.COPY_TO_CLIPBOARD, true)
        set(value) = set(Key.COPY_TO_CLIPBOARD, value)

    var simpleAutoFocus: Boolean
        get() = get(Key.SIMPLE_AUTO_FOCUS, false)
        set(value) = set(Key.SIMPLE_AUTO_FOCUS, value)

    var flash: Boolean
        get() = get(Key.FLASH, false)
        set(value) = set(Key.FLASH, value)

    var batchScanning: Boolean
        get() = get(Key.BATCH_SCANNING, true)
        set(value) = set(Key.BATCH_SCANNING, value)

  var autoSearch: Boolean
        get() = get(Key.AUTO_SEARCH, false)
        set(value) = set(Key.AUTO_SEARCH, value)


    var vibrate: Boolean
        get() = get(Key.VIBRATE, true)
        set(value) = set(Key.VIBRATE, value)

    var beep: Boolean
        get() = get(Key.BEEP, true)
        set(value) = set(Key.BEEP, value)

    var isBackCamera: Boolean
        get() = get(Key.SELECT_CAMERA, true)
        set(value) = set(Key.SELECT_CAMERA, value)

    var saveQrCodesToHistory: Boolean
        get() = get(Key.SAVE_QR_CODES_TO_HISTORY, true)
        set(value) = set(Key.SAVE_QR_CODES_TO_HISTORY, value)

    var doNotSaveDuplicates: Boolean
        get() = get(Key.DO_NOT_SAVE_DUPLICATES, false)
        set(value) = set(Key.DO_NOT_SAVE_DUPLICATES, value)

    var searchEngine: SearchEngineMain
        get() = get(Key.SEARCH_ENGINE, SearchEngineMain.GOOGLE)
        set(value) = set(Key.SEARCH_ENGINE, value)

    fun setUpTheme() {
        applyTheme(theme)
    }

    private fun get(key: Key, default: Int): Int {
        return sharedPreferences.getInt(key.name, default)
    }

    private fun set(key: Key, value: Int) {
        return sharedPreferences.edit()
            .putInt(key.name, value)
            .apply()
    }

    private fun get(key: Key, default: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key.name, default)
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, default)
    }

    fun setBoolean(key: String, value: Boolean) {
        sharedPreferences.edit()
            .putBoolean(key, value)
            .apply()
    }

    private fun set(key: Key, value: Boolean) {
        sharedPreferences.edit()
            .putBoolean(key.name, value)
            .apply()
    }

    private fun get(key: Key, default: SearchEngineMain = SearchEngineMain.GOOGLE): SearchEngineMain {
        val rawValue = sharedPreferences.getString(key.name, null) ?: default.name
        return SearchEngineMain.valueOf(rawValue)
    }

    private fun set(key: Key, value: SearchEngineMain) {
        sharedPreferences.edit()
            .putString(key.name, value.name)
            .apply()
    }

    private fun applyTheme(theme: Int) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        /* when (theme) {
             AppCompatDelegate.MODE_NIGHT_NO, AppCompatDelegate.MODE_NIGHT_YES -> {
                 AppCompatDelegate.setDefaultNightMode(theme)
             }
             else -> {
                 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                     AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                 } else {
                     AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                 }
             }
         }*/
    }

    private fun isSystemDarkThemeEnabled(): Boolean {
        val mode = context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
        return mode == Configuration.UI_MODE_NIGHT_YES
    }
}
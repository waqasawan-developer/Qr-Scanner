package com.utilitytoolbox.qrscanner.barcodescanner

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }

   /* override fun attachBaseContext(base: Context) {
        super.attachBaseContext(ApplicationLevel.localeManager.setLocale(base))
    }*/

    /*    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
            if (overrideConfiguration != null) {
                val uiMode = overrideConfiguration.uiMode
                overrideConfiguration.setTo(baseContext.resources.configuration)
                overrideConfiguration.uiMode = uiMode
            }
            super.applyOverrideConfiguration(overrideConfiguration)
        }*/
}
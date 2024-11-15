package com.utilitytoolbox.qrscanner.barcodescanner

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class ApplicationClass : Application() {
   /* @Inject
    lateinit var SettingsMainNew: SettingsMainNew*/
    var isAdDismissed: Boolean = false

    override fun onCreate() {
       // handleErrors()
       // setupTheme()
        super.onCreate()
    }


    companion object {
        //lateinit var localeManager: LocaleManagerX
    }


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
      //  localeManager = LocaleManagerX(base)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
     //   localeManager.setLocale(this)
    }

 /*   private fun setupTheme() {
        SettingsMainNew.setUpTheme()
    }*/


}
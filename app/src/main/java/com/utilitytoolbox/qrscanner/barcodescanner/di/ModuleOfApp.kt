package com.utilitytoolbox.qrscanner.barcodescanner.di

import android.content.Context
import androidx.room.Room
import com.utilitytoolbox.qrscanner.barcodescanner.ApplicationClass
import com.utilitytoolbox.qrscanner.barcodescanner.usecase.BarcodeDatabase
import com.utilitytoolbox.qrscanner.barcodescanner.usecase.BarcodeDatabaseFactory
import com.utilitytoolbox.qrscanner.barcodescanner.usecase.SettingsMainNew
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModuleOfApp {

    @Provides
    @Singleton
    fun provideContextProvider(application: ApplicationClass): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun getSettingMain(@ApplicationContext context: Context): SettingsMainNew {
        return SettingsMainNew(context)
    }


    @Provides
    @Singleton
    fun barcodeDatabase(barcodeDatabaseFactory: BarcodeDatabaseFactory): BarcodeDatabase {
        return barcodeDatabaseFactory.getBarcodeDatabase()
    }

    @Provides
    @Singleton
    fun getInstanceDatabase(@ApplicationContext context: Context): BarcodeDatabaseFactory {
        return Room.databaseBuilder(
            context,
            BarcodeDatabaseFactory::class.java,
            "qr_scanner_personel_db"
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }


}
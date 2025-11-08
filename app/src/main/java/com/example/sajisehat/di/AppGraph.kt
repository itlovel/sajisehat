package com.example.sajisehat.di

import android.content.Context
import com.example.sajisehat.data.auth.AuthRepository
import com.example.sajisehat.data.auth.FirebaseAuthRepository
import com.example.sajisehat.data.prefs.AppPrefs
// import scan - nofa
import com.example.sajisehat.data.scan.DocumentScannerDataSource
import com.example.sajisehat.data.scan.MlKitDocumentScannerDataSource
import com.example.sajisehat.data.scan.NutritionLabelParser
import com.example.sajisehat.data.scan.ScanRepository
import com.example.sajisehat.data.scan.ScanRepositoryImpl

object AppGraph {
    val authRepo: AuthRepository by lazy { FirebaseAuthRepository() }
    fun prefs(context: Context) = AppPrefs(context.applicationContext)

    val documentScannerDataSource: DocumentScannerDataSource by lazy {
        MlKitDocumentScannerDataSource()
    }
    fun scanRepository(context: Context): ScanRepository =
        ScanRepositoryImpl(
            appContext = context.applicationContext,
            documentScannerDataSource = documentScannerDataSource,
            nutritionLabelParser = NutritionLabelParser()
        )


}

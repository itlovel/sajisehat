package com.example.sajisehat.di
import okhttp3.OkHttpClient
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit

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
import com.example.sajisehat.data.trek.FirestoreTrekRepository
import com.example.sajisehat.data.trek.TrekRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.example.sajisehat.data.scan.remote.ScanNutritionApiService
import com.example.sajisehat.data.scan.remote.ScanNutritionRemoteDataSource
import com.example.sajisehat.data.scan.ocr.MlKitTextRecognitionDataSource
import com.example.sajisehat.data.scan.ocr.TextRecognitionDataSource

object AppGraph {
    val authRepo: AuthRepository by lazy { FirebaseAuthRepository() }
    fun prefs(context: Context) = AppPrefs(context.applicationContext)

    val documentScannerDataSource: DocumentScannerDataSource by lazy {
        MlKitDocumentScannerDataSource()
    }
    fun scanRepository(context: Context): ScanRepository =
        ScanRepositoryImpl(
            appContext = context.applicationContext,
            layoutRemote = scanNutritionRemoteDataSource,
            textRecognizer = textRecognitionDataSource,
            nutritionLabelParser = NutritionLabelParser()
        )

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    val trekRepository: TrekRepository by lazy {
        FirestoreTrekRepository(firestore)
    }

    // BACKEND ---
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder().build()
    }

    // Retrofit pointing ke backend
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://sajisehat-backend.onrender.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()) // ⬅️ pakai Gson
            .build()
    }


    // API service /scan-nutrition
    private val scanNutritionApiService: ScanNutritionApiService by lazy {
        retrofit.create(ScanNutritionApiService::class.java)
    }

    // Remote data source untuk layout detection
    private val scanNutritionRemoteDataSource: ScanNutritionRemoteDataSource by lazy {
        ScanNutritionRemoteDataSource(scanNutritionApiService)
    }

    // Data source OCR ML Kit Text Recognizer
    private val textRecognitionDataSource: TextRecognitionDataSource by lazy {
        MlKitTextRecognitionDataSource()
    }

}

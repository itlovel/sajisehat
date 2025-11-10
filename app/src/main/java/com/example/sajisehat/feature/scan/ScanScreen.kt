package com.example.sajisehat.feature.scan

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.sajisehat.navigation.Dest
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import kotlinx.coroutines.launch
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun ScanScreen(
    state: ScanUiState,
    onRequestCameraPermission: () -> Unit,
    onScanAgain: () -> Unit,
    onToggleExpanded: () -> Unit,
    onPickFromGallery: () -> Unit,
    onBackFromResult: () -> Unit,
    onSaveToTrek: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (state.step) {
        ScanStep.PERMISSION -> ScanPermissionSection(
            permissionStatus = state.permissionStatus,
            onGrantClick = onRequestCameraPermission
        )

        ScanStep.SCANNING -> ScanCameraSection(
            isProcessing = state.isProcessing,
            onPickFromGallery = onPickFromGallery
        )

        ScanStep.PROCESSING -> ScanCameraSection(
            isProcessing = true,
            onPickFromGallery = onPickFromGallery
        )

        ScanStep.RESULT -> ScanResultSection(
            result = state.lastResult,
            isExpanded = state.isExpandedInfo,
            onToggleExpanded = onToggleExpanded,
            onScanAgain = onScanAgain,
            onBack = onBackFromResult,
            onSaveToTrek = onSaveToTrek
        )
    }
}


@Composable
fun ScanRoute(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as Activity

    val viewModel: ScanViewModel = viewModel(
        factory = ScanViewModelFactory(context.applicationContext)
    )
    val state by viewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // 🔹 FLAG: sudah pernah auto-start scan?
    var hasStartedScan by rememberSaveable { mutableStateOf(false) }

    // === ML Kit Document Scanner options & client ===
    val scannerOptions = remember {
        GmsDocumentScannerOptions.Builder()
            .setGalleryImportAllowed(true)
            .setPageLimit(3)
            .setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_JPEG)
            .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
            .build()
    }
    val scannerClient = remember { GmsDocumentScanning.getClient(scannerOptions) }

    // launcher scanner
    val scannerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val scanResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
            val uris = mutableListOf<Uri>()
            scanResult?.pages?.forEach { page ->
                page.imageUri?.let { uris.add(it) }
            }
            if (uris.isNotEmpty()) {
                // proses seperti biasa → isi lastResult + step RESULT di ViewModel
                viewModel.onScanImagesResult(uris)
                // ❗ JANGAN navigate ke Home di sini, biarkan ScanScreen menampilkan hasil
                return@rememberLauncherForActivityResult
            }
        }

        // sampai sini: user cancel / error / nggak ada uri → balik ke Home
        navController.navigate(Dest.Home.route) {
            popUpTo(Dest.Home.route) { inclusive = false }
        }
    }

    // launcher galeri manual (pakai GetContent) – masih bisa dipakai dari layar hasil
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        viewModel.onScanImagesResult(listOf(uri))
    }

    fun startScan() {
        scannerClient.getStartScanIntent(activity)
            .addOnSuccessListener { intentSender ->
                val request = IntentSenderRequest.Builder(intentSender).build()
                scannerLauncher.launch(request)
            }
            .addOnFailureListener { e ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        e.message ?: "Gagal memulai scanner."
                    )
                }
                // kalau scanner gagal banget → balik ke Home
                navController.navigate(Dest.Home.route) {
                    popUpTo(Dest.Home.route) { inclusive = false }
                }
            }
    }

    // === AUTO SCAN SEKALI SAAT MASUK HALAMAN SCAN ===
    LaunchedEffect(hasStartedScan) {
        if (!hasStartedScan) {
            hasStartedScan = true
            startScan()
        }
    }

    // snackbar error dari ViewModel (tetap dipakai)
    LaunchedEffect(state.errorMessage) {
        val msg = state.errorMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.onErrorShown()
        }
    }

    // TAMPILKAN UI HASIL HANYA KALAU SUDAH ADA RESULT
    if (state.step == ScanStep.RESULT) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { padding ->
            ScanScreen(
                state = state,
                // permission nggak dipakai lagi, tapi kalau kepanggil kita langsung scan aja
                onRequestCameraPermission = {
                    startScan()
                },
                // "Scan Lagi" dari layar hasil → langsung buka scanner lagi
                onScanAgain = {
                    // reset state kalau perlu
                    viewModel.onScanAgainClicked()
                    startScan()
                },
                onToggleExpanded = { viewModel.onToggleExpanded() },
                onPickFromGallery = { galleryLauncher.launch("image/*") },
                onBackFromResult = {
                    // back dari halaman hasil → balik ke Home
                    navController.navigate(Dest.Home.route) {
                        popUpTo(Dest.Home.route) { inclusive = false }
                    }
                },

                onSaveToTrek = {
                    val sugar = state.lastResult?.sugarPerServingGram ?: 0.0

                    navController.navigate(
                        Dest.SaveTrek.route(sugar)
                    )
                },

                modifier = modifier.padding(padding)
            )
        }
    }
    // else: sebelum ada result, kita nggak gambar apa-apa → user hanya lihat UI kamera ML Kit
}

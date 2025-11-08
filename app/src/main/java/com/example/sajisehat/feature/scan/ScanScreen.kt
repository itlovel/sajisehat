package com.example.sajisehat.feature.scan

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import kotlinx.coroutines.launch



import android.net.Uri

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
                viewModel.onScanImagesResult(uris)
            }
        }
    }

    // launcher galeri manual (pakai GetContent)
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
            }
    }

    // FLAG supaya tidak auto-scan berkali-kali
    var hasRequestedScan by remember { mutableStateOf(false) }

    // Auto jalankan scanner saat masuk step SCANNING
    LaunchedEffect(state.step) {
        if (state.step == ScanStep.SCANNING && !state.isProcessing && !hasRequestedScan) {
            hasRequestedScan = true
            startScan()
        }
        if (state.step != ScanStep.SCANNING) {
            hasRequestedScan = false
        }
    }

    // snackbar error dari ViewModel
    LaunchedEffect(state.errorMessage) {
        val msg = state.errorMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.onErrorShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        ScanScreen(
            state = state,
            onRequestCameraPermission = {
                viewModel.onCameraPermissionResult(
                    granted = true,
                    permanentlyDenied = false
                )
            },
            onStartScan = { /* sekarang dipanggil otomatis via LaunchedEffect */ },
            onScanAgain = { viewModel.onScanAgainClicked() },
            onToggleExpanded = { viewModel.onToggleExpanded() },
            onPickFromGallery = { galleryLauncher.launch("image/*") },
            modifier = modifier.padding(padding)
        )
    }
}


@Composable
fun ScanScreen(
    state: ScanUiState,
    onRequestCameraPermission: () -> Unit,
    onStartScan: () -> Unit,
    onScanAgain: () -> Unit,
    onToggleExpanded: () -> Unit,
    onPickFromGallery: () -> Unit,        // 👈 tambah
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
            onScanAgain = onScanAgain
        )
    }
}



package com.dartscore.feature.play.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.dartscore.feature.play.domain.ScanResult
import java.io.File

@Composable
fun ScanScreen(
    onResult: (ScanResult) -> Unit,
    onCancel: () -> Unit,
    viewModel: ScanViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    var capturedFile by remember { mutableStateOf<File?>(null) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val scanState by viewModel.state.collectAsStateWithLifecycle()

    Box(Modifier.fillMaxSize().background(Color.Black)) {

        if (!hasPermission) {
            Column(
                Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text("Potrzebny dostęp do aparatu", color = Color.White)
                Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) { Text("Zezwól") }
                OutlinedButton(onClick = onCancel) { Text("Wróć") }
            }
            return
        }

        val file = capturedFile
        if (file == null) {
            CameraPreview(imageCapture, Modifier.fillMaxSize())
            TargetCircleOverlay(Modifier.fillMaxSize())
            IconButton(
                onClick = onCancel,
                modifier = Modifier.align(Alignment.TopStart).padding(8.dp),
            ) { Icon(Icons.Filled.Close, contentDescription = "Zamknij", tint = Color.White) }
            Button(
                onClick = { takePhoto(context, imageCapture) { saved -> capturedFile = saved } },
                modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp),
            ) { Text("Zrób zdjęcie") }
        } else {
            AsyncImage(
                model = file,
                contentDescription = "Wykonane zdjęcie",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )

            fun retake() {
                file.delete(); capturedFile = null; viewModel.reset()
            }

            Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(24.dp)) {
                when (val st = scanState) {
                    is ScanUiState.Idle -> Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = { retake() }, modifier = Modifier.weight(1f)) { Text("Zrób nowe") }
                        Button(onClick = { viewModel.scan(file) }, modifier = Modifier.weight(1f)) { Text("Prześlij dalej") }
                    }
                    is ScanUiState.Loading -> Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        CircularProgressIndicator(color = Color.White)
                        Text("Analizuję tarczę...", color = Color.White)
                    }
                    is ScanUiState.Success -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Odczyt: ${st.result.darts.joinToString(", ").ifBlank { "brak" }}", color = Color.White)
                        Text("Suma: ${st.result.total}" + if (st.result.mockMode) "  (tryb MOCK)" else "", color = Color.White)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(onClick = { retake() }, modifier = Modifier.weight(1f)) { Text("Zrób nowe") }
                            Button(onClick = { viewModel.reset(); onResult(st.result) }, modifier = Modifier.weight(1f)) { Text("Gotowe") }
                        }
                    }
                    is ScanUiState.Error -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(st.message, color = Color.White)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(onClick = { retake() }, modifier = Modifier.weight(1f)) { Text("Zrób nowe") }
                            Button(onClick = { viewModel.scan(file) }, modifier = Modifier.weight(1f)) { Text("Ponów") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CameraPreview(imageCapture: ImageCapture, modifier: Modifier = Modifier) {
    val lifecycleOwner = LocalLifecycleOwner.current
    androidx.compose.ui.viewinterop.AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val providerFuture = ProcessCameraProvider.getInstance(ctx)
            providerFuture.addListener({
                val provider = providerFuture.get()
                val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
                try {
                    provider.unbindAll()
                    imageCapture.targetRotation = previewView.display.rotation
                    provider.bindToLifecycle(
                        lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture,
                    )
                } catch (_: Exception) { }
            }, ContextCompat.getMainExecutor(ctx))
            previewView
        },
    )
}

@Composable
private fun TargetCircleOverlay(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val radius = (minOf(size.width, size.height) / 2f) - 8.dp.toPx()
        drawCircle(
            color = Color.White,
            radius = radius,
            center = Offset(size.width / 2f, size.height / 2f),
            style = Stroke(width = 3.dp.toPx()),
        )
    }
}

private fun takePhoto(context: android.content.Context, imageCapture: ImageCapture, onSaved: (File) -> Unit) {
    val file = File(context.cacheDir, "scan_${System.currentTimeMillis()}.jpg")
    val output = ImageCapture.OutputFileOptions.Builder(file).build()
    imageCapture.takePicture(
        output,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(results: ImageCapture.OutputFileResults) = onSaved(file)
            override fun onError(exception: ImageCaptureException) { }
        },
    )
}
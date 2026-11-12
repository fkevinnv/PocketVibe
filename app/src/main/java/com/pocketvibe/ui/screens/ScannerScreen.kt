package com.pocketvibe.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay

@Composable
fun ScannerScreen(
    onScanned: (amount: Double) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasPermission by remember { mutableStateOf(false) }
    var scanning by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { hasPermission = it }

    LaunchedEffect(Unit) { launcher.launch(Manifest.permission.CAMERA) }

    // Láser: sube y baja infinitamente
    val laserAnim = rememberInfiniteTransition(label = "laser")
    val laserY by laserAnim.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            tween(1600, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "laserY"
    )

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        if (hasPermission) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).also { view ->
                        val provider = ProcessCameraProvider.getInstance(ctx)
                        provider.addListener({
                            val cameraProvider = provider.get()
                            val preview = Preview.Builder().build().also {
                                it.surfaceProvider = view.surfaceProvider
                            }
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview
                            )
                        }, ContextCompat.getMainExecutor(ctx))
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                "Se necesita permiso de cámara para escanear",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center).padding(32.dp)
            )
        }

        // Overlay: marco de escaneo + línea láser
        ScannerOverlay(laserY)

        TextButton(
            onClick = onClose,
            modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
        ) { Text("✕", color = Color.White) }

        Column(
            Modifier.align(Alignment.BottomCenter).padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                if (scanning) "Analizando ticket…" else "Enfoca el ticket en el recuadro",
                color = Color.White
            )
            Spacer(Modifier.height(16.dp))
            Button(
                enabled = !scanning,
                onClick = { scanning = true }
            ) { Text(if (scanning) "Procesando…" else "📸 Capturar") }
        }
    }

    LaunchedEffect(scanning) {
        if (scanning) {
            delay(1800)
            // Simulación de procesamiento OCR.
            // Integración real: ML Kit TextRecognition sobre ImageProxy + regex
            // """(?i)total.*?(\d+[.,]\d{2})""" para extraer el importe.
            onScanned(23.45)
        }
    }
}

@Composable
private fun ScannerOverlay(laserY: Float) {
    Canvas(Modifier.fillMaxSize()) {
        val w = size.width * 0.75f
        val h = size.height * 0.35f
        val left = (size.width - w) / 2
        val top = (size.height - h) / 2

        // Marco
        drawRoundRect(
            color = Color(0xFF4ADE80),
            topLeft = Offset(left, top),
            size = Size(w, h),
            cornerRadius = CornerRadius(32f, 32f),
            style = Stroke(width = 6f)
        )
        // Línea láser
        val laserPosY = top + h * laserY
        drawLine(
            color = Color(0xFFFF3B3B),
            start = Offset(left + 12f, laserPosY),
            end = Offset(left + w - 12f, laserPosY),
            strokeWidth = 8f
        )
        // Resplandor
        drawRect(
            color = Color(0xFFFF3B3B).copy(alpha = 0.15f),
            topLeft = Offset(left, laserPosY - 60f),
            size = Size(w, 120f)
        )
    }
}

package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.VerifiedGovtGreen
import java.util.concurrent.Executors

@OptIn(ExperimentalGetImage::class)
private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap {
    val bitmap = imageProxy.toBitmap()
    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
    val rotatedBitmap = if (rotationDegrees != 0) {
        val matrix = Matrix().apply {
            postRotate(rotationDegrees.toFloat())
        }
        val result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        if (result != bitmap) {
            bitmap.recycle()
        }
        result
    } else {
        bitmap
    }
    return if (rotatedBitmap.config == Bitmap.Config.HARDWARE) {
        val softwareCopy = rotatedBitmap.copy(Bitmap.Config.ARGB_8888, false)
        rotatedBitmap.recycle()
        softwareCopy ?: rotatedBitmap
    } else {
        rotatedBitmap
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraCaptureScreen(
    onImageCaptured: (Bitmap) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    titleText: String = "📷 दस्तावेज लाइव कैमरा स्कैन",
    subTitleText: String = "दस्तावेज को आयताकार फ्रेम के अंदर रखें और फोटो खींचें"
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            Toast.makeText(context, "कैमरा उपयोग करने के लिए अनुमति आवश्यक है", Toast.LENGTH_SHORT).show()
        }
    }

    if (!hasCameraPermission) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF111827))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = SaffronPrimary.copy(alpha = 0.2f),
                        modifier = Modifier.size(72.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = SaffronPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    Text(
                        text = "कैमरा अनुमति आवश्यक है",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "दस्तावेज स्कैन करने और AI द्वारा वैधता जांचने के लिए कैमरा एक्सेस की आवश्यकता है।",
                        fontSize = 14.sp,
                        color = Color.LightGray,
                        textAlign = TextAlign.Center
                    )

                    Button(
                        onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Camera, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("कैमरा अनुमति दें (Grant Camera Permission)", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onClose,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("रद्द करें (Cancel)")
                    }
                }
            }
        }
        return
    }

    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    var flashMode by remember { mutableIntStateOf(ImageCapture.FLASH_MODE_OFF) }
    var isCapturing by remember { mutableStateOf(false) }

    val previewView = remember { PreviewView(context) }
    val imageCapture = remember {
        ImageCapture.Builder()
            .setFlashMode(flashMode)
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()
    }

    LaunchedEffect(lensFacing, flashMode) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                cameraProvider.unbindAll()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                imageCapture.flashMode = flashMode

                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(lensFacing)
                    .build()

                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Log.e("CameraCaptureScreen", "Camera binding failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    // Scanning animation for laser line overlay
    val infiniteTransition = rememberInfiniteTransition(label = "laserScan")
    val laserYRatio by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserPosition"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // CameraX Live Preview View
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // Document Viewfinder Overlay
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Document frame dimensions (Aspect ratio approx 1.58 like ID cards / certificates)
            val rectWidth = canvasWidth * 0.88f
            val rectHeight = rectWidth / 1.58f
            val left = (canvasWidth - rectWidth) / 2f
            val top = (canvasHeight - rectHeight) / 2.3f
            val right = left + rectWidth
            val bottom = top + rectHeight

            // Draw viewfinder rectangle border
            drawRoundRect(
                color = Color(0xFF4CAF50),
                topLeft = Offset(left, top),
                size = Size(rectWidth, rectHeight),
                cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
                style = Stroke(
                    width = 3.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f), 0f)
                )
            )

            // Draw Corner Guides
            val cornerLength = 32.dp.toPx()
            val cornerStroke = 5.dp.toPx()
            val cornerColor = Color(0xFFFF9800)

            // Top-Left
            drawLine(cornerColor, Offset(left - 2f, top), Offset(left + cornerLength, top), cornerStroke)
            drawLine(cornerColor, Offset(left, top - 2f), Offset(left, top + cornerLength), cornerStroke)

            // Top-Right
            drawLine(cornerColor, Offset(right + 2f, top), Offset(right - cornerLength, top), cornerStroke)
            drawLine(cornerColor, Offset(right, top - 2f), Offset(right, top + cornerLength), cornerStroke)

            // Bottom-Left
            drawLine(cornerColor, Offset(left - 2f, bottom), Offset(left + cornerLength, bottom), cornerStroke)
            drawLine(cornerColor, Offset(left, bottom + 2f), Offset(left, bottom - cornerLength), cornerStroke)

            // Bottom-Right
            drawLine(cornerColor, Offset(right + 2f, bottom), Offset(right - cornerLength, bottom), cornerStroke)
            drawLine(cornerColor, Offset(right, bottom + 2f), Offset(right, bottom - cornerLength), cornerStroke)

            // Animated Scanning Laser Line
            val laserY = top + (rectHeight * laserYRatio)
            drawLine(
                color = Color(0xFFFF3D00),
                start = Offset(left + 12.dp.toPx(), laserY),
                end = Offset(right - 12.dp.toPx(), laserY),
                strokeWidth = 3.dp.toPx()
            )
        }

        // Top Header Overlay Bar
        Surface(
            color = Color.Black.copy(alpha = 0.65f),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = titleText,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subTitleText,
                        color = Color.LightGray,
                        fontSize = 11.sp
                    )
                }

                IconButton(
                    onClick = {
                        flashMode = when (flashMode) {
                            ImageCapture.FLASH_MODE_OFF -> ImageCapture.FLASH_MODE_ON
                            ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_AUTO
                            else -> ImageCapture.FLASH_MODE_OFF
                        }
                    }
                ) {
                    val (icon, tint) = when (flashMode) {
                        ImageCapture.FLASH_MODE_ON -> Icons.Default.FlashOn to Color(0xFFFFD54F)
                        ImageCapture.FLASH_MODE_AUTO -> Icons.Default.FlashAuto to Color(0xFF81C784)
                        else -> Icons.Default.FlashOff to Color.White
                    }
                    Icon(icon, contentDescription = "Flash", tint = tint)
                }
            }
        }

        // Instruction badge overlay
        Surface(
            color = Color(0xFF1565C0).copy(alpha = 0.85f),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CropFree,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "दस्तावेज़ को हरे फ्रेम में सीधा रखकर स्पष्ट फोटो खींचें",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Bottom Camera Controls Bar
        Surface(
            color = Color.Black.copy(alpha = 0.8f),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Front / Back Lens Toggle
                IconButton(
                    onClick = {
                        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                            CameraSelector.LENS_FACING_FRONT
                        } else {
                            CameraSelector.LENS_FACING_BACK
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.Cameraswitch,
                        contentDescription = "Switch Camera",
                        tint = Color.White
                    )
                }

                // Big Shutter Button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(SaffronPrimary)
                        .border(4.dp, Color.White, CircleShape)
                        .clickable(enabled = !isCapturing) {
                            if (!isCapturing) {
                                isCapturing = true
                                val executor = Executors.newSingleThreadExecutor()
                                imageCapture.takePicture(
                                    executor,
                                    object : ImageCapture.OnImageCapturedCallback() {
                                        override fun onCaptureSuccess(imageProxy: ImageProxy) {
                                            try {
                                                val bitmap = imageProxyToBitmap(imageProxy)
                                                imageProxy.close()
                                                executor.shutdown()
                                                ContextCompat.getMainExecutor(context).execute {
                                                    isCapturing = false
                                                    onImageCaptured(bitmap)
                                                }
                                            } catch (e: Exception) {
                                                imageProxy.close()
                                                executor.shutdown()
                                                ContextCompat.getMainExecutor(context).execute {
                                                    isCapturing = false
                                                    Toast.makeText(context, "प्रोसेसिंग त्रुटि: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }

                                        override fun onError(exception: ImageCaptureException) {
                                            executor.shutdown()
                                            ContextCompat.getMainExecutor(context).execute {
                                                isCapturing = false
                                                Toast.makeText(context, "फोटो कैप्चर त्रुटि: ${exception.localizedMessage}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                )
                            }
                        }
                ) {
                    if (isCapturing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(36.dp)
                        )
                    } else {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Take Photo",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                // Close / Exit
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Done",
                        tint = VerifiedGovtGreen
                    )
                }
            }
        }
    }
}

package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.MainViewModel
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.VerifiedGovtGreen
import java.io.ByteArrayOutputStream

private fun decodeUriToSoftwareBitmap(context: Context, uri: Uri, maxDimension: Int = 1024): Bitmap? {
    return try {
        var inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeStream(inputStream, null, options)
        inputStream.close()

        val origWidth = options.outWidth
        val origHeight = options.outHeight
        if (origWidth <= 0 || origHeight <= 0) return null

        var sampleSize = 1
        while ((origWidth / sampleSize) > maxDimension || (origHeight / sampleSize) > maxDimension) {
            sampleSize *= 2
        }

        inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val decodeOptions = BitmapFactory.Options().apply {
            inJustDecodeBounds = false
            inSampleSize = sampleSize
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        val bitmap = BitmapFactory.decodeStream(inputStream, null, decodeOptions)
        inputStream.close()

        if (bitmap != null && bitmap.config == Bitmap.Config.HARDWARE) {
            val softwareCopy = bitmap.copy(Bitmap.Config.ARGB_8888, false)
            bitmap.recycle()
            softwareCopy ?: bitmap
        } else {
            bitmap
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun generateSampleDocBitmap(docType: String, stateName: String): Bitmap {
    val width = 800
    val height = 500
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val cleanState = stateName.replace(Regex("""\s*\(.*?\)\s*"""), "").trim()

    val bgPaint = Paint().apply {
        color = android.graphics.Color.parseColor("#FFFDF5")
        style = Paint.Style.FILL
    }
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

    val borderPaint = Paint().apply {
        color = android.graphics.Color.parseColor("#1565C0")
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }
    canvas.drawRect(20f, 20f, (width - 20).toFloat(), (height - 20).toFloat(), borderPaint)

    val goldPaint = Paint().apply {
        color = android.graphics.Color.parseColor("#FF9800")
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
    canvas.drawRect(28f, 28f, (width - 28).toFloat(), (height - 28).toFloat(), goldPaint)

    val titlePaint = Paint().apply {
        color = android.graphics.Color.parseColor("#0D47A1")
        textSize = 24f
        isFakeBoldText = true
        textAlign = Paint.Align.CENTER
    }

    val docTitlePaint = Paint().apply {
        color = android.graphics.Color.parseColor("#B71C1C")
        textSize = 28f
        isFakeBoldText = true
        textAlign = Paint.Align.CENTER
    }

    val textPaint = Paint().apply {
        color = android.graphics.Color.parseColor("#212121")
        textSize = 20f
    }

    when {
        docType.contains("आधार") || docType.contains("Aadhaar") -> {
            canvas.drawText("GOVERNMENT OF INDIA - UNIQUE IDENTIFICATION AUTHORITY OF INDIA", (width / 2).toFloat(), 65f, titlePaint)
            canvas.drawText("आधार कार्ड (Aadhaar Card) - आम आदमी का अधिकार", (width / 2).toFloat(), 110f, docTitlePaint)
            canvas.drawText("आधार संख्या (Aadhaar No): 5829 1029 4812", 50f, 175f, textPaint)
            canvas.drawText("नाम (Name): राजेश शर्मा (Rajesh Sharma)", 50f, 215f, textPaint)
            canvas.drawText("जन्म तिथि (DOB): 12/08/1988 | लिंग (Gender): पुरुष", 50f, 255f, textPaint)
            canvas.drawText("पता (Address): मकान नं 42, $cleanState, India", 50f, 295f, textPaint)
            canvas.drawText("जारीकर्ता (Authority): UIDAI / भारत सरकार", 50f, 335f, textPaint)
        }
        docType.contains("पैन") || docType.contains("PAN") -> {
            canvas.drawText("INCOME TAX DEPARTMENT - GOVERNMENT OF INDIA", (width / 2).toFloat(), 65f, titlePaint)
            canvas.drawText("स्थायी खाता संख्या कार्ड (PAN CARD)", (width / 2).toFloat(), 110f, docTitlePaint)
            canvas.drawText("पैन संख्या (PAN No): ABCDE1234F", 50f, 175f, textPaint)
            canvas.drawText("नाम (Name): सुनीता वर्मा (Sunita Verma)", 50f, 215f, textPaint)
            canvas.drawText("पिता का नाम (Father's Name): रमेश वर्मा", 50f, 255f, textPaint)
            canvas.drawText("जन्म तिथि (Date of Birth): 05/11/1992", 50f, 295f, textPaint)
            canvas.drawText("जारीकर्ता (Authority): Income Tax Dept, India", 50f, 335f, textPaint)
        }
        docType.contains("राशन") || docType.contains("Ration") -> {
            canvas.drawText("खाद्य एवं नागरिक आपूर्ति विभाग - $cleanState सरकार", (width / 2).toFloat(), 65f, titlePaint)
            canvas.drawText("राष्ट्रीय खाद्य सुरक्षा अधिनियम (NFSA RATION CARD)", (width / 2).toFloat(), 110f, docTitlePaint)
            canvas.drawText("राशन कार्ड संख्या: 109283749182", 50f, 175f, textPaint)
            canvas.drawText("मुखिया का नाम: श्रीमती माया देवी (Smt. Maya Devi)", 50f, 215f, textPaint)
            canvas.drawText("कार्ड श्रेणी: पात्र गृहस्थी (PHH) / बीपीएल", 50f, 255f, textPaint)
            canvas.drawText("कुल सदस्य संख्या: 5 सदस्य", 50f, 295f, textPaint)
            canvas.drawText("जारीकर्ता राज्य: $cleanState (State Food Dept)", 50f, 335f, textPaint)
        }
        docType.contains("ड्राइविंग") || docType.contains("DL") -> {
            canvas.drawText("UNION / STATE TRANSPORT DEPARTMENT - $cleanState", (width / 2).toFloat(), 65f, titlePaint)
            canvas.drawText("भारतीय ड्राइविंग लाइसेंस (INDIAN DRIVING LICENCE)", (width / 2).toFloat(), 110f, docTitlePaint)
            canvas.drawText("लाइसेंस संख्या (DL No): UP-0420210082716", 50f, 175f, textPaint)
            canvas.drawText("नाम (Name): विक्रम सिंह (Vikram Singh)", 50f, 215f, textPaint)
            canvas.drawText("जारी तिथि: 15/01/2021 | वैध तिथि (Valid Till): 14/01/2041", 50f, 255f, textPaint)
            canvas.drawText("वाहन वर्ग (Vehicle Class): LMV / MCWG", 50f, 295f, textPaint)
            canvas.drawText("प्राधिकरण: RTO Office, $cleanState", 50f, 335f, textPaint)
        }
        docType.contains("आयुष्मान") || docType.contains("Ayushman") -> {
            canvas.drawText("NATIONAL HEALTH AUTHORITY - GOVERNMENT OF INDIA", (width / 2).toFloat(), 65f, titlePaint)
            canvas.drawText("आयुष्मान भारत कार्ड (AYUSHMAN BHARAT PM-JAY)", (width / 2).toFloat(), 110f, docTitlePaint)
            canvas.drawText("PMJAY ID: P18920192801", 50f, 175f, textPaint)
            canvas.drawText("लाभार्थी नाम: अनीता देवी (Anita Devi)", 50f, 215f, textPaint)
            canvas.drawText("स्वास्थ्य सुरक्षा कवर: ₹5,00,000/- प्रति वर्ष मुफ्त इलाज", 50f, 255f, textPaint)
            canvas.drawText("राज्य: $cleanState | ई-केवाईसी स्थिति: Verified 🟢", 50f, 295f, textPaint)
            canvas.drawText("जारीकर्ता: एनएचए / स्वास्थ्य एवं परिवार कल्याण मंत्रालय", 50f, 335f, textPaint)
        }
        docType.contains("ई-श्रम") || docType.contains("e-Shram") -> {
            canvas.drawText("MINISTRY OF LABOUR & EMPLOYMENT - GOVT OF INDIA", (width / 2).toFloat(), 65f, titlePaint)
            canvas.drawText("ई-श्रम कार्ड (e-SHRAM UNORGANISED WORKER ID)", (width / 2).toFloat(), 110f, docTitlePaint)
            canvas.drawText("UAN संख्या: 1009 2831 0921", 50f, 175f, textPaint)
            canvas.drawText("श्रमिक का नाम: रमेश कुमार (Ramesh Kumar)", 50f, 215f, textPaint)
            canvas.drawText("व्यवसाय: कृषि / निर्माण कार्य (Agriculture/Construction)", 50f, 255f, textPaint)
            canvas.drawText("दुर्घटना बीमा लाभ: ₹2,00,000/- सुरक्षा कवर", 50f, 295f, textPaint)
            canvas.drawText("राज्य: $cleanState", 50f, 335f, textPaint)
        }
        docType.contains("जाति") -> {
            canvas.drawText("$cleanState सरकार - राजस्व विभाग (ServicePlus / e-District)", (width / 2).toFloat(), 65f, titlePaint)
            canvas.drawText("जाति प्रमाण पत्र (CASTE CERTIFICATE)", (width / 2).toFloat(), 110f, docTitlePaint)
            canvas.drawText("प्रमाणपत्र संख्या: 202301982731", 50f, 175f, textPaint)
            canvas.drawText("आवेदनकर्ता का नाम: महेश कुमार (Mahesh Kumar)", 50f, 215f, textPaint)
            canvas.drawText("आवेदित वर्ग: अन्य पिछड़ा वर्ग (OBC / SC)", 50f, 255f, textPaint)
            canvas.drawText("जारी तिथि: 10/06/2022 | मियाद: आजीवन वैध (Lifetime)", 50f, 295f, textPaint)
            canvas.drawText("जारीकर्ता प्राधिकरण: तहसीलदार / SDM, $cleanState", 50f, 335f, textPaint)
        }
        docType.contains("निवास") || docType.contains("Domicile") -> {
            canvas.drawText("$cleanState सरकार - ई-डिस्ट्रिक्ट पोर्टल", (width / 2).toFloat(), 65f, titlePaint)
            canvas.drawText("मूल निवास / अधिवास प्रमाण पत्र (DOMICILE CERTIFICATE)", (width / 2).toFloat(), 110f, docTitlePaint)
            canvas.drawText("प्रमाणपत्र संख्या: 30192837190", 50f, 175f, textPaint)
            canvas.drawText("नागरिक का नाम: प्रिया शर्मा (Priya Sharma)", 50f, 215f, textPaint)
            canvas.drawText("निवासी: $cleanState, भारत", 50f, 255f, textPaint)
            canvas.drawText("जारी तिथि: 01/03/2023 | वैध स्थिति: आजीवन मान्य 🟢", 50f, 295f, textPaint)
            canvas.drawText("सत्यापन: राजस्व विभाग $cleanState", 50f, 335f, textPaint)
        }
        else -> {
            canvas.drawText("$cleanState सरकार - आधिकारिक e-District पोर्टल", (width / 2).toFloat(), 65f, titlePaint)
            canvas.drawText("आय प्रमाण पत्र (INCOME CERTIFICATE)", (width / 2).toFloat(), 110f, docTitlePaint)
            canvas.drawText("प्रमाणपत्र संख्या (Doc No): 198223004561234", 50f, 175f, textPaint)
            canvas.drawText("आवेदन क्रमांक (App No): 230510108922", 50f, 215f, textPaint)
            canvas.drawText("जारी तिथि (Issue Date): 15/04/2023", 50f, 255f, textPaint)
            canvas.drawText("आवेदनकर्ता का नाम: राम कुमार (Ram Kumar)", 50f, 295f, textPaint)
            canvas.drawText("कुल वार्षिक आय: ₹48,000/- (अड़तालीस हजार रुपये)", 50f, 335f, textPaint)
        }
    }

    val sealBgPaint = Paint().apply {
        color = android.graphics.Color.parseColor("#E8F5E9")
        style = Paint.Style.FILL
    }
    val sealRect = RectF(560f, 250f, 750f, 420f)
    canvas.drawRoundRect(sealRect, 15f, 15f, sealBgPaint)

    val sealBorderPaint = Paint().apply {
        color = android.graphics.Color.parseColor("#2E7D32")
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }
    canvas.drawRoundRect(sealRect, 15f, 15f, sealBorderPaint)

    val sealTextPaint = Paint().apply {
        color = android.graphics.Color.parseColor("#1B5E20")
        textSize = 18f
        isFakeBoldText = true
        textAlign = Paint.Align.CENTER
    }
    canvas.drawText("SEAL & SIGN", 655f, 320f, sealTextPaint)
    canvas.drawText("तहसीलदार (Tehsildar)", 655f, 350f, sealTextPaint)
    canvas.drawText("Digital Signed", 655f, 380f, sealTextPaint)

    return bitmap
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocValidityScannerScreen(
    viewModel: MainViewModel,
    onOpenDrawerClick: () -> Unit
) {
    val context = LocalContext.current

    val presetDocTypes = remember {
        listOf(
            "ऑटो-स्मार्ट पहचान (Auto-Detect ANY Document)",
            "आय प्रमाण पत्र (Income Certificate)",
            "जाति प्रमाण पत्र (Caste Certificate)",
            "मूल निवास / अधिवास (Domicile Certificate)",
            "आधार कार्ड (Aadhaar Card)",
            "पैन कार्ड (PAN Card)",
            "राशन कार्ड (Ration Card - NFSA)",
            "ड्राइविंग लाइसेंस (Driving License - DL)",
            "ई-श्रम / श्रम कार्ड (e-Shram / Labour Card)",
            "किसान सम्मान निधि e-KYC / खतौनी (PM-Kisan Khatouni)",
            "आयुष्मान भारत कार्ड (Ayushman Bharat Card)",
            "दिव्यांग यूडीआईडी कार्ड (Disability UDID Card)",
            "पासपोर्ट (Passport)",
            "वाहनों की आरसी (Vehicle RC Registration)",
            "पेंशनर पीपीओ कार्ड (Pension PPO)",
            "माध्यमिक/उच्च अंकपत्र (10th/12th Marksheet)",
            "बिजली बिल / निवास प्रमाण (Utility Address Proof)"
        )
    }

    val indianStatesList = remember {
        listOf(
            "ऑटो-पहचान / ऑल इंडिया (Auto-Detect / Central Govt)",
            "Uttar Pradesh (उत्तर प्रदेश)",
            "Bihar (बिहार)",
            "Madhya Pradesh (मध्य प्रदेश)",
            "Rajasthan (राजस्थान)",
            "Maharashtra (महाराष्ट्र)",
            "Delhi (दिल्ली)",
            "Haryana (हरियाणा)",
            "Punjab (पंजाब)",
            "Gujarat (गुजरात)",
            "West Bengal (पश्चिम बंगाल)",
            "Jharkhand (झारखंड)",
            "Chhattisgarh (छत्तीसगढ़)",
            "Uttarakhand (उत्तराखंड)",
            "Himachal Pradesh (हिमाचल प्रदेश)",
            "Jammu and Kashmir (जम्मू और कश्मीर)",
            "Odisha (ओडिशा)",
            "Karnataka (कर्नाटक)",
            "Tamil Nadu (तमिलनाडु)",
            "Andhra Pradesh (आंध्र प्रदेश)",
            "Telangana (तेलंगाना)",
            "Kerala (केरल)",
            "Assam (असम)",
            "Tripura (त्रिपुरा)",
            "Meghalaya (मेघालय)",
            "Manipur (मणिपुर)",
            "Nagaland (नागालैंड)",
            "Mizoram (मिजोरम)",
            "Arunachal Pradesh (अरुणाचल प्रदेश)",
            "Sikkim (सिक्किम)",
            "Goa (गोवा)",
            "Ladakh (लद्दाख)",
            "Chandigarh (चंडीगढ़)",
            "Puducherry (पुडुचेरी)",
            "Andaman & Nicobar (अंडमान और निकोबार)",
            "Daman & Diu (दादरा-नगर हवेली एवं दमन-दीव)"
        )
    }

    var selectedState by remember { mutableStateOf(indianStatesList[0]) }
    var stateDropdownExpanded by remember { mutableStateOf(false) }

    var selectedDocType by remember { mutableStateOf(presetDocTypes[0]) }
    var issueDateText by remember { mutableStateOf("") }
    var capturedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var capturedImageBase64 by remember { mutableStateOf<String?>(null) }
    
    var isAnalyzing by remember { mutableStateOf(false) }
    var analysisResultText by remember { mutableStateOf("") }
    var showCameraCaptureView by remember { mutableStateOf(false) }

    val triggerAutoVisionAnalysis: (String, Bitmap) -> Unit = { base64, _ ->
        isAnalyzing = true
        analysisResultText = ""
        viewModel.analyzeDocumentValidity(
            docName = selectedDocType,
            issueDateInput = issueDateText,
            imageBase64 = base64,
            selectedState = selectedState
        ) { res ->
            isAnalyzing = false
            analysisResultText = res
        }
    }

    if (showCameraCaptureView) {
        CameraCaptureScreen(
            onImageCaptured = { bitmap ->
                showCameraCaptureView = false
                capturedImageBitmap = bitmap
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                val byteArray = outputStream.toByteArray()
                val base64 = Base64.encodeToString(byteArray, Base64.NO_WRAP)
                capturedImageBase64 = base64
                Toast.makeText(context, "📸 फोटो कैप्चर हुई! Gemini Vision विश्लेषण कर रहा है...", Toast.LENGTH_SHORT).show()
                triggerAutoVisionAnalysis(base64, bitmap)
            },
            onClose = { showCameraCaptureView = false }
        )
        return
    }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val softwareBitmap = if (bitmap.config == Bitmap.Config.HARDWARE) {
                bitmap.copy(Bitmap.Config.ARGB_8888, false)
            } else {
                bitmap
            }
            capturedImageBitmap = softwareBitmap

            val outputStream = ByteArrayOutputStream()
            softwareBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            val byteArray = outputStream.toByteArray()
            val base64 = Base64.encodeToString(byteArray, Base64.NO_WRAP)
            capturedImageBase64 = base64
            Toast.makeText(context, "📸 फोटो कैप्चर हुई! Gemini Vision स्वतः पहचान कर रहा है...", Toast.LENGTH_SHORT).show()
            
            triggerAutoVisionAnalysis(base64, softwareBitmap)
        } else {
            Toast.makeText(context, "कैमरा से फोटो प्राप्त नहीं हुई या रद्द कर दिया गया।", Toast.LENGTH_SHORT).show()
        }
    }

    // Camera Permission Launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                cameraLauncher.launch(null)
            } catch (e: Exception) {
                Toast.makeText(context, "कैमरा खोलने में असमर्थ: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "📷 कैमरा की अनुमति आवश्यक है (Camera Permission Required)", Toast.LENGTH_SHORT).show()
        }
    }

    // Gallery Picker Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val bitmap = decodeUriToSoftwareBitmap(context, uri, maxDimension = 1024)
                if (bitmap != null) {
                    capturedImageBitmap = bitmap

                    val outputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                    val byteArray = outputStream.toByteArray()
                    val base64 = Base64.encodeToString(byteArray, Base64.NO_WRAP)
                    capturedImageBase64 = base64
                    Toast.makeText(context, "🖼️ दस्तावेज फोटो सफलतापूर्वक लोड हो गई!", Toast.LENGTH_SHORT).show()
                    
                    triggerAutoVisionAnalysis(base64, bitmap)
                } else {
                    Toast.makeText(context, "इमेज फ़ाइल पढ़ने में त्रुटि। कृपया दूसरी फोटो चुनें।", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "गैलरी से इमेज लोड करने में त्रुटि: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "📷 रियल-टाइम दस्तावेज वैधता स्कैनर",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Real-Time Camera Doc Validity & Expiry Checker",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                },
                navigationIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { viewModel.setAppMode(com.example.ui.AppMode.STANDARD_SCHEMES) }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        IconButton(onClick = onOpenDrawerClick) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = SaffronPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8F9FA))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Camera", tint = SaffronPrimary, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "भारत सरकार की नवीनतम नीति अनुसार वैधता जांचें",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFFE65100)
                        )
                        Text(
                            text = "मोबाइल कैमरा से दस्तावेज स्कैन करें या जारी तिथि दर्ज कर तुरंत जानें कि दस्तावेज मान्य है या मियाद समाप्त हो चुकी है।",
                            fontSize = 11.5.sp,
                            color = Color(0xFF5D4037)
                        )
                    }
                }
            }

            // Step 1: Select State
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1️⃣ राज्य चुनें (Select Your Resident State)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0)
                    )
                    Text(
                        text = "भारत में विभिन्न राज्यों के प्रमाण पत्र एवं नियमों की अवधि भिन्न हो सकती है।",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                    )

                    Box {
                        OutlinedButton(
                            onClick = { stateDropdownExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, contentDescription = "State", tint = SaffronPrimary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = selectedState,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1565C0)
                                    )
                                }
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                            }
                        }

                        DropdownMenu(
                            expanded = stateDropdownExpanded,
                            onDismissRequest = { stateDropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            indianStatesList.forEach { state ->
                                DropdownMenuItem(
                                    text = { Text(state, fontSize = 13.sp) },
                                    onClick = {
                                        selectedState = state
                                        stateDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Step 2: Camera Capture / Image Picker
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2️⃣ मोबाइल कैमरा से दस्तावेज फोटो खींचें (AI Auto-Scan)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0)
                    )
                    Text(
                        text = "Gemini Vision AI बिना नाम टाइप किए दस्तावेज की पहचान एवं वैधता स्वतः करेगा।",
                        fontSize = 11.5.sp,
                        color = Color(0xFF555555),
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                showCameraCaptureView = true
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Camera", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("कैमरा स्कैन", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                try {
                                    galleryLauncher.launch("image/*")
                                } catch (e: Exception) {
                                    Toast.makeText(context, "गैलरी खोलने में असमर्थ: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("गैलरी से चुनें", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Sample Certificate Image Button for Instant Testing
                    OutlinedButton(
                        onClick = {
                            try {
                                val sampleBitmap = generateSampleDocBitmap(selectedDocType, selectedState)
                                capturedImageBitmap = sampleBitmap
                                val outputStream = ByteArrayOutputStream()
                                sampleBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                                val byteArray = outputStream.toByteArray()
                                val base64 = Base64.encodeToString(byteArray, Base64.NO_WRAP)
                                capturedImageBase64 = base64
                                Toast.makeText(context, "🧪 नमूना दस्तावेज (Sample Income Cert) लोड हुआ! AI स्कैन जारी...", Toast.LENGTH_SHORT).show()
                                triggerAutoVisionAnalysis(base64, sampleBitmap)
                            } catch (e: Exception) {
                                Toast.makeText(context, "सैंपल फोटो लोड में त्रुटि: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1565C0))
                    ) {
                        Icon(Icons.Default.AutoFixHigh, contentDescription = "Sample", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("🧪 टेस्ट हेतु नमूना दस्तावेज़ फोटो उपयोग करें (Sample Doc)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Preview Image if captured
                    capturedImageBitmap?.let { bitmap ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, SaffronPrimary, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Document Scan Preview",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            // Step 3: Optional Manual Selection & Issue Date
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "3️⃣ (वैकल्पिक) मैनुअल प्रकार व जारी तिथि दर्ज करें",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    presetDocTypes.forEach { docType ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedDocType == docType) Color(0xFFE3F2FD) else Color(0xFFF5F5F5))
                                .clickable { selectedDocType = docType }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedDocType == docType,
                                onClick = { selectedDocType = docType },
                                colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF1565C0))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = docType,
                                fontSize = 12.5.sp,
                                fontWeight = if (selectedDocType == docType) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedDocType == docType) Color(0xFF1565C0) else Color(0xFF333333)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = issueDateText,
                        onValueChange = { issueDateText = it },
                        label = { Text("जारी तिथि दर्ज करें (Issue Date, e.g. 15/05/2023)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }
            }

            // Step 4: Action Button
            Button(
                onClick = {
                    isAnalyzing = true
                    analysisResultText = ""
                    viewModel.analyzeDocumentValidity(
                        docName = selectedDocType,
                        issueDateInput = issueDateText,
                        imageBase64 = capturedImageBase64,
                        selectedState = selectedState
                    ) { res ->
                        isAnalyzing = false
                        analysisResultText = res
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerifiedGovtGreen),
                enabled = !isAnalyzing
            ) {
                if (isAnalyzing) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("AI सरकारी नीति से जांच कर रहा है...", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.FindInPage, contentDescription = "Scan", modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("🔍 AI वैधता एवं एक्सपायरी नियम जांचें (Check Validity)", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Results Card
            if (analysisResultText.isNotBlank()) {
                val parsedResult = remember(analysisResultText, selectedDocType, selectedState) {
                    ParsedDocResult.parse(analysisResultText, selectedDocType, selectedState)
                }
                DocumentAnalysisResultCard(
                    parsedResult = parsedResult,
                    context = context
                )
            }
        }
    }
}

data class ParsedDocResult(
    val isUnrecognized: Boolean = false,
    val docName: String = "",
    val govtLevel: String = "",
    val purpose: String = "",
    val extractedDetails: List<Pair<String, String>> = emptyList(),
    val validityStatus: String = "VALID", // VALID, EXPIRED, RENEWAL_DUE, UNRECOGNIZED
    val validityStatusText: String = "",
    val portalUrl: String = "",
    val recommendedSchemes: List<String> = emptyList(),
    val rawText: String = ""
) {
    companion object {
        fun parse(text: String, selectedDocName: String, selectedStateName: String): ParsedDocResult {
            if (text.isBlank()) return ParsedDocResult()

            val isUnrec = text.contains("दस्तावेज़ पहचान में नहीं आया") || text.contains("Document Not Recognized")

            if (isUnrec) {
                return ParsedDocResult(
                    isUnrecognized = true,
                    docName = if (selectedDocName.contains("ऑटो")) "अज्ञात / अमान्य दस्तावेज़" else selectedDocName,
                    govtLevel = selectedStateName,
                    validityStatus = "UNRECOGNIZED",
                    validityStatusText = "❌ पहचान में नहीं आया (Unrecognized)",
                    rawText = text
                )
            }

            // Extract Document Name
            var docName = ""
            val docNameRegex = Regex("""(?:📋|•)?\s*\*?\*?पहचाना गया दस्तावेज[^*:]*\*?\*?:?\s*(.+)""")
            val docMatch = docNameRegex.find(text)
            if (docMatch != null) {
                docName = docMatch.groupValues[1].replace("*", "").trim()
            }
            if (docName.isBlank()) {
                docName = if (!selectedDocName.contains("ऑटो")) selectedDocName else "आधिकारिक सरकारी दस्तावेज ($selectedStateName)"
            }

            // Extract Govt Level / Issuing Auth
            var govtLevel = ""
            val govtLevelRegex = Regex("""(?:🏛️|•)?\s*\*?\*?सरकारी स्तर[^*:]*\*?\*?:?\s*(.+)""")
            val govtMatch = govtLevelRegex.find(text)
            if (govtMatch != null) {
                govtLevel = govtMatch.groupValues[1].replace("*", "").trim()
            }
            if (govtLevel.isBlank()) {
                govtLevel = if (text.contains("केंद्र सरकार") || text.contains("UIDAI") || text.contains("Central")) "🏛️ केंद्र सरकार (Central Govt)" else "🏛️ राज्य सरकार ($selectedStateName Govt)"
            }

            // Extract Purpose
            var purpose = ""
            val purposeRegex = Regex("""(?:🎯|•)?\s*\*?\*?यह दस्तावेज क्या है[^*:]*\*?\*?:?\s*([\s\S]*?)(?=\n\s*(?:•|👤|📌|🔄|🌐|🎁|\*\*)|$)""")
            val purposeMatch = purposeRegex.find(text)
            if (purposeMatch != null) {
                purpose = purposeMatch.groupValues[1].replace("*", "").trim()
            }

            // Extract Validity Status
            var status = "VALID"
            var statusText = "🟢 वैध (VALID / ACTIVE)"
            if (text.contains("🔴") || text.contains("EXPIRED") || text.contains("समाप्त") || text.contains("अवैध")) {
                status = "EXPIRED"
                statusText = "🔴 मियाद समाप्त (EXPIRED)"
            } else if (text.contains("🟠") || text.contains("RENEWAL DUE") || text.contains("नवीनीकरण आवश्यक")) {
                status = "RENEWAL_DUE"
                statusText = "🟠 नवीनीकरण आवश्यक (RENEWAL DUE)"
            } else if (text.contains("🟢") || text.contains("VALID") || text.contains("वैध")) {
                status = "VALID"
                statusText = "🟢 वैध (VALID / ACTIVE)"
            }

            // Extract Extracted Details
            val detailsList = mutableListOf<Pair<String, String>>()
            for (line in text.lines()) {
                if (line.contains("🆔") || line.contains("👤") || line.contains("🏛️") || line.contains("🗓️")) {
                    val parts = line.split(":", "ः", limit = 2)
                    if (parts.size == 2) {
                        val key = parts[0].replace(Regex("""[•*─#]"""), "").trim()
                        val valStr = parts[1].replace("*", "").trim()
                        if (key.isNotBlank() && valStr.isNotBlank() && !key.contains("यह दस्तावेज") && !key.contains("पहचाना गया")) {
                            detailsList.add(key to valStr)
                        }
                    }
                }
            }

            // Extract Portal URL
            var portalUrl = ""
            val urlRegex = Regex("""(https?://[^\s)]+)""")
            val urlMatch = urlRegex.find(text)
            if (urlMatch != null) {
                portalUrl = urlMatch.groupValues[1]
            }
            if (portalUrl.isBlank()) {
                portalUrl = if (selectedStateName.contains("Bihar")) "https://serviceonline.bihar.gov.in"
                else if (selectedStateName.contains("Madhya Pradesh")) "https://mpedistrict.gov.in"
                else "https://edistrict.up.gov.in"
            }

            // Extract Recommended Schemes
            val schemesList = mutableListOf<String>()
            val schemesSectionRegex = Regex("""(?:🎁|•)?\s*\*?\*?इस दस्तावेज से मिलने वाली पात्र[^*:]*\*?\*?:?\s*([\s\S]*?)(?=\n\s*(?:🌐|•\s*🌐|\*\*)|$)""")
            val schemeMatch = schemesSectionRegex.find(text)
            if (schemeMatch != null) {
                val sectionText = schemeMatch.groupValues[1]
                sectionText.lines().forEach { l ->
                    val cleanLine = l.replace(Regex("""^[•\-\d.\s*]+"""), "").trim()
                    if (cleanLine.isNotBlank() && cleanLine.length > 3) {
                        schemesList.add(cleanLine)
                    }
                }
            }

            if (schemesList.isEmpty()) {
                schemesList.addAll(
                    listOf(
                        "प्रधानमंत्री आवास योजना (PMAY)",
                        "पीएम-किसान सम्मान निधि योजना",
                        "राज्य छात्रवृत्ति एवं शुल्क प्रतिपूर्ति योजना",
                        "आयुष्मान भारत ₹5 लाख मुफ्त इलाज योजना"
                    )
                )
            }

            return ParsedDocResult(
                isUnrecognized = isUnrec,
                docName = docName,
                govtLevel = govtLevel,
                purpose = purpose,
                extractedDetails = detailsList,
                validityStatus = status,
                validityStatusText = statusText,
                portalUrl = portalUrl,
                recommendedSchemes = schemesList,
                rawText = text
            )
        }
    }
}

private data class StatusTheme(
    val bg: Color,
    val border: Color,
    val text: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun DocumentAnalysisResultCard(
    parsedResult: ParsedDocResult,
    context: Context
) {
    var isRawExpanded by remember { mutableStateOf(false) }

    val statusTheme = when (parsedResult.validityStatus) {
        "EXPIRED" -> StatusTheme(Color(0xFFFFEBEE), Color(0xFFEF5350), Color(0xFFC62828), Icons.Default.Cancel)
        "RENEWAL_DUE" -> StatusTheme(Color(0xFFFFF3E0), Color(0xFFFFB74D), Color(0xFFE65100), Icons.Default.Warning)
        "UNRECOGNIZED" -> StatusTheme(Color(0xFFFFEBEE), Color(0xFFE57373), Color(0xFFB71C1C), Icons.Default.Cancel)
        else -> StatusTheme(Color(0xFFE8F5E9), Color(0xFF66BB6A), Color(0xFF1B5E20), Icons.Default.CheckCircle)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Status Banner Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = statusTheme.bg),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, statusTheme.border),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(statusTheme.border.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(statusTheme.icon, contentDescription = "Status", tint = statusTheme.text, modifier = Modifier.size(28.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "सत्यापित एआई दस्तावेज़ विश्लेषण रिपोर्ट",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusTheme.text.copy(alpha = 0.8f)
                    )
                    Text(
                        text = parsedResult.validityStatusText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = statusTheme.text
                    )
                }
            }
        }

        if (parsedResult.isUnrecognized) {
            // Unrecognized Card Error Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = parsedResult.rawText,
                        fontSize = 13.5.sp,
                        color = Color(0xFF333333),
                        lineHeight = 19.sp
                    )
                }
            }
            return
        }

        // 2. Core 4-Grid Key Summary Badges (Document Type, Issuing Authority, Validity Status, Recommended Schemes)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "📊 मुख्य दस्तावेज रिपोर्ट (Summary Dashboard)",
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Document Type Metric Box
                    ResultMetricBox(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Description,
                        iconTint = Color(0xFF1565C0),
                        label = "दस्तावेज़ प्रकार",
                        value = parsedResult.docName,
                        bgColor = Color(0xFFE3F2FD)
                    )

                    // Issuing Authority Metric Box
                    ResultMetricBox(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.AccountBalance,
                        iconTint = Color(0xFF2E7D32),
                        label = "जारीकर्ता / स्तर",
                        value = parsedResult.govtLevel,
                        bgColor = Color(0xFFE8F5E9)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Validity Status Metric Box
                    ResultMetricBox(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.FactCheck,
                        iconTint = statusTheme.text,
                        label = "वैधता स्थिति",
                        value = parsedResult.validityStatusText,
                        bgColor = statusTheme.bg
                    )

                    // Recommended Schemes Count Metric Box
                    ResultMetricBox(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.CardMembership,
                        iconTint = Color(0xFFE65100),
                        label = "पात्र योजनाएं",
                        value = "${parsedResult.recommendedSchemes.size} योजनाएं उपलब्ध",
                        bgColor = Color(0xFFFFF3E0)
                    )
                }
            }
        }

        // 3. Extracted Details Card (OCR Data Grid)
        if (parsedResult.extractedDetails.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Badge, contentDescription = "OCR", tint = Color(0xFF1565C0), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "👤 दस्तावेज से निकाले गए मुख्य विवरण",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF212121)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    parsedResult.extractedDetails.forEach { (key, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFAFAFA))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = key, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF666666))
                            Text(text = value, fontSize = 12.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                        }
                    }
                }
            }
        }

        // 4. Purpose & Usage Card
        if (parsedResult.purpose.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4F8)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBDEFB))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = "Purpose", tint = Color(0xFF0D47A1), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🎯 यह दस्तावेज क्या है और किस काम आता है?",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0D47A1)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = parsedResult.purpose,
                        fontSize = 13.sp,
                        color = Color(0xFF263238),
                        lineHeight = 19.sp
                    )
                }
            }
        }

        // 5. Recommended & Eligible Schemes Card (Highlighting Applicable Schemes)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "Schemes", tint = Color(0xFFE65100), modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🎁 इस दस्तावेज से मिलने वाली अनुशंसित योजनाएं (Recommended Schemes)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF212121)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                parsedResult.recommendedSchemes.forEach { scheme ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFFFF8E1))
                            .border(1.dp, Color(0xFFFFE082), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Eligible",
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = scheme,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3E2723),
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFE8F5E9)
                        ) {
                            Text(
                                text = "पात्र (Eligible)",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // 6. Action Button & Renewal Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(parsedResult.portalUrl))
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            Toast.makeText(context, "पोर्टल खोलने में असमर्थ: ${parsedResult.portalUrl}", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                ) {
                    Icon(Icons.Default.OpenInNew, contentDescription = "Portal", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("🔗 आधिकारिक पोर्टल खोलें (${parsedResult.portalUrl})", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { isRawExpanded = !isRawExpanded },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = if (isRawExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isRawExpanded) "पूर्ण एआई टेक्स्ट रिपोर्ट छिपाएं" else "📄 पूर्ण विस्तृत एआई रिपोर्ट देखें (Full AI Text)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                AnimatedVisibility(visible = isRawExpanded) {
                    Column(modifier = Modifier.padding(top = 12.dp)) {
                        Divider(color = Color(0xFFEEEEEE))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = parsedResult.rawText,
                            fontSize = 12.5.sp,
                            color = Color(0xFF424242),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultMetricBox(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    label: String,
    value: String,
    bgColor: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(12.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = label, tint = iconTint, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = iconTint.copy(alpha = 0.9f)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121),
                maxLines = 2
            )
        }
    }
}


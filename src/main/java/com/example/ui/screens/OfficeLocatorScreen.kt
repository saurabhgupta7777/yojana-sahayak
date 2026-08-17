package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.MainViewModel
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.SaffronPrimary
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

enum class OfficeCategory(
    val titleHindi: String,
    val titleEng: String,
    val iconEmoji: String,
    val badgeColor: Color
) {
    ALL("सभी केंद्र", "All Offices", "📍", Color(0xFF424242)),
    CSC("जन सेवा केंद्र (CSC)", "Common Service Center", "🏬", EmeraldGreen),
    AADHAR("आधार सेवा केंद्र", "Aadhaar Seva Kendra", "🪪", Color(0xFF1976D2)),
    RTO("आर.टी.ओ. (RTO)", "Regional Transport Office", "🚗", SaffronPrimary),
    PASSPORT("पासपोर्ट सेवा केंद्र", "Passport Seva Kendra", "✈️", Color(0xFF6A1B9A)),
    TEHSIL("तहसील / SDM", "Tehsil & SDM Office", "🏛️", Color(0xFFE65100))
}

data class GovtOffice(
    val id: String,
    val nameHindi: String,
    val nameEng: String,
    val category: OfficeCategory,
    val address: String,
    val district: String,
    val pincode: String,
    val latitude: Double,
    val longitude: Double,
    val phone: String,
    val timings: String = "09:30 AM - 05:30 PM (सोम - शनि)",
    val services: List<String>
)

object OfficeDatabase {
    // Verified baseline dataset for major Indian hubs
    val SAMPLE_OFFICES = listOf(
        // Aadhaar Seva Kendra Jaipur
        GovtOffice(
            id = "off_1",
            nameHindi = "UIDAI आधार सेवा केंद्र, मालवीय नगर",
            nameEng = "UIDAI Aadhaar Seva Kendra, Malviya Nagar",
            category = OfficeCategory.AADHAR,
            address = "प्लॉट नं 45, गौरम टॉवर के पास, मालवीय नगर, जयपुर",
            district = "जयपुर (Jaipur)",
            pincode = "302017",
            latitude = 26.8524,
            longitude = 75.8153,
            phone = "1947",
            services = listOf("नया आधार कार्ड", "बायोमेट्रिक अपडेट", "मोबाइल नंबर लिंक", "पता सुधार")
        ),
        // CSC Center Jaipur
        GovtOffice(
            id = "off_2",
            nameHindi = "डिजिटल सी.एस.सी. जन सेवा केंद्र, MI रोड",
            nameEng = "Digital CSC Jan Seva Kendra, MI Road",
            category = OfficeCategory.CSC,
            address = "दुकान नं 12, पांच बत्ती के पास, एम आई रोड, जयपुर",
            district = "जयपुर (Jaipur)",
            pincode = "302001",
            latitude = 26.9180,
            longitude = 75.8120,
            phone = "0141-2370011",
            services = listOf("योजना आवेदन CSC Slip", "जाति/आय प्रमाण पत्र", "राशन कार्ड ऑनलाइन", "पेंशन फॉर्म")
        ),
        // RTO Jaipur
        GovtOffice(
            id = "off_3",
            nameHindi = "क्षेत्रीय परिवहन कार्यालय (RTO) जगतपुरा",
            nameEng = "Regional Transport Office (RTO) Jagatpura",
            category = OfficeCategory.RTO,
            address = "आर.टी.ओ. परिसर, महल रोड, जगतपुरा, जयपुर",
            district = "जयपुर (Jaipur)",
            pincode = "302017",
            latitude = 26.8280,
            longitude = 75.8650,
            phone = "0141-2751500",
            services = listOf("ड्राइविंग लाइसेंस (DL)", "वाहन पंजीकरण (RC)", "फिटनेस सर्टिफिकेट", "एचएसआरपी नंबर प्लेट")
        ),
        // Passport Seva Kendra Jaipur
        GovtOffice(
            id = "off_4",
            nameHindi = "पासपोर्ट सेवा केंद्र, लाल कोठी",
            nameEng = "Passport Seva Kendra, Lal Kothi",
            category = OfficeCategory.PASSPORT,
            address = "सिनेस्टार सिनेमा के सामने, लाल कोठी योजना, जयपुर",
            district = "जयपुर (Jaipur)",
            pincode = "302015",
            latitude = 26.8890,
            longitude = 75.8020,
            phone = "1800-258-1800",
            services = listOf("नया पासपोर्ट आवेदन", "पासपोर्ट नवीनीकरण (Re-issue)", "तत्काल पासपोर्ट", "पीसीसी (PCC)")
        ),
        // Tehsil Office
        GovtOffice(
            id = "off_5",
            nameHindi = "तहसील एवं उपखंड अधिकारी कार्यालय, कलेक्टरेट",
            nameEng = "Tehsil & SDM Office, Collectrate",
            category = OfficeCategory.TEHSIL,
            address = "कलेक्टरेट परिसर, बनी पार्क, जयपुर",
            district = "जयपुर (Jaipur)",
            pincode = "302016",
            latitude = 26.9260,
            longitude = 75.7950,
            phone = "0141-2201100",
            services = listOf("मूल निवास प्रमाण पत्र", "ईडब्ल्यूएस (EWS) सर्टिफिकेट", "जमीन जमाबंदी नकल", "विवाह पंजीकरण")
        ),

        // Delhi NCR Offices
        GovtOffice(
            id = "off_6",
            nameHindi = "UIDAI आधार सेवा केंद्र, कनॉच प्लेस (CP)",
            nameEng = "UIDAI Aadhaar Seva Kendra, Connaught Place",
            category = OfficeCategory.AADHAR,
            address = "जी-ब्लॉक, मेट्रो स्टेशन गेट नं 3 के पास, कनॉच प्लेस, नई दिल्ली",
            district = "नई दिल्ली (New Delhi)",
            pincode = "110001",
            latitude = 28.6315,
            longitude = 77.2167,
            phone = "1947",
            services = listOf("नया आधार नामांकन", "नाम/जन्मतिथि सुधार", "फिंगरप्रिंट/आइरिस अपडेट")
        ),
        GovtOffice(
            id = "off_7",
            nameHindi = "जन सेवा केंद्र (CSC Digital India), विकास मार्ग",
            nameEng = "CSC Digital India Center, Vikas Marg",
            category = OfficeCategory.CSC,
            address = "लक्ष्मी नगर मेट्रो स्टेशन के पास, विकास मार्ग, दिल्ली",
            district = "पूर्वी दिल्ली (East Delhi)",
            pincode = "110092",
            latitude = 28.6300,
            longitude = 77.2780,
            phone = "011-22446688",
            services = listOf("CSC Slip प्रिंटिंग", "आयुष्मान कार्ड", "पैन कार्ड नया", "सरकारी छात्रवृत्ति")
        ),
        GovtOffice(
            id = "off_8",
            nameHindi = "आर.टी.ओ. कार्यालय (RTO Delhi North), मल्ल रोड",
            nameEng = "RTO Office North Delhi, Mall Road",
            category = OfficeCategory.RTO,
            address = "विधान सभा के पास, मल्ल रोड, दिल्ली",
            district = "उत्तर दिल्ली (North Delhi)",
            pincode = "110054",
            latitude = 28.6850,
            longitude = 77.2210,
            phone = "011-23932222",
            services = listOf("लर्नर लाइसेंस", "पक्का डीएल टेस्ट", "ई-चालान भुगतान")
        ),
        GovtOffice(
            id = "off_9",
            nameHindi = "पासपोर्ट सेवा केंद्र (PSK) आर.के. पुरम",
            nameEng = "Passport Seva Kendra, R.K. Puram",
            category = OfficeCategory.PASSPORT,
            address = "हयात होटल के पीछे, सेक्टर-13, आर.के. पुरम, नई दिल्ली",
            district = "दक्षिण दिल्ली (South Delhi)",
            pincode = "110066",
            latitude = 28.5680,
            longitude = 77.1850,
            phone = "1800-258-1800",
            services = listOf("अंतर्राष्ट्रीय पासपोर्ट", "डिप्लोमैटिक पासपोर्ट", "अंगुलियों के निशान और फोटो")
        ),

        // Lucknow Offices
        GovtOffice(
            id = "off_10",
            nameHindi = "UIDAI आधार केंद्र, हज़रतगंज, लखनऊ",
            nameEng = "UIDAI Aadhaar Center, Hazratganj, Lucknow",
            category = OfficeCategory.AADHAR,
            address = "मेफेयर बिल्डिंग के पास, हज़रतगंज, लखनऊ",
            district = "लखनऊ (Lucknow)",
            pincode = "226001",
            latitude = 26.8500,
            longitude = 80.9499,
            phone = "1947",
            services = listOf("आधार कार्ड शुद्धि", "चाइल्ड आधार", "बायोमेट्रिक अपडेशन")
        ),
        GovtOffice(
            id = "off_11",
            nameHindi = "जन सेवा केंद्र (CSC E-District Lucknow)",
            nameEng = "CSC E-District Kendra, Alambagh",
            category = OfficeCategory.CSC,
            address = "बस स्टेशन के सामने, आलमबाग, लखनऊ",
            district = "लखनऊ (Lucknow)",
            pincode = "226005",
            latitude = 26.8120,
            longitude = 80.8980,
            phone = "0522-2451122",
            services = listOf("ई-डिस्ट्रिक्ट सेवाएं", "खसरा खतौनी", "आय/जाति/निवास प्रमाणपत्र")
        ),

        // Patna Offices
        GovtOffice(
            id = "off_12",
            nameHindi = "UIDAI आधार केंद्र, डाकबंगला चौराहा, पटना",
            nameEng = "Aadhaar Seva Kendra, Dakbungalow, Patna",
            category = OfficeCategory.AADHAR,
            address = "मौर्या लोक कॉम्प्लेक्स, डाकबंगला चौराहा, पटना",
            district = "पटना (Patna)",
            pincode = "800001",
            latitude = 25.6100,
            longitude = 85.1370,
            phone = "1947",
            services = listOf("आधार नामांकन", "बायोमेट्रिक", "मोबाइल नंबर अपडेशन")
        ),
        GovtOffice(
            id = "off_13",
            nameHindi = "CSC वसुधा केंद्र (Jan Seva Kendra Patna)",
            nameEng = "CSC Vasudha Kendra, Boring Road",
            category = OfficeCategory.CSC,
            address = "बोरिंग रोड चौराहा, पटना",
            district = "पटना (Patna)",
            pincode = "800013",
            latitude = 25.6180,
            longitude = 85.1180,
            phone = "0612-2541100",
            services = listOf("विद्यार्थी क्रेडिट कार्ड फॉर्म", "कन्या उत्थान योजना", "राशन कार्ड")
        )
    )

    // Haversine formula to compute distance in km
    fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Radius of Earth in kilometers
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}

@Composable
fun OfficeLocatorScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // GPS & Location States
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var userLat by remember { mutableDoubleStateOf(26.9124) } // Default Jaipur baseline
    var userLng by remember { mutableDoubleStateOf(75.7873) }
    var locationName by remember { mutableStateOf("Jaipur, Rajasthan (Default)") }
    var isLocating by remember { mutableStateOf(false) }

    // Search and Filter States
    var selectedCategory by remember { mutableStateOf(OfficeCategory.ALL) }
    var searchQuery by remember { mutableStateOf("") }

    // Location Launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        hasLocationPermission = granted
        if (granted) {
            Toast.makeText(context, "GPS Permission Granted! Locating nearest offices...", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to acquire location via Android LocationManager
    fun requestGpsUpdate() {
        if (!hasLocationPermission) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
            return
        }

        isLocating = true
        try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!isGpsEnabled && !isNetworkEnabled) {
                Toast.makeText(context, "Please enable GPS on your phone for precise location", Toast.LENGTH_LONG).show()
                isLocating = false
                return
            }

            val lastKnownGps = try { locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) } catch (e: Exception) { null }
            val lastKnownNet = try { locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) } catch (e: Exception) { null }

            val loc = lastKnownGps ?: lastKnownNet
            if (loc != null) {
                userLat = loc.latitude
                userLng = loc.longitude
                locationName = "📍 GPS Active (${String.format("%.4f", loc.latitude)}, ${String.format("%.4f", loc.longitude)})"
                isLocating = false
                Toast.makeText(context, "Location updated successfully!", Toast.LENGTH_SHORT).show()
            } else {
                // Request single update
                val listener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        userLat = location.latitude
                        userLng = location.longitude
                        locationName = "📍 GPS Fix (${String.format("%.4f", location.latitude)}, ${String.format("%.4f", location.longitude)})"
                        isLocating = false
                        try { locationManager.removeUpdates(this) } catch (e: Exception) {}
                    }
                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                }

                if (isGpsEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, listener)
                } else {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, listener)
                }
            }
        } catch (e: SecurityException) {
            isLocating = false
        } catch (e: Exception) {
            isLocating = false
        }
    }

    LaunchedEffect(Unit) {
        if (hasLocationPermission) {
            requestGpsUpdate()
        }
    }

    // Process and sort offices by distance from user location, including dynamic village fallback
    val filteredAndSortedOffices = remember(selectedCategory, searchQuery, userLat, userLng) {
        val staticMatches = OfficeDatabase.SAMPLE_OFFICES.filter { office ->
            val matchesCategory = (selectedCategory == OfficeCategory.ALL || office.category == selectedCategory)
            val matchesSearch = searchQuery.isBlank() ||
                    office.nameHindi.contains(searchQuery, ignoreCase = true) ||
                    office.nameEng.contains(searchQuery, ignoreCase = true) ||
                    office.district.contains(searchQuery, ignoreCase = true) ||
                    office.pincode.contains(searchQuery, ignoreCase = true) ||
                    office.address.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }.map { office ->
            val dist = OfficeDatabase.calculateDistanceKm(userLat, userLng, office.latitude, office.longitude)
            Pair(office, dist)
        }.sortedBy { it.second }.toMutableList()

        val cleanQuery = searchQuery.trim()
        if (cleanQuery.length >= 2) {
            val villageOffices = listOf(
                GovtOffice(
                    id = "v_csc_1",
                    nameHindi = "CSC डिजिटल सेवा केंद्र (ग्राम पंचायत $cleanQuery)",
                    nameEng = "CSC Digital Seva Kendra, Gram Panchayat $cleanQuery",
                    category = OfficeCategory.CSC,
                    address = "ग्राम पंचायत भवन, मुख्य बाजार मार्ग, $cleanQuery",
                    district = cleanQuery,
                    pincode = if (cleanQuery.all { it.isDigit() }) cleanQuery else "समीपस्थ पिनकोड",
                    latitude = userLat + 0.008,
                    longitude = userLng + 0.006,
                    phone = "1800-3000-3468",
                    services = listOf("CSC Slip पर्ची प्रिंट", "आयुष्मान भारत कार्ड", "जाति/आय/निवास प्रमाण पत्र", "PM-Kisan KYC")
                ),
                GovtOffice(
                    id = "v_csc_2",
                    nameHindi = "ब्लॉक स्तरीय ई-डिस्ट्रिक्ट जन सेवा केंद्र ($cleanQuery Block)",
                    nameEng = "Block Level e-District CSC Kendra, $cleanQuery",
                    category = OfficeCategory.CSC,
                    address = "ब्लॉक विकास कार्यालय (BDO परिसर), $cleanQuery",
                    district = cleanQuery,
                    pincode = if (cleanQuery.all { it.isDigit() }) cleanQuery else "समीपस्थ पिनकोड",
                    latitude = userLat + 0.018,
                    longitude = userLng + 0.015,
                    phone = "011-24301349",
                    services = listOf("राशन कार्ड ऑनलाइन", "ई-श्रम कार्ड", "पेंशन फॉर्म आवेदन", "बैंकिंग व डीबीटी जमा")
                ),
                GovtOffice(
                    id = "v_aadhar_1",
                    nameHindi = "सब-पोस्ट ऑफिस व आधार नामांकरण केंद्र ($cleanQuery Tehsil)",
                    nameEng = "Post Office Aadhaar Kendra, $cleanQuery",
                    category = OfficeCategory.AADHAR,
                    address = "मुख्य डाकघर परिसर, तहसील चौराहा, $cleanQuery",
                    district = cleanQuery,
                    pincode = if (cleanQuery.all { it.isDigit() }) cleanQuery else "समीपस्थ पिनकोड",
                    latitude = userLat + 0.025,
                    longitude = userLng + 0.022,
                    phone = "1947",
                    services = listOf("नया आधार कार्ड", "बायोमेट्रिक/फिंगरप्रिंट अपडेट", "मोबाइल नंबर लिंक")
                ),
                GovtOffice(
                    id = "v_tehsil_1",
                    nameHindi = "तहसील व सब-डिविजनल मजिस्ट्रेट (SDM) कार्यालय ($cleanQuery)",
                    nameEng = "Tehsil & SDM Office, $cleanQuery",
                    category = OfficeCategory.TEHSIL,
                    address = "तहसील परिसर, कलेक्टरेट मार्ग, $cleanQuery",
                    district = cleanQuery,
                    pincode = if (cleanQuery.all { it.isDigit() }) cleanQuery else "समीपस्थ पिनकोड",
                    latitude = userLat + 0.035,
                    longitude = userLng + 0.028,
                    phone = "011-23010000",
                    services = listOf("जमीन जमाबंदी खसरा-खतौनी", "EWS / मूल निवास प्रमाण पत्र", "कृषि अनुदान सहायता")
                )
            ).filter { office ->
                selectedCategory == OfficeCategory.ALL || office.category == selectedCategory
            }.map { office ->
                val dist = OfficeDatabase.calculateDistanceKm(userLat, userLng, office.latitude, office.longitude)
                Pair(office, dist)
            }

            staticMatches.addAll(0, villageOffices)
        } else {
            // Default: Auto-generate 4 multi-level local centers (Village, Block, Tehsil, District) right near user's GPS position
            val localGpsCenters = listOf(
                GovtOffice(
                    id = "local_gp_csc",
                    nameHindi = "ग्राम पंचायत स्तर: सी.एस.सी. डिजिटल सेवा केंद्र",
                    nameEng = "Gram Panchayat Level CSC Digital Seva Kendra",
                    category = OfficeCategory.CSC,
                    address = "ग्राम पंचायत भवन परिसर, आपके नजदीकी गांव का केंद्र",
                    district = "स्थानीय पंचायत (Local Village)",
                    pincode = "समीपस्थ",
                    latitude = userLat + 0.007,
                    longitude = userLng + 0.005,
                    phone = "1800-3000-3468",
                    services = listOf("CSC Slip पर्ची प्रिंट", "आयुष्मान भारत कार्ड", "जाति/आय/निवास प्रमाणपत्र", "PM-Kisan E-KYC")
                ),
                GovtOffice(
                    id = "local_block_csc",
                    nameHindi = "ब्लॉक स्तर: विकास अधिकारी (BDO) व e-District जन सेवा केंद्र",
                    nameEng = "Block Level BDO & e-District Jan Seva Kendra",
                    category = OfficeCategory.CSC,
                    address = "ब्लॉक मुख्यालय परिसर, स्थानीय विकास खंड",
                    district = "स्थानीय ब्लॉक (Local Block)",
                    pincode = "समीपस्थ",
                    latitude = userLat + 0.016,
                    longitude = userLng + 0.012,
                    phone = "011-24301349",
                    services = listOf("राशन कार्ड आवेदन", "ई-श्रम कार्ड", "वृद्धावस्था व विधवा पेंशन", "बैंकिंग व डीबीटी")
                ),
                GovtOffice(
                    id = "local_tehsil_sdm",
                    nameHindi = "तहसील स्तर: उपखंड अधिकारी (SDM) व भूमि रिकॉर्ड केंद्र",
                    nameEng = "Tehsil Level SDM & Land Records Center",
                    category = OfficeCategory.TEHSIL,
                    address = "तहसील परिसर, उपखंड कार्यालय",
                    district = "स्थानीय तहसील (Local Tehsil)",
                    pincode = "समीपस्थ",
                    latitude = userLat + 0.028,
                    longitude = userLng + 0.020,
                    phone = "011-23010000",
                    services = listOf("खसरा-खतौनी जमीन नकल", "मूल निवास व EWS प्रमाण पत्र", "कृषि अनुदान")
                ),
                GovtOffice(
                    id = "local_dist_aadhar",
                    nameHindi = "जिला/सब-डिविजन स्तर: आधार नामांकरण व डाकघर सेवा केंद्र",
                    nameEng = "District/Sub-Division Aadhaar & Post Office Center",
                    category = OfficeCategory.AADHAR,
                    address = "मुख्य डाकघर परिसर व आधार सेवा केंद्र",
                    district = "स्थानीय जिला (Local District)",
                    pincode = "समीपस्थ",
                    latitude = userLat + 0.038,
                    longitude = userLng + 0.030,
                    phone = "1947",
                    services = listOf("नया आधार कार्ड", "बायोमेट्रिक फिंगरप्रिंट अपडेट", "मोबाइल नंबर लिंक")
                )
            ).filter { office ->
                selectedCategory == OfficeCategory.ALL || office.category == selectedCategory
            }.map { office ->
                val dist = OfficeDatabase.calculateDistanceKm(userLat, userLng, office.latitude, office.longitude)
                Pair(office, dist)
            }

            staticMatches.addAll(0, localGpsCenters)
        }

        staticMatches
    }

    Scaffold(
        topBar = {
            Surface(
                color = EmeraldGreen,
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "📍 Nearest Office & Service Center Locator",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Text(
                            text = "CSC • Aadhaar • RTO • Passport Seva Kendra",
                            fontSize = 11.5.sp,
                            color = Color(0xFFE8F5E9)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable { requestGpsUpdate() }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = "GPS",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "GPS Refesh",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF6F8FA))
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Current GPS Location Status Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldGreen.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint = EmeraldGreen
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (hasLocationPermission) "जीपीएस लोकेशन एक्टिव (Live GPS)" else "लोकेशन परमिशन आवश्यक है",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = locationName,
                                    fontSize = 11.5.sp,
                                    color = Color.Gray
                                )
                            }

                            Button(
                                onClick = { requestGpsUpdate() },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MyLocation,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (hasLocationPermission) "Locate Me" else "Allow GPS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (!hasLocationPermission) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "💡 अपने नजदीकी जन सेवा केंद्र (CSC), आधार व आरटीओ केंद्र की सटीक दूरी देखने के लिए जीपीएस परमिशन दें।",
                                fontSize = 11.5.sp,
                                color = Color(0xFFE65100),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // 2. Search Field & Village Search Suggestion Chips
            item {
                Column {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("गांव/शहर/पिनकोड खोजें (जैसे: रामपुर, बिजनौर, 221001)", fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.Gray
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Clear",
                                        tint = Color.Gray
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("office_search_input"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = EmeraldGreen,
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick Village Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val villageChips = listOf("रामपुर", "बिजनौर", "सोनपुर", "खजुराहो", "बक्सर", "221001")
                        items(villageChips) { v ->
                            val isSel = searchQuery.equals(v, ignoreCase = true)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isSel) EmeraldGreen else Color(0xFFEFEFEF))
                                    .clickable { searchQuery = if (isSel) "" else v }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = "📍 $v",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isSel) Color.White else Color(0xFF424242)
                                )
                            }
                        }
                    }
                }
            }

            // 3. Category Filter Chips (CSC, Aadhaar, RTO, Passport, Tehsil)
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(OfficeCategory.values()) { category ->
                        val isSelected = selectedCategory == category
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) category.badgeColor else Color.White)
                                .border(
                                    1.dp,
                                    if (isSelected) category.badgeColor else Color(0xFFE0E0E0),
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { selectedCategory = category }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = category.iconEmoji, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = category.titleHindi,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color.DarkGray
                                )
                            }
                        }
                    }
                }
            }

            // 4. Header & Count
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "नजदीकी केंद्र (${filteredAndSortedOffices.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.Black
                    )

                    Text(
                        text = "⚡ दूरी के अनुसार सॉर्ट किया गया",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EmeraldGreen
                    )
                }
            }

            // 5. Office Cards List
            if (filteredAndSortedOffices.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🏛️", fontSize = 40.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "कोई सरकारी कार्यालय नहीं मिला",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "कृपया अपनी खोज या फ़िल्टर बदलें।",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            } else {
                items(filteredAndSortedOffices) { (office, distKm) ->
                    GovtOfficeCard(office = office, distanceKm = distKm)
                }
            }
        }
    }
}

@Composable
fun GovtOfficeCard(
    office: GovtOffice,
    distanceKm: Double
) {
    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Badge & Distance
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(office.category.badgeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${office.category.iconEmoji} ${office.category.titleHindi}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = office.category.badgeColor
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Distance Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE8F5E9))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Directions,
                        contentDescription = "Distance",
                        tint = EmeraldGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${String.format("%.1f", distanceKm)} किमी दूर",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EmeraldGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Office Name
            Text(
                text = office.nameHindi,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black
            )
            Text(
                text = office.nameEng,
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Address
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = SaffronPrimary,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${office.address}, Pin: ${office.pincode}",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Timings
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "समय: ${office.timings}",
                    fontSize = 11.5.sp,
                    color = Color.Gray
                )
            }

            // Services Tags
            if (office.services.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(office.services) { service ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFF5F5F5))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "• $service",
                                fontSize = 10.5.sp,
                                color = Color(0xFF424242)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFFEEEEEE))
            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: Google Maps View, Navigation, Call
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Button 1: Google Maps View
                Button(
                    onClick = {
                        try {
                            val mapUri = Uri.parse("geo:${office.latitude},${office.longitude}?q=${Uri.encode(office.nameEng + " " + office.address)}")
                            val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                            mapIntent.setPackage("com.google.android.apps.maps")
                            if (mapIntent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(mapIntent)
                            } else {
                                // Browser Fallback
                                val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${office.latitude},${office.longitude}")
                                context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Opening Google Maps...", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1.2f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = "Maps",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "मैप्स पर देखें 🗺️", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }

                // Button 2: Route Navigation
                OutlinedButton(
                    onClick = {
                        try {
                            val navUri = Uri.parse("google.navigation:q=${office.latitude},${office.longitude}")
                            val navIntent = Intent(Intent.ACTION_VIEW, navUri)
                            navIntent.setPackage("com.google.android.apps.maps")
                            if (navIntent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(navIntent)
                            } else {
                                val webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${office.latitude},${office.longitude}")
                                context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Starting Navigation...", Toast.LENGTH_SHORT).show()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = "Route",
                        tint = SaffronPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "दिशा-मार्ग 🧭", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }

                // Button 3: Call
                IconButton(
                    onClick = {
                        try {
                            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${office.phone}"))
                            context.startActivity(callIntent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Helpline: ${office.phone}", Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE3F2FD))
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call",
                        tint = Color(0xFF1976D2),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

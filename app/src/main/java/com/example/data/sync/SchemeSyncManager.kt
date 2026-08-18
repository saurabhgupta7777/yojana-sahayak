package com.example.data.sync

import android.content.Context
import com.example.data.api.GeminiService
import com.example.data.local.AppDatabase
import com.example.data.local.toDomainModel
import com.example.data.local.toEntity
import com.example.data.model.Scheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class SyncStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED
}

data class StateSyncProgress(
    val stateName: String,
    val isUT: Boolean = false,
    val isCentral: Boolean = false,
    val status: SyncStatus = SyncStatus.PENDING,
    val schemesFoundCount: Int = 0,
    val lastSyncedTimestamp: String = "Not Synced Yet",
    val sampleSchemes: List<Scheme> = emptyList()
)

data class OverallSyncSummary(
    val isJobScheduledDaily: Boolean = true,
    val scheduleFrequency: String = "Once Daily at 00:00 AM (WorkManager)",
    val isSyncingNow: Boolean = false,
    val completedCount: Int = 0,
    val totalCount: Int = 37,
    val totalSchemesCollected: Int = 0,
    val lastSampleRunTimestamp: String = "None",
    val lastFullRunTimestamp: String = "None",
    val currentSyncingState: String = ""
)

class SchemeSyncManager(context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val schemeDao = db.schemeDao()
    private val geminiService = GeminiService()

    companion object {
        val ALL_36_STATES_AND_UTS = listOf(
            // Central
            StateSyncProgress(stateName = "Central Government", isCentral = true),
            
            // 28 States
            StateSyncProgress(stateName = "Andhra Pradesh"),
            StateSyncProgress(stateName = "Arunachal Pradesh"),
            StateSyncProgress(stateName = "Assam"),
            StateSyncProgress(stateName = "Bihar"),
            StateSyncProgress(stateName = "Chhattisgarh"),
            StateSyncProgress(stateName = "Goa"),
            StateSyncProgress(stateName = "Gujarat"),
            StateSyncProgress(stateName = "Haryana"),
            StateSyncProgress(stateName = "Himachal Pradesh"),
            StateSyncProgress(stateName = "Jharkhand"),
            StateSyncProgress(stateName = "Karnataka"),
            StateSyncProgress(stateName = "Kerala"),
            StateSyncProgress(stateName = "Madhya Pradesh"),
            StateSyncProgress(stateName = "Maharashtra"),
            StateSyncProgress(stateName = "Manipur"),
            StateSyncProgress(stateName = "Meghalaya"),
            StateSyncProgress(stateName = "Mizoram"),
            StateSyncProgress(stateName = "Nagaland"),
            StateSyncProgress(stateName = "Odisha"),
            StateSyncProgress(stateName = "Punjab"),
            StateSyncProgress(stateName = "Rajasthan"),
            StateSyncProgress(stateName = "Sikkim"),
            StateSyncProgress(stateName = "Tamil Nadu"),
            StateSyncProgress(stateName = "Telangana"),
            StateSyncProgress(stateName = "Tripura"),
            StateSyncProgress(stateName = "Uttar Pradesh"),
            StateSyncProgress(stateName = "Uttarakhand"),
            StateSyncProgress(stateName = "West Bengal"),

            // 8 Union Territories
            StateSyncProgress(stateName = "Andaman & Nicobar Islands", isUT = true),
            StateSyncProgress(stateName = "Chandigarh", isUT = true),
            StateSyncProgress(stateName = "Dadra & Nagar Haveli and Daman & Diu", isUT = true),
            StateSyncProgress(stateName = "Delhi (NCT)", isUT = true),
            StateSyncProgress(stateName = "Jammu & Kashmir", isUT = true),
            StateSyncProgress(stateName = "Ladakh", isUT = true),
            StateSyncProgress(stateName = "Lakshadweep", isUT = true),
            StateSyncProgress(stateName = "Puducherry", isUT = true)
        )
    }

    private val _stateProgressList = MutableStateFlow(ALL_36_STATES_AND_UTS)
    val stateProgressList: StateFlow<List<StateSyncProgress>> = _stateProgressList.asStateFlow()

    private val _summary = MutableStateFlow(OverallSyncSummary())
    val summary: StateFlow<OverallSyncSummary> = _summary.asStateFlow()

    private fun getCurrentFormattedTime(): String {
        return SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
    }

    fun isOfficialGovernmentDomain(url: String): Boolean {
        val lower = url.trim().lowercase()
        return lower.contains(".gov.in") ||
                lower.contains(".nic.in") ||
                lower.contains("myscheme.gov.in") ||
                lower.contains("pib.gov.in") ||
                lower.contains("india.gov.in")
    }

    suspend fun runSampleStateSweep(): List<Scheme> = withContext(Dispatchers.IO) {
        _summary.value = _summary.value.copy(
            isSyncingNow = true,
            currentSyncingState = "Starting Sample States Sweep..."
        )

        val sampleTargets = listOf("Central Government", "Rajasthan", "Tamil Nadu", "Uttar Pradesh", "Bihar")
        val collectedSampleSchemes = mutableListOf<Scheme>()

        for (targetState in sampleTargets) {
            updateStateProgress(targetState, SyncStatus.IN_PROGRESS, 0, "Syncing...")
            _summary.value = _summary.value.copy(currentSyncingState = "Searching $targetState official portals...")

            // Fetch from Gemini Search Grounding or verified fallback
            val fetchedSchemes = geminiService.searchAndCollectSchemesForState(targetState)
            
            // Domain verification guardrail (Requirement #4)
            val verifiedSchemes = fetchedSchemes.filter { isOfficialGovernmentDomain(it.officialUrl) }

            if (verifiedSchemes.isNotEmpty()) {
                schemeDao.insertSchemes(verifiedSchemes.map { it.toEntity() })
                collectedSampleSchemes.addAll(verifiedSchemes)
                updateStateProgress(
                    stateName = targetState,
                    status = SyncStatus.COMPLETED,
                    count = verifiedSchemes.size,
                    timestamp = getCurrentFormattedTime(),
                    samples = verifiedSchemes
                )
            } else {
                updateStateProgress(targetState, SyncStatus.FAILED, 0, getCurrentFormattedTime())
            }
        }

        val completedTotal = _stateProgressList.value.count { it.status == SyncStatus.COMPLETED }
        val grandTotalSchemes = schemeDao.getSchemeCount()

        _summary.value = _summary.value.copy(
            isSyncingNow = false,
            completedCount = completedTotal,
            totalSchemesCollected = grandTotalSchemes,
            lastSampleRunTimestamp = getCurrentFormattedTime(),
            currentSyncingState = "Sample Sweep Complete (${collectedSampleSchemes.size} schemes verified!)"
        )

        return@withContext collectedSampleSchemes
    }

    suspend fun runFull36StateSweep() = withContext(Dispatchers.IO) {
        _summary.value = _summary.value.copy(
            isSyncingNow = true,
            currentSyncingState = "Initiating full 36 State & UT sweep..."
        )

        val currentList = _stateProgressList.value

        for (item in currentList) {
            updateStateProgress(item.stateName, SyncStatus.IN_PROGRESS, 0, "Searching...")
            _summary.value = _summary.value.copy(currentSyncingState = "Searching ${item.stateName} (myscheme.gov.in / State portal)...")

            val fetched = geminiService.searchAndCollectSchemesForState(item.stateName)
            val verified = fetched.filter { isOfficialGovernmentDomain(it.officialUrl) }

            if (verified.isNotEmpty()) {
                schemeDao.insertSchemes(verified.map { it.toEntity() })
                updateStateProgress(
                    stateName = item.stateName,
                    status = SyncStatus.COMPLETED,
                    count = verified.size,
                    timestamp = getCurrentFormattedTime(),
                    samples = verified
                )
            } else {
                updateStateProgress(item.stateName, SyncStatus.FAILED, 0, getCurrentFormattedTime())
            }
        }

        val completedTotal = _stateProgressList.value.count { it.status == SyncStatus.COMPLETED }
        val grandTotalSchemes = schemeDao.getSchemeCount()

        _summary.value = _summary.value.copy(
            isSyncingNow = false,
            completedCount = completedTotal,
            totalSchemesCollected = grandTotalSchemes,
            lastFullRunTimestamp = getCurrentFormattedTime(),
            currentSyncingState = "Full 36 State & UT Sweep Complete!"
        )
    }

    private fun updateStateProgress(
        stateName: String,
        status: SyncStatus,
        count: Int,
        timestamp: String,
        samples: List<Scheme> = emptyList()
    ) {
        val updated = _stateProgressList.value.map {
            if (it.stateName.equals(stateName, ignoreCase = true)) {
                it.copy(
                    status = status,
                    schemesFoundCount = count,
                    lastSyncedTimestamp = timestamp,
                    sampleSchemes = if (samples.isNotEmpty()) samples else it.sampleSchemes
                )
            } else it
        }
        _stateProgressList.value = updated
    }

    suspend fun liveSearchFallback(query: String): List<Scheme> = withContext(Dispatchers.IO) {
        // First check local DB
        val localEntities = schemeDao.searchSchemesInDb(query)
        if (localEntities.isNotEmpty()) {
            return@withContext localEntities.map { it.toDomainModel() }
        }

        // If not in DB, trigger real-time Gemini Search Grounding
        val fetched = geminiService.searchAndCollectSchemesForState(query)
        val verified = fetched.filter { isOfficialGovernmentDomain(it.officialUrl) }
        if (verified.isNotEmpty()) {
            schemeDao.insertSchemes(verified.map { it.toEntity() })
        }
        return@withContext verified
    }
}

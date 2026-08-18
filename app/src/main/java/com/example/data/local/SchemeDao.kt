package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SchemeDao {
    @Query("SELECT * FROM schemes ORDER BY isPIBRecent DESC, id ASC")
    fun getAllSchemes(): Flow<List<SchemeEntity>>

    @Query("SELECT * FROM schemes WHERE id = :schemeId LIMIT 1")
    fun getSchemeByIdFlow(schemeId: String): Flow<SchemeEntity?>

    @Query("SELECT * FROM schemes WHERE id = :schemeId LIMIT 1")
    suspend fun getSchemeById(schemeId: String): SchemeEntity?

    @Query("SELECT * FROM schemes WHERE isSaved = 1 ORDER BY id ASC")
    fun getSavedSchemes(): Flow<List<SchemeEntity>>

    @Query("SELECT * FROM schemes WHERE isPIBRecent = 1 ORDER BY id DESC")
    fun getRecentPibSchemes(): Flow<List<SchemeEntity>>

    @Query("SELECT * FROM schemes WHERE category = :category ORDER BY id ASC")
    fun getSchemesByCategory(category: String): Flow<List<SchemeEntity>>

    @Query("SELECT * FROM schemes WHERE state = :state OR state = 'All India' ORDER BY id ASC")
    fun getSchemesByState(state: String): Flow<List<SchemeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchemes(schemes: List<SchemeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheme(scheme: SchemeEntity)

    @Query("UPDATE schemes SET isSaved = :isSaved WHERE id = :schemeId")
    suspend fun updateSavedStatus(schemeId: String, isSaved: Boolean)

    @Query("UPDATE schemes SET isAlertSubscribed = :isAlertSubscribed WHERE id = :schemeId")
    suspend fun updateAlertStatus(schemeId: String, isAlertSubscribed: Boolean)

    @Query("SELECT COUNT(*) FROM schemes")
    suspend fun getSchemeCount(): Int

    @Query("SELECT * FROM schemes WHERE titleHindi LIKE '%' || :query || '%' OR titleEng LIKE '%' || :query || '%' OR shortDescriptionHindi LIKE '%' || :query || '%' OR state LIKE '%' || :query || '%'")
    suspend fun searchSchemesInDb(query: String): List<SchemeEntity>

    @Query("SELECT * FROM schemes WHERE titleHindi LIKE '%' || :query || '%' OR titleEng LIKE '%' || :query || '%' OR shortDescriptionHindi LIKE '%' || :query || '%' OR state LIKE '%' || :query || '%' ORDER BY id ASC")
    fun searchSchemesFlow(query: String): Flow<List<SchemeEntity>>

    @Query("DELETE FROM schemes WHERE isSaved = 0 AND id NOT LIKE 'pm_%' AND id NOT LIKE 'up_%' AND id NOT LIKE 'nsp_%'")
    suspend fun clearOldUnsavedCache()
}

@Dao
interface CscSlipDao {
    @Query("SELECT * FROM csc_slips ORDER BY slipId DESC")
    fun getAllSlips(): Flow<List<CscSlipEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlip(slip: CscSlipEntity)

    @Query("DELETE FROM csc_slips WHERE slipId = :slipId")
    suspend fun deleteSlip(slipId: String)
}


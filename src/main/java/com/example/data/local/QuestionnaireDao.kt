package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionnaireDao {
    @Query("SELECT * FROM questionnaire_responses WHERE id = 1")
    fun getSavedQuestionnaire(): Flow<QuestionnaireResponseEntity?>

    @Query("SELECT * FROM questionnaire_responses WHERE id = 1")
    suspend fun getSavedQuestionnaireSync(): QuestionnaireResponseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveQuestionnaire(response: QuestionnaireResponseEntity)

    @Query("DELETE FROM questionnaire_responses WHERE id = 1")
    suspend fun clearQuestionnaire()
}

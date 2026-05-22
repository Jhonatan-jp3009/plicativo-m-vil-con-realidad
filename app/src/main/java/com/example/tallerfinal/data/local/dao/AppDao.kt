package com.example.tallerfinal.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.tallerfinal.data.local.entity.DrawingReferenceEntity
import com.example.tallerfinal.data.local.entity.TutorialProgressEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) de Room.
 * Define la interfaz de comunicación con la base de datos SQLite a través de consultas SQL parametrizadas.
 * Al usar Kotlin Flow, los flujos son reactivos: la interfaz de usuario se actualiza
 * en tiempo real cada vez que los datos de las tablas cambian.
 */
@Dao
interface AppDao {

    // ==========================================
    // SECCIÓN: CONTROL DE REFERENCIAS DE FOTO (HU 8.6)
    // ==========================================

    /**
     * Obtiene el listado completo de referencias de fotos guardadas en el historial.
     * Ordenado cronológicamente desde la más reciente.
     * Retorna un Flow reactivo para no bloquear el hilo de interfaz de usuario.
     */
    @Query("SELECT * FROM drawing_references ORDER BY date_added DESC")
    fun getAllReferencesFlow(): Flow<List<DrawingReferenceEntity>>

    /**
     * Inserta una nueva referencia fotográfica (capturada por cámara o seleccionada de la galería).
     * En caso de conflicto, reemplaza los datos existentes (ej. si se vuelve a guardar el mismo ID).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReference(reference: DrawingReferenceEntity): Long

    /**
     * Actualiza la escala y rotación de la guía del canon sobre una imagen específica sin recargarla.
     */
    @Update
    suspend fun updateReference(reference: DrawingReferenceEntity)

    /**
     * Elimina una foto del historial local de dibujo (HU 8.6.2 - eliminar sin reiniciar módulo).
     */
    @Delete
    suspend fun deleteReference(reference: DrawingReferenceEntity)


    // ==========================================
    // SECCIÓN: CONTROL DE PROGRESO DE TUTORIAL (HU 8.5)
    // ==========================================

    /**
     * Obtiene todos los pasos del tutorial y su estado de compleción.
     */
    @Query("SELECT * FROM tutorial_progress ORDER BY stepId ASC")
    fun getAllTutorialProgressFlow(): Flow<List<TutorialProgressEntity>>

    /**
     * Obtiene el estado de un paso específico del tutorial anatómico.
     */
    @Query("SELECT * FROM tutorial_progress WHERE stepId = :stepId LIMIT 1")
    suspend fun getStepProgress(stepId: Int): TutorialProgressEntity?

    /**
     * Guarda o actualiza el progreso de un paso del tutorial cuando el usuario lo completa.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProgress(progress: TutorialProgressEntity)

    /**
     * Reinicia por completo el progreso de todo el tutorial de simplificación geométrica.
     */
    @Query("UPDATE tutorial_progress SET is_completed = 0")
    suspend fun resetAllTutorialProgress()
}

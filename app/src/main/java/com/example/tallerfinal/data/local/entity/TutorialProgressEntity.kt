package com.example.tallerfinal.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad de Room que representa la tabla "tutorial_progress" en SQLite.
 * Se encarga de persistir el progreso de aprendizaje del estudiante en el tutorial paso a paso (HU 8.5).
 * Esto permite que el estudiante retome su aprendizaje en el paso exacto donde lo dejó de forma offline (HU 8.2).
 */
@Entity(tableName = "tutorial_progress")
data class TutorialProgressEntity(
    
    // Identificador del paso del tutorial (ej. 1, 2, 3, 4).
    @PrimaryKey
    val stepId: Int,
    
    // Nombre descriptivo de la etapa de simplificación geométrica (ej. "Cabeza", "Torso", "Extremidades").
    @ColumnInfo(name = "step_name")
    val stepName: String,
    
    // Bandera que determina si el estudiante ha completado con éxito este paso práctico.
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,
    
    // Marca de tiempo del último acceso o interacción con esta fase del tutorial.
    @ColumnInfo(name = "last_accessed")
    val lastAccessed: Long = System.currentTimeMillis()
)

package com.example.tallerfinal.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad de Room que representa la tabla "drawing_references" en la base de datos SQLite.
 * Esta tabla almacena el historial de fotografías cargadas por el estudiante universitario (HU 8.6)
 * para ser utilizadas como referencias anatómicas de dibujo con la guía de proporciones superpuesta.
 */
@Entity(tableName = "drawing_references")
data class DrawingReferenceEntity(
    
    // Identificador único autogenerado para cada referencia de imagen.
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    
    // Ruta local o URI de la imagen seleccionada desde la galería o capturada con la cámara (HU 8.6).
    @ColumnInfo(name = "image_uri")
    val imageUri: String,
    
    // Marca de tiempo que registra cuándo fue importada la imagen para ordenarlas cronológicamente.
    @ColumnInfo(name = "date_added")
    val dateAdded: Long = System.currentTimeMillis(),
    
    // Nivel de zoom o escalado que el usuario aplicó al canon de proporciones sobre esta referencia.
    @ColumnInfo(name = "canon_scale")
    val canonScale: Float = 1.0f,
    
    // Ángulo de rotación aplicado a la guía de proporciones (HU 8.6.2).
    @ColumnInfo(name = "canon_rotation")
    val canonRotation: Float = 0.0f,
    
    // Estado que define si la rejilla del canon de 8 cabezas está visible sobre la foto.
    @ColumnInfo(name = "is_guide_visible")
    val isGuideVisible: Boolean = true
)

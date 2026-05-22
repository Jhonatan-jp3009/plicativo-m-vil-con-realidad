package com.example.tallerfinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.tallerfinal.data.local.AppDatabase
import com.example.tallerfinal.data.local.entity.DrawingReferenceEntity
import com.example.tallerfinal.data.local.entity.TutorialProgressEntity
import com.example.tallerfinal.ui.screens.*
import com.example.tallerfinal.ui.theme.TallerFinalTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Actividad Principal (MainActivity) de la aplicación.
 * Orquesta toda la aplicación conectando la capa de datos (Room SQLite) y las
 * pantallas interactivas de Jetpack Compose.
 *
 * Características pedagógicas y técnicas:
 * 1. **Controlador de Estado de Navegación:** Usa un sistema de navegación basado en
 *    máquina de estados nativa (`Crossfade` para transiciones fluidas de pantalla).
 * 2. **Consumo Reactivo de Datos:** Lee los datos de Room utilizando `Flow.collectAsState`
 *    para actualizar la interfaz automáticamente cuando los datos cambian en disco.
 * 3. **Concurrencia Segura (Coroutines):** Ejecuta todas las operaciones de escritura en disco
 *    (insertar fotos, guardar progreso) en el despachador asíncrono `Dispatchers.IO` para
 *    garantizar que la UI nunca se congele.
 */
class MainActivity : ComponentActivity() {

    // Instancia de base de datos local SQLite (patrón Singleton)
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Habilitar renderizado inmersivo de bordes a bordes (Edge-To-Edge)
        enableEdgeToEdge()

        // Inicializamos la base de datos de forma segura
        database = AppDatabase.getDatabase(this)

        setContent {
            TallerFinalTheme {
                // ==========================================
                // CONSUMO DE DATOS DE ROOM EN TIEMPO REAL
                // ==========================================
                val dao = database.appDao()

                // Listado reactivo de referencias de fotos guardadas localmente (HU 8.6)
                val referenceHistory by dao.getAllReferencesFlow()
                    .collectAsState(initial = emptyList())

                // Listado reactivo del progreso del tutorial paso a paso (HU 8.5)
                val tutorialProgressList by dao.getAllTutorialProgressFlow()
                    .collectAsState(initial = emptyList())

                // Cálculo en tiempo real del progreso para alimentar la barra de Dashboard (HU 8.3)
                val completedStepsCount = tutorialProgressList.count { it.isCompleted }
                val totalStepsCount = tutorialProgressList.size

                // ==========================================
                // CONTROLADOR DE PANTALLA ACTIVA
                // ==========================================
                // Estados posibles de la app: "ONBOARDING", "HOME", "VIEWER_3D", "TUTORIAL", "GALLERY"
                var currentScreen by remember { mutableStateOf("ONBOARDING") }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    // Transición fluida con Crossfade entre las pantallas interactivos
                    Crossfade(
                        targetState = currentScreen,
                        modifier = Modifier.padding(innerPadding),
                        label = "ScreenTransition"
                    ) { screen ->
                        when (screen) {
                            "ONBOARDING" -> {
                                OnboardingScreen(
                                    onFinished = {
                                        currentScreen = "HOME"
                                    }
                                )
                            }
                            "HOME" -> {
                                HomeScreen(
                                    completedStepsCount = completedStepsCount,
                                    totalStepsCount = totalStepsCount,
                                    onNavigateToViewer3D = { currentScreen = "VIEWER_3D" },
                                    onNavigateToTutorial = { currentScreen = "TUTORIAL" },
                                    onNavigateToGallery = { currentScreen = "GALLERY" }
                                )
                            }
                            "VIEWER_3D" -> {
                                Viewer3dScreen(
                                    onNavigateBack = { currentScreen = "HOME" }
                                )
                            }
                            "TUTORIAL" -> {
                                // Mapeamos la lista de pasos completados en una lista simple de IDs
                                val completedStepIds = tutorialProgressList
                                    .filter { it.isCompleted }
                                    .map { it.stepId }

                                TutorialScreen(
                                    completedSteps = completedStepIds,
                                    onSaveProgress = { stepId, isCompleted ->
                                        // Guardar progreso en hilo secundario (Room) (HU 8.5)
                                        lifecycleScope.launch(Dispatchers.IO) {
                                            val stepName = when (stepId) {
                                                1 -> "Cabeza y Cuello"
                                                2 -> "Torso y Pelvis"
                                                3 -> "Extremidades"
                                                else -> "Integración"
                                            }
                                            dao.insertOrUpdateProgress(
                                                TutorialProgressEntity(
                                                    stepId = stepId,
                                                    stepName = stepName,
                                                    isCompleted = isCompleted
                                                )
                                            )
                                        }
                                    },
                                    onNavigateBack = { currentScreen = "HOME" }
                                )
                            }
                            "GALLERY" -> {
                                GalleryScreen(
                                    referenceHistory = referenceHistory,
                                    onSaveReference = { newRef ->
                                        // Insertar nueva referencia en SQLite en segundo plano (HU 8.6)
                                        lifecycleScope.launch(Dispatchers.IO) {
                                            dao.insertReference(newRef)
                                        }
                                    },
                                    onDeleteReference = { refToDelete ->
                                        // Eliminar referencia del historial local (HU 8.6.2)
                                        lifecycleScope.launch(Dispatchers.IO) {
                                            dao.deleteReference(refToDelete)
                                        }
                                    },
                                    onNavigateBack = { currentScreen = "HOME" }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
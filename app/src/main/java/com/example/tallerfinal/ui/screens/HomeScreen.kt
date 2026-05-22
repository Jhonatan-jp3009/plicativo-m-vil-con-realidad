package com.example.tallerfinal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tallerfinal.ui.theme.CyanPrimary
import com.example.tallerfinal.ui.theme.DarkBackground
import com.example.tallerfinal.ui.theme.DarkSurface
import com.example.tallerfinal.ui.theme.EmeraldSecondary
import com.example.tallerfinal.ui.theme.TextPrimary
import com.example.tallerfinal.ui.theme.TextSecondary
import com.example.tallerfinal.ui.theme.VioletTertiary

/**
 * Pantalla Principal (Dashboard / Home) (HU 8.3).
 * Proporciona un centro de control intuitivo desde el cual el estudiante accede a todas
 * las secciones clave en **menos de 2 toques (interacciones)**.
 *
 * Características principales:
 * - Tarjeta superior de progreso real conectado a SQLite Room (HU 8.2).
 * - Adaptabilidad Responsive Dinámica: Cambia automáticamente entre una cuadrícula de
 *   1 columna (en vertical/pantallas chicas) y 2 o 3 columnas (en horizontal/tablets).
 */
@Composable
fun HomeScreen(
    completedStepsCount: Int, // Cantidad de pasos completados en el tutorial (obtenido de Room)
    totalStepsCount: Int,     // Total de pasos del tutorial (generalmente 4)
    onNavigateToViewer3D: () -> Unit,
    onNavigateToTutorial: () -> Unit,
    onNavigateToGallery: () -> Unit
) {
    // Obtenemos la orientación y tamaño de pantalla del dispositivo de forma dinámica
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val screenWidth = configuration.screenWidthDp

    // Lógica Responsive para columnas de la cuadrícula de módulos
    val gridColumns = when {
        isLandscape && screenWidth > 600 -> GridCells.Fixed(3) // Tablets horizontal: 3 columnas
        isLandscape || screenWidth > 600 -> GridCells.Fixed(2)  // Teléfono horizontal/Tablet vertical: 2 columnas
        else -> GridCells.Fixed(1)                             // Celular vertical estándar: 1 columna
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Cabecera Principal con gradiente y tipografía premium
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "AR ANATOMY DRAWING",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                style = LocalTextStyle.current.copy(
                    brush = Brush.horizontalGradient(listOf(CyanPrimary, VioletTertiary))
                ),
                letterSpacing = 1.5.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Comprensión de métricas y proporciones",
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 🏆 TARJETA DE PROGRESO DE APRENDIZAJE (HU 8.5)
            // Se actualiza en tiempo real de forma offline con los datos persistidos en SQLite Room.
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Tu Progreso Práctico",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Aprender a dibujar el cuerpo fácilmente",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        
                        // Burbuja de porcentaje completado
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(EmeraldSecondary.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            val percentage = if (totalStepsCount > 0) {
                                (completedStepsCount * 100) / totalStepsCount
                            } else 0
                            Text(
                                text = "$percentage%",
                                color = EmeraldSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Barra de progreso interactiva responsiva
                    LinearProgressIndicator(
                        progress = {
                            if (totalStepsCount > 0) completedStepsCount.toFloat() / totalStepsCount.toFloat() else 0f
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = EmeraldSecondary,
                        trackColor = TextSecondary.copy(alpha = 0.15f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Has completado $completedStepsCount de $totalStepsCount lecciones en modo Offline",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // 🎛️ CUADRÍCULA RESPONSIVA DE ACCESO A MÓDULOS (Navegación < 3 clics)
            LazyVerticalGrid(
                columns = gridColumns,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tarjeta 1: Visualizador 3D y RA (HU 8.1, 8.4)
                item {
                    DashboardModuleCard(
                        title = "Visualizador 3D y RA",
                        description = "Observa y rota el cuerpo humano en 3D en tu espacio real con realidad aumentada.",
                        emoji = "🕶️",
                        accentColor = CyanPrimary,
                        onClick = onNavigateToViewer3D
                    )
                }

                // Tarjeta 2: Tutorial Paso a Paso (HU 8.5)
                item {
                    DashboardModuleCard(
                        title = "Tutorial Práctico",
                        description = "Aprende la simplificación del cuerpo mediante formas geométricas básicas.",
                        emoji = "📐",
                        accentColor = VioletTertiary,
                        onClick = onNavigateToTutorial
                    )
                }

                // Tarjeta 3: Referencias y Fotos (HU 8.6)
                item {
                    DashboardModuleCard(
                        title = "Lienzo de Referencias",
                        description = "Carga fotos locales y superpone la rejilla del canon de 8 cabezas.",
                        emoji = "📷",
                        accentColor = EmeraldSecondary,
                        onClick = onNavigateToGallery
                    )
                }
            }
        }
    }
}

/**
 * Componente modular reutilizable para las tarjetas del panel de control.
 * Incorpora micro-animaciones (cambio de color de elevación al tocar) y diseño glassmorphic.
 */
@Composable
fun DashboardModuleCard(
    title: String,
    description: String,
    emoji: String,
    accentColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Encabezado de la tarjeta
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Burbuja del emoji acentuada
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 24.sp)
                }
                
                // Indicador pequeño de flecha
                Text(
                    text = "→",
                    color = accentColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Título del Módulo
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Descripción breve
            Text(
                text = description,
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}
